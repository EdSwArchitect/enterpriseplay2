package com.ekb.akka.networking;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.ByteIterator;
import akka.util.ByteString;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by EdwinBrown on 1/2/2017.
 */
public class Buffering extends UntypedActor {
    /**
     * logger
     */
    private final LoggingAdapter log = Logging.getLogger(context().system(), this);
    /**
     * byte buffer
     */
    private ArrayList<ByteArrayOutputStream> buffers;
    /**
     * next in line actor
     */
    private ActorRef output;
    private ActorSelection outputSelection;
    private Charset utf8;

    /**
     * Constructor
     */
    public Buffering() {
        buffers = new ArrayList<ByteArrayOutputStream>();
        utf8 = Charset.forName("UTF-8");
        this.outputSelection = context().actorSelection("/user/Output");
    }

    /**
     * Constructor
     * @param output Specifies the output actor
     */
    public Buffering(ActorRef output) {
        buffers = new ArrayList<ByteArrayOutputStream>();
        utf8 = Charset.forName("UTF-8");
        this.output = output;
    }

    /**
     * Get the data and buffer it
     *
     * @param msg
     * @throws Throwable
     */
    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof ByteString) {
            ByteString barray = (ByteString) msg;

            ByteIterator bi = barray.iterator();
            byte bite;

            while (bi.hasNext()) {
                bite = bi.next();

                if ((char) bite != '\n') {
                    buffers.get(buffers.size() - 1).write(bite);
                } // if ((char)bite == '\n') {
                else {
                    buffers.add(new ByteArrayOutputStream());
                }
            } // while (bi.hasNext()) {

            while (buffers.size() > 1) {
                ByteArrayOutputStream baos = buffers.remove(0);

                String strang = new String(baos.toByteArray(), utf8);

                if (output != null) {
                    output.tell(strang, getSelf());
                }
                else {
                    outputSelection.tell(strang, getSelf());
                }

            } // while (buffers.size() > 1) {

//            getSender().tell(TcpMessage.write(data), getSelf());

        } // if (msg instanceof ByteString) {
        else {
            unhandled(msg);
        }
    }



}
