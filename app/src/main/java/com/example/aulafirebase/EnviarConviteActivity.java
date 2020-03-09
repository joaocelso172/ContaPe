package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.aulafirebase.DAL.ConviteDAO;
import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.GrupoDAO;
import com.example.aulafirebase.Model.Convite;
import com.example.aulafirebase.Model.Grupo;
import com.google.android.material.textfield.TextInputEditText;

public class EnviarConviteActivity extends AppCompatActivity {

    private Button btnEnviarConvite;
    private TextInputEditText edEmailIntegrante;
    private Convite convite = new Convite();
    private ConviteDAO conviteDAO = new ConviteDAO();
    private Spinner spinnerGrupoSelecionado;
    private GrupoDAO grupoDAO = new GrupoDAO();
    private Grupo grupoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_convite);

        btnEnviarConvite = findViewById(R.id.btnEnviarConvite);
        edEmailIntegrante = findViewById(R.id.edIntegranteInserido);
        spinnerGrupoSelecionado = findViewById(R.id.spinnerGrupoSelecionado); //Inutilizar

        btnEnviarConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
