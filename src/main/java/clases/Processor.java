package clases;

import entities.Package;
import network.Network;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Processor {
    private static ExecutorService threadPool = Executors.newFixedThreadPool(6);

    private static AtomicInteger counter = new AtomicInteger(0);



    public static void process(Network network,Package pack){
        threadPool.submit(()->{
            try {
                String message = pack.getMessage();
                byte[] bytePack = PackageProcessor.encode(pack);
                System.out.println("Sending response "+pack.getBmsq()+" to "+InetAddress.getLocalHost());
                String answerMessage;
                if (message.equals("time")) {
                    answerMessage="now()";
                } else {
                    answerMessage = "other";
                }
                Package answerPackage = new Package((byte) 1, pack.getbPktId(),1,1,answerMessage, pack.getClientInetAddress(),pack.getClientPort());

                network.send(answerPackage);
            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } );
    }




    public static  void shutdown(){
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            e.printStackTrace();
            threadPool.shutdownNow();
        }
    }
    public static void initService(){
        if(threadPool.isTerminated()) threadPool = Executors.newFixedThreadPool(6);
    }


}
