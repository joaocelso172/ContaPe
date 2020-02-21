package com.example.aulafirebase.DAL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UsuariosDAO {
    //Autentificacao
    private FirebaseAuth autentificacao = FirebaseConfig.getFirebaseAuth();
    //Referencia ao nó usuários
    private DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
    //Referencia que será usada como nó do usuário cadastrado
    private DatabaseReference usuariosAdd;
    //objeto Usuario que receberá o usuario logado
    private Usuario usuario = new Usuario();
    //Boolean que registra se foi executado com sucesso o cadastro ou login
    private Boolean isSucess = false;



    public Boolean getOrSubUsuario(){
        //Codifica ID do usuário
        String idUsuario = Base64Custom.codificarBase64(autentificacao.getCurrentUser().getEmail());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        usuariosAdd = usuariosRef.child(idUsuario);
        //Inicia o eventListener para buscar usuários
        usuariosAdd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Se existir informação com o email codificado, pega valor
                if (dataSnapshot.exists()) {
                    usuario = dataSnapshot.getValue(Usuario.class);
                    isSucess = true;
                } //Se não, cadastra
                else {
                    Log.i("Logando", "Usuario não cadastrado");
                    if(usuariosRef.child(idUsuario)
                            .setValue(usuario).isSuccessful()){
                        isSucess = true;
                    }else isSucess = false;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       return isSucess;
    }
}


