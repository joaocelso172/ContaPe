package com.devinstance.contape.controller.group_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import com.devinstance.contape.DAL.GroupDAO;
import com.devinstance.contape.DAL.UserGroupDAO;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.GroupUser;
import com.devinstance.contape.model.UserGroup;
import com.devinstance.contape.R;

public class AddGroupMember extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteEmailAdd;

    private Button btnAddUsuarioGrupo;

    private CheckBox checkEhAdmin;

    private GroupDAO groupDAO = new GroupDAO();

    private GroupUser groupUser = new GroupUser();

    private UserGroupDAO userGroupDAO = new UserGroupDAO();

    private UserGroup userGroup = new UserGroup();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_integrante_grupo);

        autoCompleteEmailAdd = findViewById(R.id.autoCompleteEmailAdd);
        btnAddUsuarioGrupo = findViewById(R.id.btnAddUsuarioGrupo);
        checkEhAdmin = findViewById(R.id.checkBoxAdm);


        btnAddUsuarioGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUsuarioGrupo();
            }
        });
    }


    private void addUsuarioGrupo(){

        Group group = recuperaBundle();

        groupUser.setAdm(checkEhAdmin.isChecked());
        groupUser.setEmailUsuario(autoCompleteEmailAdd.getText().toString());
        groupUser.setNomeUsuario("Sem Nome");

        userGroup.setGrupoIdUsuario(group.getGrupoId());
        userGroup.setNomeGrupo(group.getNomeGrupo());

        groupDAO.salvarIntegranteGrupo(group, groupUser, checkEhAdmin.isSelected());
        userGroupDAO.gravarGrupoUsuarios(groupUser, userGroup);

        finish();
    }


    public Group recuperaBundle() {

        Intent intent = getIntent();
        Group groupUsuario = (Group) intent.getSerializableExtra("grupo");

        return groupUsuario;
    }
}
