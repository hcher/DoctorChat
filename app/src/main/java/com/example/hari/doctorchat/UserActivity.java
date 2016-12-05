package com.example.hari.doctorchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.onesignal.OneSignal;

/**
 * Created by hari on 11/26/2016.
 */

public class UserActivity extends AppCompatActivity{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mChatRef = database.getReference("chats");
    private DatabaseReference mUsers = database.getReference("users");
    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String oneSignalId;
    private String role;
    private EditText mNewRoom;
    private ListView mChatListView;
    private FirebaseListAdapter<ChatRoom> mAdapter;

    /**
     * Assign the EditText and ListView, and store the current patients role from the database.
     * @param savedInstanceState, the Bundle passed through
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        mNewRoom = (EditText) findViewById(R.id.add_chat_name);
        mChatListView = (ListView) findViewById(R.id.list_view_user_chats);


        mUsers.child(currentUid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                role = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set up the adapter to display all the chat rooms the user is a part of in the ListView.
     * Set up an OnItemClickListener to open the chat that the user clicks on.
     */
    @Override
    protected void onStart(){
        super.onStart();

        mAdapter = new FirebaseListAdapter<ChatRoom>(this, ChatRoom.class, R.layout.list_item_chat,
                mUsers.child(currentUid).child("chats")) {
            @Override
            protected void populateView(View v, final ChatRoom model, int position) {
                TextView mTextView = (TextView) v.findViewById(R.id.chat_name_text_view);
                mTextView.setText(model.getName());
            }
        };

        mChatListView.setAdapter(mAdapter);

        mChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoom chatRoom =  mAdapter.getItem(position);
                Intent intent;
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.CHAT_KEY, chatRoom.getKey());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null)
            mAdapter.cleanup();
    }

    /**
     * Inflate the menu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_settings, menu);
        return true;
    }

    /**
     * When the settings option is clicked on, open SettingsActivity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When the user clicks to add a new chat, use the input from the EditText to make one and
     * store to the database.
     * @param view
     */
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

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                oneSignalId = userId;
            }
        });

        mChatRef.child(key).child("IDs").push().setValue(oneSignalId);

        mUsers.child(currentUid).child("chats").push().setValue(chatRoom);
        mNewRoom.setText("");
    }


}
