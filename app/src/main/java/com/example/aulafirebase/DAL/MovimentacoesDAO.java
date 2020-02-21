package com.example.aulafirebase.DAL;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.helper.Base64Custom;
import com.example.aulafirebase.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MovimentacoesDAO {

    //Variavel de status
    private boolean isSucess;
    //Referencia ao BD configurado no json
    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
    //
    private ValueEventListener valueEventListener;
    //Despesa total
    private Double despesaTotal = null;
    //Receita total
    private Double receitaTotal = null;
    //Saldo, opcional
    private Double saldo = null;
    //Saldo
    private Double alerta = null;


    public boolean buscarMovimentacao(){

        //DatabaseReference tarefasQuery = tarefas.child("comprar pao");

        DatabaseReference tarefas = getDatabaseMovimentacaoInstance();

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

    public boolean salvarMovimentacao(Movimentacao movimentacaoSalva){


        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());


        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);


        try {
            //Cria uma Pk e insere os dados de um usuário ao BD
            movimentacoesData.push().setValue(movimentacaoSalva);
            Log.i("Movimentacao", "Movimentacao cadastrada com sucesso!");
            return true;
        }catch (Exception e){
            Log.i("Logando", e.getMessage());
            return false;
        }

    }

    public List<Movimentacao> listarMovimentacoes(List<Movimentacao> listaMovimentacaos, MovimentacoesAdapter movimentacoesAdapter){

       // List<Movimentacao> listaMovimentacaos = new ArrayList<>();

        DatabaseReference tarefas = getDatabaseMovimentacaoInstance();

        valueEventListener = tarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (listaMovimentacaos != null) {
                    listaMovimentacaos.clear();
                }

                for(DataSnapshot dados: dataSnapshot.getChildren()){

                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    listaMovimentacaos.add(movimentacao);

                }


              //  movimentacoesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (listaMovimentacaos.size() != 0) {
                    handler.removeCallbacks(this);
                    Log.i("Tarefas", listaMovimentacaos.get(0).getNomeTarefa());
                }else handler.postDelayed(this,3000);


            }
        }, 3000);

        return listaMovimentacaos;

    }

    private DatabaseReference getDatabaseMovimentacaoInstance(){
        //Usuario Logado
         FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente
         DatabaseReference mov = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes");

         return mov;
    }

    private DatabaseReference getDatabaseUserSaldo(){

        FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

        DatabaseReference usuarios = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));

        return usuarios;
    }



    public Double getDespesaTotal(){

        getDatabaseUserSaldo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                despesaTotal = usuario.getDespesaTotal();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return despesaTotal;
    }

    public Double getReceitaTotal(){

        getDatabaseUserSaldo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retorna valores do usuário para a classe
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                //Passa para a variavel receitaTotal de Usuarios
                receitaTotal = usuario.getReceitaTotal();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return receitaTotal;

    }

    public Double getSaldo(){

        getDatabaseUserSaldo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retorna valores do usuário para a classe
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                //Passa para a variavel receitaTotal de Usuarios
                saldo = usuario.getSaldoDisponivel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return saldo;

    }

    public Double getAlerta (){

        getDatabaseUserSaldo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retorna valores do usuário para a classe
                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                //Passa para a variavel de Usuarios
                alerta = usuario.getValorAlerta();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return alerta;

    }

    public void atualizarReceita(Double receitaAtualizada){

        getDatabaseUserSaldo().child("receitaTotal").setValue(receitaAtualizada);

    }

    public void atualizarDespesa(Double despesaAtualizada){

        getDatabaseUserSaldo().child("despesaTotal").setValue(despesaAtualizada);

    }

    public void atualizarSaldo(Double saldoAtualizado) {

        getDatabaseUserSaldo().child("saldoDisponivel").setValue(saldoAtualizado);

    }

}
