package com.rockit.mq;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.*;
import com.ibm.mq.*;
import com.ibm.mq.constants.MQConstants;
/**
 * This calls provides basic QManager operations
 */
public class QManagerService {
	
	private static Logger logger = Logger.getAnonymousLogger();
	private static MQQueueManager queueManager;
	
	/**
	 * create a specific QManager
	 * @param queueManagerName
	 * @param hostname
	 * @param channel
	 * @param port
	 */
	public QManagerService(String queueManagerName, String hostname, String channel, int port) {
			try{
				MQEnvironment.hostname = hostname;
				
		         MQEnvironment.channel = channel;
		         MQEnvironment.port = port;

		         MQEnvironment.properties.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
		        
				queueManager = new MQQueueManager(queueManagerName);
			}catch(com.ibm.mq.MQException mqex){
				logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("qmanager.connection.exception"), mqex);

				System.out.println("merge test at line 34 developer 2");

			}
	}
	/**
	 * Write to a Queue
	 * @param queueName 
	 * @param msg
	 */
	public void writeQueue(String queueName, String msg){
	     int openOptions = MQConstants.MQOO_OUTPUT + MQConstants.MQOO_FAIL_IF_QUIESCING;

	     try {
	    	 
			MQQueue queue = queueManager.accessQueue( queueName , openOptions,
	                 null,           // default q manager
	                 null,           // no dynamic q name
	                 null );         // no alternate user id


	         // Define a simple MQ message, and write some text in UTF format..
	         MQMessage sendmsg               = new MQMessage();
	         sendmsg.format                  = MQConstants.MQFMT_STRING;
	         sendmsg.feedback                = MQConstants.MQFB_NONE;
	         sendmsg.messageType             = MQConstants.MQMT_DATAGRAM;
	         sendmsg.replyToQueueName        = "";
	         sendmsg.replyToQueueManagerName = "";

	         MQPutMessageOptions pmo = new MQPutMessageOptions();  // accept the defaults, same as MQPMO_DEFAULT constant

	             sendmsg.clearMessage();
	             sendmsg.messageId     = MQConstants.MQMI_NONE;
	             sendmsg.correlationId = MQConstants.MQCI_NONE;
	             sendmsg.writeString(msg);
	             
	             // put the message on the queue
	             queue.put(sendmsg, pmo);

	         queue.close();

	        }catch (com.ibm.mq.MQException mqex){
	        	logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("write.queue.exception"), mqex);
	        }
	        catch (java.io.IOException ioex){
	        	logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("write.io.exception"), ioex);
	        }
	}
	/**
	 * read a single message from QManager
	 * @param queueName
	 */
	public String readQueue(String queueName){
		String msgText = "";
		
		try{
			int openOptions = MQConstants.MQOO_FAIL_IF_QUIESCING | MQConstants.MQOO_INPUT_SHARED | MQConstants.MQOO_BROWSE;
			MQQueue queue = queueManager.accessQueue( queueName , openOptions,
	                null,           // default q manager
	                null,           // no dynamic q name
	                null );         // no alternate user id
			
			MQGetMessageOptions gmo = new MQGetMessageOptions();
		    gmo.options=MQConstants.MQGMO_WAIT | MQConstants.MQGMO_BROWSE_FIRST;
		    gmo.matchOptions=MQConstants.MQMO_NONE;
		    gmo.waitInterval=5000;
		    
			MQMessage theMessage    = new MQMessage();
			 queue.get(theMessage,gmo); 
			 msgText = theMessage. readStringOfCharLength(theMessage.getMessageLength());
			 
			 gmo.options = MQConstants.MQGMO_MSG_UNDER_CURSOR; 
             queue.get(theMessage, gmo);

			 queue.close();
			 
		}catch (com.ibm.mq.MQException mqex){
			logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("read.queue.exception"), mqex);
		}catch (java.io.IOException ioex){
			logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("read.io.exception"), ioex);
        }
		return msgText;
	}
	/**
	 * get depth of a Queue
	 * @param queueName
	 */
	public int getQueueSize(String queueName){
		int queueSize = 0 ;
		
		try{
			int openOptions = MQConstants.MQOO_INQUIRE + MQConstants.MQOO_FAIL_IF_QUIESCING;
			MQQueue queue = queueManager.accessQueue(queueName, openOptions);
			
			queueSize = queue.getCurrentDepth();
			queue.close();
			
		}catch(com.ibm.mq.MQException mqex){
			logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("queueSize.exception"), mqex);
		}
		return queueSize;
	}
	/**
	 * Disconnect QManager connection
	 */
	public void disconnectQManager(){
		try{
			queueManager.disconnect();		
		}catch(com.ibm.mq.MQException mqex){
			logger.log(Level.SEVERE, ConfigurationService.getConfiguration().getString("qmanager.disconnection.exception"), mqex);
		}
	}	
	/**
	 * get QManager
	 */
	public static MQQueueManager getQueueManager() {
		return queueManager;
	}
	
//	public void clearQueue(String queueName){
//		int openOptions = MQConstants.MQOO_INQUIRE + MQConstants.MQOO_FAIL_IF_QUIESCING;
//		try {
//			MQQueue queue = queueManager.accessQueue(queueName, openOptions);
//			 queue.;
//			queue.close();
//		} catch (MQException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
