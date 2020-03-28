package com.example.aulafirebase.DAL;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.Controller.ActivityMovimentacao.MovimentacaoActivity;
import com.example.aulafirebase.Model.Grupo;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void salvarMovimentacao(Movimentacao movimentacaoSalva){

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", movimentacaoSalva.getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV
        idMov = movimentacoesData.push().getKey();
        //Cria uma Pk e insere os dados de um usuário ao BD
        if (movimentacaoSalva.isParcelado()) salvarMovimentacaoParcelada(movimentacaoSalva, idMov);
        else movimentacoesData.child(idMov).setValue(movimentacaoSalva);
    }

    public void salvarMovimentacao(List<Movimentacao> movimentacaoSalva, List<String> datas){

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", movimentacaoSalva.get(0).getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.get(0).getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV que será reutilizada em todos os meses
        idMov = movimentacoesData.push().getKey();

     /*   for ( Movimentacao movimentacao : movimentacaoSalva ){
            //Formata uma nova data, para cada parcela
            String dataMovimentacao = DateCustom.firebaseFormatDate(movimentacao.getDataTarefa());
            //Cria uma nova referencia, para cada parcela
            DatabaseReference movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
            //Insere parcela, que terá mesmo ID
            movParceladoRef.setValue(movimentacao);
            Log.i("Parcelado", "No DAO - " + "Parcela: " + movimentacao.getParcelaAtual() + ", Item: " + movimentacao.getDescTarefa());
            Log.i("Parcelado", "Tamanho da lista: " + movimentacaoSalva.size() );
        }*/
     int i;

     for (i = 0; i< movimentacaoSalva.size(); i++){
         //Formata uma nova data, para cada parcela
         String dataMovimentacao = DateCustom.firebaseFormatDate(datas.get(i));
         movimentacaoSalva.get(i).setDataTarefa(datas.get(i));
         movimentacaoSalva.get(i).setParcelaAtual(i + 1);
         //Cria uma nova referencia, para cada parcela
         DatabaseReference movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
         //Insere parcela, que terá mesmo ID
         movParceladoRef.setValue(movimentacaoSalva.get(i));
         Log.i("Parcelado", "No DAO - " + "Parcela: " + movimentacaoSalva.get(i).getParcelaAtual() + ", Item: " + movimentacaoSalva.get(i).getDescTarefa());
     }

        Log.i("Parcelado", "Tamanho da lista: " + movimentacaoSalva.size() );



    }

    private void salvarMovimentacaoParcelada(Movimentacao movimentacao, String id){
        int parcelaTotal = movimentacao.getParcelaTotal();
        String data[] = DateCustom.firebaseFormatDateBuild(movimentacao.getDataTarefa());

            int i;
            int mes = Integer.parseInt(data[1]);
            int ano = Integer.parseInt(data[2]);

            for (i = 1; i<=parcelaTotal; i++){
                movimentacao.setParcelaAtual(i);
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

                DatabaseReference dbRef = getDatabaseMovimentacaoInstance().child(anoParcela + "/" + mesParcela); //Ano + mês
                dbRef.child(id).setValue(movimentacao);
                Log.i("Parcelado", "Parcelas: " + mes + "/" + ano);

            }

    }

    public Boolean listarMovimentacoes(List<Movimentacao> listaMovimentacao, String ano, String mes, MovimentacoesAdapter movimentacoesAdapter, Context c, View view){
        status = false;

        tarefas = getDatabaseMonthMovInstance(ano, mes);

        tarefas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) Toast.makeText(c, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();

                //Limpa lista
                listaMovimentacao.clear();

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    if (movimentacao.getTipo() != null) {
                        movimentacao.setID(dados.getKey());
                        listaMovimentacao.add(movimentacao);

                        if (listaMovimentacao.size() == dataSnapshot.getChildrenCount()) status = true;
                        else status = false;
                    }

                }

                MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();
                movimentacoesAdapter.notifyDataSetChanged();
                movimentacaoActivity.calcularRendaMensal(listaMovimentacao, view);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("CANCELADO", databaseError.getMessage());

            }

        });



        return status;
    }

    public List<Movimentacao> listarMovimentacoes(List<Movimentacao> listaMovimentacao, String ano, String mes, Grupo grupo, MovimentacoesAdapter movimentacoesAdapter, Context c, View view){

        tarefas = getDatabaseMonthMovInstanceGrupo(ano, mes, grupo);

        tarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (!dataSnapshot.exists()) Toast.makeText(c, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();

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

                MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();
                movimentacoesAdapter.notifyDataSetChanged();
                movimentacaoActivity.calcularRendaMensal(listaMovimentacao, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Retorna lista finalizada
        Log.i("Lista MvDAO", "Tamanho da lista: " + listaMovimentacao.size());

        movimentacoesAdapter.notifyDataSetChanged();

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

    public Boolean atualizarDespesa(Double despesaAtualizada, Grupo grupo){

        //Se houver êxito na gravação retorna true, se não, retorna false
        return getDatabaseGrupoSaldo(grupo).child("despesaGrupo").setValue(despesaAtualizada).isSuccessful();

    }

    public void atualizarSaldo(Double saldoAtualizado) {

        getDatabaseUserSaldo().child("saldoDisponivel").setValue(saldoAtualizado);

    }

    public void removerMovimentacao(Movimentacao movimentacao, MovimentacoesAdapter movimentacoesAdapter, int pos){

        MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacao.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstance();
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesKey = movimentacoes.child(dataMov).child(movimentacao.getID());

        movimentacoesKey.removeValue();
        movimentacoesAdapter.notifyItemRemoved(pos);

        movimentacaoActivity.notifyMovimentacaoRemoved();

    }

    //Os métodos abaixo são todos referentes a exibição a GRUPO



    //Pega instancia da movimentação de acordo com o ano/mes/dia
    private DatabaseReference getDatabaseMonthMovInstanceGrupo(String year, String month, Grupo grupo){

        return refenciaDb.child("grupos").child(grupo.getGrupoId()).child("Movimentacoes").child(year).child(month);
    }




    //GRUPOS



    public Boolean salvarMovimentacao(Movimentacao movimentacaoSalva, Grupo grupo){

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Cria uma Pk e insere os dados de um usuário ao BD
        return movimentacoesData.push().setValue(movimentacaoSalva).isSuccessful();
    }

    private DatabaseReference getDatabaseMovimentacaoInstanceGrupo(Grupo grupo){

        //Referencia ao branch de tarefas dentro de usuarios feito baseada numa referencia geral já existente

        return refenciaDb.child("grupos").child(grupo.getGrupoId()).child("Movimentacoes");
    }

    public void atualizarReceita(Double receitaAtualizada, Grupo grupo){

        getDatabaseGrupoSaldo(grupo).child("receitaGrupo").setValue(receitaAtualizada);

    }

    public Boolean atualizarDespesaGrupo(Double despesaAtualizada, Grupo grupo){

        //Se houver êxito na gravação retorna true, se não, retorna false
        return getDatabaseGrupoSaldo(grupo).child("despesaGrupo").setValue(despesaAtualizada).isSuccessful();

    }

    public void atualizarSaldoGrupo(Double saldoAtualizado, Grupo grupo) {

        getDatabaseGrupoSaldo(grupo).child("saldoDisponivel").setValue(saldoAtualizado);

    }

    //Pega instancia do usuário
    private DatabaseReference getDatabaseGrupoSaldo(Grupo grupo){

        return refenciaDb.child("grupos").child(grupo.getGrupoId());
    }

    public Double getDespesaTotal(Grupo grupo){

        getDatabaseGrupoSaldo(grupo).addValueEventListener(new ValueEventListener() {
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

    public Double getReceitaTotal(Grupo grupo){

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



}
