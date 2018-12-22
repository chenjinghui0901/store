package com.store.page.service.impl;

import com.store.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class PageDeleteListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage textMessage = (ObjectMessage)message;

        try {
            Long[] goodsIds = (Long[]) textMessage.getObject();
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("删除商品页" + b);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
