package fr.ensea.rts.kardellas.suprano.client;

import java.io.Console;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient extends Client {
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
            client.start();
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + args[1]);
        }
    }

    @Override
    public void start() {
        try (DatagramSocket socket = new DatagramSocket()) {
            Console console = System.console();

            if (console == null) {
                System.out.println("No console available");
                return;
            }

            System.out.println("Enter text (type 'exit' to quit):");
            String line;

            while (!(line = console.readLine()).equalsIgnoreCase("exit")) {
                byte[] buffer = line.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverAddress), serverPort);
                socket.send(packet);
                System.out.println("Sent: " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}