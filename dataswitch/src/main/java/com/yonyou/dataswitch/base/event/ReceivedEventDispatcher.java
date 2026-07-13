package com.yonyou.dataswitch.base.event;

import com.yonyou.dataswitch.base.auth.network.cryptor.EncryptionHolder;
import com.yonyou.dataswitch.base.event.pojo.Event;

import java.util.List;

/**
 * @author yonyou
 */
public interface ReceivedEventDispatcher {

    @SuppressWarnings("rawtypes")
    List<EventListener> findEventListens();

    void dispatch(Event event, String source, EncryptionHolder holder);
}
