package com.ekb.akka.cluster;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.LoggingAdapter;

/**
 * Created by EdwinBrown on 2/24/2017.
 */
public class Member extends UntypedActor {
    private LoggingAdapter log = context().system().log();

    private Cluster cluster = Cluster.get(getContext().system());

    private String memberName;

    /**
     * Constructor
     *
     * @param memberName
     */
    public Member(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public void preStart() throws Exception {
        log.info(memberName + ": preStart");

        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        log.info(memberName + ": postStop");

        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        log.info(memberName + ": Received message: " + message);

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
            // ignore
            log.info("ClusterEvent.MemberEvent received: ");

        } else {
            unhandled(message);
        }
    }
}
