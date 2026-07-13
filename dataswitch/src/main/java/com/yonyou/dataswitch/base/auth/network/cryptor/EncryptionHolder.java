package com.yonyou.dataswitch.base.auth.network.cryptor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加密后的消息载体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptionHolder {

    /**
     * 消息签名
     */
    private String msgSignature;

    private String signature;

    /**
     * 消息发送 unix 时间戳
     */
    private long timestamp;

    /**
     * 随机值，盐
     */
    private String nonce;

    /**
     * AES -> BASE64 之后的消息体
     */
    private String encrypt;

    public EncryptionHolder(long timestamp, String nonce) {
        this.timestamp = timestamp;
        this.nonce = nonce;
    }

    public EncryptionHolder(String signature, long timestamp, String nonce, String encrypt) {
        this.signature = signature;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.encrypt = encrypt;
    }
}
