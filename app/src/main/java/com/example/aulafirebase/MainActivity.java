package com.example.aulafirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
    private String nomeUsuario, tipoUsuario;

    private Button btnEnviarTarefa;
    private EditText edNome, edDesc, edPrioridade, edAssociado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnviarTarefa = findViewById(R.id.btnEnviarTarefa);

        edNome = findViewById(R.id.editNomeTarefa);
        edDesc = findViewById(R.id.editDescTarefa);
        edPrioridade = findViewById(R.id.editPrioridadeTarefa);
        edAssociado = findViewById(R.id.editAssociado);

      /*  refenciaDb.child( "usuarios" ).child("administrador").child("nome").setValue("João");
        refenciaDb.child( "usuarios" ).child("visitantes").child("nome").setValue("Visitante Teste");*/

        DatabaseReference usuariosAdd = refenciaDb.child( "usuarios" );

        Usuario usuarioObj = new Usuario();
        usuarioObj.setNome("Visitante Teste Objeto");


        tipoUsuario = "administrador";

        usuariosAdd.child("visitantes").setValue(usuarioObj);

        btnEnviarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravarFirebase();
            }
        });



    }

    public void gravarFirebase(){

        Tarefa tarefaObj = new Tarefa();

        if (!edNome.getText().toString().equals(null) || !edDesc.getText().toString().equals(null) || !edAssociado.getText().toString().equals(null)
                || !edPrioridade.getText().toString().equals(null)) {

            tarefaObj.setNomeTarefa(edNome.getText().toString());
            tarefaObj.setPrioridadeTarefa(Integer.parseInt(edPrioridade.getText().toString()));
            tarefaObj.setDescTarefa(edDesc.getText().toString());
            tarefaObj.setPessoaAtribuida(edAssociado.getText().toString());

            DatabaseReference tarefasAdd = refenciaDb.child("usuarios").child(tipoUsuario).child(tarefaObj.getPessoaAtribuida()).child(tarefaObj.getNomeTarefa());
            tarefasAdd.child("desc da Tarefa").setValue(tarefaObj.getDescTarefa());
            tarefasAdd.child("pessoa Atribuida").setValue(tarefaObj.getPessoaAtribuida());
            tarefasAdd.child("prioridade da tarefa").setValue(tarefaObj.getPrioridadeTarefa());
        }

        else {
            Toast.makeText(this, "Preencha todos os campos antes de continuar...\n\t :)", Toast.LENGTH_SHORT).show();
        }

    }

}
