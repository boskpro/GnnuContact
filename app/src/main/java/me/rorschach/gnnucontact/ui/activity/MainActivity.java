package me.rorschach.gnnucontact.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.squareup.leakcanary.RefWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.ui.fragment.CollegeFragment;
import me.rorschach.gnnucontact.ui.fragment.DetailFragment;
import me.rorschach.gnnucontact.ui.fragment.StarFragment;
import me.rorschach.gnnucontact.utils.DbUtil;

public class MainActivity extends AppCompatActivity implements
        DetailFragment.StarChangeListener,
        StarFragment.ListChangeListener {

    @Bind(R.id.materialViewPager)
    MaterialViewPager mViewPager;

    private StarFragment mStarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    private void initView() {
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Log.d("TAG", position % 3 + "");
                switch (position % 3) {
                    case 0:
                        return CollegeFragment.newInstance();
                    case 1:
                        return CollegeFragment.newInstance();
                    case 2:
                        mStarFragment = StarFragment.newInstance();
                        return mStarFragment;
                    default:
                        return CollegeFragment.newInstance();
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
                                R.color.holo_blue,
                                "http://cdn1.tnwcdn.com/wp-content/blogs.dir/1/files/2014/06/wallpaper_51.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final ViewPager viewPager = mViewPager.getViewPager();
        viewPager.setCurrentItem(1);

        Toolbar toolbar = mViewPager.getToolbar();
        toolbar.setPopupTheme(R.style.AppTheme_ToolBarTheme);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.inflateMenu(R.menu.menu_main);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    class ParseTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DbUtil.insertFromXml();
            return null;
        }
    }

    @OnClick(R.id.fab)
    public void goToDiaActivity() {
//        finish();
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage(getBaseContext().getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(i);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @DebugLog
    public boolean changeStarState() {
        updateList();
        return false;
    }

    @Override
    @DebugLog
    public boolean updateList() {
        mStarFragment.updateAdapter();
        return false;
    }
}
