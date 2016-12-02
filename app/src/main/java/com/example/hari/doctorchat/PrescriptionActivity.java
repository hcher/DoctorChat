package com.example.hari.doctorchat;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class PrescriptionActivity extends AppCompatActivity {
    public static final String CHAT_KEY = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mChats = database.getReference("chats");
    private DatabaseReference mChat;
    private DatabaseReference mUsers = database.getReference("users");
    private DatabaseReference mUsernames = database.getReference("usernames");
    private String currentUserRole;
    private String currentUserName;
    private String chatName;
    private String medication;
    private String instructions;
    private EditText mNewPresctiptionField;
    private EditText mMessage;
    private FirebaseListAdapter<Prescription> mPresciptionAdapter;
    private ListView mPresciptionsListView;
    private Button mAddPrescriptionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prescriptions);

        final String chatKey = getIntent().getStringExtra(CHAT_KEY);
        Log.d("chat key: ", chatKey);


        mPresciptionsListView = (ListView) findViewById(R.id.list_view_prescriptions);

        mAddPrescriptionButton = (Button) findViewById(R.id.add_prescription);


        Log.d("key", chatKey);
        mChat = mChats.child(chatKey);

        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserRole = dataSnapshot.getValue(String.class);
                        setLayout();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("alias")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUserName = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();

        mPresciptionAdapter = new FirebaseListAdapter<Prescription>(this, Prescription.class,
                R.layout.list_item_prescription, mChat.child("prescriptions")) {
            @Override
            protected void populateView(View v, Prescription model, int position) {
                TextView doctorTextView = (TextView) v.findViewById(R.id.doctor_text_view);
                doctorTextView.setText(model.doctor);

                TextView medicationTextView = (TextView) v.findViewById(R.id.medication_text_view);
                medicationTextView.setText(model.medication);

                TextView instructionTextView = (TextView) v.findViewById(R.id.instruction_text_view);
                instructionTextView.setText(model.instructions);
            }

        };


        //mChatListView.getId();
        mPresciptionsListView.setAdapter(mPresciptionAdapter);

        Log.d("set", "SET");
    }

    public void setLayout(){
        if(currentUserRole.equals("patient"))
            mAddPrescriptionButton.setVisibility(View.INVISIBLE);
    }

    public void newPrescription(View view){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PrescriptionActivity.this);
        alertBuilder.setTitle("What Medication?");

        final EditText input = new EditText(PrescriptionActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);


        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                medication = input.getText().toString();
                setInstructions();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertBuilder.show();

    }

    public void setInstructions(){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PrescriptionActivity.this);
        alertBuilder.setTitle("Type All Instructions");

        final EditText input = new EditText(PrescriptionActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alertBuilder.setView(input);


        alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                instructions = input.getText().toString();
                addPrescriptionToDatabase();
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertBuilder.show();
    }

    public void addPrescriptionToDatabase(){
        Prescription prescription = new Prescription(currentUserName, medication, instructions);
        mChat.child("prescriptions").push().setValue(prescription);
    }

}
