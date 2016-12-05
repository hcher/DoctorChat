package com.example.hari.doctorchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

/**
 * Created by hari on 12/3/2016.
 */

public class SettingsActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mUsers = database.getReference("users");
    private boolean notification;
    private Switch toggle;


    /**
     * Set up the toggle based on the current state of the user's preference
     * @param savedInstanceState, the Bundle passed through
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        setContentView(R.layout.settings_activity);
                        toggle = (Switch) findViewById(R.id.notification_switch);
                        notification = dataSnapshot.getValue(Boolean.class);
                        toggle.setChecked(notification);

                        if (notification){
                            OneSignal.setSubscription(true);
                        }
                        else{
                            OneSignal.setSubscription(false);
                        }

                        setOnClickListener();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    /**
     * When the toggle is changed, subscribe or remove subscription from notifications and update
     * the database.
     */
    public void setOnClickListener(){
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OneSignal.setSubscription(true);
                    mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("notification").setValue(true);
                } else {
                    OneSignal.setSubscription(false);
                    mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("notification").setValue(false);
                }
            }
        });
    }
}
