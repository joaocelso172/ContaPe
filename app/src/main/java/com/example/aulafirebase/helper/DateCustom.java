package com.example.aulafirebase.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){

        long date = System.currentTimeMillis();

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return simpleDateFormat.format(date);
    }

    public static String horaAtual(){

        long date = System.currentTimeMillis();

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        return simpleDateFormat.format(date);
    }

    public static String firebaseFormatDate(String data){

        //Quebra a string no padrão escrito, neste caso, quebrará a cada '/'
        String[] dataFormatada = data.split("/");

        //Estabelece um novo padrão, agora todos são separados por Hífen
        String dataFormatoNovo = dataFormatada[0] + " - "
                + dataFormatada[1]
                + " - " + dataFormatada[2];

        //Pega novamente a data completa e quebra a cada ' - '.
        String[] dataBuilder = dataFormatoNovo.split(" - ");

        //Pega o indice 0, equivalente ao dia no nosso formato estabelecido
        String dia = dataBuilder[0];
        //Pega o indice 1, equivalente ao mes no nosso formato estabelecido
        String mes = dataBuilder[1];
        //Pega o indice 2, equivalente ao ano no nosso formato estabelecido
        String ano = dataBuilder[2];
        /*//Pega o indice 3, equivalente a hora + minutos no nosso formato estabelecido - DEPRECATED
        String hora = dataBuilder[3];*/


        return ano + "/" + mes;
    }


    public static String udemyFormatDate(String data){

        //Quebra a string no padrão escrito, neste caso, quebrará a cada '/'
        String[] dataFormatada = data.split("/");

        //Pega o indice 0, equivalente ao dia no nosso formato estabelecido
        String dia = dataFormatada[0];
        //Pega o indice 1, equivalente ao mes no nosso formato estabelecido
        String mes = dataFormatada[1];
        //Pega o indice 2, equivalente ao ano no nosso formato estabelecido
        String ano = dataFormatada[2];


        return mes + ano;
    }

    public static String[] recuperarHora(String hora){
        //Quebra a string no padrão escrito, neste caso, quebrará a cada '/'
        String[] horaBuilder = hora.split(":");

        return horaBuilder;
    }

    public static String recuperarMinuto(String hora){

        //Quebra a string no padrão escrito, neste caso, quebrará a cada '/'
        String[] minutoBuilder = hora.split(":");

        //Pega o indice 0, equivalente ao dia no nosso formato estabelecido
 //       String horaNova = horaBuilder[0];
        //Pega o indice 1, equivalente ao mes no nosso formato estabelecido
        String minutoNovo = "10";

        return minutoNovo;
    }

}
