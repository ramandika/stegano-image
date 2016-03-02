/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company;

/**
 *
 * @author Rakhmatullah Yoga S
 */
public class CipherTools {
    
    static String encryptVigenereStandard(String plain, String key) {
        int skip = 0;
        char[] cipher = new char[plain.length()];
        
        key = key.toUpperCase();
        for(int i=0; i<plain.length(); i++) {
            if(plain.charAt(i)>96&&plain.charAt(i)<123) {
                cipher[i] = (char) (97 + ((int) plain.charAt(i) - 97 + key.charAt((i-skip)%key.length()) - 65) % 26);
            }
            else if(plain.charAt(i)>64&&plain.charAt(i)<91)
                cipher[i] = (char) (65 + ((int) plain.charAt(i) - 65 + key.charAt((i-skip)%key.length()) - 65) % 26);
            else {
                skip++;
                cipher[i] = plain.charAt(i);
            }
        }
        return String.valueOf(cipher);
    }
    
    static String decryptVigenereStandard(String cipher, String key) {
        int skip = 0;
        char[] plain = new char[cipher.length()];
        
        key = key.toUpperCase();
        for(int i=0; i<cipher.length(); i++) {
            if(cipher.charAt(i)>96&&cipher.charAt(i)<123) {
                plain[i] = (char) (97 + (26 + ((int) cipher.charAt(i) - 97 - ((int) key.charAt((i-skip)%key.length()) - 65))) % 26);
            }
            else if(cipher.charAt(i)>64&&cipher.charAt(i)<91)
                plain[i] = (char) (65 + (26 + ((int) cipher.charAt(i) - 65 - ((int) key.charAt((i-skip)%key.length()) - 65))) % 26);
            else {
                skip++;
                plain[i] = cipher.charAt(i);
            }
        }
        return String.valueOf(plain);
    }
    
    static String encryptVigenereExtended(String plain, String key) {
        int skip = 0;
        char[] cipher = new char[plain.length()];
        for(int i=0; i<plain.length(); i++) {
            if(plain.charAt(i)>255) {
                skip++;
                cipher[i] = plain.charAt(i);
            }
            else {
                cipher[i] = (char) (((int) plain.charAt(i) + (int) key.charAt((i-skip)%key.length())) % 256);
            }
        }
        return String.valueOf(cipher);
    }

    static byte[] encryptFileVigenere(byte[] plain, String key) {
        int skip = 0;
        byte[] cipher = new byte[plain.length];
        for(int i=0; i<plain.length; i++) {
            if(plain[i]>255) {
                skip++;
                cipher[i] = plain[i];
            }
            else {
                cipher[i] = (byte) (((int) plain[i] + (int) key.charAt((i-skip)%key.length())) % 256);
            }
        }
        return cipher;
    }
    
    static String decryptVigenereExtended(String cipher, String key) {
        //Data extracted
        System.out.println("In Vigenere extracted");
        for(int i=0;i<cipher.length();i++){
            
            for(int j=0;j<8;j++){
                boolean b=(cipher.charAt(i)&(1<<(7-j)))!=0;
                System.out.print((b?1:0)+" ");
            }
            System.out.println();
        }
        int skip = 0;
        char[] plain = new char[cipher.length()];
        for(int i=0; i<cipher.length(); i++) {
            if(cipher.charAt(i)>255) {
                skip++;
                plain[i] = cipher.charAt(i);
            }
            else {
                plain[i] = (char) (((int) cipher.charAt(i) - (int) key.charAt((i-skip)%key.length()) + 256) % 256);
            }
        }
        return String.valueOf(plain);
    }

    static byte[] decryptFileVigenere(byte[] cipher, String key) {
        int skip = 0;
        byte[] plain = new byte[cipher.length];
        for(int i=0; i<cipher.length; i++) {
            if(cipher[i]>255) {
                skip++;
                plain[i] = cipher[i];
            }
            else {
                plain[i] = (byte) (((int) cipher[i] - (int) key.charAt((i-skip)%key.length()) + 256) % 256);
            }
        }
        return plain;
    }
    
    static String removeDuplicatedChar(String str) {
        String unique = "";
        for(int i=0; i<str.length(); i++) {
            if(unique.indexOf(str.charAt(i))==-1)
                unique = unique.concat(str.substring(i,i+1));
        }
        return unique;
    }

