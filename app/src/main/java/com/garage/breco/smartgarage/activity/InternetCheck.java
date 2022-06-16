package com.garage.breco.smartgarage.activity;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by stormset on 2016. 10. 29.
 */
class InternetCheck extends AsyncTask<Void, Void, Boolean> {

    private final Consumer mConsumer;

    public InternetCheck(Consumer consumer) {
        mConsumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean internet) {
        mConsumer.accept(internet);
    }

    public interface Consumer {
        void accept(Boolean internet);
    }
}
