 package com.hypergryph.arknights.core.function;
 
 import java.security.SecureRandom;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 public class randomPwd
 {
   private static final String lowStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
   private static final String hashStr = "abcdef";
   private static final String specialStr = "~!@#$%/";
   private static final String numStr = "0123456789";
   
   private static char getRandomChar(String str) {
     SecureRandom random = new SecureRandom();
     return str.charAt(random.nextInt(str.length()));
   }
 
   
   private static char getLowChar() {
     return getRandomChar("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
   }
 
   
   private static char getUpperChar() {
     return Character.toUpperCase(getLowChar());
   }
 
   
   private static char getNumChar() {
     return getRandomChar("0123456789");
   }
   
   private static char getHashChar() {
     return getRandomChar("abcdef");
   }
   
   private static char getSpecialChar() {
     return getRandomChar("~!@#$%/");
   }
 
   
   private static char getRandomChar(int funNum) {
     switch (funNum) {
       case 0:
         return getLowChar();
       case 1:
         return getUpperChar();
       case 2:
         return getNumChar();
     } 
     return getSpecialChar();
   }
 
 
 
   
   public static String getRandomPwd(int num) {
     List<Character> list = new ArrayList<>(num);
     list.add(Character.valueOf(getLowChar()));
     list.add(Character.valueOf(getUpperChar()));
     list.add(Character.valueOf(getNumChar()));
     list.add(Character.valueOf(getSpecialChar()));
     
     for (int i = 4; i < num; i++) {
       SecureRandom random = new SecureRandom();
       int funNum = random.nextInt(4);
       list.add(Character.valueOf(getRandomChar(funNum)));
     } 
     
     Collections.shuffle(list);
     StringBuilder stringBuilder = new StringBuilder(list.size());
     for (Character c : list) {
       stringBuilder.append(c);
     }
     
     return stringBuilder.toString();
   }
 
   
   public static String randomKey(int num) {
     List<Character> list = new ArrayList<>(num);
     list.add(Character.valueOf(getLowChar()));
     list.add(Character.valueOf(getUpperChar()));
     list.add(Character.valueOf(getNumChar()));
     
     for (int i = 4; i < num; i++) {
       SecureRandom random = new SecureRandom();
       int funNum = random.nextInt(4);
       list.add(Character.valueOf(getRandomChar(funNum)));
     } 
     
     Collections.shuffle(list);
     StringBuilder stringBuilder = new StringBuilder(list.size());
     for (Character c : list) {
       stringBuilder.append(c);
     }
     
     return stringBuilder.toString();
   }
 
   
   public static String randomHash(int num) {
     List<Character> list = new ArrayList<>(num);
     for (int i = 0; i < num; i++) {
       list.add(Character.valueOf(getHashChar()));
     }
     
     Collections.shuffle(list);
     StringBuilder stringBuilder = new StringBuilder(list.size());
     for (Character c : list) {
       stringBuilder.append(c);
     }
     
     return stringBuilder.toString();
   }
 }