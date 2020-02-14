package com.example.aulafirebase.DAL;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Adapter.TarefasAdapter;
import com.example.aulafirebase.Model.Tarefa;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TarefasDAO {

    //Variavel de status
    private boolean isSucess;

    //Referencia ao BD configurado no json
    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //
    private ValueEventListener valueEventListenerTarefas;



    public boolean buscarTarefa(){

        //DatabaseReference tarefasQuery = tarefas.child("comprar pao");

        DatabaseReference tarefas = getDatabaseTarefasInstance();

        Query tarefasQuery = tarefas.orderByChild("desc");

        tarefasQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    Log.i("Tarefas", "tarefa não encontrada");
                }else {
                    Log.i("Tarefas", "tarefa encontrada! " + dataSnapshot.getValue());
                    isSucess = true;
                //    Log.i("Tarefas", dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return isSucess;
    }

    public void salvarTarefa(String nomeTarefa, String descTarefa){

        DatabaseReference tarefas = getDatabaseTarefasInstance();

        //Objeto que receberá uma tarefa
        Tarefa tarefa = new Tarefa();

        //define nome e descricao da tarefa
        tarefa.setNomeTarefa(nomeTarefa);
        tarefa.setDescTarefa(descTarefa);

        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            tarefas.push().setValue(tarefa);

            Log.i("Tarefa", "Tarefa cadastrada com sucesso!");
        }catch (Exception e){
            Log.i("Logando", e.getMessage());
        }

    }

    public List<Tarefa> listarTarefas(List<Tarefa> listaTarefas, TarefasAdapter tarefasAdapter){

       // List<Tarefa> listaTarefas = new ArrayList<>();

        DatabaseReference tarefas = getDatabaseTarefasInstance();

        valueEventListenerTarefas = tarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (listaTarefas != null) {
                    listaTarefas.clear();
                }

                for(DataSnapshot dados: dataSnapshot.getChildren()){

                    Tarefa tarefa = dados.getValue(Tarefa.class);
                    listaTarefas.add(tarefa);

                }


              //  tarefasAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (listaTarefas.size() != 0) {
                    handler.removeCallbacks(this);
                    Log.i("Tarefas", listaTarefas.get(0).getNomeTarefa());
                }else handler.postDelayed(this,3000);


            }
        }, 3000);

        return listaTarefas;

    }

    private DatabaseReference getDatabaseTarefasInstance(){
        //Usuario Logado
         FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente
         DatabaseReference tarefas = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("tarefas");

         return tarefas;
    }

}
