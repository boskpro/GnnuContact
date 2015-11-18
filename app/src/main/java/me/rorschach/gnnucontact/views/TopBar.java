package me.rorschach.gnnucontact.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.rorschach.gnnucontact.R;

/**
 * Created by root on 15-11-17.
 */
public class TopBar extends RelativeLayout {

    private LayoutParams mTitleParams;
    private TextView mTitle;
    private String mTitleText;
    private float mTitleSize;
    private int mTitleColor;

    private LayoutParams mLeftParams;
    private Button mLeftButton;
    private String mLeftText;
    private int mLeftTextColor;
    private float mLeftTextSize;
    private Drawable mLeftBtground;

    private LayoutParams mRightParams;
    private Button mRightButton;
    private String mRightText;
    private float mRightTextSize;
    private int mRightTextColor;
    private Drawable mRightBtground;

    private topbarClickListener mListener;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TopBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.TopBar);
        try {
            mTitleText = typedArray.getString(R.styleable.TopBar_titleName);
            mTitleSize = typedArray.getDimension(R.styleable.TopBar_titleTextSize, 15);
            mTitleColor = typedArray.getColor(R.styleable.TopBar_titleTextColor, 0);

            mLeftText = typedArray.getString(R.styleable.TopBar_leftText);
            mLeftTextSize = typedArray.getDimension(R.styleable.TopBar_leftTextSize, 15);
            mLeftTextColor = typedArray.getColor(R.styleable.TopBar_leftTextColor, 0);
            mLeftBtground = typedArray.getDrawable(R.styleable.TopBar_leftBtground);

            mRightText = typedArray.getString(R.styleable.TopBar_rightText);
            mRightTextSize = typedArray.getDimension(R.styleable.TopBar_rightTextSize, 15);
            mRightTextColor = typedArray.getColor(R.styleable.TopBar_rightTextColor, 0);
            mRightBtground = typedArray.getDrawable(R.styleable.TopBar_rightBtground);
        }finally {
            typedArray.recycle();
        }
        set(context);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void set(Context context) {
        mTitle = new TextView(context);
        mTitle.setText(mTitleText);
        mTitle.setTextSize(mTitleSize);
        mTitle.setTextColor(mTitleColor);
        mTitle.setGravity(Gravity.CENTER);
        mTitleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        addView(mTitle, mTitleParams);

        mLeftButton = new Button(context);
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLeftClick(v);
            }
        });
        mLeftButton.setBackground(mLeftBtground);
        mLeftButton.setText(mLeftText);
        mLeftButton.setTextSize(mLeftTextSize);
        mLeftButton.setTextColor(mLeftTextColor);
        mLeftParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        addView(mLeftButton, mLeftParams);


        mRightButton = new Button(context);
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRightClick(v);
            }
        });
        mRightButton.setBackground(mRightBtground);
        mRightButton.setText(mRightText);
        mRightButton.setTextSize(mRightTextSize);
        mRightButton.setTextColor(mRightTextColor);
        mRightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        addView(mRightButton, mRightParams);
    }

    public void setOnTopBarClickListener(topbarClickListener mListener) {
        this.mListener = mListener;
    }

    public interface topbarClickListener {
        void onLeftClick(View v);

        void onRightClick(View v);
    }

}
