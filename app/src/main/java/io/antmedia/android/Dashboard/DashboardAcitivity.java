package io.antmedia.android.Dashboard;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.antmedia.android.Broadcast.BroadcastActivity;
import io.antmedia.android.Play.PlayActivity;
import io.antmedia.android.User;
import io.antmedia.android.broadcaster.R;

public class DashboardAcitivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab,fabNew,fabJoin;
    private CircleImageView imgAva;
    private boolean isFABOpen = true;
    private String email,userID;
    public static final String RTMP_BASE_URL = "rtmp://nguyenquoccuong.xyz/LiveApp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_acitivity);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        userID = intent.getStringExtra("userID");

        Log.d("TAG", "email: "+email);
        Log.d("TAG", "userID: "+userID);
        init();
        initData();
    }

    private void initData() {
        DatabaseReference all = FirebaseDatabase.getInstance().getReference();
        Query getUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d("TAG", "onDataChange: "+user.getAvatar());
                Picasso.with(DashboardAcitivity.this).load(user.getAvatar()).into(imgAva);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        fab = findViewById(R.id.fab);
        fabNew = findViewById(R.id.fabNew);
        fabJoin = findViewById(R.id.fabJoin);
        imgAva = findViewById(R.id.imgAva);

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
                Intent intent = new Intent(DashboardAcitivity.this, BroadcastActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                break;
            case R.id.fabJoin:
                openJoinD();
        }
    }

    private void openJoinD() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.join_dialog, null);
        final EditText txtRoomID = alertLayout.findViewById(R.id.txtRoomID);
        Button btnSubmit = alertLayout.findViewById(R.id.btnSubmit);
        final ProgressBar progressBar_submit = alertLayout.findViewById(R.id.progressBar_submit);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Join Your Meet");
        alert.setView(alertLayout);

        final AlertDialog dialog = alert.create();
        dialog.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomID = txtRoomID.getText().toString().trim() + "live=1";
                Log.d("TAG", "RoomID: "+roomID);
                Intent intentJoin = new Intent(DashboardAcitivity.this, PlayActivity.class);
                intentJoin.putExtra("roomID",roomID);
                startActivity(intentJoin);
                dialog.cancel();
            }
        });
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