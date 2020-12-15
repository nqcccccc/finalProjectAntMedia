package xyz.nguyenquoccuong.mymeeting.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import xyz.nguyenquoccuong.mymeeting.R;
import xyz.nguyenquoccuong.mymeeting.SignIn.SignInActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtName,txtEmail,txtPass,txtRePass;
    private Button btnSignin,btnSignup;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference ref;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        init();
        signUp();


    }

    private void signUp() {
        ref = firebaseFirestore.collection("user").document();
        btnSignup.setOnClickListener(this);
    }

    private void init() {
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        txtRePass = findViewById(R.id.txtRePass);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                pushUser();
                break;
            case R.id.btnSignin:
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                break;
        }
    }

    private void pushUser() {
        String email = txtEmail.getText().toString().trim();
        String Pass = txtPass.getText().toString().trim();
        String fullName = txtName.getText().toString();
        String rePass = txtRePass.getText().toString().trim();

        if(TextUtils.isEmpty(fullName)){
            txtName.setError("Please enter your name !");
            return;
        }

        if(TextUtils.isEmpty(email)){
            txtEmail.setError("Please enter your email !");
            return;
        }

        if(TextUtils.isEmpty(Pass)){
            txtPass.setError("Please enter your password");
            return;
        }

        if(Pass.length() < 6){
            txtPass.setError("Password must be >= 6 characters");
            return;
        }

        if(!rePass.equals(Pass)){
            txtRePass.setError("Password mismatch");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // register the user in firebase

        firebaseAuth.createUserWithEmailAndPassword(email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // send verification link

                    FirebaseUser fuser = firebaseAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SignUpActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    Toast.makeText(SignUpActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("fName",fullName);
                    user.put("email",email);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "onSuccess: user Profile is created for "+ userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "onFailure: " + e.toString());
                        }
                    });
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                }else {
                    Toast.makeText(SignUpActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}