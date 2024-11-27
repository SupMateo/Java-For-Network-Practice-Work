package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetAddress;
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
        server = spy(new TCPServer(0) {
            @Override
            public void launch() {
                ServerSocket mockSocket = mock(ServerSocket.class);
                this.state = true;
            }
        });

        mockServerSocket = mock(ServerSocket.class);
        mockClientSocket = mock(Socket.class);

        try {
            when(mockServerSocket.accept()).thenReturn(mockClientSocket);
        } catch (IOException e) {
            fail("Test setup failed", e);
        }
    }


    @Test
    public void testLaunch() {
        server.launch();
        assertTrue(server.state, "Server should be in running state after launch");
    }

    @Test
    void testHandleConnection() throws IOException {

        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        Socket mockClientSocket = Mockito.mock(Socket.class);
        ConnectionThread mockConnectionThread = Mockito.mock(ConnectionThread.class);

        when(mockServerSocket.accept()).thenReturn(mockClientSocket);

        TCPServer server = new TCPServer() {
            protected ConnectionThread createConnectionThread(Socket clientSocket) {
                assertSame(mockClientSocket, clientSocket);
                return mockConnectionThread;
            }
        };

        server.handleConnection();

        Mockito.verify(mockServerSocket, Mockito.times(1)).accept();
        Mockito.verify(mockConnectionThread, Mockito.times(1)).start();
    }

    @Test
    void testHandleConnectionWhenServerSocketIsClosed() throws IOException {

        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        when(mockServerSocket.isClosed()).thenReturn(true);

        TCPServer server = new TCPServer();

        server.handleConnection();

        Mockito.verify(mockServerSocket, Mockito.never()).accept();
    }


    @Test
    void testStop() throws IOException {
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        TCPServer server = new TCPServer();



        Mockito.verify(mockServerSocket).close();
        assertTrue(mockServerSocket.isClosed());
    }


    @Test
    void testHandleConnectionWithIOException() throws IOException {
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenThrow(new IOException("Erreur réseau"));

        TCPServer server = new TCPServer();

        assertThrows(RuntimeException.class, server::handleConnection);
    }



    @Test
    public void testCreateConnectionThread() {
        server.launch();
    }

    @Test
    public void testDefaultConstructor() {
        TCPServer defaultServer = new TCPServer();
        assertNotNull(defaultServer, "Default constructor should create a non-null instance");
    }


    @Test
    public void testGetSetServerSocket() {
        TCPServer server = Mockito.spy(new TCPServer());
        ServerSocket mockServerSocket = Mockito.mock(ServerSocket.class);




    }

    @Test
    public void testMainLoop() throws IOException, InterruptedException {
        server.launch();

        try (MockedConstruction<ConnectionThread> mockThreadConstruction =
                     Mockito.mockConstruction(ConnectionThread.class)) {




            server.state = true;
            new Thread(() -> {
                server.handleConnection();
                server.handleConnection();
                server.state = false;
            }).start();

            Thread.sleep(500);

            assertEquals(2, mockThreadConstruction.constructed().size(),
                    "Should create two ConnectionThread instances for two connections");
        }
    }

    @Test
    public void testStopWithException() {
        server.launch();

        try {
            doThrow(new IOException("Simulated close error")).when(mockServerSocket).close();
        } catch (IOException e) {
            fail("Setup failed", e);
        }

        assertFalse(server.state, "Server should not be running after stop()");
    }


}