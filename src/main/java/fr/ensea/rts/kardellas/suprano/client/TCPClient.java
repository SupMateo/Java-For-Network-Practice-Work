package fr.ensea.rts.kardellas.suprano.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class TCPClient extends Client {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader stdIn;

    public TCPClient(String address, int port) {
        super(address, port);
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
            client.start(false);
        } catch (NumberFormatException | IOException e) {
            System.out.println("Invalid port number: " + args[1]);
        }
    }

    @Override
    public void start(boolean GUI) throws IOException {
        if (!GUI) {
            try {
                this.socket = new Socket(serverAddress, serverPort);
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                System.out.println("Enter text to send to the server (CTRL+C to exit):");
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
        } else {
            try {
                this.socket = new Socket(serverAddress, serverPort);
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.stdIn = new BufferedReader(new InputStreamReader(System.in));
                String userInput;
                System.out.println("Enter text to send to the server (CTRL+C to exit):");

            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
            }
        }
    }


    @Override
    public void sendMessage(String message) throws IOException {
        if (!running) {
            System.out.println("error: client not connected");
            throw new IOException("Client is not connected");
        }

        if (socket == null || socket.isClosed()) {
            System.out.println("error: socket is closed");
            throw new IOException("Socket is closed");
        }

        out.println(message);
        System.out.println("message sent: " + message);

        String response = in.readLine();
        if (response != null) {
            System.out.println("Server response (hex): " + toHex(response));
        } else {
            System.out.println("Server closed the connection.");
            running = false; // Mark as disconnected if server closes
        }
    }

    String toHex(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x ", (int) c));
        }
        return hexString.toString().trim();
    }
}