package me.rorschach.gnnucontact.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import me.rorschach.gnnucontact.ui.fragment.RecordFragment;
import me.rorschach.gnnucontact.ui.fragment.StarFragment;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.gnnucontact.utils.DisplayUtils;

public class MainActivity extends AppCompatActivity implements
        DetailFragment.StarChangeListener,
        StarFragment.ListChangeListener,
        RecordFragment.RecordChangeListener{

    @Bind(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private CollegeFragment mCollegeFragment;
    private StarFragment mStarFragment;
    private RecordFragment mRecordFragment;
    private ViewPager viewPager;
    private FragmentStatePagerAdapter mStatePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseTask parseTask = new ParseTask();
        parseTask.execute();
    }

    @Override
    protected void onDestroy() {
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

        viewPager = mViewPager.getViewPager();

        mStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position % 3) {
                    case 0:
                        mRecordFragment = RecordFragment.newInstance();
                        return mRecordFragment;
                    case 1:
                        mCollegeFragment = CollegeFragment.newInstance();
                        return mCollegeFragment;
                    case 2:
                        mStarFragment = StarFragment.newInstance();
                        return mStarFragment;
                    default:
                        mCollegeFragment = CollegeFragment.newInstance();
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
//                    case 0:
//                        return "record";
//                    case 1:
//                        return "college";
//                    case 2:
//                        return "star";
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
//                        return HeaderDesign.fromColorAndDrawable(
//                                getResources().getColor(R.color.blue),
//                                getResources().getDrawable(R.drawable.images, getTheme())
//                        );
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

        viewPager.setOffscreenPageLimit(viewPager.getAdapter().getCount());
        viewPager.setCurrentItem(1);

        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

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
    public void addRecord() {
        updateRecordList();
    }

    @Override
    @DebugLog
    public void changeStarState() {
        updateStarList();
    }

    @Override
    @DebugLog
    public boolean updateStarList() {
        mStarFragment.updateAdapter();
        return false;
    }

    @Override
    public void updateRecordList() {
        mRecordFragment.updateAdapter();
    }
}
