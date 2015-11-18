package me.rorschach.gnnucontact.utils;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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

    public static List<Contact> parseXmlToDb() {
//        Context context = MyApplication.getInstance();
        Contact contact;
        XmlResourceParser xmlPullParser = null;

//        String fileNames[] =context.getAssets().list(path);

//        AssetManager assetManager = null;
//        XmlPullParser xmlPullParser = null;
//        InputStream inputStream = null;
        ArrayList<Contact> ContactsList = new ArrayList<>();

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

            xmlPullParser = MyApplication.getInstance().getResources().getXml(R.xml.contacts);

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
                            ContactsList.add(contact);
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
//        finally {
//            try {
//                inputStream.close();
//                assetManager.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        return ContactsList;
    }
}
