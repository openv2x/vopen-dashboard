package org.vopen.vopengateway;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.List;

/**
 * Created by giovanni on 10/23/16.
 */

public class IncomingHandler extends Handler
{
    private static final String TAG = "IncomingHandler";
    private static final int MSG_PRIVATE_SUBSCRIBE = 0;
    private static final int MSG_PRIVATE_UNSUBSCRIBE = 1;
    private static final int MSG_TOPICUPDATE = 2;
    private static final int MSG_PUBLISH = 3;
    private static final int MSG_ENABLE = 4;
    private static final int MSG_DISABLE = 5;
    private static final int MSG_APIKEYCHANGED = 6;
    private static final int MSG_HOSTCHANGED = 7;
    private static final int MSG_PORTCHANGED = 8;
    private static final int MSG_PUBLIC_SUBSCRIBE = 9;
    private static final int MSG_PUBLIC_UNSUBSCRIBE = 10;
    private static final int MSG_REST_API_CALL = 11;
    private static final int MSG_REST_API_RESP = 12;

    VOpenGatewayService service;

    public IncomingHandler(VOpenGatewayService service)
    {
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg)
    {
        String topic;
        List<Messenger> clients;
        Log.d(TAG, "What: " + msg.what);

        switch (msg.what)
        {
            case MSG_PRIVATE_SUBSCRIBE:
                Log.d(TAG, "MSG_PRIVATE_SUBSCRIBE to " + msg.getData().getCharSequence("topic").toString());

                String addUsrId = msg.getData().getCharSequence("skipUsrId","").toString();
                if (addUsrId.compareTo("yes")==0)
                {
                    topic = "private/" + msg.getData().getCharSequence("topic").toString();
                }
                else
                {
                    topic = "private/" + service.getUserId() + "/" + msg.getData().getCharSequence("topic").toString();
                }
                Log.d(TAG, "topic calculated to " + topic);
                clients = service.getSubscriptionGraph().getClientsForTopic(topic);
                Log.d(TAG, "there are " + clients.size() + " subscribed to " + topic);
                if (clients.size() == 0 && topic.compareTo("private/" + service.getUserId() + "/gwStatus") != 0)
                {
                    service.setSubscription(topic, true);
                }
                else if (topic.compareTo("private/" + service.getUserId() + "/gwStatus") == 0)
                {
                    service.sendConnectivityUpdate(msg.replyTo);
                }
                service.getSubscriptionGraph().addSubscription(topic, msg.replyTo);
                break;


            case MSG_PRIVATE_UNSUBSCRIBE:
                topic = "private/" + service.getUserId() + "/" + msg.getData().getCharSequence("topic").toString();
                service.getSubscriptionGraph().removeSubscription(topic, msg.replyTo);
                clients =  service.getSubscriptionGraph().getClientsForTopic(topic);
                if (clients.size() == 0 && topic.compareTo("private/" + service.getUserId() + "/gwStatus") != 0)
                {
                    service.setSubscription(topic, false);
                }
                break;

            case MSG_PUBLIC_SUBSCRIBE:
                Log.d(TAG, "MSG_PUBLIC_SUBSCRIBE to " + msg.getData().getCharSequence("topic").toString());
                topic = "public/" + msg.getData().getCharSequence("topic").toString();
                Log.d(TAG, "topic calculated to " + topic);

                clients = service.getSubscriptionGraph().getClientsForTopic(topic);
                Log.d(TAG, "there are " + clients.size() + " subscribed to " + topic);
                if (clients.size() == 0 && topic.compareTo("public/gwStatus") != 0)
                {
                    service.setSubscription(topic, true);
                }
                else if (topic.compareTo("public/gwStatus") == 0)
                {
                    service.sendConnectivityUpdate(msg.replyTo);
                }
                service.getSubscriptionGraph().addSubscription(topic, msg.replyTo);
                break;

            case MSG_PUBLIC_UNSUBSCRIBE:
                topic = "public/" + msg.getData().getCharSequence("topic").toString();
                service.getSubscriptionGraph().removeSubscription(topic, msg.replyTo);
                clients = service.getSubscriptionGraph().getClientsForTopic(topic);
                if (clients.size() == 0 && topic.compareTo("public/gwStatus") != 0)
                {
                    service.setSubscription(topic, false);
                }
                break;

            case MSG_PUBLISH:
                topic = msg.getData().getCharSequence("topic").toString();
                String value = msg.getData().getCharSequence("value").toString();

                service.publish(topic, value);
                break;

            case MSG_ENABLE:
                service.enable();
                service.doConnect();
                break;

            case MSG_DISABLE:
                service.disable();
                service.doDisconnect();
                break;

            case MSG_APIKEYCHANGED:
                if (service.getBrokerApiKey().compareTo(msg.getData().getCharSequence("apiKey").toString())!=0)
                {
                    service.doDisconnect();
                    try
                    {
                        service.setBrokerApiKey(msg.getData().getCharSequence("apiKey").toString());
                        service.doConnect();
                    }
                    catch (MalformedApiKeyException e)
                    {
                        e.printStackTrace();
                    }

                }
                break;

            case MSG_HOSTCHANGED:
                if (service.getBrokerAddress().compareTo(msg.getData().getCharSequence("address").toString())!=0)
                {
                    service.doDisconnect();
                    service.setBrokerAddress(msg.getData().getCharSequence("address").toString());
                    service.doConnect();
                }
                break;

            case MSG_PORTCHANGED:
                if (service.getBrokerPort() !=Integer.parseInt(msg.getData().getCharSequence("port").toString()))
                {
                    service.doDisconnect();
                    service.setBrokerPort(Integer.parseInt(msg.getData().getCharSequence("port").toString()));

                    service.doConnect();
                }
                break;

            case MSG_REST_API_CALL:
                String method = msg.getData().getCharSequence("method").toString();
                String params = msg.getData().getCharSequence("params").toString();
                service.restApiCall(method,params,msg.replyTo);
                break;

            default:
                super.handleMessage(msg);
        }
    }
}