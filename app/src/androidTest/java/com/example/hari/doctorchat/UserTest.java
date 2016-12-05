package com.example.hari.doctorchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.FALSE;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {


    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mUsers = database.getReference("users");

    @Test
    public void readDatabase() throws Exception {
        final CountDownLatch writeSignal = new CountDownLatch(1);

        mUsers.child("2FZiOKnjSHTeWhi9m3YyQBmPQr82").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assertEquals("har", user.getAlias());
                assertEquals("Patient", user.getRole());
                assertEquals(FALSE, user.getNotification());
                assertEquals(4, user.getChats().size());
                writeSignal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);


    }


}
