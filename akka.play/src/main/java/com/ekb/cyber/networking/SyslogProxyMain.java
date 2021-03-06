package com.ekb.cyber.networking;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/9/2017.
 */
public class SyslogProxyMain {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            int port;
            String espUri;
            String hostName;

            if (args.length >= 3) {
                hostName = args[0];
                port = Math.abs(Integer.parseInt(args[1]));
                espUri = args[2];
            }
            else {
                hostName = InetAddress.getLocalHost().getHostName();
                port = 2057;
                espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            }

            SyslogProxy proxy = new SyslogProxy(hostName, port, espUri);

            proxy.start();

            TimeUnit.MINUTES.sleep(5L);


            proxy.stop();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
