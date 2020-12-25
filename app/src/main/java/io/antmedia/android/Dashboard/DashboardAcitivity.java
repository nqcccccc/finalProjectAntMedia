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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import io.antmedia.android.SignIn.SignInActivity;
import io.antmedia.android.User;
import io.antmedia.android.broadcaster.R;

public class DashboardAcitivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab,fabNew,fabJoin;
    private CircleImageView imgAva;
    private boolean isFABOpen = true;
    private String email,userID;
    private User user;
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
                user = dataSnapshot.getValue(User.class);
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
        imgAva.setOnClickListener(this);
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
                intent.putExtra("userID",userID);
                intent.putExtra("email",user.getEmail());
                intent.putExtra("avatar",user.getAvatar());
                intent.putExtra("fullName",user.getFullName());
                startActivity(intent);
                break;
            case R.id.fabJoin:
                openJoinD();
                break;
            case R.id.imgAva:
                openInfoD();
                break;
        }
    }

    private void openInfoD() {
        LayoutInflater inflaterI = getLayoutInflater();
        View alertLayoutI = inflaterI.inflate(R.layout.info_dialog, null);

        LinearLayout signout = alertLayoutI.findViewById(R.id.signout);
        LinearLayout info = alertLayoutI.findViewById(R.id.info);
        CircleImageView imgAvaD = alertLayoutI.findViewById(R.id.imgAvaD);
        TextView tvName = alertLayoutI.findViewById(R.id.tvName);

        Picasso.with(DashboardAcitivity.this).load(user.getAvatar()).into(imgAvaD);
        tvName.setText(user.getFullName().trim());

        AlertDialog.Builder alertI = new AlertDialog.Builder(this);
        alertI.setView(alertLayoutI);

        final AlertDialog dialogI = alertI.create();
        dialogI.show();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAcitivity.this, SignInActivity.class));
                finish();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardAcitivity.this, "App info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openJoinD() {
        LayoutInflater inflaterJ = getLayoutInflater();
        View alertLayoutJ = inflaterJ.inflate(R.layout.join_dialog, null);
        final EditText txtRoomID = alertLayoutJ.findViewById(R.id.txtRoomID);
        Button btnSubmit = alertLayoutJ.findViewById(R.id.btnSubmit);
        final ProgressBar progressBar_submit = alertLayoutJ.findViewById(R.id.progressBar_submit);

        AlertDialog.Builder alertJ = new AlertDialog.Builder(this);
        alertJ.setTitle("Join Your Meet");
        alertJ.setView(alertLayoutJ);

        final AlertDialog dialogJ = alertJ.create();
        dialogJ.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check active roomID
                final String roomID = txtRoomID.getText().toString().trim();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("live").child(roomID);
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                        {
                            String roomID = txtRoomID.getText().toString().trim() + " live=1";
                            Log.d("TAG", "RoomID: "+roomID);
                            Intent intentJoin = new Intent(DashboardAcitivity.this, PlayActivity.class);
                            intentJoin.putExtra("roomID",roomID);
                            intentJoin.putExtra("userID",userID);
                            intentJoin.putExtra("email",user.getEmail());
                            intentJoin.putExtra("avatar",user.getAvatar());
                            intentJoin.putExtra("fullName",user.getFullName());
                            startActivity(intentJoin);
                            dialogJ.cancel();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "RoomID is incorrect!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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