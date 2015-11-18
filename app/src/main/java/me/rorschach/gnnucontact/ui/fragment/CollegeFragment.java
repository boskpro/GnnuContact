package me.rorschach.gnnucontact.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import me.rorschach.gnnucontact.adapter.CollegeAdapter;
import me.rorschach.gnnucontact.utils.DbUtil;

public class CollegeFragment extends Fragment {

    private static CollegeFragment mFragment;

    @Bind(R.id.college_list)
    RecyclerView mRecyclerView;

    private List<String> collegeList = new ArrayList<>();

//    private static final int ITEM_COUNT = 10;
//    private List<Object> mContentItems = new ArrayList<>();

    private RecyclerView.Adapter mAdapter;

    public static CollegeFragment newInstance() {
//        if (mFragment == null) {
//            mFragment = new CollegeFragment();
//        }
//        return mFragment;
        return new CollegeFragment();
    }

    public CollegeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
//            DbUtil.insertFromXml();
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
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
