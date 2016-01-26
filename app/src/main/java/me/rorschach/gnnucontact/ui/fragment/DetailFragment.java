package me.rorschach.gnnucontact.ui.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.leakcanary.RefWatcher;

import org.simple.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.User;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class DetailFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_CONTACT = "ARG_CONTACT";

    private boolean isStar = false;

    @Bind(R.id.detail_switcher)
    ViewSwitcher mDetailSwitcher;
    @Bind(R.id.test_view)
    FrameLayout mTestView;
    @Bind(R.id.avatar)
    ImageView mAvatar;
    @Bind(R.id.name)
    TextView mName;
    @Bind(R.id.college)
    TextView mCollege;
    @Bind(R.id.call)
    ImageButton mCall;
    @Bind(R.id.sms)
    ImageButton mSms;
    @Bind(R.id.star)
    ImageButton mStar;
    @Bind(R.id.more)
    ImageButton mMore;
    @Bind(R.id.card_view)
    CardView mCardView;

    private Contact mContact;

    private static DetailFragment sFragment;

    public static DetailFragment newInstance(Contact contact) {
        if (sFragment == null) {
            sFragment = new DetailFragment();
        }
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contact);
        sFragment.setArguments(args);
        return sFragment;
    }

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);

        if (getArguments() != null) {
            mContact = (Contact) getArguments().getSerializable(ARG_CONTACT);
            if (mContact != null) {
                isStar = mContact.getIsStar();
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.list_item_card_big, null);
        ButterKnife.bind(this, view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        initView();
        return dialog;
    }

    private void initView() {
        mName.setText(mContact.getName());
        mCollege.setText(mContact.getCollege());
        mStar.setImageResource(mContact.getIsStar() ?
                R.drawable.ic_star_grey_32 : R.drawable.ic_unstar_grey_32);
        mCall.setOnClickListener(this);
        mSms.setOnClickListener(this);
        mStar.setOnClickListener(this);
        mMore.setOnClickListener(this);
        mTestView.setOnClickListener(this);
    }

    @Override
    @DebugLog
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        sFragment = null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (isStar != mContact.getIsStar()) {
            DbUtil.insertOrReplace(mContact);
            EventBus.getDefault().post(new User("star"), "update_star_list");
        }
        EventBus.getDefault().post(new User("record"), "update_record_list");
    }

    @Override
    public void onClick(View v) {
        Uri uri;
        Intent intent;
        switch (v.getId()) {
            case R.id.call:
                uri = Uri.parse("tel:" + mContact.getTel());
                intent = new Intent(Intent.ACTION_CALL, uri);
                getActivity().startActivity(intent);

                mContact.setIsRecord(true);
                DbUtil.insertOrReplace(mContact);

                break;

            case R.id.sms:
                uri = Uri.parse("sms:" + mContact.getTel());
                intent = new Intent(Intent.ACTION_SENDTO, uri);
                getActivity().startActivity(intent);
                break;

            case R.id.star:
                mContact.setIsStar(!mContact.getIsStar());
                mStar.setImageResource(mContact.getIsStar() ?
                        R.drawable.ic_star_grey_32 : R.drawable.ic_unstar_grey_32);
                mContact.setIsStar(mContact.getIsStar());

                break;

            case R.id.more:
                mDetailSwitcher.showNext();
                break;

            case R.id.test_view:
                mDetailSwitcher.showNext();
                break;

            default:
                break;
        }
    }
}
