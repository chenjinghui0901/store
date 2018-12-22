package com.store.page.service.impl;

import com.store.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 生成页面
 */
@Component
public class PageListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;
    
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;

        try {
            String goodsId = textMessage.getText();
            System.out.println("接收到消息");
            boolean b = itemPageService.genItemHtml(Long.parseLong(goodsId));
            System.out.println("商品静态页生成" + b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
