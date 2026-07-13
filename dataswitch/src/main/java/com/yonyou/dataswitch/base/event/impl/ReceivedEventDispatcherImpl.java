package com.yonyou.dataswitch.base.event.impl;

import com.yonyou.dataswitch.base.auth.network.cryptor.EncryptionHolder;
import com.yonyou.dataswitch.base.event.EventListener;
import com.yonyou.dataswitch.base.event.ReceivedEventDispatcher;
import com.yonyou.dataswitch.base.event.pojo.Event;
import com.yonyou.dataswitch.base.utils.JacksonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SuppressWarnings({"rawtypes", "unchecked"})
@Slf4j
@RequiredArgsConstructor
public class ReceivedEventDispatcherImpl implements ReceivedEventDispatcher {

    private final ApplicationContext applicationContext;
    private List<EventListener> listeners;

    @Override
    public List<EventListener> findEventListens() {
        if (listeners != null) {
            return listeners;
        }
        Map<String, EventListener> beansOfType = applicationContext.getBeansOfType(EventListener.class);
        listeners = beansOfType.values().stream()
                .sorted(Comparator.comparing(EventListener::priority))
                .collect(Collectors.toList());
        return listeners;
    }

    @Override
    public void dispatch(Event event, String source, EncryptionHolder holder) {

        for (EventListener listener : findEventListens()) {
            try {
                if (!listener.supportTypes().contains(event.getType())) {
                    continue;
                }
                Class<? extends Event> eventClass = listener.getEventClass();
                boolean isContinue = listener.onEvent(event.getType(), JacksonUtils.toJavaObject(source, eventClass), holder);
                if (!isContinue) {
                    log.warn("listener {} want to stop event processing", listener.getClass());
                    return;
                }
            } catch (Exception e) {
                log.error("Exception when isv event listener " + listener.getClass() + " processing", e);
            }
        }
    }
}
