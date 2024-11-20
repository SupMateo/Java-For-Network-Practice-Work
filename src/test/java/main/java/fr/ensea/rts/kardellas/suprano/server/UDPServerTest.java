package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.*;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

class UDPServerTest {

    private UDPServer udpServer;
    private static final int TEST_PORT = 9999;

    @BeforeEach
    void setUp() {
        udpServer = new UDPServer(TEST_PORT);
    }

    @Test
    @DisplayName("Test server launches correctly")
    void testServerLaunch() {
        assertDoesNotThrow(() -> udpServer.launch(), "Server launch should not throw an exception.");
        assertTrue(udpServer.state, "Server state should be running after launch.");
    }

    @Test
    @DisplayName("Test server handles connection and echoes data")
    void testHandleConnection() throws Exception {
        udpServer.launch();

        // Create a client socket to send and receive data
        DatagramSocket clientSocket = new DatagramSocket();
        String message = "Hello, UDPServer!";
        byte[] sendData = message.getBytes();

        InetAddress serverAddress = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, TEST_PORT);
        clientSocket.send(sendPacket);
        
        Thread serverThread = new Thread(udpServer::handleConnection);
        serverThread.start();
        serverThread.join(1000);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        assertEquals(message, receivedMessage, "The server should echo back the same message.");

        clientSocket.close();
    }

    @Test
    @DisplayName("Test server fails to launch on invalid port")
    void testServerLaunchInvalidPort() {
        UDPServer invalidServer = new UDPServer(-1); // Invalid port
        Exception exception = assertThrows(RuntimeException.class, invalidServer::launch, "Launching server on invalid port should throw an exception.");
        assertTrue(exception.getCause() instanceof SocketException, "Cause of exception should be a SocketException.");
    }

    @AfterEach
    void tearDown() {
        if (udpServer.state) {
            udpServer.stop();
        }
    }
}

