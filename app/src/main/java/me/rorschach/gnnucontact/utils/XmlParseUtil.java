package me.rorschach.gnnucontact.utils;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.rorschach.greendao.Contact;

/**
 * Created by root on 15-11-9.
 */
public class XmlParseUtil {

    public static List<Contact> parseXml(XmlPullParser xmlPullParser) {
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
