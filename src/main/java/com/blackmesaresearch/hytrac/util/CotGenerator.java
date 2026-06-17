package com.blackmesaresearch.hytrac.util;

import java.time.LocalDate;
import java.util.UUID;

public class CotGenerator {


    public static String generar() {

        String fecha =
                LocalDate.now()
                .toString()
                .replace("-", "");


        String random =
                UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();


        return "COT-"
                + fecha
                + "-"
                + random;
    }

}