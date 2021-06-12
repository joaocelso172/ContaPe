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
import com.example.aulafirebase.Model.UsuarioGrupo;
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
    private DatabaseReference gruposUsuario;
    private DatabaseReference gruposMovimentacoes;
    private int status;


    public void salvarMovimentacao(Movimentacao movimentacaoSalva, Grupo grupo){


        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", movimentacaoSalva.getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.getDataTarefa());
        DatabaseReference movimentacoes;
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        if (grupo == null) {
            movimentacoes = getDatabaseMovimentacaoInstance();
            Log.i("Grupo nulo", "grupo nulo");
        }
        else {
            movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo);
            Log.i("Grupo nulo", "grupo encontrado, " + grupo.getGrupoId());
        }
            //Modificação do método utilizado para agrupar em datas agora
            DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
            //Grava ID da MOV
            idMov = movimentacoesData.push().getKey();

            movimentacoesData.child(idMov).setValue(movimentacaoSalva);
    }

    public void salvarMovimentacao(List<Movimentacao> movimentacaoSalva, List<String> datas, Grupo grupo){ //Cosntrutor para salvar listas de movimentacoes

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoSalva.get(0).getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        DatabaseReference movimentacoesData;
        if (grupo == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo); //Ano + mês
        //Modificação do método utilizado para agrupar em datas agora
        movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV que será reutilizada em todos os meses
        if (movimentacaoSalva.get(0).getID() == null) idMov = movimentacoesData.push().getKey();
        else idMov = movimentacaoSalva.get(0).getID();

     int i;

     for (i = 0; i< movimentacaoSalva.size(); i++){
         //Formata uma nova data, para cada parcela
         String dataMovimentacao = DateCustom.firebaseFormatDate(datas.get(i));
         movimentacaoSalva.get(i).setDataTarefa(datas.get(i));
         movimentacaoSalva.get(i).setParcelaAtual(i + 1);
         //Cria uma nova referencia, para cada parcela
         DatabaseReference movParceladoRef;
         if (grupo == null) movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
         else movParceladoRef = getDatabaseMovimentacaoInstanceGrupo(grupo).child(dataMovimentacao).child(idMov);
         //Insere parcela, que terá mesmo ID
         movParceladoRef.setValue(movimentacaoSalva.get(i));
     }

    }

    public void atualizarMovimentacao (Movimentacao movimentacaoAtualizada, Grupo grupo){
        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        Log.i("Data Mov", movimentacaoAtualizada.getDataTarefa());
        dataMov = DateCustom.firebaseFormatDate(movimentacaoAtualizada.getDataTarefa());
        DatabaseReference movimentacoes;
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        if (grupo == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV
        idMov = movimentacaoAtualizada.getID();

        movimentacoesData.child(idMov).setValue(movimentacaoAtualizada);

    }

    public void atualizarMovimentacao (List <Movimentacao> movimentacaoAtualizada,List<String> datas, Grupo grupo){

        String dataMov, idMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacaoAtualizada.get(0).getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        DatabaseReference movimentacoesData;
        if (grupo == null) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo); //Ano + mês
        //Modificação do método utilizado para agrupar em datas agora
        movimentacoesData = movimentacoes.child(dataMov);
        //Grava ID da MOV que será reutilizada em todos os meses
        idMov = movimentacaoAtualizada.get(0).getID();

        int i;

        for (i = 0; i< movimentacaoAtualizada.size(); i++){
            //Formata uma nova data, para cada parcela
            String dataMovimentacao = DateCustom.firebaseFormatDate(datas.get(i));
            movimentacaoAtualizada.get(i).setDataTarefa(datas.get(i));
            movimentacaoAtualizada.get(i).setParcelaAtual(i + 1);
            //Cria uma nova referencia, para cada parcela
            DatabaseReference movParceladoRef;
            if (grupo == null) movParceladoRef = getDatabaseMovimentacaoInstance().child(dataMovimentacao).child(idMov);
            else movParceladoRef = getDatabaseMovimentacaoInstanceGrupo(grupo).child(dataMovimentacao).child(idMov);
            //Insere parcela, que terá mesmo ID
            movParceladoRef.setValue(movimentacaoAtualizada.get(i));
        }

    }



    private void salvarMovimentacaoParcelada(Movimentacao movimentacao, String id, Grupo grupo){
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

                DatabaseReference dbRef;

                if (grupo == null)  dbRef = getDatabaseMovimentacaoInstance().child(anoParcela + "/" + mesParcela); //Ano + mês
                else dbRef = getDatabaseMovimentacaoInstanceGrupo(grupo).child(anoParcela + "/" + mesParcela); //Ano + mês
                dbRef.child(id).setValue(movimentacao);
                Log.i("Parcelado", "Parcelas: " + mes + "/" + ano);

            }

    }

    public void listarMovimentacoes(List<Movimentacao> listaMovimentacao, String ano, String mes, MovimentacoesAdapter movimentacoesAdapter, Context c, View view){
        status = 0;

        FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();
        MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();

        Double valorAnterior = movimentacaoActivity.calcularRendaAnterior(listaMovimentacao);

        gruposUsuario = refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("UsuarioGrupo");
        tarefas = getDatabaseMonthMovInstance(ano, mes);

        tarefas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpa lista
                listaMovimentacao.clear();

                if (!dataSnapshot.exists()) {
                    movimentacoesAdapter.notifyDataSetChanged();
                    status++;
                }

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Movimentacao movimentacao = dados.getValue(Movimentacao.class);
                    if (movimentacao.getTipo() != null) {
                        movimentacao.setID(dados.getKey());
                        listaMovimentacao.add(movimentacao);

//                        if (listaMovimentacao.size() == dataSnapshot.getChildrenCount()) status++;
                    }

                    status++;

                }


                movimentacoesAdapter.notifyDataSetChanged();
                movimentacaoActivity.calcularRendaMensal(0.0, listaMovimentacao, view);

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
                    movimentacoesAdapter.notifyDataSetChanged();
                    movimentacaoActivity.calcularRendaMensal(valorAnterior, listaMovimentacao, view);
                }

                //Para cada informação disponivel, executa looping
                for(DataSnapshot dadosGrupos: dataSnapshot.getChildren()){
                    UsuarioGrupo usuarioGrupo = dadosGrupos.getValue(UsuarioGrupo.class);
                    if (usuarioGrupo.getReceberMovFeed() == null){
                        gruposMovimentacoes = getDatabaseMonthMovInstanceGrupo(ano, mes, usuarioGrupo.getGrupoIdUsuario());
                        gruposMovimentacoes.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot movGrupo : dataSnapshot.getChildren()){ //EventListener de grupo
                                    Movimentacao movimentacaoGrupo = movGrupo.getValue(Movimentacao.class);
                                    if (movimentacaoGrupo.getTipo() != null && movimentacaoGrupo.getAtribuicao() != null && movimentacaoGrupo.getAtribuicao().equals(mAuth.getCurrentUser().getEmail())) {
                                        if ( (movimentacaoGrupo.getTipo().equals("r") && movimentacaoGrupo.getInverso().equals(true))) {
                                            movimentacaoGrupo.setTipo("d");
                                        }else if ((movimentacaoGrupo.getTipo().equals("d") && movimentacaoGrupo.getInverso().equals(true))) movimentacaoGrupo.setTipo("r");
                                        movimentacaoGrupo.setID(movGrupo.getKey());
                                        listaMovimentacao.add(movimentacaoGrupo);

                                    }
                                }

                                status++;

                                movimentacoesAdapter.notifyDataSetChanged();
                                movimentacaoActivity.calcularRendaMensal(valorAnterior, listaMovimentacao, view);
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

    public List<Movimentacao> listarMovimentacoes(List<Movimentacao> listaMovimentacao, String ano, String mes, Grupo grupo, MovimentacoesAdapter movimentacoesAdapter, Context c, View view){

        tarefas = getDatabaseMonthMovInstanceGrupo(ano, mes, grupo);

        MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();

        Double valorAnterior = movimentacaoActivity.calcularRendaAnterior(listaMovimentacao);

        tarefas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


//                if (!dataSnapshot.exists()) Toast.makeText(c, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();

                movimentacaoActivity.calcularRendaAnterior(listaMovimentacao);

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
                movimentacaoActivity.calcularRendaMensal(valorAnterior, listaMovimentacao, view);
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

    public void removerMovimentacao(Movimentacao movimentacao, MovimentacoesAdapter movimentacoesAdapter, int pos, Grupo grupo){

        MovimentacaoActivity movimentacaoActivity = new MovimentacaoActivity();

        String dataMov;
        //Trata campo data para agrupar em ano, mes e dia
        dataMov = DateCustom.firebaseFormatDate(movimentacao.getDataTarefa());
        //Chamada do método que retorna instancia do BD e nó de Movimentacao
        DatabaseReference movimentacoes;
        if (grupo == null ) movimentacoes = getDatabaseMovimentacaoInstance();
        else movimentacoes = getDatabaseMovimentacaoInstanceGrupo(grupo);
        //Modificação do método utilizado para agrupar em datas agora
        DatabaseReference movimentacoesKey = movimentacoes.child(dataMov).child(movimentacao.getID());

        movimentacoesKey.removeValue();
        movimentacoesAdapter.notifyItemRemoved(pos);

    }


    public void recuperarMovFuturas(Movimentacao movimentacao, MovimentacoesAdapter movimentacoesAdapter, int pos, Grupo grupo){

        String data[] = DateCustom.firebaseFormatDateBuild(movimentacao.getDataTarefa());
        int parcelasRestantes = (movimentacao.getParcelaTotal() - (movimentacao.getParcelaAtual()-1));
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
            if (grupo == null) tarefasExclusao = getDatabaseMonthMovInstance(anoStr, mesStr).child(movimentacao.getID());
            else tarefasExclusao = getDatabaseMonthMovInstanceGrupo(anoStr, mesStr, grupo).child(movimentacao.getID());

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

        movimentacoesAdapter.notifyItemRemoved(pos);

    }

    //Os métodos abaixo são todos referentes a exibição a GRUPO



    //Pega instancia da movimentação de acordo com o ano/mes/dia
    private DatabaseReference getDatabaseMonthMovInstanceGrupo(String year, String month, Grupo grupo){

        return refenciaDb.child("grupos").child(grupo.getGrupoId()).child("Movimentacoes").child(year).child(month);
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
