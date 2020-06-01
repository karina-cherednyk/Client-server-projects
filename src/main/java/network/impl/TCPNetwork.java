package network.impl;

import clases.PacketProcessor;
import clases.Processor;
import entities.Packet;
import network.Network;
import utils.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.Arrays;

public class TCPNetwork implements Network {

    ServerSocket serverSocket;
    ThreadLocal<Socket> localSoket;

    @Override
    public void listen() throws IOException {
        if (serverSocket == null)
            serverSocket = new ServerSocket(Properties.SERVER_PORT);
        getClient();

    }
    public void getClient(){
        localSoket = ThreadLocal.withInitial(() -> {
            try {
                return serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void connect() {
        localSoket = ThreadLocal.withInitial(() -> {
            try {
                return new Socket(InetAddress.getByName(Properties.INET_ADDRESS_NAME), Properties.SERVER_PORT);
            }catch (ConnectException e){
                return doReconnect();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    private Socket doReconnect() {
        boolean isConnected = false;
        Socket s = null;
        while (!isConnected){
            try {
                s = new Socket(InetAddress.getByName(Properties.INET_ADDRESS_NAME), Properties.SERVER_PORT);
                isConnected=true;
            }catch (ConnectException e){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("reconnect");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  s;
    }

    @Override
    public Packet receive() {

        try {
            while (true) {
                InputStream inputStream = localSoket.get().getInputStream();
                byte[] packageBufer = new byte[Properties.packetMaxSize];
                int length = inputStream.read(packageBufer);
                if (length > 0) {
                    byte[] fullPacket = Arrays.copyOfRange(packageBufer, 0, length);

                    System.out.println("Received");
                    System.out.println(Arrays.toString(fullPacket) + "\n");

                    Packet p = PacketProcessor.decode(fullPacket);

                    if (serverSocket != null)
                        Processor.process(this, p, localSoket.get());
                    else
                        return p;
                } else {
                    break;
                }
            }

        } catch (SocketException e) {
            try {
                close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void send(Packet pack) {
        try {
            OutputStream socketOutputStream = localSoket.get().getOutputStream();
            byte[] packetBytes = PacketProcessor.encode(pack);

            socketOutputStream.write(packetBytes);
            socketOutputStream.flush();

            System.out.println("Send");
            System.out.println(Arrays.toString(packetBytes) + "\n");
        }catch (SocketException e) {
            doReconnect();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void send(Packet pack, Socket socket) {
        try {
            OutputStream socketOutputStream = socket.getOutputStream();
            byte[] packetBytes = PacketProcessor.encode(pack);

            socketOutputStream.write(packetBytes);
            socketOutputStream.flush();

            System.out.println("Send");
            System.out.println(Arrays.toString(packetBytes) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
        else
            localSoket.get().close();

    }
}
