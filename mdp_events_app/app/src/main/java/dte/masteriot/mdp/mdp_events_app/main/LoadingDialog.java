package dte.masteriot.mdp.mdp_events_app.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dte.masteriot.mdp.mdp_events_app.R;

public class LoadingDialog extends Dialog {

    int i = 0;
    ProgressBar progressBar;
    int max = 0;

    Context global_context;
    public LoadingDialog(@NonNull Context context) {
        super(context);

        global_context = context;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        setContentView(view);
//        String msg = max + " events to load";
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if(max != 0){ //Because some time this method is called before configure the Panal (i dont know why)
            progressBar.setProgress(i++);
        }
    }

    @Override
    public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu) {
        max = featureId;
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(10);
        String msg = featureId + " events found";
        Toast.makeText(global_context, msg, Toast.LENGTH_LONG).show();

        return super.onPreparePanel(featureId, view, menu);
    }
}
