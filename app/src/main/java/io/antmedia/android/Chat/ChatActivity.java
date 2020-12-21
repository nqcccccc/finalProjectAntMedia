package io.antmedia.android.Chat;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class ChatActivity extends DialogFragment implements View.OnClickListener {

    public static ChatActivity newInstance()
    {
        return new ChatActivity();
    }

    private DatabaseReference myDatabase;

    // Element
    private ListView listViewMessage;
    private Button btnSend;
    private EditText editTextInput;
    private List<Message> messageList;

    // Info
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String roomID;
    private String messageContent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_chat, container, false);

        listViewMessage = view.findViewById(R.id.list_view_message);
        btnSend = view.findViewById(R.id.btn_send);
        editTextInput = view.findViewById(R.id.edit_text_input_message);

        messageList = new ArrayList<>();
        myDatabase = FirebaseDatabase.getInstance().getReference("Messages");

        btnSend.setOnClickListener(this);

        roomID = getArguments().getString("roomID").toString();

        if (roomID.equals("null"))
        {
            roomID = userID;
        }
        messageContent = editTextInput.getText().toString();


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

        return view;
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
                    Log.d("ERROR", userID);
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
