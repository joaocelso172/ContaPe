package com.example.aulafirebase.DAL;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GrupoDAO {

    //Referencia ao BD configurado no json
    private DatabaseReference refenciaDb = FirebaseDatabase.getInstance().getReference();

    //Referencia ao branch de usuarios feito baseada numa referencia geral já existente
    private DatabaseReference usuarios = refenciaDb.child("comunicade");


}
