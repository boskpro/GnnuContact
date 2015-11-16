package me.rorschach.gnnucontact.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.R;
import me.rorschach.greendao.Contact;

/**
 * Created by root on 15-11-16.
 */
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private Activity mActivity;
    private List<Contact> mList;

    private PersonViewHolder mViewHolder;

    public PersonAdapter(Activity activity, List<Contact> list) {
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
    @DebugLog
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
//
//                int position = getAdapterPosition();
//                final Contact contact = mList.get(position);

//                mFragment = DetailDialogFragment.newInstance(
//                        contacts.getName(), contacts.getTel(),
//                        contacts.getCollege(), contacts.getIsFavorite());
//                showDetailDialog();
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

