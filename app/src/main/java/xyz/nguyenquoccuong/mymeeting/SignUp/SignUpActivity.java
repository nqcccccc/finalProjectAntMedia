package xyz.nguyenquoccuong.mymeeting.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import xyz.nguyenquoccuong.mymeeting.Dashboard.DashboardActivity;
import xyz.nguyenquoccuong.mymeeting.R;
import xyz.nguyenquoccuong.mymeeting.SignIn.SignInActivity;
import xyz.nguyenquoccuong.mymeeting.User;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtName,txtEmail,txtPass,txtRePass;
    private Button btnSignin,btnSignup;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
        signUp();

    }

    private void signUp() {
        btnSignup.setOnClickListener(this);
        btnSignin.setOnClickListener(this);
    }

    private void init() {
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        txtRePass = findViewById(R.id.txtRePass);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                pushUser();
                break;
            case R.id.btnSignin:
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
                break;
        }
    }

    private void pushUser() {
        String email = txtEmail.getText().toString().trim();
        String Pass = txtPass.getText().toString().trim();
        String fullName = txtName.getText().toString();
        String rePass = txtRePass.getText().toString().trim();


        progressBar.setVisibility(View.VISIBLE);

        // register the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email,Pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(email,fullName);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (firebaseUser.isEmailVerified()){
                                }else {
                                    firebaseUser.sendEmailVerification();
                                    Toast.makeText(SignUpActivity.this, "Check your email to verify your account ! ", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    finish();
                                }
                            }else {
                                Toast.makeText(SignUpActivity.this, "Sign up failed ! Please try again !", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed ! Please try again !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}