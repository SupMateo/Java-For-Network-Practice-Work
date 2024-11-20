package fr.ensea.rts.kardellas.suprano.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient extends Client {
    protected String serverAddress;
    protected int serverPort;

    public TCPClient(String address, int port) {
        super(address, port);
        this.serverAddress = address;
        this.serverPort = port;
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

    @Override
    public void start() {
        try {
            Socket socket = createSocket();
            PrintWriter out = createPrintWriter(socket);
            BufferedReader in = createBufferedReader(socket.getInputStream());
            BufferedReader stdIn = createStdinReader();

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

            // Close resources
            closeResources(socket, out, in, stdIn);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        }
    }

    // Extracted methods to improve testability
    protected Socket createSocket() throws IOException {
        return new Socket(serverAddress, serverPort);
    }

    protected PrintWriter createPrintWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream(), true);
    }

    protected BufferedReader createBufferedReader(java.io.InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    protected BufferedReader createStdinReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    protected void closeResources(Socket socket, PrintWriter out, BufferedReader in, BufferedReader stdIn) throws IOException {
        if (socket != null) socket.close();
        if (out != null) out.close();
        if (in != null) in.close();
        if (stdIn != null) stdIn.close();
    }

    // Public method for hex conversion
    public String toHex(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x ", (int) c));
        }
        return hexString.toString().trim();
    }
}