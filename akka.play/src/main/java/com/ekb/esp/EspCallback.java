package com.ekb.esp;

import com.sas.esp.api.pubsub.clientCallbacks;


/**
 * Contract interface for ESP callbacks used by the package
 */
public interface EspCallback extends clientCallbacks {
    /**
     *
     * @return
     */
    public boolean isNonBusyWait();

}
