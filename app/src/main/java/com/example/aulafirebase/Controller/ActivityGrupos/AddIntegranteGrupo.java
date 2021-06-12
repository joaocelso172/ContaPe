package com.example.aulafirebase.Controller.ActivityGrupos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.DAL.UsuarioGrupoDAO;
import com.example.aulafirebase.DAL.UsuariosDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.GrupoUsuario;
import com.example.aulafirebase.Model.UsuarioGrupo;
import com.example.aulafirebase.R;
import com.example.aulafirebase.RecuperaBundle;

public class AddIntegranteGrupo extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteEmailAdd;

    private Button btnAddUsuarioGrupo;

    private CheckBox checkEhAdmin;

    private GrupoDAO grupoDAO = new GrupoDAO();

    private GrupoUsuario grupoUsuario = new GrupoUsuario();

    private UsuarioGrupoDAO usuarioGrupoDAO = new UsuarioGrupoDAO();

    private UsuarioGrupo usuarioGrupo = new UsuarioGrupo();

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

        Grupo grupo = recuperaBundle();

        grupoUsuario.setAdm(checkEhAdmin.isChecked());
        grupoUsuario.setEmailUsuario(autoCompleteEmailAdd.getText().toString());
        grupoUsuario.setNomeUsuario("Sem Nome");

        usuarioGrupo.setGrupoIdUsuario(grupo.getGrupoId());
        usuarioGrupo.setNomeGrupo(grupo.getNomeGrupo());

        grupoDAO.salvarIntegranteGrupo(grupo, grupoUsuario, checkEhAdmin.isSelected());
        usuarioGrupoDAO.gravarGrupoUsuarios(grupoUsuario, usuarioGrupo);

        finish();
    }


    public Grupo recuperaBundle() {

        Intent intent = getIntent();
        Grupo grupoUsuario = (Grupo) intent.getSerializableExtra("grupo");

        return grupoUsuario;
    }
}
