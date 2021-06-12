package com.example.aulafirebase.Controller.ActivityMovimentacao;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.example.aulafirebase.Adapter.MovimentacoesAdapter;
import com.example.aulafirebase.BundleRecuperado;
import com.example.aulafirebase.Controller.ActivityGrupos.AddGrupoActivity;
import com.example.aulafirebase.Controller.ActivityGrupos.AddIntegranteGrupo;
import com.example.aulafirebase.Controller.ActivityGrupos.ListarGruposActivity;
import com.example.aulafirebase.Controller.ActivityLogin.LoginActivity;
import com.example.aulafirebase.DAL.FirebaseConfig;
import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.example.aulafirebase.DAL.ResumoMensalDAO;
import com.example.aulafirebase.DAL.UsuarioGrupoDAO;
import com.example.aulafirebase.Model.Grupo;
import com.example.aulafirebase.Model.Movimentacao;
import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.Model.UsuarioGrupo;
import com.example.aulafirebase.MovimentacaoListener;
import com.example.aulafirebase.R;
import com.example.aulafirebase.RecuperaBundle;
import com.example.aulafirebase.RecyclerViewConfig.RecyclerViewConfig;
import com.example.aulafirebase.helper.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoActivity extends AppCompatActivity implements RecuperaBundle, BundleRecuperado, MovimentacaoListener {

    private FirebaseAuth auth;
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
    private List<Grupo> listaGrupos = new ArrayList<>();
    private List<Movimentacao> ListaMovGrupos = new ArrayList<>();
    //Objeto movimentacao
    private Movimentacao movimentacao;
    //Adapter recycler
    private MovimentacoesAdapter movAdapter;
    //Alterar padrão de exibição
    private final DecimalFormat decimalFormat = new DecimalFormat("0.##");
    private ResumoMensalDAO resumoMensalDAO = new ResumoMensalDAO();
    private ResumoMensal resumoMensal;

    private Grupo grupoUsuario;
    private Double receitaAnteriorD, despesaAnteriorD;

    private ImageButton imgBtnAddUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (!recuperaBundle()) {
            getSupportActionBar().setTitle("Movimentações");
        }else {
            getSupportActionBar().setTitle(grupoUsuario.getNomeGrupo());
        }

        imgBtnAddUsuario = findViewById(R.id.imgBtnAddPessoa);


        //Instancia o adapter
        movAdapter = new MovimentacoesAdapter(listaMov, getApplicationContext(), grupoUsuario);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mov, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                auth = FirebaseConfig.getFirebaseAuth();
                auth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.menuCriarGrupos:
                telaCriarGrupo();
                finish();
                break;
            case R.id.menuListarGrupos:
                telaListaGrupo();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarReceita(View view){

        Intent intentGanho = new Intent(this, AddDespesaActivity.class);
        intentGanho.putExtra("ano", anoSel);
        intentGanho.putExtra("mes", mesSel);
        intentGanho.putExtra("tipo", "r");
        if (recuperaBundle()) intentGanho.putExtra("grupo", grupoUsuario);
        startActivity(intentGanho);
    }

    public void adicionarDespesa(View view){

        Intent intentDespesa = new Intent(this, AddDespesaActivity.class);
        intentDespesa.putExtra("ano", anoSel);
        intentDespesa.putExtra("mes", mesSel);
        intentDespesa.putExtra("tipo", "d");
        if (recuperaBundle()) intentDespesa.putExtra("grupo", grupoUsuario);
        startActivity(intentDespesa);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!recuperaBundle()) {
            imgBtnAddUsuario.setVisibility(View.GONE );
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

        //Ativa edição por click
        editarMov(grupoUsuario);
        //Ativa swipe
        swipe(grupoUsuario);

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

        recuperarDadosMes(anoSel, mesSel, grupo);
        Log.i("Logando", anoSel + "/" + mesSel);

        //Quando alterado o mês
        materialCalendarView.setOnMonthChangedListener((widget, date) -> {
            anoSel = String.valueOf(date.getYear());
            //Talvez seja necessário adicionar +1
            mesSel = String.valueOf(date.getMonth());
            //Se for menor que dez, adiciona zero
            if (Integer.parseInt(mesSel) < 10) mesSel = "0" + Integer.parseInt(mesSel);
            //Recupera os valores mensais
            recuperarDadosMes(anoSel, mesSel, grupo);
        });
    }

   /* private void recuperarDadosMes(String ano, String mes){
        //Consulta lista
        movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, movAdapter, this, findViewById(android.R.id.content));
    }*/

    private void recuperarDadosMes(String ano, String mes, Grupo grupo){
        if (grupo == null) {
            movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, movAdapter, this, findViewById(android.R.id.content));

        }
        else listaMov = movimentacoesDAO.listarMovimentacoes(listaMov, ano, mes, grupo, movAdapter, MovimentacaoActivity.this, findViewById(android.R.id.content));
    }

    private void recuperarDadosGrupos(String ano, String mes, Grupo grupo) { //Método para recuperar as movimetnacoes de grupos deste usuário e exibir na home

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

    private void swipe(Grupo grupo){

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
                excluirMov(viewHolder, grupo);
            }
        };

        new ItemTouchHelper(itemTouch).attachToRecyclerView(rMov);
    }

    private void editarMov(Grupo grupo){
        rMov.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(), rMov,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                iniciarDialogoEdição(position, grupo);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void iniciarDialogoEdição(int pos, Grupo grupo){

        if ( (listaMov.get(pos).getAtribuicao() != null && grupo == null) ) {
            Toast.makeText(this, "Esta movimentação é de um grupo, peça a um administrador para editar!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intentEdit = new Intent(this, AddDespesaActivity.class);

            intentEdit.putExtra("movEdit", listaMov.get(pos));
            intentEdit.putExtra("grupo", grupo);
            startActivity(intentEdit);
        }
    }

    private void excluirMov(RecyclerView.ViewHolder viewHolder, Grupo grupo) {

        AlertDialog.Builder alertDialogExcluir = new AlertDialog.Builder(this);
        //Pega posição do item alterado na lista
        int position = viewHolder.getAdapterPosition();
        //Pega o objeto específico na lista
        movimentacao = listaMov.get(position);
        if ( (movimentacao.getAtribuicao() != null && grupo == null) ) {
            Toast.makeText(this, "Esta movimentação é de um grupo, peça a um administrador para excluir!", Toast.LENGTH_SHORT).show();
            movAdapter.notifyDataSetChanged();
        } else {
            //Configurando AlertDialog
            alertDialogExcluir.setTitle("Excluir movimentação");
            alertDialogExcluir.setMessage("Você tem certeza de que deseja excluir movimentação?\nEsta ação não pode ser revertida.");
            alertDialogExcluir.setCancelable(false);

            //Configurando botões
            if (!movimentacao.getTipoFaturamento().equals("aVista")) {
                alertDialogExcluir.setPositiveButton("Excluir todas as parcelas", (dialog, which) -> {
                    //Chama método DAO de exclusão
                    movimentacoesDAO.recuperarMovFuturas(movimentacao, movAdapter, position, grupo);
                    notifyMovimentacaoRemoved(position);

                });
            }

            alertDialogExcluir.setNegativeButton("Excluir", (dialog, which) -> {
                //Chama método DAO de exclusão
                movimentacoesDAO.removerMovimentacao(movimentacao, movAdapter, position, grupo);
                notifyMovimentacaoRemoved(position);

            });


            alertDialogExcluir.setNeutralButton("Cancelar", (dialog, which) -> movAdapter.notifyDataSetChanged());

            alertDialogExcluir.show();
        }

    }

    private void notifyMovimentacaoRemoved(int pos){
        //Remove item da lista
        listaMov.remove(pos);
        //Atualiza saldo
        calcularRendaMensal(null, listaMov, findViewById(android.R.id.content));

    }

    private void atualizarSaldo(Movimentacao movimentacao){

     //   resumoMensal = resumoMensalDAO.getResumoMensal();

   /* if (movimentacao.getTipo().equals("r")){
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
        }*/
/*        resumoMensal.setSaldoMensal(resumoMensal.getReceitaMensal() - resumoMensal.getDespesaMensal());

        resumoMensalDAO.setResumoMensal(resumoMensal);

        movimentacoesDAO.atualizarSaldo(saldoFinal);*/

        calcularRendaMensal(null, listaMov, findViewById(android.R.id.content));

//        recuperarSaldoMensal(anoSel, mesSel);
    }

    private void telaCriarGrupo(){
        Intent intentGrupo = new Intent(this, AddGrupoActivity.class);
        startActivity(intentGrupo);
    }

    private void telaListaGrupo(){
        Intent intentGrupo = new Intent(this, ListarGruposActivity.class);
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

    public void setarLoading (View view){
        ProgressBar progressBarSaldo = view.findViewById(R.id.progressBarSaldo);
        progressBarSaldo.setVisibility(View.VISIBLE);

        TextView txtReceitaMensal;
        TextView txtDespesaMensal;
        TextView txtSaldo;
        txtSaldo = view.findViewById(R.id.txtSaldo);
        txtDespesaMensal = view.findViewById(R.id.txtCustoMensal);
        txtReceitaMensal = view.findViewById(R.id.txtReceitaMensal);
        txtReceitaMensal.setText("");
        txtDespesaMensal.setText("");
        txtSaldo.setText("");
    }

    public Double calcularRendaAnterior(List<Movimentacao> listaMovimentacoes){

        Double receitaAnterior = 0.0, despesaAnterior = 0.0;

        for ( Movimentacao mov : listaMovimentacoes ){

            if (mov.getTipo().equals("r")) receitaAnterior = receitaAnterior + mov.getValor();
            else if (mov.getTipo().equals("d")) despesaAnterior = despesaAnterior + mov.getValor();

        }

        return receitaAnterior - despesaAnterior;

    }

    public void calcularRendaMensal(Double valorAntigo, List<Movimentacao> listaMovimentacoes, View view){

        TextView txtRemanescente = view.findViewById(R.id.txtRemanescente);
       // if ( !valorAntigo.equals(null) ) txtRemanescente.setText("Remanescente:\nR$ " + valorAntigo);
        TextView txtReceitaMensal;
        TextView txtDespesaMensal;
        TextView txtSaldo;
        ConstraintLayout constraintValor;
        ProgressBar progressBarSaldo;
        progressBarSaldo = view.findViewById(R.id.progressBarSaldo);
        txtSaldo = view.findViewById(R.id.txtSaldo);
        txtDespesaMensal = view.findViewById(R.id.txtCustoMensal);
        txtReceitaMensal = view.findViewById(R.id.txtReceitaMensal);
        constraintValor = view.findViewById(R.id.constraintValor);

        txtReceitaMensal.setText("");
        txtDespesaMensal.setText("");
        txtSaldo.setText("");

        Double despesaTotalMes = 0.0, receitaTotalMes = 0.0;

        for ( Movimentacao mov : listaMovimentacoes ){

            if (mov.getTipo().equals("r")) receitaTotalMes = receitaTotalMes + mov.getValor();
            else if (mov.getTipo().equals("d")) despesaTotalMes = despesaTotalMes + mov.getValor();

        }

        if ( (receitaTotalMes - despesaTotalMes) < 0) constraintValor.setBackgroundColor(Color.rgb(252, 137, 81));
        else constraintValor.setBackgroundColor(Color.rgb(67, 197, 165));

        progressBarSaldo.setVisibility(View.GONE);
        txtReceitaMensal.setText("Receita:\nR$ " + decimalFormat.format(receitaTotalMes));
        txtDespesaMensal.setText("Despesa:\nR$ " + decimalFormat.format(despesaTotalMes));

        Double valor = receitaTotalMes - despesaTotalMes;
        if (valor >= 0) txtSaldo.setText("  R$ " + decimalFormat.format(valor));
        else txtSaldo.setText("- R$ " + decimalFormat.format(valor*-1));


    }

}
