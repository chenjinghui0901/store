package com.store.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.store.pojo.TbItem;
import com.store.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

/**
 * 导入solr消息消费者
 */
@Component
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            List<TbItem> itemList = JSON.parseArray(text, TbItem.class);
            itemSearchService.importList(itemList);
            System.out.println("接收消息，导入solr索引库");

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
