package com.store.search.service.impl;

import com.store.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

/**
 * 删除索引消息消费者
 */
@Component
public class ItemDeleteListener implements MessageListener{

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;

        try {
            Long[] ids = (Long[]) objectMessage.getObject();

            itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
            System.out.println("删除solr");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
