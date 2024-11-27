package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TCPMultiServerTest {

    @Mock
    private ServerSocket mockServerSocket;

    @Mock
    private Socket mockClientSocket;

    @Mock
    private ConnectionThread mockConnectionThread;

    private TCPMultiServer server;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Custom ConnectionThreadFactory for testing
        TCPMultiServer.ConnectionThreadFactory customFactory = new TCPMultiServer.ConnectionThreadFactory() {
            @Override
            public ConnectionThread createConnectionThread(Socket socket) {
                return mockConnectionThread;
            }
        };


    }

    @Test
    void testCustomConnectionThreadFactory() throws IOException {
        // Prepare mock socket acceptance
        when(mockServerSocket.accept()).thenReturn(mockClientSocket);

        // Launch and handle connection
        server.launch();
        server.handleConnection();

        // Verify custom factory was used and thread was started
        verify(mockServerSocket).accept();
        verify(mockConnectionThread).start();


    }

    @Test
    void testConcurrentConnections() throws IOException {
        // Simulate multiple socket connections
        when(mockServerSocket.accept())
                .thenReturn(mockClientSocket)  // First connection
                .thenReturn(mock(Socket.class))  // Second connection
                .thenThrow(new IOException("Simulated connection limit"));

        server.launch();

        // Simulate connection handling in a way that can be verified
        try {
            server.handleConnection();
        server.handleConnection();
    } catch (RuntimeException ignored) {
    }

        // Verify multiple connection thread creations
        verify(mockServerSocket, times(2)).accept();
    }

    @Test
    void testMainMethodWithDefaultConstructor() {
        // Capture arguments passed to main method
        assertDoesNotThrow(() -> {
            TCPMultiServer.main(new String[]{});
        }, "Main method with no args should not throw exception");
    }

    @Test
    void testMainMethodWithCustomPort() {
        // Capture arguments passed to main method
        assertDoesNotThrow(() -> {
            TCPMultiServer.main(new String[]{"8080"});
        }, "Main method with port should not throw exception");
    }

    @Test
    void testConnectionError() throws IOException {
        // Simulate connection acceptance failure
        when(mockServerSocket.accept()).thenThrow(new IOException("Simulated connection error"));

        server.launch();

        // Verify error handling
        assertDoesNotThrow(() -> server.handleConnection(),
                "Should handle connection errors gracefully");

    }
}