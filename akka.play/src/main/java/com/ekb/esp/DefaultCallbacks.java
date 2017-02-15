package com.ekb.esp;

import com.sas.esp.api.pubsub.clientFailureCodes;
import com.sas.esp.api.pubsub.clientFailures;
import com.sas.esp.api.pubsub.clientGDStatus;
import com.sas.esp.api.server.ReferenceIMPL.dfESPeventblock;
import com.sas.esp.api.server.ReferenceIMPL.dfESPschema;

/**
 * Created by ebrown on 2/14/2017.
 */
public class DefaultCallbacks implements EspCallback {
    /*
     * Flag indicating whether to keep main in a non-busy wait while the
     * publisher does its thing.
     */
    boolean nonBusyWait = true;

    /**
     * @return
     */
    @Override
    public boolean isNonBusyWait() {
        return nonBusyWait;
    }

    @Override
    public void dfESPsubscriberCB_func(dfESPeventblock dfESPeventblock, dfESPschema dfESPschema, Object o) {

    }

    @Override
    public void dfESPpubsubErrorCB_func(clientFailures failure, clientFailureCodes code, Object ctx) {
        if (failure==clientFailures.pubsubFail_APIFAIL) {
			/*
			 * Don't print error for client busy
			 */
            if (code!=clientFailureCodes.pubsubCode_CLIENTEVENTSQUEUED) {
                System.err.println("Client services api failed with code: "+code);
            }
        }
        else {
            System.err.println("Client services thread failed with code: "+code);
        }
		/* Release the busy wait which will end the program. */
        nonBusyWait = false;

    }

    @Override
    public void dfESPGDpublisherCB_func(clientGDStatus clientGDStatus, long l, Object o) {

    }
}
