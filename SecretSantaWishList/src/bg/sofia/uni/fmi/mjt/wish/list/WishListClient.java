package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class WishListClient {

    private static final String serverHost = "localhost";
    private final int serverPort;

    private static final String newCommandLine = "=> ";
    private static final String disconnectCommand = "disconnect";
    private static final String communicationProblemMessage = "There is a problem with the network communication";

    public WishListClient(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(serverHost, serverPort));

            while (true) {
                System.out.print(newCommandLine);
                String message = scanner.nextLine();

                if (disconnectCommand.equals(message)) {
                    break;
                }

                writer.println(message);

                String serverReply = reader.readLine();
                System.out.println(serverReply);
            }
        } catch (IOException e) {
            throw new IllegalStateException(communicationProblemMessage, e);
        }
    }
}
