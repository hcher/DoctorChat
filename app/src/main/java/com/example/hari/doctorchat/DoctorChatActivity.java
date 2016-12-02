package com.example.hari.doctorchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

/**
 * Created by hari on 11/30/2016.
 */

public class DoctorChatActivity extends AppCompatActivity {
    public static final String CHAT_KEY = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mChats = database.getReference("chats");
    private DatabaseReference mChat;
    private DatabaseReference mUsers = database.getReference("users");
    private DatabaseReference mUsernames = database.getReference("usernames");
    private  String newUserUid;
    private String currentUserName;
    private String chatName;
    private EditText mAddUser;
    private EditText mMessage;
    private FirebaseListAdapter<ChatMessage> mChatAdapter;
    private ListView mMessagesListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_chat);

        final String chatKey = getIntent().getStringExtra(CHAT_KEY);
        Log.d("chat key: ", chatKey);
        mChat = mChats.child(chatKey);


        mAddUser = (EditText) findViewById(R.id.add_user_field);
        mMessagesListView = (ListView) findViewById(R.id.list_view_chat_messages);
        mMessage = (EditText) findViewById(R.id.chat_message_field);


        Log.d("key", chatKey);

        mChat.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatName = dataSnapshot.getValue(String.class);
                ((TextView) findViewById(R.id.chat_name)).setText(chatName);
                //Log.d("chatName", chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        mChatAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item_chat_message,
                mChat.child("messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageTextView = (TextView) v.findViewById(R.id.messageTextView);
                messageTextView.setText(model.message);

                TextView authorTextView = (TextView) v.findViewById(R.id.nameTextView);
                authorTextView.setText(model.author);

                TextView timeTextView = (TextView) v.findViewById(R.id.timeTextView);
                timeTextView.setText(model.time);
            }

        };


        //mChatListView.getId();
        mMessagesListView.setAdapter(mChatAdapter);

        Log.d("set", "SET");
    }

    public void newMessage(View view) {


        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("alias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserName = dataSnapshot.getValue(String.class);
                        sendMessage();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    public void sendMessage(){
        String newMessage = mMessage.getText().toString();

        if(newMessage.length() == 0){
            return;
        }
        mMessage.setText("");

        ChatMessage chatMessage = new ChatMessage(currentUserName, newMessage,
                Calendar.getInstance().getTime().toString());

        mChat.child("messages").push().setValue(chatMessage);
    }



    public void searchUser(View view) throws InterruptedException {
        final String newUserName = mAddUser.getText().toString();

        if(newUserName.length() == 0){
            Toast.makeText(getApplicationContext(), "Type a Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAddUser.setText("");




        Log.d("Username", newUserName);

        mUsernames.child(newUserName).child("Uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newUserUid = dataSnapshot.getValue(String.class);
                Log.d("FTEST", "onDataChange: " + newUserUid);
                addUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void addUser(){
        if(newUserUid == null){
            Toast.makeText(getApplicationContext(), "Username Not Found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ChatRoom chatRoom = new ChatRoom(chatName, getIntent().getStringExtra(CHAT_KEY), "patient");
        mUsers.child(newUserUid).child("chats").push().setValue(chatRoom);
    }

}
