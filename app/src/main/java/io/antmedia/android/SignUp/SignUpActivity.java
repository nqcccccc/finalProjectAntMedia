package io.antmedia.android.SignUp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import io.antmedia.android.SignIn.SignInActivity;
import io.antmedia.android.User;
import io.antmedia.android.broadcaster.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtName,txtEmail,txtPass,txtRePass;
    private Button btnSignin,btnSignup;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private String userID;

    // Image onClick
    private ImageView imgProfile;
    private Uri imgUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String pathImage;
    private boolean chooseImg;

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
        imgProfile.setOnClickListener(this);
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

        // Image storage
        imgProfile = findViewById(R.id.imgProfile);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        pathImage = "";
        chooseImg = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgProfile:
                choosePicture();
                break;
            case R.id.btnSignup:
                if (!chooseImg)
                {
                    Toast.makeText(getApplicationContext(), "Please select your picture!", Toast.LENGTH_LONG).show();
                }
                else{
                    uploadImage();
                }
                break;
            case R.id.btnSignin:
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
                break;
        }
    }

    private void choosePicture()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imgUri = data.getData();
            imgProfile.setImageURI(imgUri);
            chooseImg = true;
        }
    }

    private void uploadImage()
    {
        final String randomKey = UUID.randomUUID().toString();
        final StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pathImage = uri.toString();
                        Log.d("TAG", "onSuccess: "+pathImage);
                        pushUser(pathImage);
                    }
                });
            }
        });

    }


    private void pushUser(String avatar) {
        final String AVA = avatar;
        Log.d("TAG", "pushUser: "+AVA);
        final String fullName = txtName.getText().toString();
        final String email = txtEmail.getText().toString().trim();
        String Pass = txtPass.getText().toString().trim();
        String rePass = txtRePass.getText().toString().trim();
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

        if(TextUtils.isEmpty(rePass)){
            txtRePass.setError("Please enter your Re. Password !");
            txtRePass.requestFocus();
            return;
        }

        if(!rePass.equals(Pass)){
            txtRePass.setError("Re. Password does not match !");
            txtRePass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // register the user in firebase
        firebaseAuth.createUserWithEmailAndPassword(email,Pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            User user = new User(email,fullName,AVA);
                            Log.d("TAG", "onComplete: " +user);
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference reference = firebaseDatabase.getReference("Users");
                            DatabaseReference userReference = reference.child(FirebaseAuth.getInstance().getUid());
                            userReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                                        if (firebaseUser.isEmailVerified()){
                                            Log.d("TAG", "onComplete: OKKK");
                                        }else {
                                            Log.d("TAG", "onComplete: OKKKK");
                                            firebaseUser.sendEmailVerification();
                                            Toast.makeText(SignUpActivity.this, "Check your email to verify your account ! ", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                            finish();
                                        }
                                    }else {
                                        Log.d("TAG", "onComplete: FFFF");
                                        Toast.makeText(SignUpActivity.this, "Sign up failed ! Please try again !", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                            // Update DTO with generated firebase key
                            String avartar = userReference.getKey();

                        }else {
                            Toast.makeText(SignUpActivity.this, "Sign up failed ! Please try again !", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}