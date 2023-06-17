package org.example;

import MSP.Configuration.ConfigFileReader;

public class Main {
    public static void main(String[] args) {
        final String ip = ConfigFileReader.getCommunicationProperty("prosumerAddress");
        String address = "http://"+ip;

        int prosumerPort = 6000;    // Port of the Prosumer you want to test
        int portRestJump = 2;

        ProsumerTest prosumerTest = new ProsumerTest(address, prosumerPort + portRestJump);

        prosumerTest.createConsumer("TV");
        prosumerTest.getConsumers();
        prosumerTest.updateConsumer("TV", "100");
        prosumerTest.deleteConsumer("TV");
        prosumerTest.getConsumers();

        prosumerTest.createProducer("MONOCRYSTALLINE_TYPE_x1");
        prosumerTest.getProducers();
        prosumerTest.updateProducer("MONOCRYSTALLINE_TYPE_x1", "100");
        prosumerTest.deleteProducer("MONOCRYSTALLINE_TYPE_x1");
        prosumerTest.getProducers();
    }
}