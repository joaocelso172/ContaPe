package com.example.aulafirebase.DAL;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.Usuario;
import com.example.aulafirebase.helper.Base64Custom;
import com.example.aulafirebase.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MovimentacoesDAO {

    //Variavel de status
    private boolean isSucess;
    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();
    //Despesa total
    private Double despesaTotal = null;
    //Receita total
    private Double receitaTotal = null;
    //Saldo, opcional
    private Double saldo = null;
    //Saldo
    private Double alerta = null;
    //Referencia às tarefas, com listener
    private DatabaseReference tarefas;

    public Boolean salvarMovimentacao(Movimentacao movimentacaoSalva){

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Cria uma Pk e insere os dados de um usuário ao BD
        return movimentacoesData.push().setValue(movimentacaoSalva).isSuccessful();
    }

    public List<Movimentacao> listarMovimentacoes(List<Movimentacao> listaMovimentacao, String ano, String mes){

        Log.i("Logando", "Executado");

        tarefas = getDatabaseMonthMovInstance(ano, mes);

        tarefas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Limpa lista
                listaMovimentacao.clear();

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    if (movimentacao.getTipo() != null) {
                        movimentacao.setID(dados.getKey());
                        listaMovimentacao.add(movimentacao);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Retorna lista finalizada
        return listaMovimentacao;

    }

    //Pega instancia da movimentacao
    private DatabaseReference getDatabaseMovimentacaoInstance(){
        //Usuario Logado
         FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes");
    }

    //Pega instancia do usuário
    private DatabaseReference getDatabaseUserSaldo(){

        FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail()));
    }

    //Pega instancia da movimentação de acordo com o ano/mes/dia
    private DatabaseReference getDatabaseMonthMovInstance(String year, String month){
        //Usuario Logado
        FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes").child(year).child(month);
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

    public Boolean atualizarDespesa(Double despesaAtualizada){

        //Se houver êxito na gravação retorna true, se não, retorna false
        return getDatabaseUserSaldo().child("despesaTotal").setValue(despesaAtualizada).isSuccessful();

    }

    public void atualizarSaldo(Double saldoAtualizado) {

        getDatabaseUserSaldo().child("saldoDisponivel").setValue(saldoAtualizado);

    }

    public void removerMovimentacao(Movimentacao movimentacao){

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacao.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesKey = movimentacoes.child(dataMov).child(movimentacao.getID());

        movimentacoesKey.removeValue();

    }


}
