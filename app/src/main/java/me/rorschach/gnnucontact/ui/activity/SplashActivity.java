package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.DbUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DbUtil.insertFromXml();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
    }
}
