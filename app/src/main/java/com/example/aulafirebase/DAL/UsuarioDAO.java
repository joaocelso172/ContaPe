package com.example.aulafirebase.DAL;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsuarioDAO {

    private boolean isSucess = false;
    private boolean usuarioExistente = false;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Referencia ao BD configurado no json
    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //Referencia ao branch de usuarios feito baseada numa referencia geral já existente
    private DatabaseReference usuarios = refenciaDb.child("usuarios");





    public boolean cadastrarUsuario(){

        //Cria uma Pk e insere os dados de um usuário ao BD
        //usuarios.push().setValue(usuario);

        // DatabaseReference usuarioPesquisa = usuariosAdd.child("-M-_OrEEiB4V42YjNQ_k");
        Query usuarioQuery = usuarios.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));

        /*
        //Ordena resultado de acordo com itens no BD
        Query usuarioQuery = usuarios.orderByKey().limitToFirst(2);
        Query usuarioQuery = usuarios.orderByKey().limitToLast(2);

        //Começa busca quando idade >= 35
        Query usuarioQuery = usuarios.orderByKey().orderByChild("idade").startAt(35);

        //Termina busca quando idade <= 22
        Query usuarioQuery = usuarios.orderByKey().orderByChild("idade").endAt(22);

        //Começa busca com 18 e termina com 22 (valores entre 18 e 22)
        Query usuarioQuery = usuarios.orderByKey().orderByChild("idade").startAt(18)
                .endAt(22);

       //Mostra nomes que só tem 'Ja', pode servir como busca
       Query usuarioQuery = usuarios.orderByKey().orderByChild("nome").startAt("Ja" + "\uf8ff")
                .endAt("Ja" + "\uf8ff");

       */


        usuarioQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               //   Usuario usuario = dataSnapshot.getValue(Usuario.class);
               //   Log.i("Logando", usuario.getNome());

                if (dataSnapshot.getValue() != null) {
                    Log.i("Logando", dataSnapshot.getValue().toString());
                    isSucess = true;
                    
                }else {
                    Log.i("Logando", "Parece que este usuário não está cadastrado... Será cadastrado agora.");
                //    isSucess = false;
                    salvarUsuario();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return isSucess;
    }

    private void salvarUsuario(){

        //Classe que receberá um usuário
        Usuario usuario = new Usuario();

        //Pega display name e email do usuário não cadastrado
            usuario.setNome("Sem Nome");
            usuario.setEmail(mAuth.getCurrentUser().getEmail());

        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            String idUsuario = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
            usuarios.child(idUsuario)
                    .setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.i("Logando", "Cadastrado com sucesso, " + Base64Custom.decodificarBase64(idUsuario));
                    }
                }
            });

            isSucess = true;

        }catch (Exception e){
            Log.i("Logando", e.getMessage());
            isSucess = false;
        }

    }

    public Usuario getUsuarioCadastradoFirebase(){
        //Cria uma Pk e insere os dados de um usuário ao BD
        //usuarios.push().setValue(usuario);

        Usuario usuario = new Usuario();

        // DatabaseReference usuarioPesquisa = usuariosAdd.child("-M-_OrEEiB4V42YjNQ_k");
        Query usuarioQuery = usuarios.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));

        usuarioQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Log.i("Logando", dataSnapshot.getValue().toString());
                    usuario.setNome(dataSnapshot.getValue().toString());
                    usuario.setEmail(mAuth.getCurrentUser().getEmail());
                    usuario.setCadastrado(true);

                }else {
                    Log.i("Logando", "Parece que este usuário não está cadastrado...");

                    if (mAuth.getCurrentUser().getDisplayName() == null) {
                        usuario.setNome(null);
                    }else usuario.setNome(mAuth.getCurrentUser().getDisplayName());

                    usuario.setEmail(mAuth.getCurrentUser().getEmail());
                    usuario.setCadastrado(false);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return usuario;
    }

    public void salvarUser(Usuario usuario){

        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            String idUsuario = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
            usuarios.child(idUsuario)
                    .setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Log.i("Logando", "Cadastrado com sucesso, " + Base64Custom.decodificarBase64(idUsuario) + ", " + usuario.getNome());
                    }
                }
            });

            isSucess = true;

        }catch (Exception e){
            Log.i("Logando", e.getMessage());
            isSucess = false;
        }

    }

}
