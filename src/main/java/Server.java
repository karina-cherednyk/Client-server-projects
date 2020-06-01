
import clases.Processor;
import network.Network;
import network.impl.TCPNetwork;
import network.impl.UDPNetwork;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) {

        //final Network network = new TCPNetwork();
        Processor.initService();
        ExecutorService threadPool = Executors.newFixedThreadPool(12);
        try {
            UDPNetwork network = new UDPNetwork();
            System.out.println("Server running via " + network + " connection");

            network.listen();
            for (int i = 0; i < 5; ++i) {
                threadPool.submit(() -> {
                    //try {
                    
                        network.receive();


                    //} /*finally {
                    //    network.close();
                    //}


                });
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {

            try {
                threadPool.shutdown();
                while (!threadPool.isTerminated())
                    threadPool.awaitTermination(5, TimeUnit.SECONDS);

                Processor.shutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


}