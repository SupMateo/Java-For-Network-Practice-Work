package fr.ensea.rts.kardellas.suprano.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TCPClientTest {
    private TCPClient tcpClient;

    @Mock
    private Socket mockSocket;

    @Mock
    private PrintWriter mockOut;

    @Mock
    private BufferedReader mockIn;

    @Mock
    private BufferedReader mockStdIn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tcpClient = spy(new TCPClient("localhost", 8080));
    }

    @Test
    void testConstructor() {
        assertEquals("localhost", tcpClient.serverAddress);
        assertEquals(8080, tcpClient.serverPort);
    }

    @Test
    void testToHex() {
        String input = "Hello";
        String expectedHex = "48 65 6c 6c 6f";
        assertEquals(expectedHex, tcpClient.toHex(input));
    }

    @Test
    void testToHexEmptyString() {
        assertEquals("", tcpClient.toHex(""));
    }

    @Test
    void testToHexWithNullInput() {
        assertEquals("", tcpClient.toHex(null));
    }

    @Test
    void testToHexWithSpecialCharacters() {
        String input = "A1!@#";
        String expectedHex = "41 31 21 40 23";
        assertEquals(expectedHex, tcpClient.toHex(input));
    }

    @Test
    void testStart_SuccessfulCommunication() throws IOException {
        doReturn(mockSocket).when(tcpClient).createSocket();
        doReturn(mockOut).when(tcpClient).createPrintWriter(mockSocket);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream("Server Response".getBytes());
        doReturn(mockInputStream).when(mockSocket).getInputStream();

        doReturn(mockIn).when(tcpClient).createBufferedReader(mockInputStream);
        doReturn(mockStdIn).when(tcpClient).createStdinReader();

        when(mockStdIn.readLine())
                .thenReturn("Hello Server")
                .thenReturn(null);
        when(mockIn.readLine()).thenReturn("Server Response");

        assertDoesNotThrow(() -> tcpClient.start());

        verify(mockOut).println("Hello Server");
        verify(mockIn).readLine();
    }

    @Test
    void testStart_ServerClosesConnection() throws IOException {
        doReturn(mockSocket).when(tcpClient).createSocket();
        doReturn(mockOut).when(tcpClient).createPrintWriter(mockSocket);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(new byte[0]); // Simulate no response
        doReturn(mockInputStream).when(mockSocket).getInputStream();

        doReturn(mockIn).when(tcpClient).createBufferedReader(mockInputStream);
        doReturn(mockStdIn).when(tcpClient).createStdinReader();

        when(mockStdIn.readLine()).thenReturn("Hello Server").thenReturn(null);
        when(mockIn.readLine()).thenReturn(null); // Simulate server closure

        tcpClient.start();

        verify(mockOut).println("Hello Server");
        verify(mockIn).readLine();
    }

    @Test
    void testCloseResources() throws IOException {
        tcpClient.closeResources(mockSocket, mockOut, mockIn, mockStdIn);

        verify(mockSocket).close();
        verify(mockOut).close();
        verify(mockIn).close();
        verify(mockStdIn).close();
    }

    @Test
    void testCloseResourcesWithNulls() throws IOException {
        assertDoesNotThrow(() -> tcpClient.closeResources(null, null, null, null));
    }

    @Test
    void testStartIOException() throws IOException {
        doThrow(new IOException("Connection failed")).when(tcpClient).createSocket();

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        tcpClient.start();
        assertTrue(errContent.toString().contains("IOException: Connection failed"));
        System.setErr(System.err);
    }

    @Test
    void testMainWithInvalidArguments() {
        String[] args = {};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        TCPClient.main(args);

        assertTrue(outContent.toString().contains("Usage: java TCPClient <address> <port>"));
        System.setOut(System.out);
    }

    @Test
    void testMainWithInvalidPort() {
        String[] args = {"localhost", "invalidPort"};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        TCPClient.main(args);

        assertTrue(outContent.toString().contains("Invalid port number: invalidPort"));
        System.setOut(System.out);
    }
}
