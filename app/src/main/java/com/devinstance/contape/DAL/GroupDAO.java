package com.devinstance.contape.DAL;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.GroupUser;
import com.devinstance.contape.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupDAO {

    private Group group = new Group();
    private UsersDAO usersDAO = new UsersDAO();

    private List<GroupUser> grupoUsuariosList = new ArrayList<>();

    private UserGroupDAO userGroupDAO = new UserGroupDAO();

   /* private List<UsuarioGrupo> gruposUsuario = usuarioGrupoDAO.getGruposUsuario();

    private List<Grupo> grupos = listarGrupos();*/
    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //Referencia ao branch de usuarios feito baseada numa referencia geral já existente
    private DatabaseReference gruposRef = refenciaDb.child("grupos");

    public void setGrupo(Group group){

        group.setGrupoId(gruposRef.push().getKey());

        gruposRef.child(group.getGrupoId()).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userGroupDAO.gravarGrupoOwnerUsuario(group);
            }
        });
    }

    public void atualizarGrupo (Group group){

        gruposRef.child(group.getGrupoId()).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userGroupDAO.gravarGrupoOwnerUsuario(group);
            }
        });
    }

    public Group getGrupo (Group group1){

//        Query queryGrupos = gruposRef.orderByKey().orderByChild("nomeGrupo").startAt(nomeGrupo + "\uf8ff")
//                .endAt(nomeGrupo + "\uf8ff");

        gruposRef.child(group1.getGrupoId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                group = dataSnapshot.getValue(Group.class);
                group.setGrupoId(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       /* grupo = dataSnapshot.getValue(Grupo.class);
        grupo.setGrupoId(dataSnapshot.getKey());*/


        return group;
    }

    public void salvarIntegranteGrupo(Group group, GroupUser groupUser, Boolean isAdm){

        String idUsuario = Base64Custom.codificarBase64(groupUser.getEmailUsuario());

        gruposRef.child(group.getGrupoId()).child("membros").child(idUsuario).setValue(groupUser);

    }

    public void retornarIntegrantes(Group group, List<String> listEmail, ArrayAdapter<String> arrayAdapter){


        gruposRef.child(group.getGrupoId()).child("membros").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    GroupUser groupUser = dados.getValue(GroupUser.class);
                    if (groupUser.getEmailUsuario() != FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail()) listEmail.add(groupUser.getEmailUsuario());
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
