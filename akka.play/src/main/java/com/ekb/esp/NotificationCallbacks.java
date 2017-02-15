package com.ekb.esp;

import com.sas.esp.api.pubsub.clientFailureCodes;
import com.sas.esp.api.pubsub.clientFailures;
import com.sas.esp.api.pubsub.clientGDStatus;
import com.sas.esp.api.server.ReferenceIMPL.dfESPeventblock;
import com.sas.esp.api.server.ReferenceIMPL.dfESPschema;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ebrown on 2/14/2017.
 */
public class NotificationCallbacks implements EspCallback {

    /*
     * Flag indicating whether to keep main in a non-busy wait while the
     * publisher does its thing.
     */
    AtomicBoolean nonBusyWait = new AtomicBoolean(true);

    /**
     * Esp state callback
     */
    private EspStateMonitor stateMonitor = null;

    /**
     * Notify callback on busy
     */
    @SuppressWarnings("unused")
    private boolean notifyIfBusy = false;

    /**
     * Default. Inaccessible because a callback has to be used with this class
     */
    @SuppressWarnings("unused")
    private NotificationCallbacks() {

    }

    /**
     * Constructor
     * @param stateCallback The callback to use to notify of state errors
     */
    public NotificationCallbacks(EspStateMonitor stateCallback) {
        this.stateMonitor = stateCallback;
    }

    /**
     * Constructor
     * @param stateCallback Callback to use to notify of state errors
     * @param notifyIfBusy If true, notify if ESP is busy.
     */
    public NotificationCallbacks(EspStateMonitor stateCallback, boolean notifyIfBusy) {
        this.stateMonitor = stateCallback;
        this.notifyIfBusy = notifyIfBusy;
    }


    @Override
    public void dfESPsubscriberCB_func(dfESPeventblock dfESPeventblock, dfESPschema dfESPschema, Object o) {
        // do nothing
    }

    /**
     * Accepts error notification and then calls the callback if appropriate or callback is set
     * @param clientFailures
     * @param clientFailureCodes
     * @param o
     */
    @SuppressWarnings("static-access")
    @Override
    public void dfESPpubsubErrorCB_func(clientFailures clientFailures, clientFailureCodes clientFailureCodes, Object o) {
        EspStateMonitor.EspState state;

        if (stateMonitor != null) {

            if (clientFailures == clientFailures.pubsubFail_APIFAIL) {
                switch (clientFailureCodes) {
                    case pubsubCode_CLIENTNOTCONNECTED:
                        state = EspStateMonitor.EspState.ESP_DISCONNECTED;
                        break;
                    case pubsubCode_READFAILED:
                        state = EspStateMonitor.EspState.ESP_RECEIVE_ERROR;
                        break;
                    case pubsubCode_WRITEFAILED:
                        state = EspStateMonitor.EspState.ESP_SEND_ERROR;
                        break;
                    case pubsubCode_CLIENTEVENTSQUEUED:
                        state = EspStateMonitor.EspState.ESP_BUSY;
                    default:
                        state = EspStateMonitor.EspState.ESP_OTHER_ERROR;
                        break;
                }
            } else if (clientFailures == clientFailures.pubsubFail_SERVERDISCONNECT) {
                state = EspStateMonitor.EspState.ESP_DISCONNECTED;
            } else if (clientFailures == clientFailures.pubsubFail_THREADFAIL) {
                state = EspStateMonitor.EspState.ESP_OTHER_ERROR;
            } else {
                state = EspStateMonitor.EspState.ESP_OKAY;
            }

            if (state != EspStateMonitor.EspState.ESP_OKAY && state != EspStateMonitor.EspState.ESP_BUSY) {
                stateMonitor.errorNotification(state);
            }

        }
        else {
            System.err.println("ESP error condition " + clientFailureCodes);
        }
		/* Release the busy wait which will end the program. */
        nonBusyWait.set(false);
    }

    @Override
    public void dfESPGDpublisherCB_func(clientGDStatus clientGDStatus, long l, Object o) {
        // implement for guarenteed delivery
    }

    /**
     * @return
     */
    @Override
    public boolean isNonBusyWait() {
        return nonBusyWait.get();
    }
}
