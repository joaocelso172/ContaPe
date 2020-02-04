package com.example.aulafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
    private String nomeUsuario, tipoUsuario;

    private Button btnEnviarTarefa;
    private EditText edNome, edDesc, edPrioridade, edAssociado;

    private Spinner spinNomes;

    String[] nomeUsuarios = new String[]{"João", "Teste 01", "Teste 02"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEnviarTarefa = findViewById(R.id.btnEnviarTarefa);

        edNome = findViewById(R.id.editNomeTarefa);
        edDesc = findViewById(R.id.editDescTarefa);
        edPrioridade = findViewById(R.id.editPrioridadeTarefa);
        edAssociado = findViewById(R.id.editAssociado);
        spinNomes = findViewById(R.id.spinnerNomes);

        tipoUsuario = "administrador";


        ArrayAdapter <String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, nomeUsuarios);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinNomes.setAdapter(arrayAdapter);

      /*  refenciaDb.child( "usuarios" ).child("administrador").child("nome").setValue("João");
        refenciaDb.child( "usuarios" ).child("visitantes").child("nome").setValue("Visitante Teste");*/

       /* DatabaseReference usuariosAdd = refenciaDb.child( "usuarios" );

        usuariosAdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("FIREBASE ", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
       });*/

       /* Usuario usuarioObj = new Usuario(edNome.getText().toString(), "administrador");
        usuarioObj.setNome("Visitante Teste Objeto");


       usuariosAdd.child("visitantes").setValue(usuarioObj);*/



        btnEnviarTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravarFirebase();
            }
        });



    }

    public void gravarFirebase(){

        Tarefa tarefaObj = new Tarefa();

        if (!spinNomes.getSelectedItem().toString().equals("") || !edDesc.getText().toString().equals("") || !edAssociado.getText().toString().equals("")
                || !edPrioridade.getText().toString().equals("")) {

            try {
                tarefaObj.setNomeTarefa(spinNomes.getSelectedItem().toString());
                tarefaObj.setPrioridadeTarefa(Integer.parseInt(edPrioridade.getText().toString()));
                tarefaObj.setDescTarefa(edDesc.getText().toString());
                tarefaObj.setPessoaAtribuida(edAssociado.getText().toString());

                DatabaseReference tarefasAdd = refenciaDb.child("usuarios").child(tipoUsuario).child(tarefaObj.getPessoaAtribuida()).child(tarefaObj.getNomeTarefa());
                tarefasAdd.child("desc da Tarefa").setValue(tarefaObj.getDescTarefa());
                tarefasAdd.child("pessoa Atribuida").setValue(tarefaObj.getPessoaAtribuida());
                tarefasAdd.child("prioridade da tarefa").setValue(tarefaObj.getPrioridadeTarefa());

                Toast.makeText(this, "Dados gravados com sucesso!", Toast.LENGTH_LONG).show();
            }catch (Exception e){
                Toast.makeText(this, "Houve um erro ao tentar gravar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        else {
            Toast.makeText(this, "Preencha todos os campos antes de continuar.  ;)", Toast.LENGTH_LONG).show();
        }

    }

}
