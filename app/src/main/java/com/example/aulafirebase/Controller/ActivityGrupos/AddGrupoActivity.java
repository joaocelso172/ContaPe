package com.example.aulafirebase.Controller.ActivityGrupos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aulafirebase.DAL.ConviteDAO;
import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.Model.Convite;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.R;

public class AddGrupoActivity extends AppCompatActivity {

    private Convite convite = new Convite();
    private ConviteDAO conviteDAO = new ConviteDAO();

    private Button btnCriarGrupo;
    private EditText editNomeGrupo;
    private EditText editDescGrupo;

    private Grupo novoGrupo = new Grupo();
    private GrupoDAO grupoDAO = new GrupoDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_grupo);

        btnCriarGrupo = findViewById(R.id.btnCriarGrupo);
        editDescGrupo = findViewById(R.id.edDescGrupo);
        editNomeGrupo = findViewById(R.id.edNomeGrupo);


        btnCriarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarGrupo();
            }
        });



    }

    private void registrarGrupo(){
        novoGrupo.setDescGrupo(editDescGrupo.getText().toString());
        novoGrupo.setNomeGrupo(editNomeGrupo.getText().toString());
        novoGrupo.setGrupoOwner(FirebaseConfig.getFirebaseAuth().getCurrentUser().getEmail());

        grupoDAO.setGrupo(novoGrupo);

    }

    public void listaGrupos (View view){
        Intent intent = new Intent(this, ListarGruposActivity.class);

        startActivity(intent);
    }

}
