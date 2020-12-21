package io.antmedia.android.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.antmedia.android.Message;
import io.antmedia.android.User;
import io.antmedia.android.broadcaster.R;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference myDatabase;

    // Element
    private ListView listViewMessage;
    private Button btnSend;
    private EditText editTextInput;
    private List<Message> messageList;

    // Info
    private String userID;
    private String roomID;
    private String messageContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_chat);

        init();

        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                if (dataSnapshot.exists())
                {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren())
                    {
                        Message m = messageSnapshot.getValue(Message.class);

                        // Load những Message có roomID bằng với lại roomID trong ChatActivity
                        if (m.getRoomID().equals(roomID))
                        {
                            messageList.add(m);
                        }
                    }
                    listViewMessage.setAdapter(new MessageAdapter(ChatActivity.this, messageList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init()
    {
        listViewMessage = findViewById(R.id.list_view_message);
        btnSend = findViewById(R.id.btn_send);
        editTextInput = findViewById(R.id.edit_text_input_message);

        messageList = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        roomID = userID;
        Intent intent = getIntent();
        if (intent.hasExtra("roomID"))
        {
            roomID = intent.getStringExtra("roomID");
        }

        messageContent = editTextInput.getText().toString();

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                pushMessage();
                break;
        }
    }

    private void pushMessage()
    {
        if (messageContent.equals(""))
        {
            return;
        }

        // Get data in RTDB
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query getUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    String userName = user.getFullName();
                    String userEmail = user.getEmail();
                    String userAvartar = user.getAvatar();

                    myDatabase.push().setValue(new Message(userID, userEmail, userName, roomID, userAvartar, messageContent));
                    editTextInput.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
