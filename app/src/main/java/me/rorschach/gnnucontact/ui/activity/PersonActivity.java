package me.rorschach.gnnucontact.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import me.rorschach.greendao.Contact;

public class PersonActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.person_list)
    RecyclerView mPersonList;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private List<Contact> mList;
    private static String COLLEGE_NAME = "COLLEGE_NAME";
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

    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

        private AppCompatActivity mActivity;
        private List<Contact> mList;

        private PersonViewHolder mViewHolder;

        public PersonAdapter(AppCompatActivity activity, List<Contact> list) {
            this.mActivity = activity;
            this.mList = list;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.view_item_person, parent, false);
            mViewHolder = new PersonViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(PersonViewHolder holder, int position) {
            final Contact contact = mList.get(position);
            holder.mName.setText(contact.getName());
            holder.mTel.setText(contact.getTel());
        }

        class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            @Bind(R.id.name)
            TextView mName;
            @Bind(R.id.tel)
            TextView mTel;

            public PersonViewHolder(View itemView) {
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
                dialogFragment.show(mActivity.getSupportFragmentManager(), "dialog");
            }
        }
    }
}
