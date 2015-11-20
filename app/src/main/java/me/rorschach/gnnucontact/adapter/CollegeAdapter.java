package me.rorschach.gnnucontact.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.ui.activity.PersonActivity;

/**
 * Created by root on 15-11-16.
 */
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

//        holder.college.setText("college");
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
