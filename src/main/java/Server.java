
import network.impl.TCPNetwork;
import network.impl.UDPNetwork;

import java.io.IOException;

public class Server {

    public static void main(String[] args) {
        try {

            UDPNetwork network = new UDPNetwork();
            //TCPNetwork network = new TCPNetwork();
            System.out.println("Server running via " + network + " connection");

            network.listen();

            network.receive();

            network.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}