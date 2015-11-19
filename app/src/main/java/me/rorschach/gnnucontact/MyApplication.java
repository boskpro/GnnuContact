package me.rorschach.gnnucontact;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import me.rorschach.greendao.DaoMaster;
import me.rorschach.greendao.DaoSession;

/**
 * Created by root on 15-10-25.
 */
public class MyApplication extends Application{

    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private RefWatcher mRefWatcher;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = MyApplication.this;
        setupDatabase();
        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher() {
        return instance.mRefWatcher;
    }

    public SQLiteOpenHelper getHelper() {
        return helper;
    }

    private DaoMaster.DevOpenHelper helper;
    public void setupDatabase() {
            helper = new DaoMaster.DevOpenHelper(this, "contact-db", null);
            db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void closeDbHelper() {
        helper.close();
    }

}
