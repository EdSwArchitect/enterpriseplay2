package com.ekb.akka.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by EdwinBrown on 1/4/2017.
 */
public class ServerNetworkingTest {
    private Selector selector;
    private Map<SocketChannel,List> dataMapper;
    private InetSocketAddress listenAddress;


    /**
     * Constructor
     * @param address
     * @param port
     * @throws IOException
     */
    public ServerNetworkingTest(String address, int port) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<SocketChannel,List>();
    }

    /**
     * Start server channel
     * @throws IOException
     */
    private void startServer() throws IOException {

        this.selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();

        serverChannel.configureBlocking(false);

        serverChannel.socket().bind(listenAddress);

        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started...");

        while (true) {

            // wait for events

            this.selector.select();

            //work on selected keys

            Iterator keys = this.selector.selectedKeys().iterator();

            while (keys.hasNext()) {

                SelectionKey key = (SelectionKey) keys.next();

                // this is necessary to prevent the same key from coming up
                // again the next time around.
                keys.remove();

                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        accept(key);
                    } // if (key.isAcceptable()) {
                    else if (key.isReadable()) {
                        read(key);
                    } // else if (key.isReadable()) {
                } // if (key.isValid()) {
                else {
                    System.out.println("Key: " + key.toString() + " is invalid");
                }
            } // while (keys.hasNext()) {
        } // while (true) {
    }



    /**
     * Accept a connection made to this channel's socket
     * @param key
     * @throws IOException
     */
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();

        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        // register channel with selector for further IO

        dataMapper.put(channel, new ArrayList());
        channel.register(this.selector, SelectionKey.OP_READ);
    }

    /**
     * read from the socket channel
     * @param key
     * @throws IOException
     */
    private void read(SelectionKey key) throws IOException {

        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int numRead = -1;

        numRead = channel.read(buffer);

        if (numRead == -1) {
            this.dataMapper.remove(channel);

            Socket socket = channel.socket();

            SocketAddress remoteAddr = socket.getRemoteSocketAddress();

            System.out.println("Connection closed by client: " + remoteAddr);

            channel.close();

            key.cancel();

            return;

        }

        byte[] data = new byte[numRead];

        System.arraycopy(buffer.array(), 0, data, 0, numRead);

        System.out.println("Got: " + new String(data));

    }

    public static void main(String... args) {
        try {
            Runnable server = new Runnable() {
                @Override
                public void run() {
                    try {
                        new ServerNetworkingTest("localhost", 8555).startServer();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread t = new Thread(server);

            t.start();

            TimeUnit.MINUTES.sleep(1L);

            t.interrupt();

            System.out.println("Interrupted");

            t.join();

            System.out.println("Ending...");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
