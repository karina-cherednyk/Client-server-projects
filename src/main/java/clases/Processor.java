package clases;

import entities.Packet;
import network.Network;
import network.impl.TCPNetwork;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private static AtomicInteger counter = new AtomicInteger(0);



    public static void process(Network network, Packet pack){
        threadPool.submit(()->{
            try {
                String message = pack.getMessage();
                byte[] bytePack = PacketProcessor.encode(pack);
                System.out.println("Sending response "+pack.getBmsq()+" to "+InetAddress.getLocalHost());
                String answerMessage;
                if (message.equals("time")) {
                    answerMessage="now()";
                } else {
                    answerMessage = "other";
                }
                Packet answerPacket = new Packet((byte) 1, pack.getbPktId(),1,1,answerMessage, pack.getClientInetAddress(),pack.getClientPort());

                network.send(answerPacket);
            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } );
    }
    public static void process(TCPNetwork network, Packet pack, Socket socket){
        threadPool.submit(()->{
            try {
                String message = pack.getMessage();
                byte[] bytePack = PacketProcessor.encode(pack);
                System.out.println("Sending response "+pack.getBmsq()+" to "+InetAddress.getLocalHost());
                String answerMessage;
                if (message.equals("time")) {
                    answerMessage="now()";
                } else {
                    answerMessage = "other";
                }
                Packet answerPacket = new Packet((byte) 1, pack.getbPktId(),1,1,answerMessage, pack.getClientInetAddress(),pack.getClientPort());

                network.send(answerPacket,socket);
            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } );
    }




    public static  void shutdown(){
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            try {
                threadPool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
            threadPool.shutdownNow();
        }*/
    }
    public static void initService(){
        if(threadPool.isTerminated()) threadPool = Executors.newFixedThreadPool(6);
    }


}
