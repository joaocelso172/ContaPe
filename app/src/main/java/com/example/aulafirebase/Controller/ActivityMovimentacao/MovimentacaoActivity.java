package com.example.aulafirebase.Controller.ActivityMovimentacao;

import android.content.Intent;
import android.os.Bundle;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.BundleRecuperado;
import com.example.aulafirebase.Controller.ActivityGrupos.AddGrupoActivity;
import com.example.aulafirebase.Controller.ActivityGrupos.AddIntegranteGrupo;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.MovimentacaoListener;
import com.example.aulafirebase.R;
import com.example.aulafirebase.RecuperaBundle;
import com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoActivity extends AppCompatActivity implements RecuperaBundle, BundleRecuperado, MovimentacaoListener {

    //Calendario
    private MaterialCalendarView materialCalendarView;
    private MovimentacoesDAO movimentacoesDAO = new MovimentacoesDAO();
    //Variaveis de retorno
    private Double receitaTotal = movimentacoesDAO.getReceitaTotal(), despesaTotal = movimentacoesDAO.getDespesaTotal()
    , saldoFinal, alerta = movimentacoesDAO.getAlerta();
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
    private ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    //Contador
    int i = 0;
    private ResumoMensal resumoMensal;

    private Grupo grupoUsuario;

    private ImageButton imgBtnAddUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        Toolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaGrupo();
            }
        });

        if (!recuperaBundle()) {
            toolbar.setTitle("Movimentações Individuais");
        }else {
            toolbar.setTitle(grupoUsuario.getNomeGrupo());
        }

        imgBtnAddUsuario = findViewById(R.id.imgBtnAddPessoa);

        //Instancia o adapter
        movAdapter = new MovimentacoesAdapter(listaMov, getApplicationContext());

        //Calendário
        materialCalendarView = findViewById(R.id.materialCalendarMov);
        //Recycler
        rMov = findViewById(R.id.recyclerMov);

        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
        });

        imgBtnAddUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarBundle(0);
            }
        });

    }


    public void adicionarReceita(View view){

        Intent intentGanho = new Intent(this, AddGanhoActivity.class);
        intentGanho.putExtra("ano", anoSel);
        intentGanho.putExtra("mes", mesSel);
        if (recuperaBundle()) intentGanho.putExtra("grupo", grupoUsuario);
        startActivity(intentGanho);
    }

    public void adicionarDespesa(View view){

        Intent intentDespesa = new Intent(this, AddDespesaActivity.class);
        intentDespesa.putExtra("ano", anoSel);
        intentDespesa.putExtra("mes", mesSel);
        if (recuperaBundle()) intentDespesa.putExtra("grupo", grupoUsuario);
        startActivity(intentDespesa);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!recuperaBundle()) {
            imgBtnAddUsuario.setVisibility(View.GONE );
            //Ativa swipe
            swipe();
            //Método que será chamado sempre para atualizar a lista/recycler
            configurarCalendarView(null);
            //Recupera valor do topo da página
            recuperaSaldo();

        }else {
            imgBtnAddUsuario.setVisibility(View.VISIBLE);
            //Método que será chamado sempre para atualizar a lista/recycler
            configurarCalendarView(grupoUsuario);
            //Configura a recycler com os itens da lista
            //recuperaSaldo(grupoUsuario);

        }

        RecyclerViewConfig.ConfigurarRecycler(getApplicationContext(), rMov, movAdapter);
    }

    private void recuperaSaldo(){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
     //           resumoMensal = resumoMensalDAO.recuperarResumoMensal(materialCalendarView.getCurrentDate().getYear() + "/" + materialCalendarView.getCurrentDate().getMonth());
                //Verifica se o valor foi setado
                receitaTotal = movimentacoesDAO.getReceitaTotal();
                //Verifica se o valor foi setado
                despesaTotal = movimentacoesDAO.getDespesaTotal();
                //Verifica se o valor foi setado
                alerta = movimentacoesDAO.getAlerta();

                //Verifica se estão nulos
                if (receitaTotal != null && despesaTotal != null) {
                    //Caso não, subtrai saldo - despesa totais e mensais
                    saldoFinal = receitaTotal - despesaTotal;
                    //Atualiza saldo
            //        movimentacoesDAO.atualizarSaldo(saldoFinal);
                    //Converte em String, adiciona máscara
                    //Remove repetição
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }


    private void configurarCalendarView(Grupo grupo){

        CharSequence[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        materialCalendarView.setTitleMonths(meses);

        CharSequence[] semanas = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        materialCalendarView.setWeekDayLabels(semanas);

        anoSel = String.valueOf(materialCalendarView.getCurrentDate().getYear());
        mesSel = String.valueOf(materialCalendarView.getCurrentDate().getMonth());

        //Se for menor que dez, adiciona zero
        if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);

        if (grupo == null) recuperarDadosMes(anoSel, mesSel);
        else recuperarDadosMes(anoSel, mesSel, grupo);
        Log.i("Logando", anoSel + "/" + mesSel);

        //Quando alterado o mês
        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            anoSel = String.valueOf(date.getYear());
            //Talvez seja necessário adicionar +1
            mesSel = String.valueOf(date.getMonth());
            //Se for menor que dez, adiciona zero
            if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);
            //Recupera os valores mensais
            if (grupo == null) recuperarDadosMes(anoSel, mesSel);
            else recuperarDadosMes(anoSel, mesSel, grupo);
        });
    }

    private void recuperarDadosMes(String ano, String mes){
        //Consulta lista
        movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, movAdapter, this, findViewById(android.R.id.content));
