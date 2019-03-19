package com.example.manantrivedi.emergencymedicalservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity{

    private EditText u_name;
    private EditText u_DOB;
    private EditText u_email;
    private EditText CName;
    private EditText CNumber;
    private Button submit;

    private Firebase url;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Firebase.setAndroidContext(this);

        u_name = (EditText) findViewById(R.id.u_name);
        u_DOB = (EditText) findViewById(R.id.u_DOB);
        u_email = (EditText) findViewById(R.id.u_email);
        CName = (EditText) findViewById(R.id.CName);
        CNumber = (EditText) findViewById(R.id.CNumber);
        submit = (Button) findViewById(R.id.submit);


        //firebaseDatabase = FirebaseDatabase.getInstance();
       // databaseReference = firebaseDatabase.getReference("Test");

        url = new Firebase("https://emsdatabase-fe6af.firebaseio.com/");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // postData();
                String name = u_name.getText().toString();
                String dob = u_DOB.getText().toString();
                String email = u_email.getText().toString();
                String contact = CName.getText().toString();
                String number = CNumber.getText().toString();

                Firebase urlName = url.child("Name");
                urlName.setValue(name);
                Firebase urldob = url.child("Date of Birth");
                urldob.setValue(dob);
                Firebase urlEmail = url.child("Email");
                urlEmail.setValue(email);
                Firebase urlContact = url.child("Contact Name");
                urlContact.setValue(contact);
                Firebase urlNumber = url.child("Contact Number");
                urlNumber.setValue(number);
            }
        });


        }

}






