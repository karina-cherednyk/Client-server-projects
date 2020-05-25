package network;

import java.net.InetAddress;

public interface Network {
    void receiveMessage(); //Receiver
    void sendMessage(byte[] mess, InetAddress target) throws Exception; //Sender
}
