package network.impl;

import clases.PacketProcessor;
import clases.Processor;
import entities.Packet;
import network.Network;
import utils.Properties;

import java.io.IOException;
import java.net.*;

import java.util.Arrays;

public class UDPNetwork implements Network {
    private DatagramSocket socket;
    private boolean isServer;

    public void listen() throws SocketException {
        socket = new DatagramSocket(Properties.SERVER_PORT);
        isServer=true;
    }

    public void connect() {
        try {
            socket = new DatagramSocket(0);
            socket.setSoTimeout(2000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public Packet receive()  {
        try {
            byte [] packageBufer = new byte [Properties.packetMaxSize];
            DatagramPacket packet = new DatagramPacket(packageBufer,packageBufer.length);

            socket.receive(packet);

            byte[] fullPacket = Arrays.copyOfRange(packageBufer,0,packet.getLength());
            Packet p = PacketProcessor.decode(fullPacket);
            p.setClientInetAddress(packet.getAddress());
            p.setClientPort(packet.getPort());

            if (isServer)
                Processor.process(this,p);
            else
                return p;

        } catch (SocketTimeoutException e){
            System.out.println("soket timeout");
            return null;
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void send(Packet pack) {

        try {
            InetAddress inetAddress = pack.getClientInetAddress() != null ? pack.getClientInetAddress() : InetAddress.getByName(Properties.INET_ADDRESS_NAME);
            int port = pack.getClientPort() != 0 ? pack.getClientPort() : 2305;

            byte[] packetBytes = PacketProcessor.encode(pack);

            DatagramPacket datagramPacket = new DatagramPacket(packetBytes, packetBytes.length, inetAddress, port);
            socket.send(datagramPacket);

            System.out.println("Send");
        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        socket.close();
    }
}
