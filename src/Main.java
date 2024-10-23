import java.io.Console;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java UDPClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            DatagramSocket socket = new DatagramSocket();
            Console console = System.console();

            if (console == null) {
                System.out.println("No console available");
                return;
            }

            System.out.println("Enter text (type 'exit' to quit):");
            String line;

            while (!(line = console.readLine()).equalsIgnoreCase("exit")) {
                byte[] buffer = line.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(hostname), port);
                socket.send(packet);
                System.out.println("Sent: " + line);
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}