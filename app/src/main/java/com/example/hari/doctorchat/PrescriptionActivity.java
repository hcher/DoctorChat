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
    private String currentUserName;
    private String medication;
    private String instructions;
    private FirebaseListAdapter<Prescription> mPresciptionAdapter;
    private ListView mPresciptionsListView;
    private Button mAddPrescriptionButton;


    /**
     * Initialize the DatabaseReference to the chat the prescriptions are for, and initialize
     * the current user's role and alias.
     * @param savedInstanceState, the Bundle passed through
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final String chatKey = getIntent().getStringExtra(CHAT_KEY);
        mChat = mChats.child(chatKey);


        mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Get the user's role and set the layout based on it.
                     * @param dataSnapshot, the data that is received from the database
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String currentUserRole = dataSnapshot.getValue(String.class);
                        setContentView(R.layout.prescriptions);
                        mPresciptionsListView = (ListView) findViewById(R.id.list_view_prescriptions);
                        mAddPrescriptionButton = (Button) findViewById(R.id.add_prescription);
                        setLayout(currentUserRole);
                        start();
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

    /**
     * If the user is a patient, hide the button to add prescriptions
     * @param currentUserRole, the role of the current user
     */
    public void setLayout(String currentUserRole){
        if(currentUserRole.equals("patient"))
            mAddPrescriptionButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Assign the adapter and populate it based on the prescriptions in the chat that called this class.
     */
    public void start(){
        super.onStart();

        mPresciptionAdapter = new FirebaseListAdapter<Prescription>(this, Prescription.class,
                R.layout.list_item_prescription, mChat.child("prescriptions")) {
            @Override
            protected void populateView(View v, Prescription model, int position) {
                TextView doctorTextView = (TextView) v.findViewById(R.id.doctor_text_view);
                doctorTextView.setText(model.getDoctor());

                TextView medicationTextView = (TextView) v.findViewById(R.id.medication_text_view);
                medicationTextView.setText(model.getMedication());

                TextView instructionTextView = (TextView) v.findViewById(R.id.instruction_text_view);
                instructionTextView.setText(model.getInstructions());
            }

        };
        mPresciptionsListView.setAdapter(mPresciptionAdapter);
    }


    /**
     * When the new prescription button is pressed, ask which medication through an Alert Dialog.
     * @param view
     */
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

    /**
     * Ask what the instructions are for the medication through an Alert Dialog.
     */
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

    /**
     * Create a new prescription object based on the input and add it to the database.
     */
    public void addPrescriptionToDatabase(){
        Prescription prescription = new Prescription(currentUserName, medication, instructions);
        mChat.child("prescriptions").push().setValue(prescription);
    }

}
