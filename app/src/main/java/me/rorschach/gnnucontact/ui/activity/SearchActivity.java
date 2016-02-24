package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.ui.fragment.DetailFragment;
import me.rorschach.gnnucontact.util.DbUtil;
import me.rorschach.gnnucontact.util.TextUtil;
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
        SearchView searchView = (SearchView) menuSearchItem.getActionView();
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>"
                + getResources().getString(R.string.search_hint) + "</font>"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            @DebugLog
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    doMySearch(newText);
                    mSearchList.setVisibility(View.VISIBLE);
                } else {
                    mSearchList.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

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

    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ResultViewHolder> {

        private Activity mActivity;
        private List<Contact> mList;
        private String query;

        private ResultViewHolder mViewHolder;

        public SearchAdapter(Activity activity, List<Contact> list, String query) {
            this.mActivity = activity;
            this.mList = list;
            this.query = query;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.view_item_person, parent, false);
            mViewHolder = new ResultViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ResultViewHolder holder, int position) {
            final Contact contact = mList.get(position);
            holder.mName.setText(contact.getName());
            holder.mTel.setText(contact.getTel());

            SpannableStringBuilder textString = TextUtil.highlight(contact.getName(), query);
            holder.mName.setText(textString);
            textString = TextUtil.highlight(contact.getTel(), query);
            holder.mTel.setText(textString);
        }

        class ResultViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            @Bind(R.id.name)
            TextView mName;
            @Bind(R.id.tel)
            TextView mTel;

            public ResultViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {

                int position = getAdapterPosition();
                final Contact contact = mList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(contact.getName())
                        .setMessage(contact.getTel() + "\n" + contact.getCollege())
                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("tel:" + contact.getTel());
                                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                                mActivity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("Sms", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("sms:" + contact.getTel());
                                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                                mActivity.startActivity(intent);
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .create()
                        .show();

                return true;
            }

            @Override
            public void onClick(View v) {

                int position = getAdapterPosition();
                final Contact contact = mList.get(position);

            DetailFragment dialogFragment = DetailFragment.newInstance(contact);
            dialogFragment.show(getSupportFragmentManager(), "dialog");

            }
        }
    }
}
