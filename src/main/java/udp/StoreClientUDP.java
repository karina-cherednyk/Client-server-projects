package udp;

import clases.PacketGenerator;
import entities.Packet;
import network.impl.UDPNetwork;


public class StoreClientUDP {
    public static void main(String[] args) {
        for(int k =0;k<10;k++)
            new Thread(()->{
                try {

                    UDPNetwork network =new UDPNetwork();
                    Packet packet;
                    Packet answerPacket;
                    System.out.println("udp.StoreClientUDP running via " + network + " connection");

                    network.connect();

                    for(int i = 0; i<10; ++i){

                        packet = PacketGenerator.generateCorrect();
                        do {
                            //Thread.sleep(1000);
                            network.send(packet);
                            answerPacket = network.receive();
                            //if answer packet is null - answer was lose, resend packet
                        }while (answerPacket==null||!answerPacket.getbPktId().equals(packet.getbPktId()));
                        System.out.println("Packet: "+ packet);
                        System.out.println("answer " + answerPacket);
                        if (answerPacket.getbPktId().equals(packet.getbPktId()))
                            System.out.println("CORRECT");
                        else
                            System.out.println("WRONG PACKET RESPONSE");

                    }
                    network.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();



}}
