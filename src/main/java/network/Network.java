package network;


import entities.Package;

import java.io.IOException;

public interface Network {

    void listen() throws IOException;//Server

    void connect() throws IOException;//client

    Package receive();//Receiver
    void send(Package pack);//Sender

    void close() throws IOException;//end
}
