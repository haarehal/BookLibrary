package com.example.haris.TestnaApp;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;


/**
 * Created by Haris on 17-May-18.
 */

public class MojResultReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public MojResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    // Deklaracija interfejsa kojeg cemo trebati implementirati
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if(mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }

}
