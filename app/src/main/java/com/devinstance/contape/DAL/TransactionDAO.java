package com.devinstance.contape.DAL;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.devinstance.contape.adapter.TransactionAdapter;
import com.devinstance.contape.controller.transaction_activity.TransactionActivity;
import com.devinstance.contape.model.Group;
import com.devinstance.contape.model.Transaction;
import com.devinstance.contape.model.User;
import com.devinstance.contape.model.UserGroup;
import com.devinstance.contape.helper.Base64Custom;
import com.devinstance.contape.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TransactionDAO {

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
    private DatabaseReference gruposUsuario;
    private DatabaseReference gruposMovimentacoes;
    private int status;


    public void salvarMovimentacao(Transaction transactionSalva, Group group){


        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", transactionSalva.getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(transactionSalva.getDataTarefa());
        DatabaseReference movimentacoes;
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        if (group == null) {
            movimentacoes = getDatabaseMovimentacaoInstance();
            Log.i("Grupo nulo", "grupo nulo");
        }
        else {
            movimentacoes = getDatabaseMovimentacaoInstanceGrupo(group);
            Log.i("Grupo nulo", "grupo encontrado, " + group.getGrupoId());
        }
            //Modificação do método utilizado para agrupar em datas agora
            DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
            //Grava ID da MOV
            idMov = movimentacoesData.push().getKey();

            movimentacoesData.child(idMov).setValue(transactionSalva);
    }

    public void salvarMovimentacao(List<Transaction> transactionSalva, List<String> datas, Group group){ //Cosntrutor para salvar listas de movimentacoes

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(transactionSalva.get(0).getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        DatabaseReference movimentacoesData;
        if (group == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(group); //Ano + mês
        //Modificação do método utilizado para agrupar em datas agora
        movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV que será reutilizada em todos os meses
        if (transactionSalva.get(0).getID() == null) idMov = movimentacoesData.push().getKey();
        else idMov = transactionSalva.get(0).getID();

     int i;

     for (i = 0; i< transactionSalva.size(); i++){
         //Formata uma nova data, para cada parcela
         String dataMovimentacao = DateCustom.firebaseFormatDate(datas.get(i));
         transactionSalva.get(i).setDataTarefa(datas.get(i));
         transactionSalva.get(i).setParcelaAtual(i + 1);
         //Cria uma nova referencia, para cada parcela
         DatabaseReference movParceladoRef;
         if (group == null) movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
         else movParceladoRef = getDatabaseMovimentacaoInstanceGrupo(group).child(dataMovimentacao).child(idMov);
         //Insere parcela, que terá mesmo ID
         movParceladoRef.setValue(transactionSalva.get(i));
     }

    }

    public void atualizarMovimentacao (Transaction transactionAtualizada, Group group){
        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", transactionAtualizada.getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(transactionAtualizada.getDataTarefa());
        DatabaseReference movimentacoes;
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        if (group == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(group);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV
        idMov = transactionAtualizada.getID();

        movimentacoesData.child(idMov).setValue(transactionAtualizada);

    }

    public void atualizarMovimentacao (List <Transaction> transactionAtualizada, List<String> datas, Group group){

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(transactionAtualizada.get(0).getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        DatabaseReference movimentacoesData;
        if (group == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(group); //Ano + mês
        //Modificação do método utilizado para agrupar em datas agora
        movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV que será reutilizada em todos os meses
        idMov = transactionAtualizada.get(0).getID();

        int i;

        for (i = 0; i< transactionAtualizada.size(); i++){
            //Formata uma nova data, para cada parcela
            String dataMovimentacao = DateCustom.firebaseFormatDate(datas.get(i));
            transactionAtualizada.get(i).setDataTarefa(datas.get(i));
            transactionAtualizada.get(i).setParcelaAtual(i + 1);
            //Cria uma nova referencia, para cada parcela
            DatabaseReference movParceladoRef;
            if (group == null) movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
            else movParceladoRef = getDatabaseMovimentacaoInstanceGrupo(group).child(dataMovimentacao).child(idMov);
            //Insere parcela, que terá mesmo ID
            movParceladoRef.setValue(transactionAtualizada.get(i));
        }

    }



    private void salvarMovimentacaoParcelada(Transaction transaction, String id, Group group){
        int parcelaTotal = transaction.getParcelaTotal();
        String data[] = DateCustom.firebaseFormatDateBuild(transaction.getDataTarefa());

            int i;
            int mes = Integer.parseInt(data[1]);
            int ano = Integer.parseInt(data[2]);

            for (i = 1; i<=parcelaTotal; i++){
                transaction.setParcelaAtual(i);
                if (mes < 12) mes++;
                else if (mes >= 12) {
                    mes = 1;
                    Log.i("Parcelado", "Ano antes alterar: " + mes + "/" + ano);
                    ano++;
                    Log.i("Parcelado", "Ano alterado: " +
                           mes + "/" + ano);
                }

                String mesParcela = String.valueOf(mes);
                String anoParcela = String.valueOf(ano);
                if (mes < 10) mesParcela = "0" + mes;

                DatabaseReference dbRef;

                if (group == null)  dbRef = getDatabaseMovimentacaoInstance().child(anoParcela + "/" + mesParcela); //Ano + mês
                else dbRef = getDatabaseMovimentacaoInstanceGrupo(group).child(anoParcela + "/" + mesParcela); //Ano + mês
                dbRef.child(id).setValue(transaction);
                Log.i("Parcelado", "Parcelas: " + mes + "/" + ano);

            }

    }

    public void listarMovimentacoes(List<Transaction> listaTransaction, String ano, String mes, TransactionAdapter transactionAdapter, Context c, View view){
        status = 0;

        FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();
        TransactionActivity transactionActivity = new TransactionActivity();

        Double valorAnterior = transactionActivity.calcularRendaAnterior(listaTransaction);

        gruposUsuario = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("UsuarioGrupo");
        tarefas = getDatabaseMonthMovInstance(ano, mes);

        tarefas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpa lista
                listaTransaction.clear();

                if (!dataSnapshot.exists()) {
                    transactionAdapter.notifyDataSetChanged();
                    status++;
                }

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Transaction transaction = dados.getValue(Transaction.class);
                    if (transaction.getTipo() != null) {
                        transaction.setID(dados.getKey());
                        listaTransaction.add(transaction);

//                        if (listaMovimentacao.size() == dataSnapshot.getChildrenCount()) status++;
                    }

                    status++;

                }


                transactionAdapter.notifyDataSetChanged();
                transactionActivity.calcularRendaMensal(0.0, listaTransaction, view);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("CANCELADO", databaseError.getMessage());

            }

        });

        gruposUsuario.addListenerForSingleValueEvent(new ValueEventListener() { //procura por mov nos grupos
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    transactionAdapter.notifyDataSetChanged();
                    transactionActivity.calcularRendaMensal(valorAnterior, listaTransaction, view);
                }

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dadosGrupos: dataSnapshot.getChildren()){
                    UserGroup userGroup = dadosGrupos.getValue(UserGroup.class);
                    if (userGroup.getReceberMovFeed() == null){
                        gruposMovimentacoes = getDatabaseMonthMovInstanceGrupo(ano, mes, userGroup.getGrupoIdUsuario());
                        gruposMovimentacoes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot movGrupo : dataSnapshot.getChildren()){ //EventListener de grupo
                                    Transaction transactionGrupo = movGrupo.getValue(Transaction.class);
                                    if (transactionGrupo.getTipo() != null && transactionGrupo.getAtribuicao() != null && transactionGrupo.getAtribuicao().equals(mAuth.getCurrentUser().getEmail())) {
                                        if ( (transactionGrupo.getTipo().equals("r") && transactionGrupo.getInverso().equals(true))) {
                                            transactionGrupo.setTipo("d");
                                        }else if ((transactionGrupo.getTipo().equals("d") && transactionGrupo.getInverso().equals(true))) transactionGrupo.setTipo("r");
                                        transactionGrupo.setID(movGrupo.getKey());
                                        listaTransaction.add(transactionGrupo);

                                    }
                                }

                                status++;

                                transactionAdapter.notifyDataSetChanged();
                                transactionActivity.calcularRendaMensal(valorAnterior, listaTransaction, view);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                       /* movimentacoesAdapter.notifyDataSetChanged();
                        movimentacaoActivity.calcularRendaMensal(listaMovimentacao, view);
                        Toast.makeText(c, "rodou", Toast.LENGTH_SHORT).show();

                        Toast.makeText(c, "Status: " + status, Toast.LENGTH_SHORT).show();*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public List<Transaction> listarMovimentacoes(List<Transaction> listaTransaction, String ano, String mes, Group group, TransactionAdapter transactionAdapter, Context c, View view){

        tarefas = getDatabaseMonthMovInstanceGrupo(ano, mes, group);

        TransactionActivity transactionActivity = new TransactionActivity();

        Double valorAnterior = transactionActivity.calcularRendaAnterior(listaTransaction);

        tarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


//                if (!dataSnapshot.exists()) Toast.makeText(c, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();

                transactionActivity.calcularRendaAnterior(listaTransaction);

                //Limpa lista
                listaTransaction.clear();

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Transaction transaction = dados.getValue(Transaction.class);
                    if (transaction.getTipo() != null) {
                        transaction.setID(dados.getKey());
                        listaTransaction.add(transaction);
                    }

                }

                TransactionActivity transactionActivity = new TransactionActivity();
                transactionAdapter.notifyDataSetChanged();
                transactionActivity.calcularRendaMensal(valorAnterior, listaTransaction, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Retorna lista finalizada
        Log.i("Lista MvDAO", "Tamanho da lista: " + listaTransaction.size());

        transactionAdapter.notifyDataSetChanged();

        return listaTransaction;

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
                User user = dataSnapshot.getValue( User.class );
                despesaTotal = user.getDespesaTotal();
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
                User user = dataSnapshot.getValue( User.class );
                //Passa para a variavel receitaTotal de Usuarios
                receitaTotal = user.getReceitaTotal();


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
                User user = dataSnapshot.getValue( User.class );
                //Passa para a variavel receitaTotal de Usuarios
                saldo = user.getSaldoDisponivel();
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
                User user = dataSnapshot.getValue( User.class );
                //Passa para a variavel de Usuarios
                alerta = user.getValorAlerta();
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

    public Boolean atualizarDespesa(Double despesaAtualizada, Group group){

        //Se houver êxito na gravação retorna true, se não, retorna false
        return getDatabaseGrupoSaldo(group).child("despesaGrupo").setValue(despesaAtualizada).isSuccessful();

    }

    public void atualizarSaldo(Double saldoAtualizado) {

        getDatabaseUserSaldo().child("saldoDisponivel").setValue(saldoAtualizado);

    }

    public void removerMovimentacao(Transaction transaction, TransactionAdapter transactionAdapter, int pos, Group group){

        TransactionActivity transactionActivity = new TransactionActivity();

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(transaction.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        if (group == null ) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(group);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesKey = movimentacoes.child(dataMov).child(transaction.getID());

        movimentacoesKey.removeValue();
        transactionAdapter.notifyItemRemoved(pos);

    }


    public void recuperarMovFuturas(Transaction transaction, TransactionAdapter transactionAdapter, int pos, Group group){

        String data[] = DateCustom.firebaseFormatDateBuild(transaction.getDataTarefa());
        int parcelasRestantes = (transaction.getParcelaTotal() - (transaction.getParcelaAtual()-1));
        int i;
        //variaveis que armazenam os valores que aumentarão de acordo com as parcelas
        int mes = Integer.parseInt(data[1]);
        int ano = Integer.parseInt(data[2]);

        for(i=0; i<=parcelasRestantes; i++) {
            boolean a = false;
            if (i == 0) a = true;

            //variaveis que contem as datas em valores Strings
            String mesStr = String.valueOf(mes);
            if (mes < 10) mesStr = "0" + mes;

            String anoStr = String.valueOf(ano);

            DatabaseReference tarefasExclusao;
            if (group == null) tarefasExclusao = getDatabaseMonthMovInstance(anoStr, mesStr).child(transaction.getID());
            else tarefasExclusao = getDatabaseMonthMovInstanceGrupo(anoStr, mesStr, group).child(transaction.getID());

            tarefasExclusao.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        tarefasExclusao.removeValue();
                    }else Log.i("Exclusão", "não encontrada");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (mes >= 12) {
                ano++;
                mes = 1;
            } else mes++;

        }

        transactionAdapter.notifyItemRemoved(pos);

    }

    //Os métodos abaixo são todos referentes a exibição a GRUPO



    //Pega instancia da movimentação de acordo com o ano/mes/dia
    private DatabaseReference getDatabaseMonthMovInstanceGrupo(String year, String month, Group group){

        return refenciaDb.child("grupos").child(group.getGrupoId()).child("Movimentacoes").child(year).child(month);
    }

    //Pega instancia da movimentação de acordo com o ano/mes/dia
    private DatabaseReference getDatabaseMonthMovInstanceGrupo(String year, String month, String id){

        return refenciaDb.child("grupos").child(id).child("Movimentacoes").child(year).child(month);
    }


    //GRUPOS



   /* public Boolean salvarMovimentacao(Movimentacao movimentacaoSalva, Grupo grupo){

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Cria uma Pk e insere os dados de um usuário ao BD
        return movimentacoesData.push().setValue(movimentacaoSalva).isSuccessful();
    }*/

    private DatabaseReference getDatabaseMovimentacaoInstanceGrupo(Group group){

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente

        return refenciaDb.child("grupos").child(group.getGrupoId()).child("Movimentacoes");
    }

    public void atualizarReceita(Double receitaAtualizada, Group group){

        getDatabaseGrupoSaldo(group).child("receitaGrupo").setValue(receitaAtualizada);

    }

    public Boolean atualizarDespesaGrupo(Double despesaAtualizada, Group group){

        //Se houver êxito na gravação retorna true, se não, retorna false
        return getDatabaseGrupoSaldo(group).child("despesaGrupo").setValue(despesaAtualizada).isSuccessful();

    }

    public void atualizarSaldoGrupo(Double saldoAtualizado, Group group) {

        getDatabaseGrupoSaldo(group).child("saldoDisponivel").setValue(saldoAtualizado);

    }

    //Pega instancia do usuário
    private DatabaseReference getDatabaseGrupoSaldo(Group group){

        return refenciaDb.child("grupos").child(group.getGrupoId());
    }

    public Double getDespesaTotal(Group group){

        getDatabaseGrupoSaldo(group).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue( User.class );
                despesaTotal = user.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return despesaTotal;
    }

    public Double getReceitaTotal(Group group){

        getDatabaseUserSaldo().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Retorna valores do usuário para a classe
                User user = dataSnapshot.getValue( User.class );
                //Passa para a variavel receitaTotal de Usuarios
                receitaTotal = user.getReceitaTotal();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return receitaTotal;

    }



}
