package me.rorschach.gnnucontact.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import me.rorschach.gnnucontact.User;
import me.rorschach.gnnucontact.util.DbUtil;
import me.rorschach.greendao.Contact;

public class StarFragment extends Fragment {

    @Bind(R.id.star_list)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty)
    TextView mEmpty;

    private AppCompatActivity mActivity;
    private List<Contact> starList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    public StarFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    @DebugLog
    public void onResume() {
        super.onResume();
        LoadStarTask starTask = new LoadStarTask();
        starTask.execute();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @DebugLog
    @Subscriber(tag = "update_star_list")
    public void updateAdapter(User user) {
        starList.clear();
        starList.addAll(DbUtil.loadStarList());
        mAdapter.notifyDataSetChanged();
        checkVisibility();
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    private void updateRecyclerView() {
        mAdapter = new RecyclerViewMaterialAdapter(new StarAdapter(mActivity, starList));
        mRecyclerView.setAdapter(mAdapter);
    }

    class LoadStarTask extends AsyncTask<Void, Void, Void> {

        public LoadStarTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmpty.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        @DebugLog
        protected Void doInBackground(Void... params) {
            starList = DbUtil.loadStarList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
            checkVisibility();
        }
    }

    private void checkVisibility() {
        if (starList.isEmpty()) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public static class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder> {

        private AppCompatActivity mActivity;
        private List<Contact> mList;

        private StarViewHolder mViewHolder;

        public StarAdapter(AppCompatActivity activity, List<Contact> list) {
            this.mActivity = activity;
            this.mList = list;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public StarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.view_item_person, parent, false);
            mViewHolder = new StarViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(StarViewHolder holder, int position) {
            final Contact contact = mList.get(position);
            holder.mName.setText(contact.getName());
            holder.mTel.setText(contact.getTel());

//            holder.mName.setText("name");
//            holder.mTel.setText("tel");
        }

        class StarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            @Bind(R.id.name)
            TextView mName;
            @Bind(R.id.tel)
            TextView mTel;

            public StarViewHolder(View itemView) {
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
                final Contact contact = mList.get(position - 1);

                DetailFragment dialogFragment = DetailFragment.newInstance(contact);
                dialogFragment.show(mActivity.getSupportFragmentManager(), "dialog");
                v.getRootView().clearFocus();
            }
        }
    }
}
