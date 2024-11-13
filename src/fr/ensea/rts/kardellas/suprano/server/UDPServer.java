package fr.ensea.rts.kardellas.suprano.server;

import java.io.IOException;
import java.net.*;

public class UDPServer extends Server {
    private DatagramSocket socket;

    public UDPServer() {
        super();
    }

    public UDPServer(int port) {
        super(port);
    }

    @Override
    public void launch() {
        try {
            socket = new DatagramSocket(this.port, InetAddress.getByName("0.0.0.0"));
            System.out.println("fr.ensea.rts.kardellas.suprano.server.Server launched on IP " + InetAddress.getLocalHost().getHostAddress() + " and on port " + this.port);
            this.state = true;
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException("Failed to launch server", e);
        }
    }

    @Override
    public void handleConnection() {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            String data = new String(packet.getData(), 0, packet.getLength());

            System.out.println(clientAddress.getHostAddress() + ":" + clientPort + " > " + socket.getLocalAddress().getHostAddress() + ":" + this.port + " / Data: " + data);

            DatagramPacket responsePacket = new DatagramPacket(packet.getData(), packet.getLength(), clientAddress, clientPort);
            socket.send(responsePacket);
        } catch (IOException e) {
            throw new RuntimeException("Error handling client connection", e);
        }
    }

    public static void main(String[] args) {
        UDPServer server = (args.length == 0) ? new UDPServer() : new UDPServer(Integer.parseInt(args[0]));
        server.launch();
        while (true) {
            server.handleConnection();
        }
    }
}
