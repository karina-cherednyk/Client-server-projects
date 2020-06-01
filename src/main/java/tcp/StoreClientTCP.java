package tcp;

import clases.PacketGenerator;
import entities.Packet;
import network.impl.TCPNetwork;


public class StoreClientTCP {
    public static void main(String[] args) {
        try {
            TCPNetwork network = new TCPNetwork();
            System.out.println("Client running via " + network + " connection");
            for (int k = 0; k < 10; k++)
                new Thread(() -> {
                    try {
                        Packet packet;
                        Packet answerPacket;
                        System.out.println("tcp.StoreClientTCP running via " + network + " connection");

                            network.connect();

                        for (int i = 0; i < 1; ++i) {
                            packet = PacketGenerator.generateCorrect();

                                network.send(packet);
                                answerPacket = network.receive();

                            while (answerPacket==null){
                                System.out.println("try to reconnect");

                                network.send(packet);
                                answerPacket = network.receive();
                            }
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

