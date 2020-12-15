package xyz.nguyenquoccuong.mymeeting.SignIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import xyz.nguyenquoccuong.mymeeting.Dashboard.DashboardActivity;
import xyz.nguyenquoccuong.mymeeting.R;
import xyz.nguyenquoccuong.mymeeting.SignUp.SignUpActivity;
import xyz.nguyenquoccuong.mymeeting.User;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtEmail,txtPass;
    private Button btnSignin,btnSignup;
    private TextView tvForgot;
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
        tvForgot = findViewById(R.id.tvForgot);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        tvForgot.setOnClickListener(this);
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
            case R.id.tvForgot:
                forgot();
                break;
        }

    }

    private void forgot() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.forgot_dialog, null);
        EditText txtEmail_forgot = alertLayout.findViewById(R.id.txtEmail_forgot);
        Button btnRequest = alertLayout.findViewById(R.id.btnRequest);
        ProgressBar progressBar_forgot = alertLayout.findViewById(R.id.progressBar_forgot);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Reset Password");
        alert.setView(alertLayout);

        AlertDialog dialog = alert.create();
        dialog.show();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_forgot  = txtEmail_forgot.getText().toString().trim();

                if(TextUtils.isEmpty(email_forgot)){
                    txtEmail_forgot.setError("Please enter your email !");
                    txtEmail_forgot.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email_forgot).matches()){
                    txtEmail_forgot.setError("Please enter valid email");
                    txtEmail_forgot.requestFocus();
                    return;
                }
                progressBar_forgot.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(email_forgot).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressBar_forgot.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Please check your email to reset your password !", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else {
                            progressBar_forgot.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Please try again ! Something wrong happened", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void signIn() {
        email = txtEmail.getText().toString().trim();
        Pass = txtPass.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            txtEmail.setError("Please enter your email !");
            txtEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter valid email");
            txtEmail.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(Pass)){
            txtPass.setError("Please enter your password !");
            txtPass.requestFocus();
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
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (firebaseUser.isEmailVerified()){
                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                        finish();
                    }else {
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(SignInActivity.this, "Your account haven't been verified ! ", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    Toast.makeText(SignInActivity.this, "Sign in failed ! Please check your credentials again !", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}