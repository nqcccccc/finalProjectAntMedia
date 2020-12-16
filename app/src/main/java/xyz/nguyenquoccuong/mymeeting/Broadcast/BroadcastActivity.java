package xyz.nguyenquoccuong.mymeeting.Broadcast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;

import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;

import xyz.nguyenquoccuong.mymeeting.R;

public class BroadcastActivity extends AppCompatActivity implements View.OnClickListener {

    private GLSurfaceView gls;
    private TextView tvStatus;
    private ImageButton btnRec,btnSetting,btnMic,btnCam,btnShare,btnChat;
    private Button btnStart;

    private static final String TAG = BroadcastActivity.class.getSimpleName();
    private ViewGroup mRootView;
    boolean mIsRecording = false;
    boolean mIsMuted = false;
    private Timer mTimer;
    private long mElapsedTime;
    public TimerHandler mTimerHandler;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        init();
    }

    private void init() {
        gls = findViewById(R.id.gls);
        tvStatus = findViewById(R.id.tvStatus);
        btnRec = findViewById(R.id.btnRec);
        btnSetting = findViewById(R.id.btnSetting);
        btnMic = findViewById(R.id.btnMic);
        btnCam = findViewById(R.id.btnCam);
        btnShare = findViewById(R.id.btnShare);
        btnChat = findViewById(R.id.btnChat);
        btnStart = findViewById(R.id.btnStart);

        btnRec.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnCam.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnMic.setOnClickListener(this);
        btnStart.setOnClickListener(this);
        btnShare.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                broadcast();
                break;
            case R.id.btnCam:
                cam();
                break;
            case R.id.btnSetting:
                setting();
                break;
            case R.id.btnChat:
                chat();
                break;
            case R.id.btnMic:
                mic();
                break;
            case R.id.btnShare:
                share();
                break;
            case R.id.btnRec:
                rec();
                break;
        }
    }

    private void setting() {
    }

    private void chat() {
    }

    private void cam() {
    }

    private void broadcast() {
    }

    private void mic() {
    }

    private void share() {
    }

    private void rec() {
    }
}