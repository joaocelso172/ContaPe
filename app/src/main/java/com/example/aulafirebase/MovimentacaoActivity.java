package com.example.aulafirebase;

import android.content.Intent;
import android.os.Bundle;

import com.example.aulafirebase.DAL.MovimentacoesDAO;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MovimentacaoActivity extends AppCompatActivity {

    private MaterialCalendarView materialCalendarView;

    private MovimentacoesDAO movimentacoesDAO = new MovimentacoesDAO();

    //Variaveis de retorno
    private Double receitaTotal = movimentacoesDAO.getReceitaTotal(), despesaTotal = movimentacoesDAO.getDespesaTotal()
    , saldoFinal, alerta = movimentacoesDAO.getAlerta();

    private TextView txtSaldo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSaldo = findViewById(R.id.txtSaldo);

        materialCalendarView = findViewById(R.id.materialCalendarMov);

        /*materialCalendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2017, 1, 1))
                .setMaximumDate(CalendarDay.from(2024, 1, 1));*/
        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro","Outubro", "Novembro", "Dezembro"};
        materialCalendarView.setTitleMonths(meses);

        CharSequence semanas[] = { "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        materialCalendarView.setWeekDayLabels(semanas);


        materialCalendarView.setWeekDayLabels(semanas);

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            }
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

        recuperaSaldo();
    }

    private void recuperaSaldo(){

        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                //Alterar padrão de exibição
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
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
                    //Remove repetição
                    handler.removeCallbacks(this);
                }else {
                    handler.postDelayed(this, 1000);
                    Toast.makeText(getApplicationContext(), "Null, " + receitaTotal, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mensagemUsuario(View viewSnack){

        Snackbar.make(viewSnack, "Botão", Snackbar.LENGTH_INDEFINITE).show();


    }
}
