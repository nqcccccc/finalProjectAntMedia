package io.antmedia.android.Broadcast;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.antmedia.android.Dashboard.DashboardAcitivity;
import io.antmedia.android.MessageAdapter;
import io.antmedia.android.SignIn.SignInActivity;
import io.antmedia.android.User;
import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.R;
import io.antmedia.android.broadcaster.utils.Resolution;

import static io.antmedia.android.Dashboard.DashboardAcitivity.RTMP_BASE_URL;


public class BroadcastActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 10;
    private GLSurfaceView gls;
    private TextView tvStatus;
    private ImageButton btnRec,btnSetting,btnMic,btnCam,btnShare,btnChat,btnSend;
    private Button btnStart;

    private String mess ="";

    private static final String TAG = BroadcastActivity.class.getSimpleName();
    private ViewGroup mRootView;
    boolean mIsRecording = false;
    boolean mIsMuted = false;
    private java.util.Timer Timer;
    private long ElapsedTime;
    public TimerHandler TimerHandler;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private String streamName = "";
    private String email,fullName,avatar,userID;

    private FirebaseListAdapter<io.antmedia.android.Message> adapter;

    private DatabaseReference ref;

    private ServiceConnection Connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            if (mLiveVideoBroadcaster == null) {
                mLiveVideoBroadcaster = binder.getService();
                mLiveVideoBroadcaster.init(BroadcastActivity.this, gls);
                mLiveVideoBroadcaster.setAdaptiveStreaming(true);
            }
            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        Intent intent = getIntent();
        streamName = intent.getStringExtra("userID");
        email = intent.getStringExtra("email");
        fullName = intent.getStringExtra("fullName");
        avatar = intent.getStringExtra("avatar");
        userID = streamName;
        init();

        //binding on resume not to having leaked service connection
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        //this makes service do its job until done
        startService(mLiveVideoBroadcasterServiceIntent);


        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL activity.

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
        mRootView = findViewById(R.id.root_layout);
        btnSend = findViewById(R.id.btnSend);

        TimerHandler = new TimerHandler();

        btnChat.setClickable(false);
        btnRec.setClickable(false);

        if (gls != null) {
            gls.setEGLContextClientVersion(2);     // select GLES 2.0
        }
    }

    public void cam(View v) {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.changeCamera();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //this lets activity bind
        bindService(mLiveVideoBroadcasterServiceIntent, Connection, 0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (mLiveVideoBroadcaster.isPermissionGranted()) {
                    mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                }
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.RECORD_AUDIO) ) {
                        mLiveVideoBroadcaster.requestPermission();
                    }
                    else {
                        new AlertDialog.Builder(BroadcastActivity.this)
                                .setTitle(R.string.permission)
                                .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            //Open the specific App Info page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                            startActivity(intent);

                                        } catch ( ActivityNotFoundException e ) {
                                            //e.printStackTrace();

                                            //Open the generic Apps page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                            startActivity(intent);

                                        }
                                    }
                                })
                                .show();
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");

        //hide dialog if visible not to create leaked window exception
        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible()) {
            mCameraResolutionsDialog.dismiss();
        }
        mLiveVideoBroadcaster.pause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(Connection);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLiveVideoBroadcaster.setDisplayOrientation();
        }

    }

    public void setting(View v) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");
        if (fragmentDialog != null) {

            ft.remove(fragmentDialog);
        }

        ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();


        if (sizeList != null && sizeList.size() > 0) {
            mCameraResolutionsDialog = new CameraResolutionsFragment();

            mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
            mCameraResolutionsDialog.show(ft, "resolutiton_dialog");
        }
        else {
            Snackbar.make(mRootView, "No resolution available", Snackbar.LENGTH_LONG).show();
        }

    }

    public void broadcast(View v) {
        if (!mIsRecording)
        {
            if (mLiveVideoBroadcaster != null) {
                if (!mLiveVideoBroadcaster.isConnected()) {
                    Log.d(TAG, "broadcast: " +streamName);
                    new AsyncTask<String, String, Boolean>() {
                        ContentLoadingProgressBar
                                progressBar;
                        @Override
                        protected void onPreExecute() {
                            progressBar = new ContentLoadingProgressBar(BroadcastActivity.this);
                            progressBar.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... url) {
                            return mLiveVideoBroadcaster.startBroadcasting(url[0]);

                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            progressBar.hide();
                            mIsRecording = result;
                            if (result) {
                                tvStatus.setVisibility(View.VISIBLE);

                                btnStart.setText("STOP");
                                btnSetting.setClickable(false);
                                btnChat.setClickable(true);
                                btnRec.setClickable(true);
                                startTimer();//start the recording duration
                            }
                            else {
                                Snackbar.make(mRootView, "Fail to start", Snackbar.LENGTH_LONG).show();

                                triggerStopRecording();
                            }
                        }
                    }.execute(RTMP_BASE_URL + streamName);
                }
                else {
                    Snackbar.make(mRootView, R.string.streaming_not_finished, Snackbar.LENGTH_LONG).show();
                }
            }
            else {
                Snackbar.make(mRootView, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG).show();
            }
        }
        else
        {
            triggerStopRecording();
        }

    }

    public void mic(View v) {
        mIsMuted = !mIsMuted;
        mLiveVideoBroadcaster.setAudioEnable(!mIsMuted);
        btnMic.setImageDrawable(getResources()
                .getDrawable(mIsMuted ? R.drawable.ic_mic_mute_off_24 : R.drawable.ic_mic_mute_on_24));
    }

    public void share(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.share_dialog, null);

        TextView tvShare = alertLayout.findViewById(R.id.tvShare);
        ImageButton btnCopy = alertLayout.findViewById(R.id.btnCopy);

        tvShare.setText(streamName);
        Log.d(TAG, "share: "+streamName);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final AlertDialog dialogI = alert.create();
        dialogI.show();

        Object clipboardService = getSystemService(CLIPBOARD_SERVICE);
        final ClipboardManager clipboardManager = (ClipboardManager)clipboardService;

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData clipData = ClipData.newPlainText("Room ID", streamName);
                clipboardManager.setPrimaryClip(clipData);
                // Popup a snackbar.
                Snackbar snackbar = Snackbar.make(view, "Room ID has been copied to system clipboard.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    public void chat(View view) {
        openDialog();
    }

    private void openDialog() {
        LayoutInflater inflaterM = getLayoutInflater();
        View alertLayoutM = inflaterM.inflate(R.layout.message_dialog, null);

        final ListView lvMsg = alertLayoutM.findViewById(R.id.lvMsg);
        final EditText txtMsg = alertLayoutM.findViewById(R.id.txtMsg);
        ImageButton btnSend = alertLayoutM.findViewById(R.id.btnSend);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayoutM);

        final AlertDialog dialogI = alert.create();
        dialogI.show();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mess = txtMsg.getText().toString();
                io.antmedia.android.Message message = new io.antmedia.android.Message(streamName,email,fullName,streamName,avatar,mess);
                FirebaseDatabase.getInstance()
                        .getReference("message")
                        .child(streamName)
                        .push()
                        .setValue(message);

                // Clear the input
                txtMsg.setText("");
            }
        });
        ref = FirebaseDatabase.getInstance().getReference("message").child(streamName);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<io.antmedia.android.Message> data = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren())
                {
                    data.add(item.getValue(io.antmedia.android.Message.class));
                    Log.d(TAG, "mess: "+data);
                }
                MessageAdapter adapter = new MessageAdapter(BroadcastActivity.this,R.layout.message_items,data);
                lvMsg.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void triggerStopRecording() {
        if (mIsRecording) {
            btnStart.setText("START");

            tvStatus.setVisibility(View.GONE);
            tvStatus.setText("ON AIR");
            btnSetting.setClickable(true);
            btnRec.setClickable(false);
            btnChat.setClickable(false);

            FirebaseDatabase.getInstance().getReference("message").child(streamName).removeValue();

            stopTimer();
            mLiveVideoBroadcaster.stopBroadcasting();
        }

        mIsRecording = false;
    }

    //This method starts a Timer and updates the textview to show elapsed time for recording
    public void startTimer() {

        if(Timer == null) {
            Timer = new Timer();
        }

        ElapsedTime = 0;
        Timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                ElapsedTime += 1; //increase every sec
                TimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();

                if (mLiveVideoBroadcaster == null || !mLiveVideoBroadcaster.isConnected()) {
                    TimerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }


    public void stopTimer() {
        if (Timer != null) {
            this.Timer.cancel();
        }
        this.Timer = null;
        this.ElapsedTime = 0;
    }


    public void setResolution(Resolution size) {
        mLiveVideoBroadcaster.setResolution(size);
    }

    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    tvStatus.setText(getDurationString((int) ElapsedTime));
                    break;
                case CONNECTION_LOST:
                    triggerStopRecording();
                    new AlertDialog.Builder(BroadcastActivity.this)
                            .setMessage(R.string.broadcast_connection_lost)
                            .setPositiveButton(android.R.string.yes, null)
                            .show();

                    break;
            }
        }
    }

    public static String getDurationString(int seconds) {

        if(seconds < 0 || seconds > 2000000)//there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if(hours == 0)
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        else
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}