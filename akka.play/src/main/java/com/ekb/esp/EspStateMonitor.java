package com.ekb.esp;

/**
 * Created by ebrown on 2/14/2017.
 */
public interface EspStateMonitor {
    /**
     * Define the states for notification
     *
     * clientFailures.pubsubFail_APIFAIL;
     * clientFailures.pubsubFail_SERVERDISCONNECT;
     * clientFailures.pubsubFail_THREADFAIL;
     * clientFailureCodes.pubsubCode_WRITEFAILED;
     * clientFailureCodes.pubsubCode_READFAILED;
     */
    public enum EspState {
        ESP_OKAY, ESP_BUSY, ESP_DISCONNECTED, ESP_SEND_ERROR, ESP_RECEIVE_ERROR, ESP_OTHER_ERROR
    }

    /**
     * Notify callback of current state
     * @param state The state of the pub/sub connection
     */
    public void errorNotification(EspState state);
}
