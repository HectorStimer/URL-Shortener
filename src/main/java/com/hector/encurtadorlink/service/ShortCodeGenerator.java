package com.hector.encurtadorlink.service;

import org.springframework.stereotype.Service;

import java.util.Random;


@Service
public class ShortCodeGenerator {

     private final static String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

     Random rand = new Random();



     public String generate(){
         char[] base62Array = base62.toCharArray();
         char[] shortCode = new char[6];

         for(int i=0; i<6; i++){

             int indice = rand.nextInt(base62Array.length);
             shortCode[i]= base62Array[indice];;

         }
         return new String(shortCode);


     }
}
