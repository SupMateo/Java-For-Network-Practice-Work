package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.io.*;
import java.net.*;
import static org.mockito.Mockito.*;
import java.util.concurrent.CountDownLatch;

class ConnectionThreadTest {

    @Mock
    private Socket mockSocket;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private BufferedReader mockReader;

    @Mock
    private PrintWriter mockWriter;

    private ConnectionThread connectionThread;

    private CountDownLatch latch;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Initialize CountDownLatch to ensure synchronization with the thread
        latch = new CountDownLatch(1);

        // Mock the socket behaviors
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        when(mockSocket.getInetAddress()).thenReturn(InetAddress.getByName("127.0.0.1"));
        when(mockSocket.getPort()).thenReturn(8080);

        // Simulate that BufferedReader reads the string "Hello Server"
        when(mockReader.read(any(char[].class))).thenAnswer(invocation -> {
            char[] buffer = invocation.getArgument(0);
            String inputData = "Hello Server";
            System.arraycopy(inputData.toCharArray(), 0, buffer, 0, inputData.length());
            return inputData.length();
        }).thenReturn(-1);

        doNothing().when(mockWriter).println(anyString());

        connectionThread = new ConnectionThread(mockSocket) {
            @Override
            public void run() {
                try {
                    super.run();
                } finally {
                    latch.countDown();
                }
            }
        };
    }

    @Test
    void testConnectionThreadRunWithValidInput() throws IOException, InterruptedException {
        Thread thread = new Thread(connectionThread);
        thread.start();

        latch.await();

        verify(mockWriter, times(1)).println("Echo: Hello Server");
        verify(mockSocket, times(1)).close();
    }

    @Test
    void testConnectionThreadHandlesEmptyData() throws IOException, InterruptedException {

        when(mockReader.read(any(char[].class))).thenReturn(-1);

        Thread thread = new Thread(connectionThread);
        thread.start();

        latch.await();

        verify(mockSocket, times(1)).close();
    }

    @Test
    void testConnectionThreadHandlesNullData() throws IOException, InterruptedException {
        String nullData = "null";
        char[] dataChars = nullData.toCharArray();
        when(mockReader.read(any(char[].class))).thenReturn(dataChars.length).thenReturn(-1);

        Thread thread = new Thread(connectionThread);
        thread.start();

        latch.await();

        verify(mockSocket, times(1)).close();
    }

    @Test
    void testConnectionThreadExceptionHandling() throws IOException, InterruptedException {
        when(mockReader.read(any(char[].class))).thenThrow(new IOException("Test exception"));

        Thread thread = new Thread(connectionThread);
        thread.start();

        latch.await();

        verify(mockSocket, times(1)).close();
    }
}
