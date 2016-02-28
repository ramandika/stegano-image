package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException,Exception {
//        File file = new File("data/message/foto.jpg");
//        byte[] fileData = new byte[(int) file.length()];
//        FileInputStream in = new FileInputStream(file);
//        in.read(fileData);
//        in.close();
//        byte[] encrypted = CipherTools.encryptFileVigenere(fileData, "yoga");
//        FileOutputStream outputStream = new FileOutputStream(new File("data/message/encrypted.jpg"));
//        outputStream.write(encrypted);
//        outputStream.close();
//        // ========= DECRYPT ==============
//        file = new File("data/message/encrypted.jpg");
//        fileData = new byte[(int) file.length()];
//        in = new FileInputStream(file);
//        in.read(fileData);
//        in.close();
//        encrypted = CipherTools.decryptFileVigenere(fileData, "yoga");
//        outputStream = new FileOutputStream(new File("data/message/decrypted.jpg"));
//        outputStream.write(encrypted);
//        outputStream.close();
//        byte[] strBin = "Hello".getBytes();
//        for (byte b : strBin) {
//            for (int i = 0; i < 8; i++) {
//                System.out.print(Integer.toBinaryString((b & 0xff) & (1 << 7 - i)) + ' ');
//            }
//            System.out.println();
//        }
//        FuncTest.testConjugate();
//        List<int[][]> a = new ArrayList<int[][]>();
//        int[][] a1 = {{0,1},{7,8}};
//        int[][] a2 = {{2,3},{9,10}};
//        int[][] a3 = {{4,5},{11,12}};
//        int[][] a4 = {{6,-1},{13,-1}};
//        int[][] a5 = {{14,15},{21,22}};
//        int[][] a6 = {{16,17},{23,24}};
//        int[][] a7 = {{18,19},{25,26}};
//        int[][] a8 = {{20,-1},{27,-1}};
//        int[][] a9 = {{28,29},{-1,-1}};
//        int[][] a10 = {{30,31},{-1,-1}};
//        int[][] a11 = {{32,33},{-1,-1}};
//        int[][] a12 = {{34,35},{-1,-1}};
//        a.add(a1);
//        a.add(a2);
//        a.add(a3);
//        a.add(a4);
//        a.add(a5);
//        a.add(a6);
//        a.add(a7);
//        a.add(a8);
//        a.add(a9);
//        a.add(a10);
//        a.add(a11);
//        a.add(a12);
//        SteganoAlgorithm.M = 2;
//        SteganoAlgorithm.N = 2;
//        SteganoAlgorithm.wh = 4;
//        SteganoAlgorithm.he = 3;
//        SteganoAlgorithm.convert8x8ToImage(a,7,5);

        SteganoAlgorithm.insertText("image.png","saya anak baik","kriptografi");
    }
}
