package fr.ensea.rts.kardellas.suprano.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionThreadTest {

    @Mock
    private Socket mockSocket;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private OutputStream mockOutputStream;

    private ConnectionThread connectionThread;
    private CountDownLatch latch;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        latch = new CountDownLatch(1);

        // Configure socket mocks
        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);
        when(mockSocket.getInetAddress()).thenReturn(InetAddress.getByName("127.0.0.1"));
        when(mockSocket.getPort()).thenReturn(8080);
    }

    @Test
    void testRunWithValidInput() throws Exception {
        // Prepare input stream simulation
        String testInput = "Hello Server";

        // Create mocks
        BufferedReader mockReader = mock(BufferedReader.class);
        PrintWriter mockWriter = mock(PrintWriter.class);

        // Configure the mock reader to return the test input and then -1 (end of stream)
        when(mockReader.read(any(char[].class)))
                .thenAnswer(invocation -> {
                    char[] buffer = invocation.getArgument(0);
                    testInput.getChars(0, testInput.length(), buffer, 0);
                    return testInput.length();
                })
                .thenReturn(-1);

        // Create connection thread with mocked readers/writers
        connectionThread = new ConnectionThread(mockSocket) {
            @Override
            protected BufferedReader createBufferedReader(InputStream inputStream) {
                return mockReader;
            }

            @Override
            protected PrintWriter createPrintWriter(OutputStream outputStream) {
                return mockWriter;
            }

            @Override
            public void run() {
                super.run();
                latch.countDown();
            }
        };

        // Start the thread and wait
        connectionThread.start();
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Thread did not complete in time");

        // Verify interactions
        verify(mockReader, atLeastOnce()).read(any(char[].class));
        verify(mockWriter).println("Echo: " + testInput);
        verify(mockSocket, times(1)).close();
    }

    @Test
    void testHandleEmptyInput() throws Exception {
        BufferedReader mockReader = mock(BufferedReader.class);

        // Simulate empty input
        when(mockReader.read(any(char[].class))).thenReturn(-1);

        // Stub socket close to prevent multiple invocation errors
        doNothing().when(mockSocket).close();

        connectionThread = new ConnectionThread(mockSocket) {
            @Override
            protected BufferedReader createBufferedReader(InputStream inputStream) {
                return mockReader;
            }

            @Override
            public void run() {
                super.run();
                latch.countDown();
            }
        };

        // Start the thread and wait
        connectionThread.start();
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Thread did not complete in time");

        // Verify socket closure
        verify(mockSocket, atMostOnce()).close();
    }

    @Test
    void testHandleNullInput() throws Exception {
        // Simulate "null" input
        String nullInput = "null";
        BufferedReader mockReader = mock(BufferedReader.class);

        // Configure mock to return "null" and then end of stream
        when(mockReader.read(any(char[].class)))
                .thenAnswer(invocation -> {
                    char[] buffer = invocation.getArgument(0);
                    nullInput.getChars(0, nullInput.length(), buffer, 0);
                    return nullInput.length();
                })
                .thenReturn(-1);

        connectionThread = new ConnectionThread(mockSocket) {

            @Override
            public BufferedReader createBufferedReader(InputStream inputStream) {
                return mockReader;
            }

            @Override
            public void run() {
                super.run();
                latch.countDown();
            }
        };

        // Start the thread and wait
        connectionThread.start();
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Thread did not complete in time");

        // Verify socket closure
        verify(mockSocket, times(1)).close();
    }

    @Test
    void testExceptionHandling() throws Exception {
        // Simulate IOException during reading
        BufferedReader mockReader = mock(BufferedReader.class);
        doThrow(new IOException("Test exception")).when(mockReader).read(any(char[].class));

        connectionThread = new ConnectionThread(mockSocket) {

            @Override
            public BufferedReader createBufferedReader(InputStream inputStream) {
                return mockReader;
            }

            @Override
            public void run() {
                super.run();
                latch.countDown();
            }
        };

        // Start the thread and wait
        connectionThread.start();
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Thread did not complete in time");

        // Verify socket closure
        verify(mockSocket, times(1)).close();
    }
}