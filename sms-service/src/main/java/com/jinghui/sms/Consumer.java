package com.jinghui.sms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消息消费者
 * @author Administrator
 *
 */
@Component
public class Consumer {

	@JmsListener(destination="itcast")
	public void readMessage(String text){
		System.out.println("接收到消息:"+text);
	}
	
	@JmsListener(destination="itcast_map")
	public void readMapMessage(Map map){
		System.out.println(map);		
	}
	
}
