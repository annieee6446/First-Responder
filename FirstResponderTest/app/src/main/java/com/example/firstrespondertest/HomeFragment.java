// this fragment will contain the option to add user contact info and login

package com.example.firstrespondertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment  {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListner;


    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null){
//                    // UNSURE (Listens for if the user logs in and the returns to the main page)?
//                    Intent intent = new Intent(getApplication(), MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
                if (user != null){
                    //updateUI(user);
                }

            }
        };
        mAuth = FirebaseAuth.getInstance();

        Button mReg = view.findViewById(R.id.regButton);
        Button mLogin= view.findViewById(R.id.loginButton);
        Button mLogout = view.findViewById(R.id.logoutButton);


        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(),RegistrationActivity.class);
                startActivity(intent);
                return;
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(),LoginActivity.class);
                startActivity(intent);
                return;
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mAuth.getInstance().signOut();
            }
        });

        return view;
    }


}
