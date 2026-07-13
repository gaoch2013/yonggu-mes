package com.yonyou.dataswitch.base.auth.network.cryptor;


import com.yonyou.dataswitch.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SHA1 class
 * <p>
 * 计算公众平台的消息签名接口.
 */
@Slf4j
public class SHA1 {

    /**
     * 用SHA1算法生成安全签名
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文
     * @return 安全签名
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) {
        String[] array = new String[]{token, timestamp, nonce, encrypt};
        StringBuilder builder = new StringBuilder();
        // 字符串排序
        Arrays.sort(array);
        for (int i = 0; i < 4; i++) {
            builder.append(array[i]);
        }
        String str = builder.toString();
        // SHA1签名生成
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            String message = "SHA-1 message digest not exists";
            log.error(message, e);
            throw new BusinessException(message, e);
        }
        md.update(str.getBytes());
        byte[] digest = md.digest();

        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            String shaHex = Integer.toHexString(b & 0xFF);
            if (shaHex.length() < 2) {
                result.append(0);
            }
            result.append(shaHex);
        }
        return result.toString();
    }
}
