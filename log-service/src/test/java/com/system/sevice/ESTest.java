package com.system.sevice;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ming
 * @date 2022/2/27 22:34
 * @desc
 */
public class ESTest {

    @Test
    public void test01() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", "myes").build();
        TransportAddress transportAddress = new TransportAddress(InetAddress.getLocalHost(), 9300);
        TransportClient client = new PreBuiltTransportClient(settings);

        client.addTransportAddress(transportAddress);
        System.out.println(client.toString());
        client.close();
    }

}
