package network;

import java.net.InetAddress;

public interface Network {
    void receiveMessage(byte[] message) throws Exception; //Receiver
    void sendMessage(byte[] message, InetAddress target) throws Exception; //Sender
}
