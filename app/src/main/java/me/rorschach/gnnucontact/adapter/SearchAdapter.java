package me.rorschach.gnnucontact.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.TextUtil;
import me.rorschach.greendao.Contact;

/**
 * Created by root on 15-11-16.
 */
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
//
//                int position = getAdapterPosition();
//                final Contact contact = mList.get(position);
            return true;
        }

        @Override
        public void onClick(View v) {

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
        }
    }
}