package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;

import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.util.DbUtil;

public class SplashActivity extends Activity {

    private File filePath;
    private XmlPullParser xmlPullParser;

    private final static String PATH = "/GnnuContact";
    private final static String FILENAME = "/contacts.xml";

    private boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkDir();

        Intent intent = getIntent();
        isFirstTime = intent.getBooleanExtra("IS_FIRST_TIME", isFirstTime);

        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    private XmlPullParser getDir() {
        try {
            xmlPullParser = Xml.newPullParser();
            Log.d("TAG", "filePath : " + filePath + FILENAME);
            xmlPullParser.setInput(new FileInputStream(filePath + FILENAME), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return xmlPullParser;
    }

    @DebugLog
    private void checkDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String sdCard = Environment.getExternalStorageDirectory().getPath();
            File sdCardPath = new File(sdCard);
            filePath = new File(sdCardPath + PATH);
            if (!filePath.exists()) {
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
        @DebugLog
        protected Void doInBackground(Void... params) {
            Log.d("TAG", "isFirstTime : " + isFirstTime);
            if (isFirstTime) {
                xmlPullParser = getResources().getXml(R.xml.contacts);
                DbUtil.insertFromXml(xmlPullParser);
                Log.d("TAG", "insert done");
            }else {
                xmlPullParser = getDir();
                DbUtil.updateDbFromXml(xmlPullParser);
                Log.d("TAG", "update done");
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
