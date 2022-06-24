package com.devinstance.contape.DAL;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseConfig {

    private static FirebaseAuth mAuth;

    public static FirebaseAuth getFirebaseAuth(){
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

}
