package network.impl;


import clases.PacketProcessor;
import entities.Packet;
import exceptions.MagicByteException;
import exceptions.WrongCrcException;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPNetworkPrev {


    private static ExecutorService threadPoolSend = Executors.newFixedThreadPool(6);
    private static ExecutorService threadPoolReceive = Executors.newFixedThreadPool(6);
    private static TCPNetworkPrev instance;

    public static TCPNetworkPrev getInstance(){
        if(instance == null) instance = new TCPNetworkPrev();
        return instance;
    }
    private TCPNetworkPrev(){}
   // @Override
    public void receiveMessage(byte[] message) throws Exception {
        threadPoolReceive.execute(()-> {
            try {
                Packet pack = PacketProcessor.decode(message);
                System.out.println("Message "+pack.getBmsq()+" was received");
                //Processor.process(pack.getBmsq());
            } catch (MagicByteException e) {
                System.err.println("Package was broken: "+e);
               // Processor.processFail();
            }catch (WrongCrcException w){
                System.err.println("Package was broken: "+w);
               // Processor.processFail();
            }catch (Exception e){
                e.printStackTrace();
            }

        });

    }
    private  static void shutdown(ExecutorService service){
        service.shutdown();
        try {
            service.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
            service.shutdownNow();
        }
    }

    public static  void shutdownSender(){
        shutdown(threadPoolSend);
    }
    public static  void shutdownReceiver(){
        shutdown(threadPoolReceive);
    }
    public static void initService(){
        if(threadPoolSend.isTerminated()) threadPoolSend = Executors.newFixedThreadPool(6);
        if(threadPoolReceive.isTerminated()) threadPoolReceive = Executors.newFixedThreadPool(6);
    }

    public void sendMessage(byte[] mess, InetAddress target) throws Exception {
        threadPoolSend.submit( ()-> System.out.println("Message was sent"));
    }



}
