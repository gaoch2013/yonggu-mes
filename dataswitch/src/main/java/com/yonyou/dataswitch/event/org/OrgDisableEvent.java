package com.yonyou.dataswitch.event.org;

import com.google.common.collect.Sets;
import com.yonyou.dataswitch.base.auth.network.cryptor.EncryptionHolder;
import com.yonyou.dataswitch.base.event.EventListener;
import com.yonyou.dataswitch.event.ISVEventExtendDto;
import com.yonyou.dataswitch.event.IsvEventExtendType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @description: 组织停用事件监听
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class OrgDisableEvent implements EventListener<ISVEventExtendDto> {


    /**
     * 可以在这个里面写具体的业务逻辑
     * @param type 事项类型
     * @param event 解析后对象
     * @param holder 响应消息体
     * @return boolean
     */
    @Override
    public boolean onEvent(String type, ISVEventExtendDto event, EncryptionHolder holder) {
        String content = event.getContent();
        return true;
    }

    @Override
    public int priority() {
        return EventListener.super.priority()-100;
    }

    @Override
    public Set<String> supportTypes() {
        return Sets.newHashSet(IsvEventExtendType.BASE_ORG_EVENT_DISABLE_AFTE);
    }

    @Override
    public Class<ISVEventExtendDto> getEventClass() {
        return ISVEventExtendDto.class;
    }

}
