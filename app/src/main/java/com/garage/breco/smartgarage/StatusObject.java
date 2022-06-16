package com.garage.breco.smartgarage;

import android.os.Handler;

import com.google.firebase.database.DatabaseReference;


/**
 * Created by stormset on 2016. 11. 03.
 */
public class StatusObject {
    private static final int REQUEST_INDEX = 0;
    private static final int ACK_INDEX = 1;
    private static final int DOOR_STATE_INDEX = 2;
    private static final int CAR_POS_INDEX = 3;
    private static final int INSIDE_LIGHT_INDEX = 4;
    private static final int OUTSIDE_LIGHT_INDEX = 5;
    private static final int TIME_INDEX = 6;
    private final DatabaseReference databaseReference;
    boolean requestCleaned = false;
    private OnRequestTimeoutListener OnRequestTimeoutListener;
    private String status;
    private boolean waitingForResponse = false;

    public StatusObject(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void refreshStatus(String status) {
        this.status = status;
        if (!requestCleaned) {
            checkAckState();
        } else {
            requestCleaned = false;
        }
    }

    public String getStatus() {
        return status;
    }

    public void sendRequest() {
        buildString(REQUEST_INDEX, 1);
        waitingForResponse = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (waitingForResponse) {
                    callRequestTimeoutListener();
                    buildString(REQUEST_INDEX, 0);
                    waitingForResponse = false;
                }
            }
        }, 8000);
    }

    public boolean getRequestState() {
        return getValueAt(REQUEST_INDEX) != 0;
    }

    public void clearRequest() {
        requestCleaned = true;
        buildString(new int[]{REQUEST_INDEX, ACK_INDEX}, new int[]{0, 0});
        waitingForResponse = false;
    }

    private void checkAckState() {
        boolean ack = getValueAt(ACK_INDEX) != 0;
        if (ack) {
            clearRequest();
            waitingForResponse = false;
        }
    }

    public int getDoorState() {
        return getValueAt(DOOR_STATE_INDEX);
    }

    public int getCarPos() {
        if (getValueAt(CAR_POS_INDEX) != 0) return 1;
        else return 0;
    }

    public void switchInsideLight() {
        buildString(INSIDE_LIGHT_INDEX, true);
    }

    public boolean getInsideLightState() {
        return getValueAt(INSIDE_LIGHT_INDEX) != 0;
    }


    public void switchOutsideLight() {
        buildString(OUTSIDE_LIGHT_INDEX, true);
    }

    public boolean getOutsideLightState() {
        return getValueAt(OUTSIDE_LIGHT_INDEX) != 0;
    }

    public long getTime() {
        return getLongValueAt(TIME_INDEX);
    }


    private void buildString(int replaceIndex, int booleanVal) {
        StringBuilder ret = new StringBuilder(status);
        if (booleanVal > 1)
            booleanVal = 1;
        String replace = String.valueOf(booleanVal);
        ret.setCharAt(replaceIndex * 2, replace.charAt(0));
        status = ret.toString();
        if (databaseReference != null) databaseReference.setValue(status);
    }

    private void buildString(int replaceIndex, boolean negate) {
        StringBuilder ret = new StringBuilder(status);
        int current = getValueAt(replaceIndex);
        if (current >= 1) current = 0;
        else current = 1;
        String replace = String.valueOf(current);
        ret.setCharAt(replaceIndex * 2, replace.charAt(0));
        status = ret.toString();
        if (databaseReference != null) databaseReference.setValue(status);
    }

    private void buildString(int[] replaceIndex, int[] booleanVal) {
        StringBuilder ret = new StringBuilder(status);
        int index = Math.min(replaceIndex.length, booleanVal.length);
        for (int i = 0; i < index; i++) {
            if (booleanVal[i] > 1) booleanVal[i] = 1;
            String replace = String.valueOf(booleanVal[i]);
            ret.setCharAt(replaceIndex[i] * 2, replace.charAt(0));
        }
        status = ret.toString();
        if (databaseReference != null) databaseReference.setValue(status);
    }

    private int getValueAt(int index) {
        return Integer.parseInt((status.split(","))[index]);
    }

    private long getLongValueAt(int index) {
        return Long.parseLong((status.split(","))[index], 10);
    }

    private void callRequestTimeoutListener() {
        OnRequestTimeoutListener listener = getOnRequestTimeoutListener();
        if (listener != null) {
            listener.onRequestTimeout();
        }
    }

    public OnRequestTimeoutListener getOnRequestTimeoutListener() {
        return OnRequestTimeoutListener;
    }

    public void setOnRequestTimeoutListener(OnRequestTimeoutListener listener) {
        OnRequestTimeoutListener = listener;
    }

    public interface OnRequestTimeoutListener {
        void onRequestTimeout();
    }

}
