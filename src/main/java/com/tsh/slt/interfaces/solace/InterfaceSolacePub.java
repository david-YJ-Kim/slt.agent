package com.tsh.slt.interfaces.solace;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.*;
import com.tsh.slt.config.SolaceSessionConfiguration;
import com.tsh.slt.interfaces.solace.util.code.ApEnumConstant;
import com.tsh.slt.util.ApCommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class InterfaceSolacePub {
    private PubEventHandler pubEventHandler = new PubEventHandler();
    private Topic topic;
    private static InterfaceSolacePub instance;
    private JCSMPSession session;
    private XMLMessageProducer messageProducer;

    public InterfaceSolacePub() {
    }

    public void init() throws JCSMPException {

        SolaceSessionConfiguration sessionConfiguration = SolaceSessionConfiguration.getSessionConfiguration();
        session = sessionConfiguration.getSession("PUB");
        this.session.connect();

        this.messageProducer = this.session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                log.debug("Producer received response for msg: {}", messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                log.error("Producer received error for msg: {} - {} - {}",
                        messageID,timestamp,e);
            }
        });
    }

    public static InterfaceSolacePub getInstance() throws JCSMPException {
        // Create the instance if it doesn't exist
        if (instance == null) {
            instance = new InterfaceSolacePub();
        }

        return instance;
    }



    public void sendTopicMessage(String sendCid, String payload, String topicName){
        try{

            XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);
            TextMessage txtMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

            SDTMap userPropMap = JCSMPFactory.onlyInstance().createMap();

            userPropMap.putString(ApEnumConstant.messageId.name(), "AP-" + ApCommonUtil.generateUUID(15));
            userPropMap.putString(ApEnumConstant.cid.name(), sendCid);

            txtMsg.setText(payload);
            txtMsg.setProperties(userPropMap);

            txtMsg.setDeliveryMode(DeliveryMode.PERSISTENT);
            prod.send(txtMsg, createTopic(topicName));
        }catch (Exception e){

            e.printStackTrace();
        }
    }




    public void sendQueueMessage(String cid, String payload, String queueName){
        try{

            XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);
            TextMessage txtMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

            SDTMap userPropMap = JCSMPFactory.onlyInstance().createMap();

//            String sendCid = null;
//
//            if ( fileType.equals(FisFileType.INSP.name()) )
//            	sendCid = FisMessageList.BRS_INSP_DATA_SAVE_REQ;
//            else
//            	sendCid = FisMessageList.BRS_MEAS_DATA_SAVE_REQ;

            userPropMap.putString(ApEnumConstant.cid.name(), cid);
            userPropMap.putString(ApEnumConstant.messageId.name(), "MSG-KEY-TMP-" + UUID.randomUUID() + "-" + System.currentTimeMillis());
            txtMsg.setText(payload);
            txtMsg.setProperties(userPropMap);

            txtMsg.setDeliveryMode(DeliveryMode.PERSISTENT);
            prod.send(txtMsg, JCSMPFactory.onlyInstance().createQueue(queueName));

            log.debug("Message has been sent. cid: {}, payload : {}, queue: {}."
                    , cid, payload, queueName);
        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public void sendBasicTextMessage(String cid, String payload, String topicName){
        try{

            XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);
            TextMessage txtMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

            SDTMap userPropMap = JCSMPFactory.onlyInstance().createMap();

//            String sendCid = null;
//
//            if ( fileType.equals(FisFileType.INSP.name()) )
//            	sendCid = FisMessageList.BRS_INSP_DATA_SAVE_REQ;
//            else
//            	sendCid = FisMessageList.BRS_MEAS_DATA_SAVE_REQ;

            userPropMap.putString("cid", cid);
            txtMsg.setText(payload);
            txtMsg.setProperties(userPropMap);

            txtMsg.setDeliveryMode(DeliveryMode.PERSISTENT);
            prod.send(txtMsg, createTopic(topicName));

            log.info("Message has been sent. cid: {}, payload : {}, topic: {}."
                    , cid, payload, topicName);
        }catch (Exception e){

            e.printStackTrace();
        }
    }

    public void sendMessage(HashMap<String, Object> sendMessage) {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            XMLMessageProducer prod = session.getMessageProducer(pubEventHandler);

            //Custom Property
            SDTMap userPropMap = JCSMPFactory.onlyInstance().createMap();

            //Byte Message
            TextMessage txtMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);

            txtMsg.setText(mapper.writeValueAsString(sendMessage));
            txtMsg.setProperties(userPropMap);
            txtMsg.setDeliveryMode(DeliveryMode.PERSISTENT);

            prod.send(txtMsg, topic);

        } catch(Exception e)
        {
//			log.error("Sender.sendMessage() Exception # ",e);
            if (session != null && !session.isClosed()) try { session.closeSession(); } catch (Exception e1) {}
        }
    }


    public class PubEventHandler implements JCSMPStreamingPublishCorrelatingEventHandler {
        @Override
        public void handleErrorEx(Object messageID, JCSMPException cause, long timestamp) {
            log.error("Producer received error for msg. cause: {}", cause);
        }

        @Override
        public void responseReceivedEx(Object messageID) {
            log.info("Producer received response for msg.");

        }
    }

    private Topic createTopic(String topicName) {
        return JCSMPFactory.onlyInstance().createTopic(topicName);
    }

    public boolean cleanUp() {
        if(!this.session.isClosed()) {
            this.session.closeSession();
        }
        return true;
    }
}
