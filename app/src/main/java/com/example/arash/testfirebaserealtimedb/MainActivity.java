package com.example.arash.testfirebaserealtimedb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

//    TextView mConditionTextView;
    Button mSubmit, signOutButton;

    SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;
    private static final String TAG="SignInActivity";
    private static final int RC_SIGN_IN= 9001;

    DatabaseReference mRootRef= FirebaseDatabase.getInstance().getReference();
    DatabaseReference mAddress= mRootRef.child("address");
    DatabaseReference mTime= mRootRef.child("time");
    DatabaseReference mSuggestedBy= mRootRef.child("suggestedby");
    DatabaseReference mRegisteredAt= mRootRef.child("registerdat");

    TextView address, time, suggestedBy, registeredAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient= new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        mConditionTextView= (TextView)findViewById(R.id.textViewCondition);

        address= (TextView)findViewById(R.id.address);
        time= (TextView)findViewById(R.id.time);
        mSubmit= (Button)findViewById(R.id.submit);

        suggestedBy= (TextView) findViewById(R.id.userName);
        registeredAt= (TextView) findViewById(R.id.registeredAt);

        signInButton=(SignInButton)findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        signOutButton= (Button) findViewById(R.id.signoutbutton);
        signOutButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAddress.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text= dataSnapshot.getValue(String.class);
                address.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text= dataSnapshot.getValue(String.class);
                time.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRegisteredAt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text= dataSnapshot.getValue(String.class);
                registeredAt.setText("Registered At: " + text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSuggestedBy.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text= dataSnapshot.getValue(String.class);
                suggestedBy.setText(text);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataOnCloud();
                enableDisableWidgets();

            }
        });
    }

    private void saveDataOnCloud(){
        mAddress.setValue(address.getText().toString());
        mTime.setValue(time.getText().toString());
        mSuggestedBy.setValue(suggestedBy.getText().toString());
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        mRegisteredAt.setValue(currentDateTimeString);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.d(TAG, "hableSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            suggestedBy.setText( acct.getDisplayName());
            enableDisableWidgets();
        }else{
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sign_in_button:
                signInGoogle();
                break;

            case R.id.signoutbutton:
                signOut();
                break;
        }

    }

    private void signInGoogle(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut (mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                suggestedBy.setText("            ");
                enableDisableWidgets();
            }
        });
    }

    private void enableDisableWidgets(){
        address.setEnabled(! address.isEnabled());
        time.setEnabled(! time.isEnabled());
        mSubmit.setEnabled(! mSubmit.isEnabled());
        signOutButton.setEnabled(! signOutButton.isEnabled());
    }
}
