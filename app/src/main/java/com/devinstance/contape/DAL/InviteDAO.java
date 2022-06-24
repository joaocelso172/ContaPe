package com.devinstance.contape.DAL;


import androidx.annotation.NonNull;

import com.devinstance.contape.model.Invite;
import com.devinstance.contape.helper.Base64Custom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InviteDAO {

    private Boolean haConvites = false;
    private Invite invite = new Invite();

    private final DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");

    public void setConviteUsuario(Invite invite){

        String idUsuario = Base64Custom.codificarBase64(invite.getEmailConvidado());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        DatabaseReference getUsuario = usuariosRef.child(idUsuario).child("convites");

        getUsuario.push().setValue(invite);

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

    public Invite getConvite(){

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        DatabaseReference getUsuario = usuariosRef.child(idUsuario).child("convites");

        getUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               invite = dataSnapshot.getValue(Invite.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return invite;
    }


}
