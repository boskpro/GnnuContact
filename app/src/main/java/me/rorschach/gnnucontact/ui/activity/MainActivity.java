package me.rorschach.gnnucontact.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.squareup.leakcanary.RefWatcher;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.ui.fragment.CollegeFragment;
import me.rorschach.gnnucontact.ui.fragment.RecordFragment;
import me.rorschach.gnnucontact.ui.fragment.StarFragment;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.gnnucontact.utils.DisplayUtils;
import me.rorschach.greendao.Contact;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private CollegeFragment mCollegeFragment;
    private StarFragment mStarFragment;
    private RecordFragment mRecordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    private void initView() {

        Toolbar toolbar = mViewPager.getToolbar();
        toolbar.setPopupTheme(R.style.AppTheme_ToolBarTheme);

        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }

        ViewPager viewPager = mViewPager.getViewPager();

        FragmentStatePagerAdapter mStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 3) {
                    case 0:
                        if (mRecordFragment == null) {
                            mRecordFragment = new RecordFragment();
                        }
                        return mRecordFragment;
                    case 1:
                        if (mCollegeFragment == null) {
                            mCollegeFragment = new CollegeFragment();
                        }
                        return mCollegeFragment;
                    case 2:
                        if (mStarFragment == null) {
                            mStarFragment = new StarFragment();
                        }
                        return mStarFragment;
                    default:
                        if (mCollegeFragment == null) {
                            mCollegeFragment = new CollegeFragment();
                        }
                        return mCollegeFragment;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 3) {
                    case 0:
                        return "通话记录";
                    case 1:
                        return "学院列表";
                    case 2:
                        return "收藏联系人";
                }
                return "";
            }
        };

        viewPager.setAdapter(mStatePagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mFab.animate()
                            .translationX(-DisplayUtils.getScreenWidth(MainActivity.this) / 2
                                    - getResources().getDimension(R.dimen.fab_margin) / 2
                                    + mFab.getWidth())
                            .setDuration(350)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();

                } else {
                    mFab.animate()
                            .translationX(0)
                            .setDuration(350)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {

                switch (page % 3) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "https://fs01.androidpit.info/a/63/0e/android-l-wallpapers-630ea6-h900.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(1);

        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

    }

    @OnClick(R.id.fab)
    public void goToDiaActivity() {
//        finish();
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        Intent intent = new Intent(Intent.ACTION_MAIN);
////        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        startActivity(i);
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setClassName("me.rorschach.xiaoji",
//                "me.rorschach.xiaoji.ui.activity.MainActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.action_update:
                Intent intent1 = new Intent(MainActivity.this, SplashActivity.class);
                intent1.putExtra("IS_FIRST_TIME", false);
                startActivity(intent1);
                break;

            case R.id.action_export:
                exportFile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @DebugLog
    private void exportFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "export later...", Toast.LENGTH_SHORT).show();
            String sdCard = Environment.getExternalStorageDirectory().getPath();
            File sdCardPath = new File(sdCard);
            File filePath;
            String PATH = "/GnnuContact";
            List<Contact> contacts = DbUtil.loadAll();

            filePath = new File(sdCardPath + PATH);
            if (!filePath.exists()) {
                filePath.mkdir();
            }

            FileWriter fw = null;
            File vcfFile;

            try {
                vcfFile = new File(filePath, "gnnucontact.vcf");
                if (vcfFile.exists()) {
                    vcfFile.delete();
                    vcfFile.createNewFile();
                }
                fw = new FileWriter(vcfFile);

                for (Contact contact : contacts) {
                    fw.write("BEGIN:VCARD\n");
                    fw.write("VERSION:3.0\n");

                    fw.write("FN:" + contact.getName() + "\n");
                    fw.write("TEL;TYPE=WORK,PREF:" + contact.getTel() + "\n");
                    fw.write("ADR;TYPE=WORK:" + contact.getCollege() + "\n");

                    fw.write("END:VCARD\n\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(this, "export success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no such dir", Toast.LENGTH_SHORT).show();
        }
    }
}
