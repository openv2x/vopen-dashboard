package org.vopen.vopengateway;

import android.os.Messenger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by gvergine on 7/8/15.
 */
public class SubscriptionGraph
{

    private Map<String,ArrayList<Messenger>> mSubscribersMap = new HashMap<String,ArrayList<Messenger>>();
    private Map<Messenger,ArrayList<String>> mTopicsMap = new HashMap<Messenger,ArrayList<String>>();

    public SubscriptionGraph()
    {

    }

    public void addSubscription(String topic, Messenger client)
    {
        ArrayList<Messenger> clients = mSubscribersMap.get(topic);
        if (clients == null)
        {
            clients = new ArrayList<Messenger>();
            mSubscribersMap.put(topic,clients);
        }
        clients.add(client);

        ArrayList<String> topics = mTopicsMap.get(client);
        if (topics == null)
        {
            topics = new ArrayList<String>();
            mTopicsMap.put(client, topics);
        }
        topics.add(topic);
    }


    public void removeSubscription(String topic, Messenger client)
    {
        ArrayList<Messenger> clients = mSubscribersMap.get(topic);
        if (clients != null)
        {
            clients.remove(client);
        }


        ArrayList<String> topics = mTopicsMap.get(client);
        if (topics != null)
        {
            topics.remove(topic);
        }
    }


    public List<Messenger> getClientsForTopic(String topic)
    {
        ArrayList<Messenger> clients = mSubscribersMap.get(topic);
        if (clients == null)
        {
            clients = new ArrayList<Messenger>();
            mSubscribersMap.put(topic, clients);
        }
        return clients;
    }



    public List<String> getTopicsForClient(Messenger client)
    {
        ArrayList<String> topics = mTopicsMap.get(client);
        if (topics == null)
        {
            topics = new ArrayList<String>();
            mTopicsMap.put(client, topics);
        }
        return topics;
    }

    public void clear()
    {
        mSubscribersMap.clear();
        mTopicsMap.clear();
    }

    public void externalClear()
    {
        HashSet<String> topics = new HashSet<>(mSubscribersMap.keySet());
        for(String topic : topics)
        {
            if (!topic.endsWith("gwStatus"))
            {
                ArrayList<Messenger> clients = new ArrayList<>(getClientsForTopic(topic));

                for (Messenger client : clients)
                {
                    removeSubscription(topic, client);
                }
            }
        }
    }

}
