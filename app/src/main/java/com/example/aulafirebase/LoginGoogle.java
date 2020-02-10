package com.example.aulafirebase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginGoogle extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;
    static String emailLogado;

    public GoogleSignInClient configurarGoogle(String string, Context c) {

        mAuth = FirebaseAuth.getInstance();
        Log.i("Logando", "Comecou");

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder().requestIdToken(string)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(c, googleSignInOptions);
        return mGoogleSignInClient;
    }


 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Logando", "onResult override");
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount conta = task.getResult(ApiException.class);

                if (conta != null) firebaseAuthAuthWithGoogle(conta);

                Log.i("Logando", "Override Sucesso");
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }*/


     void firebaseAuthAuthWithGoogle(GoogleSignInAccount conta) {
       //  Log.i("Logando", "firebase: " + conta.getId());
         AuthCredential credencial = GoogleAuthProvider
                .getCredential(conta.getIdToken(), null);
        mAuth.signInWithCredential(credencial)
                .addOnCompleteListener(this, task ->{
                    if (task.isSuccessful()){
                        Log.i("Logando", "Login bem-sucedido");
                        user = mAuth.getCurrentUser();
                        if (user != null){
                            emailLogado = user.getEmail();
                //            Log.i("Logando", emailLogado);

                        }
                       // updateUI(user);
                    }else Log.i("Logando", "Falha no Login");
                });
    }

     void updateUI(FirebaseUser usuario) {

        if (usuario != null){

            emailLogado = usuario.getEmail();
            Log.i("Logando", emailLogado);
          //  Toast.makeText(c, "Bem vindo, " + emailLogado + "!", Toast.LENGTH_SHORT).show();

        }
    }

    public void Logout(){

        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this,
                        task ->
                                Log.i("Logando", "Deslogado"));
    }

}
