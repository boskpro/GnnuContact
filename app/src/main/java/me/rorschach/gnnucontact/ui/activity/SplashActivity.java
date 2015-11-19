package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.DbUtil;

public class SplashActivity extends Activity {

    private final static String PATH = "/GnnuContact";
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mkdir();

        Intent intent = getIntent();
        isUpdate = intent.getBooleanExtra("UPDATE", isUpdate);

        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    @DebugLog
    private void mkdir() {
        Log.d("TAG", "mkdir");
        File sdCardPath;
        File filePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String sdCard = Environment.getExternalStorageDirectory().getPath();
            sdCardPath = new File(sdCard);
            filePath = new File(sdCardPath + PATH);
            if (!filePath.exists()) {
                Log.d("TAG", filePath + "");
                filePath.mkdir();
            }
            Log.d("TAG", filePath + "");
        }
    }

    class ParseTask extends AsyncTask<Void, Void, Void> {

        public ParseTask() {
            super();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("TAG", "isUpdate" + isUpdate);
            if (!isUpdate) {
                DbUtil.insertFromXml();
            }else {
                DbUtil.updateDbFromXml();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}
