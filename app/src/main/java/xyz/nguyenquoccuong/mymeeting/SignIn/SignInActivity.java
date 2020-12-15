package xyz.nguyenquoccuong.mymeeting.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.List;

import xyz.nguyenquoccuong.mymeeting.Dashboard.DashboardActivity;
import xyz.nguyenquoccuong.mymeeting.R;
import xyz.nguyenquoccuong.mymeeting.SignUp.SignUpActivity;
import xyz.nguyenquoccuong.mymeeting.User;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail,txtPass;
    private Button btnSignin,btnSignup;
    private ProgressBar progressBar;

    private String email,Pass;
    private List<User> users;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();
    }

    private void init() {
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
            case R.id.btnSignin:
                signIn();
                break;
        }

    }

    private void signIn() {
        email = txtEmail.getText().toString().trim();
        Pass = txtPass.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            txtEmail.setError("Please enter your email !");
            txtEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(Pass)){
            txtPass.setError("Please enter your password !");
            txtPass.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter valid email");
            txtEmail.requestFocus();
            return;
        }

        if(Pass.length() < 6){
            txtPass.setError("Password must be >= 6 characters");
            txtPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // login with firebase authentication
        firebaseAuth.signInWithEmailAndPassword(email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Goto dashboard
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                    finish();
                }else {
                    Toast.makeText(SignInActivity.this, "Sign in failed ! Please check your credentials again !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}