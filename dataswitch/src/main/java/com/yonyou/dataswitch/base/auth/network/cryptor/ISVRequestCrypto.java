package com.yonyou.dataswitch.base.auth.network.cryptor;

import com.yonyou.dataswitch.base.exception.BusinessException;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class ISVRequestCrypto {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final byte[] aesKey;
    private final String appKey;
    private final String appSecret;

    /**
     * 构造函数
     *
     * @param appSecret      开放平台上，开发者设置的token
     * @param appKey         套件的 appKey
     */
    public ISVRequestCrypto(String appKey, String appSecret) {
        String encodingAesKey = buildAesKeyFromSecret(appSecret);
        if (encodingAesKey.length() != 43) {
            throw new BusinessException("invalid AES key");
        }
        aesKey = Base64.getDecoder().decode(encodingAesKey + "=");
        this.appSecret = appSecret;
        this.appKey = appKey;
    }

    // 生成4个字节的网络字节序
    private byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte) (sourceNumber & 0xFF);
        orderBytes[2] = (byte) (sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte) (sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte) (sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    // 还原4个字节的网络字节序
    private int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; i++) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xff;
        }
        return sourceNumber;
    }

    // 随机生成16位字符串
    public String getRandomStr() {
        return RandomStringUtils.random(16, true, true);
    }

    /**
     * 对明文进行加密.
     *
     * @param text 需要加密的明文
     * @return 加密后base64编码的字符串
     */

    public String encrypt(String randomStr, String text) {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(CHARSET);
        byte[] textBytes = text.getBytes(CHARSET);
        byte[] networkBytesOrder = getNetworkBytesOrder(textBytes.length);
        byte[] suiteKeyBytes = appKey.getBytes(CHARSET);

        // randomStr + networkBytesOrder + text + corpid
        byteCollector.addBytes(randomStrBytes)
                .addBytes(networkBytesOrder)
                .addBytes(textBytes)
                .addBytes(suiteKeyBytes);

        // ... + pad: 使用自定义的填充方式对明文进行补位填充
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);

        // 获得最终的字节流, 未加密
        byte[] unencrypted = byteCollector.toBytes();

        return doEncrypt(unencrypted);
    }

    private String doEncrypt(byte[] unencrypted) {
        try {
            // 设置加密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(aesKey, 0, 16);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);

            // 加密
            byte[] encrypted = cipher.doFinal(unencrypted);

            // 使用BASE64对加密后的字符串进行编码
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new BusinessException("AES encrypt failed", e);
        }
    }


    /**
     * 对密文进行解密.
     *
     * @param cipherText 需要解密的密文
     * @return 解密得到的明文
     * @ aes解密失败
     */
    public String decrypt(String cipherText) {
        byte[] original;
        try {
            // 设置解密模式为AES的CBC模式
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec iv =
                    new IvParameterSpec(Arrays.copyOfRange(aesKey, 0, 16));
            cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

            // 使用BASE64对密文进行解码
            byte[] encrypted = Base64.getDecoder().decode(cipherText);

            // 解密
            original = cipher.doFinal(encrypted);
        } catch (Exception e) {
            log.error("AES 解密失败, cause: {}", e.toString());
            throw new BusinessException("AES decrypt failed", e);
        }

        String message, from_suiteKey;
        try {
            // 去除补位字符
            byte[] bytes = PKCS7Encoder.decode(original);

            // 分离16位随机字符串,网络字节序和corpId
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

            int xmlLength = recoverNetworkBytesOrder(networkOrder);

            message = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
            from_suiteKey = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), CHARSET);
        } catch (Exception e) {
            log.error("无效的 AES key");
            throw new BusinessException("invalid aes key", e);
        }

        // suiteKey 不相同的情况
        if (!from_suiteKey.equals(appKey)) {
            log.error("suiteKey 校验失败");
            throw new BusinessException(
                    "suiteKey inconsistent exists,  from suite key: " + from_suiteKey + ", actual suite key: " + appKey);
        }
        return message;
    }


    /**
     * 将要发送的消息加密加前，打包为要发送的格式
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成 json 格式</li>
     * </ol>
     *
     * @param source    要发送的未加密消息体
     * @param timestamp unix 时间戳
     * @param nonce     随机串
     * @return 加密后的可以直接回复用户的密文，包括 msgSignature, timestamp, nonce,
     * encrypt 的 json 格式的字符串
     * @ 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public EncryptionHolder encrypt(String source, long timestamp, String nonce) {
        // 加密
        String encrypt = encrypt(getRandomStr(), source);
        // 加签
        String signature = SHA1.getSHA1(appSecret, String.valueOf(timestamp), nonce, encrypt);
        return new EncryptionHolder(signature, timestamp, nonce, encrypt);
    }


    /**
     * 将要发送的消息加密加前，打包为要发送的格式
     * <ol>
     * <li>对要发送的消息进行AES-CBC加密</li>
     * <li>生成安全签名</li>
     * <li>将消息密文和安全签名打包成 json 格式</li>
     * </ol>
     *
     * @param source 要发送的未加密消息体
     * @return 加密后的可以直接回复用户的密文，包括 msgSignature, timestamp, nonce,
     * encrypt 的 json 格式的字符串
     * @ 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public EncryptionHolder encrypt(String source) {
        return encrypt(source, System.currentTimeMillis(), getRandomStr());
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param signature 签名串，对应 signature
     * @param timestamp 时间戳，对应 timestamp
     * @param nonce     随机串，对应 nonce
     * @param encrypt   密文，对应 encrypt
     * @return 解密后的原文
     * @ 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decrypt(String signature, long timestamp, String nonce, String encrypt) {
        // 验签
        String targetSignature = SHA1.getSHA1(appSecret, String.valueOf(timestamp), nonce, encrypt);
        if (!targetSignature.equals(signature)) {
            throw new BusinessException("signature invalid, required: " + targetSignature + ", actual: " + targetSignature);
        }
        // 解密
        return decrypt(encrypt);
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param json 收到的加密加签的 json String
     * @return 解密后的原文
     * @ 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decryptFromJson(String json) {
        EncryptionHolder holder = jsonToHolder(json);
        return decrypt(holder.getMsgSignature(), holder.getTimestamp(), holder.getNonce(), holder.getEncrypt());
    }

    /**
     * 检验消息的真实性，并且获取解密后的明文.
     * <ol>
     * <li>利用收到的密文生成安全签名，进行签名验证</li>
     * <li>若验证通过，则提取 json 中的加密消息</li>
     * <li>对消息进行解密</li>
     * </ol>
     *
     * @param holder 收到的携带密文及加密信息的
     * @return 解密后的原文
     * @ 执行失败，请查看该异常的错误码和具体的错误信息
     */
    public String decrypt(EncryptionHolder holder) {
        return decrypt(holder.getMsgSignature(), holder.getTimestamp(), holder.getNonce(), holder.getEncrypt());
    }

    public EncryptionHolder jsonToHolder(String jsonStr) {
        return JacksonUtils.toJavaObject(jsonStr, EncryptionHolder.class);
    }

    /**
     * AES key 构建逻辑，使用 appSecret 移除掉 "-"，然后拼接 "0"，长度 43 位
     *
     * <pre>
     *     1. appSecret.replaceAll("-", "")
     *     2. appSecret + "00000000"，保证总长度 43 位
     * </pre>
     *
     *
     * @param appSecret 自建应用的 appSecret
     * @return
     */
    public static String buildAesKeyFromSecret(String appSecret) {
        String encodingAesKey = appSecret;
        encodingAesKey = encodingAesKey.replaceAll("-", "");
        if (encodingAesKey.length() == 43) {
            return encodingAesKey;
        }

        if (encodingAesKey.length() > 43) {
            return encodingAesKey.substring(0, 43);
        }

        StringBuilder stringBuilder = new StringBuilder(encodingAesKey);
        while (stringBuilder.length() < 43) {
            stringBuilder.append("0");
        }

        return stringBuilder.toString();
    }

}
