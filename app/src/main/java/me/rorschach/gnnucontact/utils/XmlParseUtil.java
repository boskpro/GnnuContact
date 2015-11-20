package me.rorschach.gnnucontact.utils;

import android.content.res.XmlResourceParser;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.greendao.Contact;

/**
 * Created by root on 15-11-9.
 */
public class XmlParseUtil {

    private final static String PATH = "/GnnuContact";
    private final static String FILENAME = "/contacts.xml";

    public static List<Contact> updateXmlToDb() {
        File sdCardPath;
        File filePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            sdCardPath = Environment.getExternalStorageDirectory();
            Log.d("TAG", "sdcard : " + sdCardPath);
            filePath = new File(sdCardPath + PATH);
            if (!filePath.exists()) {
                filePath.mkdir();
            }
        }

        XmlPullParser xmlPullParser = null;
        try {
            xmlPullParser = Xml.newPullParser();
            Log.d("TAG", "filePath : " + filePath + FILENAME);
            xmlPullParser.setInput(new FileInputStream(filePath + FILENAME), "utf-8");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return parseXml(xmlPullParser);
    }

    public static List<Contact> parseXmlToDb() {
//        Context context = MyApplication.getInstance();
//
//        String fileNames[] =context.getAssets().list(path);
//
//        AssetManager assetManager = null;
//        XmlPullParser xmlPullParser = null;
//        InputStream inputStream = null;
//
//
//        try {
//            assetManager = context.getAssets();
//            inputStream = assetManager.open(filename);
//            xmlPullParser = Xml.newPullParser();
//            xmlPullParser.setInput(inputStream, "utf-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }
//
//      finally {
//            try {
//                inputStream.close();
//                assetManager.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

//        XmlResourceParser xmlPullParser = null;
        XmlResourceParser xmlPullParser =
                MyApplication.getInstance().getResources().getXml(R.xml.contacts);

        return parseXml(xmlPullParser);
    }

    private static List<Contact> parseXml(XmlPullParser xmlPullParser) {
        Log.d("TAG", "parseXml start");
        Contact contact;
        ArrayList<Contact> contactsList = new ArrayList<>();

        try {
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = xmlPullParser.getName();
                        if (tagName.equals("channel")) {
                            contact = new Contact();
                            contact.setName(xmlPullParser.getAttributeValue(0));
                            contact.setTel(xmlPullParser.getAttributeValue(1));
                            contact.setCollege(xmlPullParser.getAttributeValue(2));
                            contact.setIsStar(false);
                            contact.setIsRecord(false);
                            contact.setCommunicateTime(0l);
                            contactsList.add(contact);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "parseXml done");
        return contactsList;
    }
}
