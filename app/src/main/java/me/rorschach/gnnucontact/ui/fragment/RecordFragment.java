package me.rorschach.gnnucontact.ui.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.squareup.leakcanary.RefWatcher;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class RecordFragment extends Fragment {

    @Bind(R.id.record_list)
    RecyclerView mRecyclerView;

    private AppCompatActivity mActivity;
    private RecyclerView.Adapter mAdapter;
    private List<Contact> recordList = new ArrayList<>();

    public RecordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();

        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    @DebugLog
    public void onResume() {
        super.onResume();

        LoadRecordTask recordTask = new LoadRecordTask();
        recordTask.execute();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    private void updateRecyclerView() {
        mAdapter = new RecyclerViewMaterialAdapter(new RecordAdapter(mActivity, recordList));
        mRecyclerView.setAdapter(mAdapter);
    }

    @DebugLog
    @Subscriber(tag = "update_record_list")
    public void updateAdapter() {
        recordList.clear();
        recordList.addAll(DbUtil.loadRecordList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class LoadRecordTask extends AsyncTask<Void, Void, Void> {

        public LoadRecordTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        @DebugLog
        protected Void doInBackground(Void... params) {
            recordList = DbUtil.loadRecordList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) activity;
    }

    public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

        private AppCompatActivity mActivity;
        private List<Contact> mList;

        private RecordViewHolder mViewHolder;

        public RecordAdapter(AppCompatActivity activity, List<Contact> list) {
            this.mActivity = activity;
            this.mList = list;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.view_item_person, parent, false);
            mViewHolder = new RecordViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            final Contact contact = mList.get(position);
            holder.mName.setText(contact.getName());
            holder.mTel.setText(contact.getTel());
        }

        class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            @Bind(R.id.name)
            TextView mName;
            @Bind(R.id.tel)
            TextView mTel;

            public RecordViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                int position = getAdapterPosition();
                final Contact contact = mList.get(position - 1);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(R.string.sure_delete_record)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contact.setIsRecord(false);
                                DbUtil.insertOrReplace(contact);
                                updateAdapter();
                                Toast.makeText(mActivity, R.string.delete_success,
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
                return true;
            }

            @Override
            public void onClick(View v) {

                int position = getAdapterPosition();
                final Contact contact = mList.get(position - 1);

                DetailFragment dialogFragment = DetailFragment.newInstance(contact);
                dialogFragment.show(mActivity.getSupportFragmentManager(), "dialog");

//                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//                builder.setTitle(contact.getName())
//                        .setMessage(contact.getTel() + "\n" + contact.getCollege())
//                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Uri uri = Uri.parse("tel:" + contact.getTel());
//                                Intent intent = new Intent(Intent.ACTION_CALL, uri);
//                                mActivity.startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton("Sms", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Uri uri = Uri.parse("sms:" + contact.getTel());
//                                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                                mActivity.startActivity(intent);
//                            }
//                        })
//                        .setNeutralButton("Cancel", null)
//                        .create()
//                        .show();
            }
        }
    }
}
