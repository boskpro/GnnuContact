package me.rorschach.gnnucontact.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.adapter.PersonAdapter;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class PersonActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.person_list)
    RecyclerView mPersonList;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private List<Contact> mList;
    public static String COLLEGE_NAME = "COLLEGE_NAME";
    private String college;

    @DebugLog
    public static void goToPersonList(Context context, String college) {
        Intent intent = new Intent(context, PersonActivity.class);
        intent.putExtra(COLLEGE_NAME, college);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);

        mToolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        LoadPersonListTask loadPersonListTask = new LoadPersonListTask();
        loadPersonListTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    class LoadPersonListTask extends AsyncTask<Void, Void, Void> {
        public LoadPersonListTask() {
            super();
        }

        @Override
        @DebugLog
        protected void onPreExecute() {
            super.onPreExecute();
            Intent intent = getIntent();
            college = intent.getStringExtra(COLLEGE_NAME);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(college);
            }

            mPersonList.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            mList = DbUtil.loadPersonByCollege(college);
            return null;
        }

        @Override
        @DebugLog
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupRecyclerView();
            mPersonList.setVisibility(View.VISIBLE);
        }
    }

    @DebugLog
    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPersonList.setLayoutManager(linearLayoutManager);
        mPersonList.setHasFixedSize(true);
        PersonAdapter mAdapter = new PersonAdapter(PersonActivity.this, mList);
        mPersonList.setAdapter(mAdapter);
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
                Intent intent = new Intent(PersonActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    @DebugLog
//    public void changeStarState() {
//
//    }
//
//    @Override
//    public void addRecord() {
//
//    }
}
