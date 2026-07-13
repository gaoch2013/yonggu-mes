package com.yonyou.dataswitch.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yonyou.dataswitch.base.auth.network.cryptor.EncryptionHolder;
import com.yonyou.dataswitch.base.auth.network.cryptor.PrivateAppCryptoSHA256;
import com.yonyou.dataswitch.base.event.ReceivedEventDispatcher;
import com.yonyou.dataswitch.base.event.pojo.Event;
import com.yonyou.dataswitch.base.properties.OpenApiProperties;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 事件订阅的回调controller
 */
@Slf4j
@ResponseBody
@RestController
@RequestMapping("/rest/event/v1")
public class EventListenerController {

    @Resource
    ReceivedEventDispatcher receivedEventDispatcher;
    @Resource
    OpenApiProperties openApiProperties;

    /**
     *  2022-07-29 后创建的应用使用该事件订阅回调
     * @param holder 开放平台请求消息体
     * @return 返回success表示成功接收，抛出异常等其他代表接收失败
     */
    @PostMapping("/new")
    public String onEventNew(@RequestBody EncryptionHolder holder) throws JsonProcessingException {
        PrivateAppCryptoSHA256 crypto = PrivateAppCryptoSHA256.newCrypto(openApiProperties.getAppKey(), openApiProperties.getAppSecret());
        // 验签解密后的消息体
        String decrypt = crypto.decryptMsg(holder);
        log.info("接收事件信息"+decrypt);
        Event event = JacksonUtils.toJavaObject(decrypt, Event.class);
        receivedEventDispatcher.dispatch(event, decrypt, holder);
        return "success";
    }

}
