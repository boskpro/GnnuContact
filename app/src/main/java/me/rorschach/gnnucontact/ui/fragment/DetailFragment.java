package me.rorschach.gnnucontact.ui.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import me.rorschach.gnnucontact.MyApplication;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.utils.DbUtil;
import me.rorschach.greendao.Contact;

public class DetailFragment extends DialogFragment implements View.OnClickListener{

    private static final String ARG_CONTACT = "ARG_CONTACT";

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

    private static DetailFragment mFragment;
    private StarChangeListener mListener;

    public static DetailFragment newInstance(Contact contact) {
        mFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contact);
        mFragment.setArguments(args);
        return mFragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mContact = (Contact) getArguments().getSerializable(ARG_CONTACT);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher();
        refWatcher.watch(this);
    }

    @Override
    @DebugLog
    public void onClick(View v) {
        Uri uri;
        Intent intent;
        switch (v.getId()) {
            case R.id.call:
                uri = Uri.parse("tel:" + mContact.getTel());
                intent = new Intent(Intent.ACTION_CALL, uri);
                getActivity().startActivity(intent);
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
                DbUtil.insertOrReplace(mContact);

                mListener.changeStarState();

                break;

            case R.id.more:
                //TODO
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (StarChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement StarChangeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface StarChangeListener{
        boolean changeStarState();
    }
}
