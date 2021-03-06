package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.wish.list.storage.UserStorage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class WishListServer {

    private static final String serverOperationsExceptionMsg =
            "A problem occurred while trying to execute operations from server.";
    private static final String clientRequestProcessingMsg = "A problem occurred while processing client request.";

    private static final String serverHost = "localhost";
    private static final int bufferSize = 1024;
    private final int serverPort;
    private boolean isClosed;

    private ByteBuffer commandByteBuffer;
    private Selector selector;

    private final CommandExecutor commandExecutor;

    public WishListServer(int port) {
        this.serverPort = port;
        this.commandExecutor = new CommandExecutor(new UserStorage());
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            this.configureServerSocketChannel(serverSocketChannel, selector);
            commandByteBuffer = ByteBuffer.allocate(bufferSize);
            isClosed = false;
            while (!isClosed) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            this.read(key);
                        } else if (key.isAcceptable()) {
                            this.accept(key, selector);
                        }
                        keyIterator.remove();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(clientRequestProcessingMsg, e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(serverOperationsExceptionMsg, e);
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel serverSocketChannel,
                                              Selector selector) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(serverHost, serverPort));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        commandByteBuffer.clear();
        int readBytes = socketChannel.read(commandByteBuffer);
        if (readBytes < 0) {
            socketChannel.close();
            return;
        }
        this.executeBuffer();
        socketChannel.write(commandByteBuffer);
    }

    private void executeBuffer() {
        commandByteBuffer.flip();
        String clientMessage = StandardCharsets.UTF_8.decode(commandByteBuffer).toString();

        String serverReply = commandExecutor
                .executeCommand(clientMessage.replace(System.lineSeparator(), ""));
        commandByteBuffer.clear();
        commandByteBuffer.put((serverReply + System.lineSeparator()).getBytes());
        commandByteBuffer.flip();
    }

    public void stop() {
        this.isClosed = true;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }
}
