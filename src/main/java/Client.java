import clases.PackageGenerator;
import com.google.common.primitives.UnsignedLong;
import entities.Package;
import network.impl.TCPNetwork;
import network.impl.UDPNetwork;


public class Client {
    public static void main(String[] args) {
        Package packet = new Package((byte) 1, UnsignedLong.ONE, 1,1,"time");

        Package secondPacket = new Package((byte) 1, UnsignedLong.ONE, 1, 1, "notTime");

        try {

            UDPNetwork network = new UDPNetwork();
            //TCPNetwork network = new TCPNetwork();
            System.out.println("Client running via " + network + " connection");

            network.connect();

            network.send(PackageGenerator.generateCorrect());
            Package answerPacketOne = network.receive();
            if (answerPacketOne.getbPktId().equals(packet.getbPktId()))
                System.out.println("CORRECT");
            else
                System.out.println("WRONG PACKET RESPONSE");

            network.send(secondPacket);
            Package answerTwo = network.receive();

            network.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
