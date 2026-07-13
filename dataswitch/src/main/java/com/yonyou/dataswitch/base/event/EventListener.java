package com.yonyou.dataswitch.base.event;

import com.yonyou.dataswitch.base.auth.network.cryptor.EncryptionHolder;
import com.yonyou.dataswitch.base.event.pojo.Event;

import java.util.Set;


public interface EventListener<T extends Event>  {

    /**
     * @param event 事件体
     * @return true 继续处理, false停止事件广播
     */
    boolean onEvent(String type, T event, EncryptionHolder holder);

    /**
     * 优先级,数字越小越优先
     *
     * @return 优先级
     */
    default int priority() {
        return 1000;
    }

    /**
     * 支持的事件类型
     *
     * @return 支持的事件类型集合
     */
    Set<String> supportTypes();

    /**
     * 返回事件类型的具体实现类
     */
    Class<T> getEventClass();
}
