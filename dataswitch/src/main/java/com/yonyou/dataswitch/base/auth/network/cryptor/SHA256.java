package com.yonyou.dataswitch.base.auth.network.cryptor;


import com.yonyou.dataswitch.base.exception.BusinessException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SHA256 {

    public static final String H_MAC_SHA256 = "HmacSHA256";

    public static String getSHA256(String token, String timestamp, String nonce, String encrypt) {
        try {
            String[] array = new String[]{ timestamp, nonce, encrypt};
            StringBuilder sb = new StringBuilder();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            Mac mac = Mac.getInstance(H_MAC_SHA256);
            mac.init(new SecretKeySpec(token.getBytes(StandardCharsets.UTF_8), H_MAC_SHA256));
            byte[] signData = mac.doFinal(str.getBytes(StandardCharsets.UTF_8));

            return new String(Base64.encodeBase64(signData));
        }catch (Exception e) {
            throw new BusinessException("sha256加密失败",e);
        }
    }
}
