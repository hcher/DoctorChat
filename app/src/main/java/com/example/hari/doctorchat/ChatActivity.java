package com.example.hari.doctorchat;


import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by hari on 11/30/2016.
 */

public class ChatActivity extends AppCompatActivity {
    public static final String CHAT_KEY = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mChats = database.getReference("chats");
    private DatabaseReference mChat;
    private DatabaseReference mUsers = database.getReference("users");
    private DatabaseReference mUsernames = database.getReference("usernames");
    private String newUserUid;
    private String newUserName;
    private String currentUserName;
    private String currentOneSignal;
    private String chatName;
    private String chatKey;
    private String chatType;
    private EditText mAddUser;
    private EditText mMessage;
    private FirebaseListAdapter<ChatMessage> mChatAdapter;
    private ListView mMessagesListView;
    private ArrayList<String> oneSignalIds;


    /**
     * Gather information about the user and the chat
     * @param savedInstanceState, the Bundle passed through
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).init();

        chatKey = getIntent().getStringExtra(CHAT_KEY);
        mChat = mChats.child(chatKey);


        mChat.child("Type").addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Assign the Layout, EditText, and ListViews, and call to assign currentUserName
             * @param dataSnapshot, the string of the chat type retrieved from the database
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatType = dataSnapshot.getValue(String.class);

                if(chatType.equals("patient"))
                    setContentView(R.layout.patient_chat);
                else
                    setContentView(R.layout.doctor_chat);

                mAddUser = (EditText) findViewById(R.id.add_user_field);
                mMessagesListView = (ListView) findViewById(R.id.list_view_chat_messages);
                mMessage = (EditText) findViewById(R.id.chat_message_field);
                getCurrentUserName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                currentOneSignal = userId;
            }
        });


        mChat.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatName = dataSnapshot.getValue(String.class);
                ((TextView) findViewById(R.id.chat_name)).setText(chatName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        oneSignalIds = new ArrayList<String>();
        mChat.child("IDs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String oneSignalId = dataSnapshot.getValue(String.class);
                oneSignalIds.add(oneSignalId);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Update the current username and then call to set the list view
     */
    public void getCurrentUserName(){
        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("alias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserName = dataSnapshot.getValue(String.class);
                        setListView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Set the adapter to the messages child from the current chat's database reference. Color the
     * list item based on if the message is send by the current user or another user.
     */
    public void setListView(){
        super.onStart();

        mChatAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.list_item_chat_message,
                mChat.child("messages")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                TextView messageTextView = (TextView) v.findViewById(R.id.messageTextView);
                messageTextView.setText(model.getMessage());

                TextView authorTextView = (TextView) v.findViewById(R.id.nameTextView);
                authorTextView.setText(model.getAuthor());

                if (model.getAuthor().equals(currentUserName)) {
                    v.setBackgroundColor(Color.rgb(112, 219, 255));
                }
                else{
                    v.setBackgroundColor(Color.rgb(240,240,240));
                }

                TextView timeTextView = (TextView) v.findViewById(R.id.timeTextView);
                timeTextView.setText(model.getTime());
            }

        };

        mMessagesListView.setAdapter(mChatAdapter);
    }

    /**
     * After the button to view prescriptions is clicked, launch the PrescriptionActivity for the
     * current chat.
     * @param view
     */
    public void viewPrescriptions(View view){
        Intent intent = new Intent(getApplicationContext(), PrescriptionActivity.class);
        intent.putExtra(PrescriptionActivity.CHAT_KEY, chatKey);
        startActivity(intent);
    }

    /**
     * Create a new ChatMessage object based on the user's input and push it to the databse. Then
     * send notifications to all users after removing the current user's id.
     * @param view
     */
    public void sendMessage(View view) {
        String newMessage = mMessage.getText().toString();

        if(newMessage.length() == 0){
            return;
        }
        mMessage.setText("");

        ChatMessage chatMessage = new ChatMessage(currentUserName, newMessage,
                Calendar.getInstance().getTime().toString());

        mChat.child("messages").push().setValue(chatMessage);

        removeOwnId();
        for(String oneSignalId: oneSignalIds){
            try {
                String jsonString = "{'contents': {'en':'"+ chatName + " : " +
                        currentUserName + ": " + newMessage +"'}" +
                        ", 'include_player_ids': [" + oneSignalId + "]}";
                OneSignal.postNotification(new JSONObject(jsonString), null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove the current user's id from the list of OneSignal Ids to send the notifications to.
     */
    public void removeOwnId(){
        for(int i = 0; i < oneSignalIds.size(); i++){
            if(oneSignalIds.get(i).equals(currentOneSignal)){
                oneSignalIds.remove(i);
                break;
            }

        }
    }


    /**
     * Find the user and if it is not found display a toast. Otherwise, proceed to add them.
     * @param view
     * @throws InterruptedException
     */
    public void searchUser(View view) throws InterruptedException {
        newUserName = mAddUser.getText().toString();

        if(newUserName.length() == 0){
            Toast.makeText(getApplicationContext(), "Type a Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAddUser.setText("");


        mUsernames.child(newUserName).child("Uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newUserUid = dataSnapshot.getValue(String.class);
                if (newUserUid != null)
                    checkUser();
                else
                    Toast.makeText(getApplicationContext(), "Username not Found",
                            Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Verify that a patient is not being added to a patient chat.
     */
    public void checkUser() {
        mUsers.child(newUserUid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newUserRole = dataSnapshot.getValue(String.class);
                if (newUserRole.equals("patient") && chatType.equals("patient"))
                {
                    Toast.makeText(getApplicationContext(), "Sorry, Patients cannot be added to Patient chats.",
                            Toast.LENGTH_SHORT).show();
                }
                else
                    addOneSignal();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Add the OneSignal Id of the new user to the list of OneSignal Ids in the database for the current chat.
     */
    public void addOneSignal(){
        mUsernames.child(newUserName).child("OneSignal ID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newUserOneSignal = dataSnapshot.getValue(String.class);
                mChat.child("IDs").push().setValue(newUserOneSignal);
                addUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /**
     * Add the current chat room to the new user's list of chats in the database and display a toast.
     */
    public void addUser(){
        ChatRoom chatRoom = new ChatRoom(chatName, getIntent().getStringExtra(CHAT_KEY), chatType);
        mUsers.child(newUserUid).child("chats").push().setValue(chatRoom);

        Toast.makeText(getApplicationContext(), "Successfully Added!",
                Toast.LENGTH_SHORT).show();

    }
}
