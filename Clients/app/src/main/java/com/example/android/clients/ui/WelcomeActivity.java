package com.example.android.clients.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.clients.Config;
import com.example.android.clients.R;
import com.example.android.clients.util.PrefUtils;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore the saved state if exists
        super.onCreate(savedInstanceState);

        // Set content view
        setContentView(R.layout.activity_welcome);

        // Link UI elements to actions in the code
        findViewById(R.id.button_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.markTosAccepted(WelcomeActivity.this);
                Intent intent = new Intent(WelcomeActivity.this, ListClientsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.button_decline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Shows the debug warning, if this is a debug build and the warning has not been shown yet
        if (Config.IS_DOGFOOD_BUILD && !PrefUtils.wasDebugWarningShown(this)) {
            new AlertDialog.Builder(this)
                    .setTitle(Config.DOGFOOD_BUILD_WARNING_TITLE)
                    .setMessage(Config.DOGFOOD_BUILD_WARNING_TEXT)
                    .setPositiveButton(android.R.string.ok, null).show();
            PrefUtils.markDebugWarningShown(this);
        }
    }
}
