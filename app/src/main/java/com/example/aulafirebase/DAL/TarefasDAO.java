package com.example.aulafirebase.DAL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TarefasDAO {

    //Referencia ao BD configurado no json
    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //Usuario Logado
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente
    private DatabaseReference tarefas = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("tarefas");

    public void buscarTarefa(){

        //DatabaseReference tarefasQuery = tarefas.child("comprar pao");

        Query tarefasQuery = tarefas.orderByChild("desc");

        tarefasQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    Log.i("Tarefas", "tarefa não encontrada");
                }else {
                    Log.i("Tarefas", "tarefa encontrada! " + dataSnapshot.getValue());
                //    Log.i("Tarefas", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