//        recuperarSaldoMensal(ano, mes);


   /*     i=0;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Verifica conexão com internet
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                //Consulta lista
                movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, movAdapter);
//                if (movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes)) {
                    if (!listaMov.isEmpty()) { //Quando não está vazia significa que foi recebido os valores da lista, se está vazia, ou os valores não foram recebidos ou a lista não retorna nada, se não retorna nada, o saldo deve ser 0
                        //Puxa método que recupera saldo mensal com loading de 1seg
                    } else {
                        Toast.makeText(MovimentacaoActivity.this, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();
                    }
                    recuperarSaldoMensal(ano, mes);
//                    movAdapter.notifyDataSetChanged();
//                }
            }
        }, 250);*/
    }


    public void notifyItemsResumoChanged(View view, ResumoMensal resumoMensalRecebido){

        resumoMensal = resumoMensalRecebido;

        TextView txtReceitaMensal, txtDespesaMensal, txtSaldo;
        ProgressBar progressBarSaldo = view.findViewById(R.id.progressBarSaldo);

        DecimalFormat decimalFormat = new DecimalFormat("0.##");

        txtDespesaMensal = view.findViewById(R.id.txtCustoMensal);
        txtReceitaMensal = view.findViewById(R.id.txtReceitaMensal);
        txtSaldo = view.findViewById(R.id.txtSaldo);


        txtReceitaMensal.setText("Receita:\nR$ " + decimalFormat.format(resumoMensal.getReceitaMensal()));
        txtDespesaMensal.setText("Despesa:\nR$ " + decimalFormat.format(resumoMensal.getDespesaMensal()));
        txtSaldo.setText("R$ " + decimalFormat.format(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal()));
        progressBarSaldo.setVisibility(View.GONE);

    }

    private void recuperarDadosMes(String ano, String mes, Grupo grupo){
    //    listaMov = null;
        //Consulta lista
        listaMov = movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, grupo, movAdapter, MovimentacaoActivity.this, findViewById(android.R.id.content));
