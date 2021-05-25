package com.example.chatapp;

import android.app.Notification;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessage;
    private EditText messageInput;
    private ScrollView scrollView;
    private TextView displayTextMessage;

    private String currentGroupName, currentuserID, currentUserName, currentDate, currentTime;

    private FirebaseAuth fAuth;
    private DatabaseReference userRef, groupNameRef, groupMessageKeyRef;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_activity);

        fAuth = FirebaseAuth.getInstance();
        currentuserID = fAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);


        initializeFields();

        getUserInfo();

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDatabase();

                messageInput.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    displayMessages(snapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                   displayMessages(snapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initializeFields() {
        toolbar = findViewById(R.id.GroupChatBarLayout);
        messageInput = findViewById(R.id.inputGroupMessage);
        sendMessage = findViewById(R.id.sendIcon);
        scrollView = (ScrollView) findViewById(R.id.GroupChatScrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        displayTextMessage = findViewById(R.id.GroupChatTextDisplay);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

    }

    private void getUserInfo() {
        userRef.child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    currentUserName = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void SaveMessageInfoToDatabase() {
        String message = messageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();
        if(message.isEmpty()) {

        } else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageKeyRef = groupNameRef.child(messageKey);


            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);

            groupMessageKeyRef.updateChildren(messageInfoMap);
        }
    }

    private void displayMessages(DataSnapshot snapshot) {
        Iterator it = snapshot.getChildren().iterator();

        while (it.hasNext()) {
            String chatDate = (String) ((DataSnapshot)it.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)it.next()).getValue();
            String chatName = (String) ((DataSnapshot)it.next()).getValue();
            String chatTime =  (String) ((DataSnapshot)it.next()).getValue();

            displayTextMessage.append(chatName + ": \n" + chatMessage + "\n" + chatTime + "    " + chatDate + "\n\n\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }
}
