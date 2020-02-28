package com.example.aulafirebase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoActivity extends AppCompatActivity {

    private MaterialCalendarView materialCalendarView;
    private final MovimentacoesDAO movimentacoesDAO = new MovimentacoesDAO();
    //Variaveis de retorno
    private Double receitaTotal = movimentacoesDAO.getReceitaTotal(), despesaTotal = movimentacoesDAO.getDespesaTotal()
    , saldoFinal, alerta = movimentacoesDAO.getAlerta();
    private TextView txtSaldo;
    //Recycler mov
    private RecyclerView rMov;
    //Ano, mes e dia
    private String anoSel, mesSel, diaSel;
    //Lista de objetos movimentacao
    private List<Movimentacao> listaMov = new ArrayList<>();
    //Objeto movimentacao
    private Movimentacao movimentacao;
    //Adapter recycler
    private MovimentacoesAdapter movAdapter;
    //Alterar padrão de exibição
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    //ResumoDAO
    private final ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Resumo mensal
    private final ResumoMensal resumoMensal = new ResumoMensal();
    private Double receitaMensal = resumoMensal.getReceitaMensal(), despesaMensal = resumoMensal.getDespesaMensal(), saldoMensal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Instancia o adapter
        movAdapter = new MovimentacoesAdapter(listaMov, getApplicationContext());
        //Text que exibe o saldo final
        txtSaldo = findViewById(R.id.txtSaldo);
        //Calendário
        materialCalendarView = findViewById(R.id.materialCalendarMov);
        //Recycler
        rMov = findViewById(R.id.recyclerMov);
        //Ativa swipe
        swipe();

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {

        });

    }




    public void adicionarReceita(View view){
        Intent intentGanho = new Intent(this, AddGanhoActivity.class);
        startActivity(intentGanho);
    }

    public void adicionarDespesa(View view){
        Intent intentDespesa = new Intent(this, AddDespesaActivity.class);
        startActivity(intentDespesa);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Método que será chamado sempre para atualizar a lista/recycler
        configurarCalendarView();
       //Configura a recycler com os itens da lista
        RecyclerViewConfig.ConfigurarRecycler(getApplicationContext(), rMov, movAdapter);
        //Recupera valor do topo da página
        recuperaSaldo();
    }

    private void recuperaSaldoMensal(){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se o valor foi setado
                receitaMensal = resumoMensal.getReceitaMensal();
                //Verifica se o valor foi setado
                despesaMensal = resumoMensal.getDespesaMensal();
                //Verifica se o valor foi setado
                alerta = movimentacoesDAO.getAlerta();

                //Verifica se estão nulos
                if (receitaMensal != null && despesaMensal != null) {
                    //Caso não, subtrai saldo - despesa
                    saldoMensal = receitaMensal - despesaMensal;
                    resumoMensal.setSaldoMensal(saldoMensal);
                    //Atualiza saldo
                    resumoMensalDAO.atualizarSaldoMensal(resumoMensal);
                    //Converte em String, adiciona máscara
                    txtSaldo.setText("R$ " + decimalFormat.format(saldoMensal));
                    Toast.makeText(MovimentacaoActivity.this, "Saldo " + saldoFinal, Toast.LENGTH_SHORT).show();
                    //Remove repetição
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this, 1000);
                    Toast.makeText(getApplicationContext(), "Null, " + receitaTotal, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void recuperaSaldo(){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                //Verifica se o valor foi setado
                receitaTotal = movimentacoesDAO.getReceitaTotal();
                //Verifica se o valor foi setado
                despesaTotal = movimentacoesDAO.getDespesaTotal();
                //Verifica se o valor foi setado
                alerta = movimentacoesDAO.getAlerta();

                //Verifica se estão nulos
                if (receitaTotal != null && despesaTotal != null) {
                    //Caso não, subtrai saldo - despesa
                    saldoFinal = receitaTotal - despesaTotal;
                    //Atualiza saldo
                    movimentacoesDAO.atualizarSaldo(saldoFinal);
                    //Converte em String, adiciona máscara
                    txtSaldo.setText("R$ " + decimalFormat.format(saldoFinal));
                    Toast.makeText(MovimentacaoActivity.this, "Saldo " + saldoFinal, Toast.LENGTH_SHORT).show();
                    //Remove repetição
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this, 1000);
                    Toast.makeText(getApplicationContext(), "Null, " + receitaTotal, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void configurarCalendarView(){
        /*materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2017, 1, 1))
                .setMaximumDate(CalendarDay.from(2024, 1, 1));*/

        CharSequence[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        materialCalendarView.setTitleMonths(meses);

        CharSequence[] semanas = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        materialCalendarView.setWeekDayLabels(semanas);

        anoSel = String.valueOf(materialCalendarView.getCurrentDate().getYear());
        mesSel = String.valueOf(materialCalendarView.getCurrentDate().getMonth());
        diaSel = String.valueOf(materialCalendarView.getCurrentDate().getDay());

        //Se for menor que dez, adiciona zero
        if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);

        recuperarLista(anoSel, mesSel);

        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            anoSel = String.valueOf(date.getYear());
            //Talvez seja necessário adicionar +1
            mesSel = String.valueOf(date.getMonth());
            diaSel = String.valueOf(date.getDay());

            //Se for menor que dez, adiciona zero
            if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);
            //Recupera os valores
            recuperarLista(anoSel, mesSel);
            //Recuperar saldo mensal aqui


            Log.i("Logando", anoSel + "/" + mesSel);
        });
    }


    private void recuperarLista(String ano, String mes){
        //Consulta lista
        listaMov = movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes);
      /*  if (resumoMensalDAO.getResumoMensal(ano, mes) != null) {
            resumoMensal = resumoMensalDAO.getResumoMensal(ano, mes);
        }*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                if (!listaMov.isEmpty()){
                   /* resumoMensal.setAno(ano);
                    resumoMensal.setMes(mes);
                    resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());
                    resumoMensalDAO.setResumoMensal(resumoMensal);*/
                    //Quando não está vazia significa que foi recebido os valores
                    handler.removeCallbacks(this);
                }else i++;
                if (i > 5) {
                    handler.removeCallbacks(this);
                    Toast.makeText(MovimentacaoActivity.this, "Não há informações para este mês", Toast.LENGTH_SHORT).show();
                }

                movAdapter.notifyDataSetChanged();
            }
        }, 100);
    }

    private void swipe(){

        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

                //Define drag and drop
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                //Define como será o swipe, neste caso da direita p esquerda e vice-versa
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //Retorna ações
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Método que exclui item da lista e bd
                excluirMov(viewHolder);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(rMov);
    }

    private void excluirMov(RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialogExcluir = new AlertDialog.Builder(this);

        //Configurando AlertDialog
        alertDialogExcluir.setTitle("Excluir movimentação");
        alertDialogExcluir.setMessage("Você tem certeza de que deseja excluir movimentação?\n Esta ação não pode ser revertida.");
        alertDialogExcluir.setCancelable(false);
        //Configurando botões
        alertDialogExcluir.setPositiveButton("Excluir", (dialog, which) -> {
            //Pega posição do item alterado na lista
            int position = viewHolder.getAdapterPosition();
            //Pega o objeto específico na lista
            movimentacao = listaMov.get(position);
            //Chama método DAO de exclusão
            movimentacoesDAO.removerMovimentacao(movimentacao);
            //Remove item da lista
            listaMov.remove(position);
            //Atualiza saldo
            atualizarSaldo(movimentacao);
            //Notifica o recycler da exclusão
            movAdapter.notifyItemRemoved(position);

        });

        alertDialogExcluir.setNegativeButton("Cancelar", (dialog, which) -> movAdapter.notifyDataSetChanged());

        alertDialogExcluir.show();

    }

    private void atualizarSaldo(Movimentacao movimentacao){

    if (movimentacao.getTipo().equals("r")){
            Double receitaAtualizada = movimentacoesDAO.getReceitaTotal() - movimentacao.getValor();
            movimentacoesDAO.atualizarReceita(receitaAtualizada);
            saldoFinal -= movimentacao.getValor();
        }else {
            Double despesaAtualizada = movimentacoesDAO.getDespesaTotal() - movimentacao.getValor();
            movimentacoesDAO.atualizarDespesa(despesaAtualizada);
            saldoFinal += movimentacao.getValor();
        }

        movimentacoesDAO.atualizarSaldo(saldoFinal);
        txtSaldo.setText("R$ " + decimalFormat.format(saldoFinal));

    }
}
