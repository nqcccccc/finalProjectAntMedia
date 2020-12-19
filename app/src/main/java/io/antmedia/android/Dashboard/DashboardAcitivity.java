package io.antmedia.android.Dashboard;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.antmedia.android.Broadcast.BroadcastActivity;
import io.antmedia.android.Play.PlayActivity;
import io.antmedia.android.broadcaster.R;

public class DashboardAcitivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab,fabNew,fabJoin;
    private boolean isFABOpen = true;
    public static final String RTMP_BASE_URL = "rtmp://nguyenquoccuong.xyz/LiveApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_acitivity);

        init();
    }

    private void init() {
        fab = findViewById(R.id.fab);
        fabNew = findViewById(R.id.fabNew);
        fabJoin = findViewById(R.id.fabJoin);

        fabNew.setVisibility(View.INVISIBLE);
        fabJoin.setVisibility(View.INVISIBLE);

        fabJoin.setOnClickListener(this);
        fabNew.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                fabNew.setVisibility(View.VISIBLE);
                fabJoin.setVisibility(View.VISIBLE);
                openFAB();
                break;
            case R.id.fabNew:
                startActivity(new Intent(DashboardAcitivity.this, BroadcastActivity.class));
                break;
            case R.id.fabJoin:
                startActivity(new Intent(DashboardAcitivity.this, PlayActivity.class));
        }
    }

    private void openFAB() {
        if(!isFABOpen){
            isFABOpen=true;
            fabNew.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
            fabJoin.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        } else{
            isFABOpen=false;
            fabNew.animate().translationY(0);
            fabJoin.animate().translationY(0);
        }
    }

}