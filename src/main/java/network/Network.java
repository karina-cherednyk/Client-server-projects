package network;


import entities.Packet;

import java.io.IOException;

public interface Network {

    void listen() throws IOException;//udp.StoreServerUDP

    void connect() throws IOException;//client

    Packet receive();//Receiver
    void send(Packet pack);//Sender

    void close() throws IOException;//end
}