//        recuperarSaldoMensal(ano, mes, grupo);
  /*      i=0;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Verifica conexão com internet
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                //Consulta lista
                listaMov = movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, grupo, movAdapter);
                if (!listaMov.isEmpty()){ //Quando não está vazia significa que foi recebido os valores da lista, se está vazia, ou os valores não foram recebidos ou a lista não retorna nada, se não retorna nada, o saldo deve ser 0
                    //Puxa método que recupera saldo mensal com loading de 1seg
                }else {
                    Toast.makeText(MovimentacaoActivity.this, "Não há movimentações para este período", Toast.LENGTH_SHORT).show();
                }
                recuperarSaldoMensal(ano, mes, grupo);
//                movAdapter.notifyDataSetChanged();
            }
        }, 350);*/
    }


    //Construtor Grupo
    private void recuperarSaldoMensal(String ano, String mes, Grupo grupo){ //Método que controle o txtSaldo e retorno de itens
        //Seta os textos como vazio e exibe loading
       /* txtReceitaMensal.setText("");
        txtDespesaMensal.setText("");
        txtSaldo.setText("");
        progressBarSaldo.setVisibility(View.VISIBLE);
        resumoMensal = resumoMensalDAO.recuperarResumoMensal(ano + "/" + mes, findViewById(android.R.id.content), grupo);*/
 /*       Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resumoMensal = resumoMensalDAO.recuperarResumoMensal(ano + "/" + mes, grupo);
                if (resumoMensal.getReceitaMensal() != null) { //Quando não está nulo significa que foi recebido o valor
                    if (resumoMensal.getAnoMes().equals(ano + "/" + mes)) { //Se data e ano do resumo forem igual à fornecida, irá retornar infos
                        Log.i("Logando", "Resumo, saldo: " + resumoMensal.getSaldoMensal() + " ano " + ano + " mes " + mes);
                        //Retorna as infos como desejado
                        txtReceitaMensal.setText("Receita:\nR$ " + decimalFormat.format(resumoMensal.getReceitaMensal()));
                        txtDespesaMensal.setText("Despesa:\nR$ " + decimalFormat.format(resumoMensal.getDespesaMensal()));
                        txtSaldo.setText("R$ " + decimalFormat.format(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal()));
                        //Esconde loading
                        progressBarSaldo.setVisibility(View.GONE);
                    }else { //Significa que o Resumo do mes atual ainda não retornou
                        Log.i("Logando", "Data resumo: " + resumoMensal.getAnoMes() + ", Data calendário: " + ano + "/" + mes);
                        handler.postDelayed(this, 30); }
                }else { //Significa que o valor ainda está nulo
                    Log.i("Logando", "Resumo, valor nulo");
                    handler.postDelayed(this, 30); }
            }
        }, 1000);*/

    }

    //Construtor individual
  /*  private void recuperarSaldoMensal(String ano, String mes){ //Método que controle o txtSaldo e retorno de itens
        //Seta os textos como vazio e exibe loading
        txtReceitaMensal.setText("");
        txtDespesaMensal.setText("");
        txtSaldo.setText("");
        progressBarSaldo.setVisibility(View.VISIBLE);
        resumoMensal = resumoMensalDAO.recuperarResumoMensal(ano + "/" + mes, findViewById(android.R.id.content));

    }*/

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
        alertDialogExcluir.setMessage("Você tem certeza de que deseja excluir movimentação?\nEsta ação não pode ser revertida.");
        alertDialogExcluir.setCancelable(false);
        //Configurando botões
        alertDialogExcluir.setPositiveButton("Excluir", (dialog, which) -> {
            //Pega posição do item alterado na lista
            int position = viewHolder.getAdapterPosition();
            //Pega o objeto específico na lista
            movimentacao = listaMov.get(position);
            //Chama método DAO de exclusão
            movimentacoesDAO.removerMovimentacao(movimentacao, movAdapter, position);
            //Remove item da lista
            listaMov.remove(position);
            //Atualiza saldo
            atualizarSaldo(movimentacao);
            //Notifica o recycler da exclusão
//            movAdapter.notifyItemRemoved(position);

        });

        alertDialogExcluir.setNegativeButton("Cancelar", (dialog, which) -> movAdapter.notifyDataSetChanged());

        alertDialogExcluir.show();

    }

    public void notifyMovimentacaoRemoved(){




    }

    private void atualizarSaldo(Movimentacao movimentacao){

     //   resumoMensal = resumoMensalDAO.getResumoMensal();

    if (movimentacao.getTipo().equals("r")){
            //Atualiza saldo geral
            Double receitaAtualizada = movimentacoesDAO.getReceitaTotal() - movimentacao.getValor();
            movimentacoesDAO.atualizarReceita(receitaAtualizada);
            saldoFinal -= movimentacao.getValor();
            //Atualiza saldo mensal, PUXAR DATA DA MOVIMENTACAO PARA LOCALIZAR RESUMO CORRETO
 //           resumoMensal.setReceitaMensal(resumoMensal.getReceitaMensal() - movimentacao.getValor());

        }else {
            Double despesaAtualizada = movimentacoesDAO.getDespesaTotal() - movimentacao.getValor();
            movimentacoesDAO.atualizarDespesa(despesaAtualizada);
            saldoFinal += movimentacao.getValor();
            //Atualiza saldo mensal
//            resumoMensal.setDespesaMensal(resumoMensal.getDespesaMensal() - movimentacao.getValor());
        }
/*        resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());

        resumoMensalDAO.setResumoMensal(resumoMensal);

        movimentacoesDAO.atualizarSaldo(saldoFinal);*/

        calcularRendaMensal(listaMov, findViewById(android.R.id.content));

//        recuperarSaldoMensal(anoSel, mesSel);
    }

    private void telaGrupo(){
        Intent intentGrupo = new Intent(this, AddGrupoActivity.class);
        startActivity(intentGrupo);
    }

    //Método que verifica se há valor no Bundle, caso tenha, significa que a activity se trata de uma
    //tela de movimentação de GRUPO
    @Override
    public Boolean recuperaBundle() {
        Intent intent = getIntent();
        grupoUsuario = (Grupo) intent.getSerializableExtra("grupo");
        if (grupoUsuario == null) return false;

        return true;
    }

    @Override
    public void enviarBundle(int pos) {
        Intent intent = new Intent(getApplicationContext(), AddIntegranteGrupo.class);
        intent.putExtra("grupo", grupoUsuario);
        startActivity(intent);
    }

    @Override
    public void houveAlteracao() {

        movAdapter.notifyDataSetChanged();

    }

    public void calcularRendaMensal(List<Movimentacao> listaMovimentacoes, View view){

        TextView txtReceitaMensal;
        TextView txtDespesaMensal;
        TextView txtSaldo;
        ProgressBar progressBarSaldo;
        progressBarSaldo = view.findViewById(R.id.progressBarSaldo);
        txtSaldo = view.findViewById(R.id.txtSaldo);
        txtDespesaMensal = view.findViewById(R.id.txtCustoMensal);
        txtReceitaMensal = view.findViewById(R.id.txtReceitaMensal);

        txtReceitaMensal.setText("");
        txtDespesaMensal.setText("");
        txtSaldo.setText("");
//        progressBarSaldo.setVisibility(View.VISIBLE);

        Double despesaTotalMes = 0.0, receitaTotalMes = 0.0;

        for ( Movimentacao mov : listaMovimentacoes ){

            if (mov.getTipo().equals("r")) receitaTotalMes = receitaTotalMes + mov.getValor();
            else if (mov.getTipo().equals("d")) despesaTotalMes = despesaTotalMes + mov.getValor();

        }

        progressBarSaldo.setVisibility(View.GONE);
        txtReceitaMensal.setText("Receita:\nR$ " + decimalFormat.format(receitaTotalMes));
        txtDespesaMensal.setText("Despesa:\nR$ " + decimalFormat.format(despesaTotalMes));
        txtSaldo.setText("R$ " + decimalFormat.format(receitaTotalMes - despesaTotalMes));


    }

}
