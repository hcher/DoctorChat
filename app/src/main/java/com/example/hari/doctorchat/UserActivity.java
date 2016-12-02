package com.example.hari.doctorchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import java.util.ArrayList;

import static java.lang.Boolean.FALSE;

/**
 * Created by hari on 11/26/2016.
 */

public class UserActivity extends AppCompatActivity{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDirectoryRef = database.getReference("directory");
    private DatabaseReference mChatRef = database.getReference("chats");
    private DatabaseReference mUsers = database.getReference("users");
    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String role;
    private EditText mNewRoom;
    private ListView mChatListView;
    private FirebaseListAdapter<ChatRoom> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        Log.d("User: ", FirebaseAuth.getInstance().getCurrentUser().getUid());

        mNewRoom = (EditText) findViewById(R.id.add_chat_name);
        mChatListView = (ListView) findViewById(R.id.list_view_user_chats);


        mUsers.child(currentUid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                role = dataSnapshot.getValue(String.class);
                Log.d("FTEST", "onDataChange: " + User.class);;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void newChat(View view){
        String newName = mNewRoom.getText().toString();

        if(newName.length() == 0){
            Toast.makeText(getApplicationContext(), "Type a Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        String key = mChatRef.push().getKey();
        ChatRoom chatRoom = new ChatRoom(newName, key, role);

        mChatRef.child(key).child("Type").setValue(role);
        mChatRef.child(key).child("Name").setValue(newName);

        mUsers.child(currentUid).child("chats").push().setValue(chatRoom);
        mDirectoryRef.push().setValue(chatRoom);
        mNewRoom.setText("");
    }

    @Override
    protected void onStart(){
        super.onStart();

        mAdapter = new FirebaseListAdapter<ChatRoom>(this, ChatRoom.class, R.layout.list_item_chat,
                mUsers.child(currentUid).child("chats")) {
            @Override
            protected void populateView(View v, final ChatRoom model, int position) {
                //((TextView) findViewById(R.id.chat_name_text_view)).setText(model.name);
                TextView mTextView = (TextView) v.findViewById(R.id.chat_name_text_view);
                mTextView.setText(model.getName());



            }
        };

        mChatListView.setAdapter(mAdapter);

        mChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoom chatRoom = (ChatRoom) mAdapter.getItem(position);
                Intent intent = null;
                if (chatRoom.getType().equals("patient")){
                    intent = new Intent(getApplicationContext(), PatientChatActivity.class);
                    Log.d("key", chatRoom.getKey());
                    intent.putExtra(PatientChatActivity.CHAT_KEY, chatRoom.getKey());
                    startActivity(intent);
                }
                else{
                    intent = new Intent(getApplicationContext(), DoctorChatActivity.class);
                    intent.putExtra(DoctorChatActivity.CHAT_KEY, chatRoom.getKey());
                    startActivity(intent);
                }
            }
        });


        //mChatListView.getId();


        Log.d("set", "SET");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.cleanup();
    }
}
