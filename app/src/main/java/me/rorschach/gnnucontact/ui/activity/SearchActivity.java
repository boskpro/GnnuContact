package me.rorschach.gnnucontact.ui.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import me.rorschach.gnnucontact.adapter.SearchAdapter;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.search_list)
    RecyclerView mSearchList;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private List<Contact> mList;
    private SearchAdapter mAdapter;

    private SearchView searchView;

    @Override
    @DebugLog
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setupRecyclerView();
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setPopupTheme(R.style.AppTheme_ToolBarTheme);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @DebugLog
    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchList.setLayoutManager(linearLayoutManager);
        mSearchList.setHasFixedSize(true);
    }

    private void updateRecyclerView(String query) {
        mAdapter = new SearchAdapter(SearchActivity.this, mList, query);
        mSearchList.setAdapter(mAdapter);
    }

    public void doMySearch(String query) {
        QueryTask queryTask = new QueryTask(query);
        queryTask.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    @DebugLog
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem menuSearchItem = menu.findItem(R.id.action_search);
        // Get the SearchView and set the searchable configuration

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView = (SearchView) menuSearchItem.getActionView();
        // Assumes current activity is the searchable activity

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>"
                + getResources().getString(R.string.search_hint) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            @DebugLog
            public boolean onQueryTextSubmit(String query) {
                Log.d("SearchActivity", "onQueryTextSubmit");
                doMySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SearchActivity", "onQueryTextChange");
                if (!TextUtils.isEmpty(newText)) {
                    doMySearch(newText);
                    mSearchList.setVisibility(View.VISIBLE);
                } else {
                    mSearchList.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

//        int searchSrcTextId = getResources().getIdentifier("android:id/search_src_text", null, null);
//        EditText searchEditText = (EditText) searchView.findViewById(searchSrcTextId);
//        searchEditText.setTextColor(Color.BLUE); // set the text color
//        searchEditText.setHintTextColor(Color.BLUE); // set the hint color

//        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
//        textView.setTextColor(Color.WHITE);

        MenuItemCompat.expandActionView(menuSearchItem);
        MenuItemCompat.setOnActionExpandListener(menuSearchItem, new MenuItemCompat.OnActionExpandListener() {//设置打开关闭动作监听
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                onBackPressed();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class QueryTask extends AsyncTask<Void, Void, Void> {

        String query;

        public QueryTask(String query) {
            this.query = query;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        @DebugLog
        protected Void doInBackground(Void... params) {
            mList = DbUtil.searchPersonByNameOrTel(query);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView(query);
            mAdapter.notifyDataSetChanged();
        }
    }
}
