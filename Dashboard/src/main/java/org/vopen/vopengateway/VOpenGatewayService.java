package org.vopen.vopengateway;

import android.os.Messenger;

/**
 * Created by giovanni on 10/23/16.
 */

public interface VOpenGatewayService
{
    void doConnect();
    void doDisconnect();

    void sendConnectivityUpdate(Messenger client);
    void setSubscription(String topic, boolean enabled);
    void publish(String topic, String value);

    void enable();
    void disable();
    boolean isEnabled();
    SubscriptionGraph getSubscriptionGraph();
    void setBrokerApiKey(String apiKey) throws MalformedApiKeyException;
    void setBrokerAddress(String address);
    void setBrokerPort(int port);
    String getBrokerApiKey();
    String getBrokerAddress();
    int getBrokerPort();
    void restApiCall(String method, String params, Messenger replyTo);

    String getUserId();

}
