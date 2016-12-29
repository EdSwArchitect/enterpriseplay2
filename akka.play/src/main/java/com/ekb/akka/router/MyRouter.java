package com.ekb.akka.router;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.ekb.akka.parse.ParseLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Playing with routing
 * Created by EdwinBrown on 12/28/2016.
 */
public class MyRouter extends UntypedActor {
    private Router router;

    public MyRouter() {
        List<Routee> routees = new ArrayList<Routee>();
        for (int i = 0; i < 5; i++) {
            ActorRef r = getContext().actorOf(Props.create(ParseLog.class));
            getContext().watch(r);
            routees.add(new ActorRefRoutee(r));
        }
        router = new Router(new RoundRobinRoutingLogic(), routees);
    }

    /**
     *
     * @param msg The message
     * @throws Throwable Thrown when blah blah blah
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof String) {
            router.route(msg, getSender());
        } else if (msg instanceof Terminated) {
            router = router.removeRoutee(((Terminated) msg).actor());
            ActorRef r = getContext().actorOf(Props.create(ParseLog.class));
            getContext().watch(r);
            router = router.addRoutee(new ActorRefRoutee(r));
        }
    }
}
