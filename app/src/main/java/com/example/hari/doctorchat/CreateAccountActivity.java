//package com.example.hari.doctorchat;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//public class MainActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}

package com.example.hari.doctorchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mUsernameField;
    private EditText mPasswordField;
    private EditText mRetypePasswordField;
    private EditText mScreenNameField;
    private RadioGroup mRoleSelector;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);



        mUsernameField = (EditText) findViewById(R.id.username);
        mScreenNameField = (EditText) findViewById(R.id.screen_name);
        mPasswordField = (EditText) findViewById(R.id.password);
        mRetypePasswordField = (EditText) findViewById(R.id.retype_password);
        mRoleSelector = (RadioGroup) findViewById(R.id.select_role);

        findViewById(R.id.create_account_button)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!validateCreate()) {
                            return;
                        }
                        if(!(mPasswordField.getText().toString().equals(mRetypePasswordField.getText().toString()))){
                            Toast.makeText(getApplicationContext(), "Passwords did not match",
                                    Toast.LENGTH_SHORT).show();
                            mPasswordField.setText("");
                            mRetypePasswordField.setText("");
                        }
                        else if(mPasswordField.getText().toString().length() < 6){
                            Toast.makeText(getApplicationContext(), "Password must be at least 6 characters",
                                    Toast.LENGTH_SHORT).show();
                            mPasswordField.setText("");
                            mRetypePasswordField.setText("");
                        }
                        else {
                            createAccount(mUsernameField.getText().toString(), mPasswordField.getText().toString());
                        }

                    }
                });




        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("signed in", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("signed out", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    public void onRadioButtonClicked(View view){
        if(view.getId() == R.id.patient) {
            if (((RadioButton) view).isChecked())
                role = "patient";
        }
        else if (view.getId() == R.id.doctor) {
            if (((RadioButton) view).isChecked())
                role = "doctor";
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private static final String TAG = "Debug";
    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);


        //showProgressDialog();

        // [START create_user_with_email]
        Log.d(email,password);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_SHORT).show();


                            signIn(email, password);
                            updateDatabase();
                            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                            startActivity(intent);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        //showProgressDialog();

        // [START create_user_with_email]
        Log.d(email,password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateCreate() {
        boolean valid = true;

        String email = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mUsernameField.setError("Required.");
            valid = false;
        } else {
            mUsernameField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String retypePassword = mRetypePasswordField.getText().toString();
        if (TextUtils.isEmpty(retypePassword)) {
            mRetypePasswordField.setError("Required.");
            valid = false;
        } else {
            mRetypePasswordField.setError(null);
        }

        if(mRoleSelector.getCheckedRadioButtonId() == -1){
            valid = false;
            RadioButton doctorButton = (RadioButton) findViewById(R.id.doctor);
            doctorButton.setError("Select Role");
        }
        return valid;
    }

    public void updateDatabase(){
        User user = new User(mScreenNameField.getText().toString(), role, Boolean.TRUE, null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mUsers = database.getReference("users");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUsers.child(firebaseUser.getUid()).setValue(user);

        DatabaseReference mUsernameMap = database.getReference("usernames");
        mUsernameMap.child(mScreenNameField.getText().toString()).child("Uid")
                .setValue(firebaseUser.getUid());
    }

}

