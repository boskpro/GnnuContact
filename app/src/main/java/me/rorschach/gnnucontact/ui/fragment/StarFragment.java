package me.rorschach.gnnucontact.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class StarFragment extends android.support.v4.app.Fragment {

    private static AppCompatActivity mActivity;
    private static StarFragment sFragment;
    private static List<Contact> starList = new ArrayList<>();
    private static RecyclerView.Adapter mAdapter;

    static RecyclerView mRecyclerView;

    private ListChangeListener mListener;

    @DebugLog
    public static StarFragment newInstance() {
        sFragment = new StarFragment();
        return sFragment;
    }

    public StarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) activity;
        try {
            mListener = (ListChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ListChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.star_list);

        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadStarTask starTask = new LoadStarTask();
        starTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @DebugLog
    public static void updateAdapter() {
        starList = DbUtil.loadStarList();
        mAdapter = new RecyclerViewMaterialAdapter(new StarAdapter(mActivity, starList));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyItemRangeChanged(0, starList.size());
        mAdapter.notifyDataSetChanged();
    }

    class LoadStarTask extends AsyncTask<Void, Void, Void> {

        public LoadStarTask() {
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
            starList = DbUtil.loadStarList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    private static void updateRecyclerView() {
        mAdapter = new RecyclerViewMaterialAdapter(new StarAdapter(mActivity, starList));
        mRecyclerView.setAdapter(mAdapter);
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
        @DebugLog
        public void onBindViewHolder(StarViewHolder holder, int position) {
            final Contact contact = mList.get(position);
            holder.mName.setText(contact.getName());
            holder.mTel.setText(contact.getTel());
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

                DetailFragment dialogFragment = DetailFragment.newInstance(contact);
                dialogFragment.show(mActivity.getSupportFragmentManager(), "dialog");
                return true;
            }

            @Override
            public void onClick(View v) {

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
            }
        }
    }

    public interface ListChangeListener {
        boolean updateList();
    }
}
