package me.rorschach.gnnucontact.ui.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import me.rorschach.gnnucontact.ui.activity.PersonActivity;
import me.rorschach.gnnucontact.utils.DbUtil;

public class CollegeFragment extends Fragment {

    @Bind(R.id.college_list)
    RecyclerView mRecyclerView;

    private List<String> collegeList = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;

    public CollegeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_college, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoadCollegeTask loadCollegeTask = new LoadCollegeTask();
        loadCollegeTask.execute();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    class LoadCollegeTask extends AsyncTask<Void, Void, Void> {

        public LoadCollegeTask() {
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
            collegeList = DbUtil.loadCollegeList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateRecyclerView();
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecyclerView() {
        mAdapter = new RecyclerViewMaterialAdapter(new CollegeAdapter(getActivity(), collegeList));
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder> {

        private Activity mActivity;
        private List<String> mList;

        private CollegeViewHolder mViewHolder;

        public CollegeAdapter(Activity activity, List<String> list) {
            this.mActivity = activity;
            this.mList = list;
        }

        @Override
        public CollegeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.view_item_college, parent, false);
            mViewHolder = new CollegeViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(CollegeViewHolder holder, int position) {
            String text = mList.get(position);
            holder.college.setText(text);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class CollegeViewHolder extends RecyclerView.ViewHolder implements
                View.OnClickListener, View.OnLongClickListener {

            @Bind(R.id.college)
            TextView college;

            public CollegeViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                PersonActivity.goToPersonList(v.getContext(),
                        college.getText().toString());
            }

            @Override
            public boolean onLongClick(View v) {
//            Snackbar.make(v, "long click", Snackbar.LENGTH_SHORT).show();
                return true;
            }
        }
    }
}
