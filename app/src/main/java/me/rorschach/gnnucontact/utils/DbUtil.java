package me.rorschach.gnnucontact.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.greendao.Contact;
import me.rorschach.greendao.ContactDao;

/**
 * Created by root on 15-11-9.
 */
public class DbUtil {

    private static ContactDao sContactDao;
    private static String name = ContactDao.Properties.Name.columnName;
    private static String orderBy = name + " COLLATE LOCALIZED ASC";
    private static SQLiteDatabase db = MyApplication.getInstance().getDb();
    private static Cursor sCursor;

    //************************ base operation ******************************

    public static boolean isEmpty() {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        return loadAll().isEmpty();
    }

    @DebugLog
    public static long insertFromXml() {
        if (isEmpty()) {
            sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
            List<Contact> list = XmlParseUtil.parseXmlToDb();
            for (Contact contact : list) {
                insertOrReplace(contact);
            }
            return list.size();
        }
        return loadAll().size();
    }

    @DebugLog
    public static boolean isSaved(long id) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        QueryBuilder<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.Id.eq(id));
        return query.buildCount().count() > 0;
    }

    @DebugLog
    public static long insertOrReplace(Contact contact) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        return sContactDao.insertOrReplace(contact);
    }

    @DebugLog
    public static boolean deleteById(long id) {
        if (!isSaved(id)) {
            return false;
        }

        QueryBuilder<Contact> queryBuilder = sContactDao.queryBuilder();
        DeleteQuery<Contact> deleteQuery = queryBuilder
                .where(ContactDao.Properties.Id.eq(id))
                .buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();

        return !isSaved(id);
    }

    @DebugLog
    public static Contact getContactById(long id) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        return sContactDao.load(id);
    }

    public static Contact getContactByName(String name) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.Name.eq(name))
                .orderAsc(ContactDao.Properties.Name)
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.unique();
    }

    //************************ store list ****************************

    @DebugLog
    public static List<Contact> loadAll() {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        return sContactDao.loadAll();
    }

    @DebugLog
    public static List<String> loadCollegeList() {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        List<String> collegeList = new ArrayList<>();
        List<Contact> list = loadAll();
        for (Contact contacts : list) {
            if (!collegeList.contains(contacts.getCollege())) {
                collegeList.add(contacts.getCollege());
            }
        }
        Collections.sort(collegeList, Collator.getInstance(java.util.Locale.CHINA));
        return collegeList;
    }

    public static List<Contact> loadPersonByCollege(String college) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.College.eq(college))
                .orderAsc(ContactDao.Properties.Name)
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

    @DebugLog
    public static List<Contact> loadStarList() {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.IsStar.eq(true))
                .orderAsc(ContactDao.Properties.Name)
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

    @DebugLog
    public static List<Contact> loadRecordList() {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.IsRecord.eq(true))
                .orderAsc(ContactDao.Properties.Name)
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

    //********************** store search list **************************

    public static List<Contact> searchPersonByNameOrTel(String text) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .whereOr(ContactDao.Properties.Name.like("%" + text + "%"),
                        ContactDao.Properties.Tel.like("%" + text + "%"))
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

    public static List<Contact> searchPersonByName(String name) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.Name.like("%" + name + "%"))
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

    @DebugLog
    public static List<Contact> searchPersonByTel(String tel) {
        sContactDao = MyApplication.getInstance().getDaoSession().getContactDao();
        Query<Contact> query = sContactDao.queryBuilder()
                .where(ContactDao.Properties.Tel.like("%" + tel + "%"))
                .orderAsc(ContactDao.Properties.Name)
                .build();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return query.list();
    }

}
