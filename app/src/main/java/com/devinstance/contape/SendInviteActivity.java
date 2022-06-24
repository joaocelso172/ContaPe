package com.devinstance.contape;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.devinstance.contape.DAL.InviteDAO;
import com.devinstance.contape.DAL.GroupDAO;
import com.devinstance.contape.model.Invite;
import com.devinstance.contape.model.Group;
import com.google.android.material.textfield.TextInputEditText;

public class SendInviteActivity extends AppCompatActivity {

    private Button btnEnviarConvite;
    private TextInputEditText edEmailIntegrante;
    private Invite invite = new Invite();
    private InviteDAO inviteDAO = new InviteDAO();
    private Spinner spinnerGrupoSelecionado;
    private GroupDAO groupDAO = new GroupDAO();
    private Group groupSelecionado;

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
