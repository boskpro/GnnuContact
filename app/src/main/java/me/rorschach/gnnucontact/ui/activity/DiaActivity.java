package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import me.rorschach.gnnucontact.R;

public class DiaActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diapad);
        ButterKnife.bind(this);
    }
}