    static String encryptPlayfair(String plain, String key) {
        String bigram = "";
        char[][] matrix;
        int candidate = 65;
        int i1=0, i2=0, j1=0, j2=0;
        String cipher = "";
        
        // Generating bigram
        plain = plain.replaceAll("[^a-zA-Z]", "");
        plain = plain.toUpperCase();
        plain = plain.replace('J', 'I');
        for(int i=0; i<plain.length(); i++) {
            bigram = bigram.concat(plain.substring(i, i+1));
            if(i+1<plain.length()) {
                if(bigram.charAt(bigram.length()-1)==plain.charAt(i+1)&&bigram.length()%3==1) {
                    bigram = bigram.concat("Z");
                }
            }
            if((bigram.length()+1)%3==0)
                bigram = bigram.concat(" ");
        }
        if((bigram.length())%3==1)
                bigram = bigram.concat("Z");
        
        // Generating matrix key
        matrix = new char[5][5];
        key = key.replaceAll("[^a-zA-Z]", "");      // remove non-alphabetic char
        key = key.toUpperCase();                    // make key uppercase
        key = key.replaceAll("J", "");              // remove 'J'
        key = removeDuplicatedChar(key);            // remove repeated char
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                if(i*5+j<key.length()&&key.charAt(i*5+j)!='J') {
                    matrix[i][j] = key.charAt(i*5+j);
                }
                else {
                    while(key.indexOf(candidate)!=-1||candidate=='J') {
                        candidate++;
                    }
                    matrix[i][j] = (char) candidate;
                    candidate++;
                }
            }
        }
        
        // Encrypt process -> only alphabetic char would be encrypted
        for(int k=0; k<bigram.length(); k+=3) {
            for(int i=0; i<matrix.length; i++) {
                for(int j=0; j<matrix[i].length; j++) {
                    if(matrix[i][j]==bigram.charAt(k)) {
                        i1=i;
                        j1=j;
                    }
                    if(matrix[i][j]==bigram.charAt(k+1)) {
                        i2=i;
                        j2=j;
                    }
                }
            }
            if(i1==i2) {
                cipher = cipher.concat(""+matrix[i1][(j1+1)%5]);
                cipher = cipher.concat(""+matrix[i2][(j2+1)%5]);
            }
            else if(j1==j2) {
                cipher = cipher.concat(""+matrix[(i1+1)%5][j1]);
                cipher = cipher.concat(""+matrix[(i2+1)%5][j2]);
            }
            else if(i1!=i2&&j1!=j2) {
                cipher = cipher.concat(""+matrix[i1][j2]);
                cipher = cipher.concat(""+matrix[i2][j1]);
            }
            cipher = cipher.concat(" ");
        }
        return cipher;
    }

    static String decryptPlayfair(String encrypted, String key) {
        String bigram = "";
        char[][] matrix;
        int candidate = 65;
        int i1=0, i2=0, j1=0, j2=0;
        String plain = "";
        
        // Generating bigram
        encrypted = encrypted.replaceAll("[^a-zA-Z]", "");
        encrypted = encrypted.toUpperCase();
        for(int i=0; i<encrypted.length(); i++) {
            bigram = bigram.concat(encrypted.substring(i, i+1));
            if((bigram.length()+1)%3==0)
                bigram = bigram.concat(" ");
        }
        
        // Generating matrix key
        matrix = new char[5][5];
        key = key.replaceAll("[^a-zA-Z]", "");      // remove non-alphabetic char
        key = key.toUpperCase();                    // make key uppercase
        key = key.replaceAll("J", "");              // remove 'J'
        key = removeDuplicatedChar(key);            // remove repeated char
        for(int i=0; i<matrix.length; i++) {
            for(int j=0; j<matrix[i].length; j++) {
                if(i*5+j<key.length()&&key.charAt(i*5+j)!='J') {
                    matrix[i][j] = key.charAt(i*5+j);
                }
                else {
                    while(key.indexOf(candidate)!=-1||candidate=='J') {
                        candidate++;
                    }
                    matrix[i][j] = (char) candidate;
                    candidate++;
                }
            }
        }
        
        // Encrypt process -> only alphabetic char would be encrypted
        for(int k=0; k<bigram.length(); k+=3) {
            for(int i=0; i<matrix.length; i++) {
                for(int j=0; j<matrix[i].length; j++) {
                    if(matrix[i][j]==bigram.charAt(k)) {
                        i1=i;
                        j1=j;
                    }
                    if(matrix[i][j]==bigram.charAt(k+1)) {
                        i2=i;
                        j2=j;
                    }
                }
            }
            if(i1==i2) {
                plain = plain.concat(""+matrix[i1][(j1-1+5)%5]);
                plain = plain.concat(""+matrix[i2][(j2-1+5)%5]);
            }
            else if(j1==j2) {
                plain = plain.concat(""+matrix[(i1-1+5)%5][j1]);
                plain = plain.concat(""+matrix[(i2-1+5)%5][j2]);
            }
            else if(i1!=i2&&j1!=j2) {
                plain = plain.concat(""+matrix[i1][j2]);
                plain = plain.concat(""+matrix[i2][j1]);
            }
            plain = plain.concat(" ");
        }
        return plain;
    }
    
    static String removeSpaces(String str) {
        return str.replaceAll(" ", "");
    }
    
    static String groupFiveChars(String str) {
        String noSpace = removeSpaces(str);
        String fiveChars = "";
        for(int i=0; i<noSpace.length(); i++) {
            fiveChars = fiveChars.concat(noSpace.substring(i, i+1));
            if((i+1)%5==0) {
                fiveChars = fiveChars.concat(" ");
            }
        }
        return fiveChars;
    }
    
}
