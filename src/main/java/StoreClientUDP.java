import clases.PackageGenerator;
import entities.Package;
import network.Network;
import network.impl.UDPNetwork;


public class StoreClientUDP {
    public static void main(String[] args) {
        Package packet;
        Package answerPacket;
        Network network;
        try {
            network = new UDPNetwork();
            System.out.println("StoreClientUDP running via " + network + " connection");

            network.connect();

            for(int i = 0; i<20; ++i){

                    packet = PackageGenerator.generateCorrect();
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
        } catch (Exception e) {
            e.printStackTrace();
        }


}}
