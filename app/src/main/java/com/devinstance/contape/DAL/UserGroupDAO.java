package com.devinstance.contape.DAL;

import androidx.annotation.NonNull;

import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.GroupUser;
import com.devinstance.contape.model.UserGroup;
import com.devinstance.contape.helper.Base64Custom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserGroupDAO {

    private final DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");

    private final DatabaseReference gruposRef = FirebaseDatabase.getInstance().getReference().child("grupos");

    private List<UserGroup> idGrupos = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    private Boolean status = false;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void gravarGrupoOwnerUsuario(Group group){ //Método responsável por registrar grupoID no grupo criado pelo usuário

        UserGroup userGroup = new UserGroup();

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        userGroup.setGrupoIdUsuario(group.getGrupoId());
        userGroup.setNomeGrupo(group.getNomeGrupo());
        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        getDatabase(idUsuario, userGroup).setValue(userGroup);
    }

    public void gravarGrupoUsuarios(GroupUser groupUser, UserGroup userGroup){ //Método responsável por registrar grupoID no usuário adicionado a um grupo

        String idUsuario = Base64Custom.codificarBase64(groupUser.getEmailUsuario());

        //Usa um dbReference novo (usuariosAdd para receber o valor usuariosRef.chil) pode ser alterado. Passa como parametro o email codificado
        getDatabase(idUsuario, userGroup).setValue(userGroup);

    }

    private DatabaseReference getDatabase (String idUsuario, UserGroup userGroup){

        DatabaseReference grupoUsuario = usuariosRef.child(idUsuario).child("UsuarioGrupo").child(userGroup.getGrupoIdUsuario());

        return grupoUsuario;
    }

    private DatabaseReference getDatabase (String idUsuario){

        DatabaseReference grupoUsuario = usuariosRef.child(idUsuario).child("UsuarioGrupo");

        return grupoUsuario;
    }

    public Boolean getGruposUsuario(List<Group> groups){
        status = false;

        String idUsuario = Base64Custom.codificarBase64(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        groups.clear();

        //Para cada ID no nó usuário, adiciona à lista idGrupos
        getDatabase(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) status = true;

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        UserGroup userGroup = dados.getValue(UserGroup.class);
                        //     idGrupos.add(usuarioGrupo);
                        //Para cada ID encontrado no nó usuário, faz a busca daquele item no nó de grupos
                        gruposRef.child(userGroup.getGrupoIdUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotxs) {
                                    Group group = dataSnapshotxs.getValue(Group.class);
                                    group.setGrupoId(dataSnapshotxs.getKey());
                                    groups.add(group);

                                    if (groups.size() == dataSnapshot.getChildrenCount())
                                        status = true;
                                    else status = false;
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

        return status;
    }

}
