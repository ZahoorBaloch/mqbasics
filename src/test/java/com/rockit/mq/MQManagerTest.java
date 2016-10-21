package com.rockit.mq;

import static org.junit.Assert.*;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import com.rockit.mq.QManagerService;

public class MQManagerTest {

	String qmanager_name;
	
	@Test
	public void testMQManagerConnection() {
		
		Configuration configuration = ConfigurationService.getConfiguration();
		QManagerService queueManagerService = new QManagerService(configuration.getString("qmanager.name"), configuration.getString("qmanager.host"), configuration.getString("qmanager.channel"), Integer.parseInt(configuration.getString("qmanager.port")));
		
		//Test QManager connection
		assertNotNull(queueManagerService.getQueueManager());
		
		//Test message is written to Queue
		String queueName = ConfigurationService.getConfiguration().getString("queue.name");
		int queueSize = queueManagerService.getQueueSize(queueName);
		queueManagerService.writeQueue(queueName, "Hello World!!");
		assertEquals(queueSize+1, queueManagerService.getQueueSize(queueName));
		
		//Tets message is read from Queue
		assertEquals("Hello World!!", queueManagerService.readQueue(ConfigurationService.getConfiguration().getString("queue.name")));
		
		queueManagerService.disconnectQManager();
	}
	
	@Test
	public void writeMessageTest(){
		
	}
	
	@Test
	public void readMessageTest(){

	}
	
}
