package fr.ensea.rts.kardellas.suprano.client;

import java.io.Console;
import java.net.*;

public class UDPClient extends Client {
    private DatagramSocket socket;
    private InetAddress address;

    protected Console getConsole() {
        return System.console();
    }

    public UDPClient(String address, int port) {
        super(address, port);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java UDPClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
            UDPClient client = new UDPClient(hostname, port);
            client.start(false);
        } catch (Exception e) {
            System.out.println("Invalid port number: " + args[1]);
        }
    }

    @Override
    public void start(boolean GUI) throws Exception {
            socket = new DatagramSocket();
            address = InetAddress.getByName(serverAddress);

            Console console = getConsole();

            running = true;

            // Start a thread to receive messages
            new Thread(this::receiveMessages).start();

            // Main thread for sending messages
            System.out.println("Enter text (type 'exit' to quit):");

            String line;
            while (isRunning() && (line = console.readLine()) != null) {
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }
                sendMessage(line);
            }

            stop();

    }

    private void receiveMessages() {
        try {
            byte[] receiveBuffer = new byte[1024];
            while (isRunning()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server response: " + received);
            }
        } catch (Exception e) {
            if (isRunning()) {
                System.err.println("Error receiving messages: " + e.getMessage());
            }
        }
    }

    @Override
    public void sendMessage(String message) throws Exception {
        if (!isRunning()) {
            throw new Exception("Client is not connected");
        }
        byte[] buffer = message.getBytes("UTF-8");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
        socket.send(packet);
        System.out.println("Sent: " + message);
    }
}