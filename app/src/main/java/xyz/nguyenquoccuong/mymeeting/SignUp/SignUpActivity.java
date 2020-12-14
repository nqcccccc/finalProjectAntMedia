package xyz.nguyenquoccuong.mymeeting.SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

import xyz.nguyenquoccuong.mymeeting.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText txtName,txtEmail,txtPass,txtRePass;
    private Button btnSignin,btnSignup;
    private FirebaseFirestore firebaseFirestore;;
    private DocumentReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseFirestore = FirebaseFirestore.getInstance();

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignup:
                pushUser();
                break;
        }
    }

    private void pushUser() {
        if(txtName.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please type a username", Toast.LENGTH_SHORT).show();

        }else if(txtEmail.getText().toString().equals("")) {
            Toast.makeText(SignUpActivity.this, "Please type an email id", Toast.LENGTH_SHORT).show();

        }else if(txtPass.getText().toString().equals("")){
            Toast.makeText(SignUpActivity.this, "Please type a password", Toast.LENGTH_SHORT).show();

        }else if(!txtRePass.getText().toString().equals(txtPass.getText().toString())){
            Toast.makeText(SignUpActivity.this, "Password mismatch", Toast.LENGTH_SHORT).show();

        }else {
            ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Toast.makeText(SignUpActivity.this, "Sorry,this user exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Map<String, Object> reg_entry = new HashMap<>();
                        reg_entry.put("Name", txtName.getText().toString());
                        reg_entry.put("Email", txtEmail.getText().toString());
                        reg_entry.put("Password", txtPass.getText().toString());
                        firebaseFirestore.collection("user")
                            .add(reg_entry)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(SignUpActivity.this, "Sign Up Successful !", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Error", e.getMessage());
                                }
                            });
                    }
                }
            });
        }
    }
}