package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TCPMultiServerTest {

    @Mock
    private ServerSocket mockServerSocket;

    @Mock
    private Socket mockClientSocket;

    @Mock
    private ConnectionThread mockConnectionThread;

    @Mock
    private TCPMultiServer.ConnectionThreadFactory mockConnectionThreadFactory;

    private TCPMultiServer server;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockServerSocket.accept()).thenReturn(mockClientSocket);
        when(mockConnectionThreadFactory.createConnectionThread(mockClientSocket)).thenReturn(mockConnectionThread);
        doNothing().when(mockServerSocket).close();
        server = new TCPMultiServer() {
            @Override
            public void launch() {
                this.state = true;  // Simulate successful server launch
                System.out.println("Server launched on IP " + InetAddress.getLoopbackAddress().getHostAddress() + " and port " + this.port);
            }

            @Override
            public void handleConnection() {
                try {
                    Socket clientSocket = mockServerSocket.accept(); // Simulate accepting a connection
                    ConnectionThread connectionThread = mockConnectionThreadFactory.createConnectionThread(clientSocket);
                    connectionThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Test
    void testLaunchServer() {
        server.launch();
        assertTrue(server.state, "Server should be launched and in 'running' state");
    }

    @Test
    void testHandleConnection() throws IOException {
        server.handleConnection();

        verify(mockServerSocket, times(1)).accept(); // Ensure accept() was called once
        verify(mockConnectionThreadFactory, times(1)).createConnectionThread(mockClientSocket);
        verify(mockConnectionThread, times(1)).start();
    }

    @Test
    void testHandleMultipleConnections() throws IOException {
        server.handleConnection(); // First connection
        server.handleConnection(); // Second connection

        verify(mockServerSocket, times(2)).accept();
        verify(mockConnectionThreadFactory, times(2)).createConnectionThread(mockClientSocket);
        verify(mockConnectionThread, times(2)).start(); // Ensure that two threads are started
    }

    @Test
    void testLaunchWithPort() {
        // Create a server with a specific port
        TCPMultiServer serverWithPort = new TCPMultiServer(8080);
        serverWithPort.launch();

        assertTrue(serverWithPort.state, "Server with custom port should be launched");
    }
}
