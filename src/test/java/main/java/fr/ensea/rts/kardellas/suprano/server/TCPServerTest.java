package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TCPServerTest {

    private TCPServer server;
    private ServerSocket mockServerSocket;
    private Socket mockClientSocket;

    @BeforeEach
    public void setUp() {
        // Create a mock ServerSocket and Socket
        mockServerSocket = mock(ServerSocket.class);
        mockClientSocket = mock(Socket.class);

        server = spy(new TCPServer(8080));

        doReturn(mockServerSocket).when(server).getServerSocket();

        try {
            when(mockServerSocket.accept()).thenReturn(mockClientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLaunch() {
        server.launch();

        assertTrue(server.state);
    }

    @Test
    public void testHandleConnection() throws IOException {
        doNothing().when(server).launch();
        assertDoesNotThrow(() -> server.handleConnection());
        verify(mockServerSocket).accept();
        verify(server).createConnectionThread(mockClientSocket);
    }

    @Test
    public void testStop() {
        doNothing().when(server).stop();
        server.stop();
        verify(server).stop();
    }
}
