package cyber.networking.udp;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.ekb.akka.networking.OutputActor;
import com.ekb.akka.networking.ServerMgr;
import com.ekb.cyber.networking.udp.SyslogUdp;

import java.util.concurrent.TimeUnit;

/**
 * Created by ebrown on 2/9/2017.
 */
public class UdpTest {
    public static void main(String... args) {
        try {
            ActorSystem system = ActorSystem.create("UdpNetwork");

            ActorRef syslogUdp = system.actorOf(Props.create(SyslogUdp.class, 5050), "SyslogUdp");

            TimeUnit.MINUTES.sleep(3L);

            system.terminate();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
