package com.example.aulafirebase.DAL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.UsuarioGrupo;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuarioGrupoDAO {

    private final DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");

    private final DatabaseReference gruposRef = FirebaseDatabase.getInstance().getReference().child("grupos");

    private List<UsuarioGrupo> idGrupos = new ArrayList<>();

    private List<Grupo> grupos = new ArrayList<>();

    public void gravarGrupoOwnerUsuario(Grupo grupo){ //Método responsável por registrar grupoID no grupo criado pelo usuário

        UsuarioGrupo usuarioGrupo = new UsuarioGrupo();

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        usuarioGrupo.setGrupoIdUsuario(grupo.getGrupoId());
        usuarioGrupo.setNomeGrupo(grupo.getNomeGrupo());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        getDatabase(idUsuario, usuarioGrupo).setValue(usuarioGrupo);
    }

    public void gravarGrupoUsuarios(String emailUsuarioAdicionado, Grupo grupo){ //Método responsável por registrar grupoID no usuário adicionado a um grupo

        String idUsuario = Base64Custom.codificarBase64(emailUsuarioAdicionado);
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
       // usuariosRef.child(idUsuario).child("grupos").setValue(grupo.getGrupoId());

    }

    private DatabaseReference getDatabase (String idUsuario, UsuarioGrupo usuarioGrupo){

        DatabaseReference grupoUsuario = usuariosRef.child(idUsuario).child("UsuarioGrupo").child(usuarioGrupo.getGrupoIdUsuario());

        return grupoUsuario;
    }

    private DatabaseReference getDatabase (String idUsuario){

        DatabaseReference grupoUsuario = usuariosRef.child(idUsuario).child("UsuarioGrupo");

        return grupoUsuario;
    }

    public List<Grupo> getGruposUsuario(){

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        grupos.clear();

        //Para cada ID no nó usuário, adiciona à lista idGrupos
        getDatabase(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    UsuarioGrupo usuarioGrupo = dados.getValue(UsuarioGrupo.class);
               //     idGrupos.add(usuarioGrupo);
                    //Para cada ID encontrado no nó usuário, faz a busca daquele item no nó de grupos
                    gruposRef.child(usuarioGrupo.getGrupoIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotxs) {
                                Grupo grupo = dataSnapshotxs.getValue(Grupo.class);
                                grupo.setGrupoId(dataSnapshotxs.getKey());
                                grupos.add(grupo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return grupos;
    }

}
