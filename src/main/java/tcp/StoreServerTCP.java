package tcp;

import clases.Processor;
import network.impl.TCPNetwork;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StoreServerTCP {
    public static void main(String[] args) {
        Processor.initService();
        ExecutorService threadPool = Executors.newFixedThreadPool(12);
        TCPNetwork network = new TCPNetwork();
       try {
           System.out.println("Server running via " + network + " connection");
           network.listen();
           for (int i = 0; i < 12; ++i) {
               threadPool.submit(() -> {
                   while (true){
                   network.receive();}

               });
           }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
           try {
               threadPool.shutdown();
               while (!threadPool.isTerminated())
                   threadPool.awaitTermination(5, TimeUnit.SECONDS);
               Processor.shutdown();
               network.close();
           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }


       }
    }
}
