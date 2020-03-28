package com.example.aulafirebase.DAL;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Controller.ActivityMovimentacao.AddGanhoActivity;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.GrupoUsuario;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.Model.UsuarioGrupo;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    private Grupo grupo = new Grupo();
    private UsuariosDAO usuariosDAO = new UsuariosDAO();

    private List<GrupoUsuario> grupoUsuariosList = new ArrayList<>();

    private UsuarioGrupoDAO usuarioGrupoDAO = new UsuarioGrupoDAO();

   /* private List<UsuarioGrupo> gruposUsuario = usuarioGrupoDAO.getGruposUsuario();

    private List<Grupo> grupos = listarGrupos();*/
    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //Referencia ao branch de usuarios feito baseada numa referencia geral já existente
    private DatabaseReference gruposRef = refenciaDb.child("grupos");

    public void setGrupo(Grupo grupo){

        grupo.setGrupoId(gruposRef.push().getKey());

        gruposRef.child(grupo.getGrupoId()).setValue(grupo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                usuarioGrupoDAO.gravarGrupoOwnerUsuario(grupo);
            }
        });
    }

    public void atualizarGrupo (Grupo grupo){

        gruposRef.child(grupo.getGrupoId()).setValue(grupo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                usuarioGrupoDAO.gravarGrupoOwnerUsuario(grupo);
            }
        });
    }

    public Grupo getGrupo (Grupo grupo1){

//        Query queryGrupos = gruposRef.orderByKey().orderByChild("nomeGrupo").startAt(nomeGrupo + "\uf8ff")
//                .endAt(nomeGrupo + "\uf8ff");

        gruposRef.child(grupo1.getGrupoId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                grupo = dataSnapshot.getValue(Grupo.class);
                grupo.setGrupoId(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* grupo = dataSnapshot.getValue(Grupo.class);
        grupo.setGrupoId(dataSnapshot.getKey());*/


        return grupo;
    }

    public void salvarIntegranteGrupo(Grupo grupo, GrupoUsuario grupoUsuario, Boolean isAdm){

        String idUsuario = Base64Custom.codificarBase64(grupoUsuario.getEmailUsuario());

        gruposRef.child(grupo.getGrupoId()).child("membros").child(idUsuario).setValue(grupoUsuario);

    }

    public void retornarIntegrantes(Grupo grupo,List<String> listEmail, ArrayAdapter<String> arrayAdapter){


        gruposRef.child(grupo.getGrupoId()).child("membros").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    GrupoUsuario grupoUsuario = dados.getValue(GrupoUsuario.class);
                    if (grupoUsuario.getEmailUsuario() != FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail()) listEmail.add(grupoUsuario.getEmailUsuario());
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

//    public List<Grupo> listarGrupos (){
//
//        gruposUsuario = usuarioGrupoDAO.getGruposUsuario();
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Grupo grupoUsuario = dataSnapshot.getValue(Grupo.class);
//                grupos.add(grupoUsuario);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        if (!gruposUsuario.isEmpty()){
//            for( UsuarioGrupo relacaoGrupo : gruposUsuario ){
//            gruposRef.child(relacaoGrupo.getGrupoIdUsuario()).addValueEventListener(valueEventListener);
//          }
//        }
//
//       /*
//       gruposRef.startAt(gruposUsuario.get(0).getGrupoIdUsuario() + "\uf8ff").endAt(gruposUsuario.get(0).getGrupoIdUsuario() + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        */
//
//        /*gruposRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                listaGrupos.clear();
//
//                for(DataSnapshot dados: dataSnapshot.getChildren()){
//                    Grupo grupoLista = dados.getValue(Grupo.class);
//                    grupo.setGrupoId(dados.getKey());
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });*/
//
//        return grupos;
//    }



}
