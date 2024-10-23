import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private String serverAddress;
    private int serverPort;

    public TCPClient(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    public void start() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            String userInput;
            System.out.println("Enter text to send to the server (CTRL+D to exit):");

            while ((userInput = stdIn.readLine()) != null) {
                // Send the user input to the server
                out.println(userInput);

                // Read the response from the server
                String response = in.readLine();
                if (response != null) {
                    // Print the response in hexadecimal format
                    System.out.println("Server response (hex): " + toHex(response));
                } else {
                    System.out.println("Server closed the connection.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    private String toHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x ", (int) c));
        }
        return hexString.toString().trim();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <address> <port>");
            return;
        }

        String address = args[0];
        int port;

        try {
            port = Integer.parseInt(args[1]);
            TCPClient client = new TCPClient(address, port);
            client.start();
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + args[1]);
        }
    }
}
