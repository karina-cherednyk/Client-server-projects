package network.impl;

import clases.PackageProcessor;
import clases.Processor;
import entities.Package;
import network.Network;
import utils.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class TCPNetwork implements Network {

    ServerSocket serverSocket;
    private static ThreadLocal<Socket> threadLocal;


    Socket socket;
    @Override
    public synchronized void listen() throws IOException {
        if(serverSocket==null)
            serverSocket = new ServerSocket(Properties.SERVER_PORT);
        socket=serverSocket.accept();
        threadLocal=ThreadLocal.withInitial(()->socket);
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(InetAddress.getByName(Properties.INET_ADDRESS_NAME),Properties.SERVER_PORT);
        threadLocal=ThreadLocal.withInitial(()->socket);
    }
    @Override
    public Package receive(){

        try {
            InputStream inputStream = threadLocal.get().getInputStream();
            byte [] packageBufer = new byte [Properties.packetMaxSize];
            int length = inputStream.read(packageBufer);
            if(length>0){
                byte[] fullPacket = Arrays.copyOfRange(packageBufer,0,length);

                System.out.println("Received");
                System.out.println(Arrays.toString(fullPacket) + "\n");

                Package p = PackageProcessor.decode(fullPacket);

                if (serverSocket != null)
                    Processor.process(this, p);
                else
                    return p;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
    public boolean getStatus(){

        return socket.isConnected();
    }

    @Override
    public void send(Package pack) {
        try {
            OutputStream socketOutputStream = socket.getOutputStream();

            byte[] packetBytes = PackageProcessor.encode(pack);

            socketOutputStream.write(packetBytes);
            socketOutputStream.flush();

            System.out.println("Send");
            System.out.println(Arrays.toString(packetBytes) + "\n");
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        if(serverSocket!=null)
            serverSocket.close();
        else
            socket.close();

    }
}
