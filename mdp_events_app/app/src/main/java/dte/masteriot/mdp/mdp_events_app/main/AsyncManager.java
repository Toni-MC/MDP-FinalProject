package dte.masteriot.mdp.mdp_events_app.main;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dte.masteriot.mdp.mdp_events_app.model.Dataset;

public class AsyncManager extends ViewModel {

    private ExecutorService es;
    private Handler handler;
    private MutableLiveData<Integer> progress;

    public AsyncManager() {
        // Among other things, create the handler to receive messages from the bgtask.
        // Whena message is received, if any of the Live Data has to be changed,
        // use setValue(), e.g.: progress.setValue(progress_received_in_message);
        // This will notify the observers.

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                int i = inputMessage.getData().getInt("progress", -1);

                progress.setValue(i);
            }
        };
    }
    public LiveData<Integer> getProgress() {
        if (progress == null) {
            progress = new MutableLiveData<Integer>();
            progress.setValue(0);
        }
        return progress;
    }
    public void launchBackgroundTask(Dataset data) {
        // create and execute the new task to be run in background.
        // Thismethod can be called from MainActivity.
        Dataset dataset = data;
        es = Executors.newSingleThreadExecutor();
        LoadEventsImages task = new LoadEventsImages(handler, dataset);
        es.execute(task);
    }

}
