package com.ekb.akka.networking;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by EdwinBrown on 1/3/2017.
 */
public class ClientNetworkingTest {
    public static void main(String... args) {
        try {
            String fileName;

            if (args.length > 0) {
                fileName = args[0];
            }
            else {
                fileName = "akka.play/src/test/resources/SystemLog.txt";
            }

            Socket socket = new Socket("localhost", 8555);

            PrintStream out = new PrintStream(socket.getOutputStream());

            FileInputStream fis = new FileInputStream(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line;

            while ((line = in.readLine()) != null) {
                out.println(line);
            } // while ((line = in.readLine()) != null) {

            in.close();
            out.close();
        }
        catch(Exception e) {
            e.printStackTrace();
            System.err.println("Current directory: " + System.getProperty("user.dir"));
        }
    }
}
