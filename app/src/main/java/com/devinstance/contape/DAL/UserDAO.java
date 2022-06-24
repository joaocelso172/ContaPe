package com.devinstance.contape.DAL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.devinstance.contape.model.User;
import com.devinstance.contape.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDAO {

    private boolean isSucess = false;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference usuarioQuery;

    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
    //Referencia ao branch de usuarios feito baseada numa referencia geral já existente
    private final DatabaseReference usuarios = refenciaDb.child("usuarios");
    //EventListener deve ser sempre fechado
    private ValueEventListener valueEventListener;

    private ValueEventListener outroValeu;


    public boolean cadastrarUsuario(){

        //Cria uma Pk e insere os dados de um usuário ao BD
        //usuarios.push().setValue(usuario);

        // DatabaseReference usuarioPesquisa = usuariosAdd.child("-M-_OrEEiB4V42YjNQ_k");
        usuarioQuery = usuarios.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));

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


        outroValeu = usuarioQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               //   Log.i("Logando", usuario.getNome());

                if (dataSnapshot.getValue() != null) {
                    Log.i("Logando", dataSnapshot.getValue().toString());
                    User user = dataSnapshot.getValue(User.class);
                    Log.i("Logando", String.valueOf(user.getSaldoDisponivel()));
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
        User user = new User();

        //Pega display name e email do usuário não cadastrado
            user.setNome("Sem Nome");
            user.setEmail(mAuth.getCurrentUser().getEmail());

        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            String idUsuario = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
            usuarios.child(idUsuario)
                    .setValue(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Log.i("Logando", "Cadastrado com sucesso, " + Base64Custom.decodificarBase64(idUsuario));
                            //removerEventListener();
                        }
                    });

            isSucess = true;

        }catch (Exception e){
            Log.i("Logando", e.getMessage());
            isSucess = false;
        }

    }

    public User getUsuarioCadastradoFirebase(){
        //Cria uma Pk e insere os dados de um usuário ao BD
        //usuarios.push().setValue(usuario);

        User user = new User();

        // DatabaseReference usuarioPesquisa = usuariosAdd.child("-M-_OrEEiB4V42YjNQ_k");
        usuarioQuery = usuarios.child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));

        valueEventListener = usuarioQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    Log.i("Logando", dataSnapshot.getValue().toString());
                    user.setNome(dataSnapshot.getValue().toString());
                    user.setEmail(mAuth.getCurrentUser().getEmail());
                    user.setCadastrado(true);

                }else {
                    Log.i("Logando", "Parece que este usuário não está cadastrado...");

                    if (mAuth.getCurrentUser().getDisplayName() == null) {
                        user.setNome(null);
                    }else user.setNome(mAuth.getCurrentUser().getDisplayName());

                    user.setEmail(mAuth.getCurrentUser().getEmail());
                    user.setCadastrado(false);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return user;
    }

    public void salvarUser(User user){

        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            String idUsuario = Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail());
            usuarios.child(idUsuario)
                    .setValue(user).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Log.i("Logando", "Cadastrado com sucesso, " + Base64Custom.decodificarBase64(idUsuario) + ", " + user.getNome());
                            //removerEventListener();
                        }
                    });

            isSucess = true;

        }catch (Exception e){
            Log.i("Logando", e.getMessage());
            isSucess = false;
        }

    }

    public void removerEventListener(){
        usuarioQuery.removeEventListener(outroValeu);
        usuarioQuery.removeEventListener(valueEventListener);

    }

}
