package me.rorschach.gnnucontact;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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

    public SQLiteDatabase getDb() {
        return db;
    }

    private DaoMaster.DevOpenHelper helper;
    public void setupDatabase() {
//        Log.d("TAG", DaoMaster.SCHEMA_VERSION + "");
//        if (DaoMaster.SCHEMA_VERSION == 1) {
            helper = new DaoMaster.DevOpenHelper(this, "contact-db", null);
            db = helper.getWritableDatabase();
//        }else {
//            db = openDatabase(this);
//        }
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

//    public SQLiteDatabase openDatabase(Context context){
//
//        String filePath = "data/data/me.rorschach.gnnucontact/databases/contact-db.db";
//        //数据库存放的文件夹 data/data/com.main.jh 下面
//        String pathStr = "data/datame.rorschach.gnnucontact/databases/";
//
//        System.out.println("filePath:"+filePath);
//        File dbPath=new File(filePath);
//        //查看数据库文件是否存在
//        if(dbPath.exists()){
//            Log.i("test", "存在数据库");
//            //存在则直接返回打开的数据库
//            return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
//        }else{
//            //不存在先创建文件夹
//            File path=new File(pathStr);
//            Log.i("test", "pathStr="+path);
//            if (path.mkdir()){
//                Log.i("test", "创建成功");
//            }else{
//                Log.i("test", "创建失败");
//            };
//            try {
//                //得到资源
//                AssetManager am= context.getAssets();
//                //得到数据库的输入流
//                InputStream is=am.open("contact-db.db");
//                Log.i("test", is+"");
//                //用输出流写到SDcard上面
//                FileOutputStream fos=new FileOutputStream(dbPath);
//                Log.i("test", "fos="+fos);
//                Log.i("test", "jhPath="+dbPath);
//                //创建byte数组  用于1KB写一次
//                byte[] buffer=new byte[1024];
//                int count = 0;
//                while((count = is.read(buffer))>0){
//                    Log.i("test", "得到");
//                    fos.write(buffer,0,count);
//                }
//                //最后关闭就可以了
//                fos.flush();
//                fos.close();
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//            //如果没有这个数据库  我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以返回数据库了
//            return openDatabase(context);
//        }
//    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void closeDbHelper() {
        helper.close();
    }

}
