package com.example.aulafirebase.DAL;


import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.Convite;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConviteDAO {

    private Boolean haConvites = false;
    private Convite convite = new Convite();

    private final DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");

    public void setConviteUsuario(Convite convite){

        String idUsuario = Base64Custom.codificarBase64(convite.getEmailConvidado());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        DatabaseReference getUsuario = usuariosRef.child(idUsuario).child("convites");

        getUsuario.push().setValue(convite);

    }

    public Boolean verificaConvites(){

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        DatabaseReference getUsuario = usuariosRef.child(idUsuario).child("convites");

        getUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                haConvites = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return haConvites;
    }

    public Convite getConvite(){

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        DatabaseReference getUsuario = usuariosRef.child(idUsuario).child("convites");

        getUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               convite = dataSnapshot.getValue(Convite.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return convite;
    }


}
