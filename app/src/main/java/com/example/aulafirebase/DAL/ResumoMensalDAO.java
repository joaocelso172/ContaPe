package com.example.aulafirebase.DAL;

import androidx.annotation.NonNull;

import com.example.aulafirebase.Model.ResumoMensal;
import com.example.aulafirebase.helper.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ResumoMensalDAO {

    private Double receitaMensal, despesaMensal;
    //Referencia ao BD configurado no json
    private final DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    private ResumoMensal resumoMensal = new ResumoMensal();

    private final FirebaseAuth mAuth = FirebaseConfig.getFirebaseAuth();

    public ResumoMensal getOrSubResumoMensal(String ano, String mes){

        getDatabaseResumo(ano, mes).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else {
                    resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, ano, mes);
                    setResumoMensal(resumoMensal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }

    public ResumoMensal getOrSubResumoMensal(String anoMes){

        getDatabaseResumo(anoMes).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    resumoMensal = dataSnapshot.getValue(ResumoMensal.class);
                }else {
                    resumoMensal = new ResumoMensal(0.0, 0.0, 0.0, anoMes);
                    setResumoMensal(resumoMensal, anoMes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return resumoMensal;
    }


    public Boolean setResumoMensal(ResumoMensal resumo){

        return getDatabaseResumo(resumo.getAno(), resumo.getMes()).setValue(resumo).isSuccessful();
    }

    public Boolean setResumoMensal(ResumoMensal resumo, String dataMov){

        return getDatabaseResumo(dataMov).setValue(resumo).isSuccessful();
    }

    private DatabaseReference getDatabaseResumo(String ano, String mes){

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes").child(ano).child(mes).child("ResumoMensal");
    }

    private DatabaseReference getDatabaseResumo(String anoMes){

        return refenciaDb.child("usuarios").child(Base64Custom.codificarBase64(mAuth.getCurrentUser().getEmail())).child("Movimentacoes").child(anoMes).child("ResumoMensal");
    }

    public Boolean atualizarSaldoMensal(ResumoMensal resumo){

        return getDatabaseResumo(resumo.getAno(), resumo.getMes()).child("saldoMensal").setValue(resumo.getSaldoMensal()).isSuccessful();
    }

}
