package com.ekb.cyber.networking.metrics;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by ebrown on 2/14/2017.
 */
public class MetricsListener extends UntypedActor {

    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    Cluster cluster = Cluster.get(getContext().system());

//    private final Cluster cluster = Cluster.get(getContext().system(), this);


    @Override
    public void preStart() throws Exception {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        log.info("Message received: " + message);

        if (message instanceof ClusterEvent.MemberUp) {
            ClusterEvent.MemberUp mUp = (ClusterEvent.MemberUp) message;
            log.info("Member is Up: {}", mUp.member());
        } else if (message instanceof ClusterEvent.UnreachableMember) {
            ClusterEvent.UnreachableMember mUnreachable = (ClusterEvent.UnreachableMember) message;
            log.info("Member detected as unreachable: {}", mUnreachable.member());
        } else if (message instanceof ClusterEvent.MemberRemoved) {
            ClusterEvent.MemberRemoved mRemoved = (ClusterEvent.MemberRemoved) message;
            log.info("Member is Removed: {}", mRemoved.member());
        } else if (message instanceof ClusterEvent.MemberEvent) {
            ClusterEvent.MemberEvent memberEvent = (ClusterEvent.MemberEvent)message;
            log.info("MemberEvent: " + memberEvent);

        } else {
            unhandled(message);
        }

    }
}
