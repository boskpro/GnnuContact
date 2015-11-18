package me.rorschach.gnnucontact.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.rorschach.gnnucontact.R;
import me.rorschach.gnnucontact.views.TopBar;

public class DiaActivity extends Activity {

    @Bind(R.id.topbar)
    TopBar mTopbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diapad);
        ButterKnife.bind(this);

        mTopbar.setOnTopBarClickListener(new TopBar.topbarClickListener() {
            @Override
            public void onLeftClick(View v) {
                Snackbar.make(v, "Left clicked", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onRightClick(View v) {
                Snackbar.make(v, "Right clicked", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
