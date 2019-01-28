package com.tdsolutions.android.tdffmpeg_videosizereducer;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class TDProgressDialog extends Dialog implements View.OnClickListener {
    private Button btnCancel;
    private ProgressBar progressBar;
    private OnProgressEventsListener onProgressEventsListener;
    private TextView txtTitle;

    public void setOnProgressEventsListener(OnProgressEventsListener onProgressEventsListener) {
        this.onProgressEventsListener = onProgressEventsListener;
    }

    public interface OnProgressEventsListener {
        void onCancelClick();
    }

    public TDProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        txtTitle = findViewById(R.id.txtTitle);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void updateProgress(final int progress) {
        progressBar.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                txtTitle.setText("Cancelling...");
                btnCancel.setEnabled(false);
                if (onProgressEventsListener != null) {
                    onProgressEventsListener.onCancelClick();
                }
                break;
        }
    }
}
