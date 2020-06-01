import clases.PackageGenerator;
import entities.Package;
import network.Network;
import network.impl.TCPNetwork;
import network.impl.UDPNetwork;

import java.net.SocketTimeoutException;


public class Client {
    public static void main(String[] args) {
        Package packet;
        Package answerPacket;
        Network network;

            network = new UDPNetwork();
        try {
            //network = new TCPNetwork();
            System.out.println("Client running via " + network + " connection");

            network.connect();

            for(int i = 0; i<15; ++i){

                    packet = PackageGenerator.generateCorrect();
                    network.send(packet);
                    answerPacket = network.receive();
                    if (answerPacket.getbPktId().equals(packet.getbPktId()))
                        System.out.println("CORRECT");
                    else
                        System.out.println("WRONG PACKET RESPONSE");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


}}
