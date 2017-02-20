package com.ekb.cyber.networking;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/20/2017.
 */
public class IngestMain {

    public static void main(String... args) {
        try {

            String hostName = InetAddress.getLocalHost().getHostName();
            int port = 2056;
            String espUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/Authentication_Query/Authentication";
            String proxyEspUri = "dfESP://zz-ed-vm01.dev.cyber.sas.com:9095/AkkaProject/WebProxyIngest_Query/WebProxy";


            SyslogAuth auth = new SyslogAuth(hostName, port, espUri);

            SyslogProxy proxy = new SyslogProxy(hostName, 2057, proxyEspUri);

            auth.start();
            proxy.start();

            TimeUnit.MINUTES.sleep(10L);

            auth.stop();
            proxy.stop();


        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
