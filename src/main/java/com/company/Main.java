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
import java.util.Arrays;
import java.util.List;

public class Main {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File("data/message/foto.jpg");
        byte[] fileData = new byte[(int)file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(fileData);
        in.close();
        byte[] encrypted = CipherTools.encryptFileVigenere(fileData, "yoga");
        FileOutputStream outputStream = new FileOutputStream(new File("data/message/encrypted.jpg"));
        outputStream.write(encrypted);
        outputStream.close();
        // ========= DECRYPT ==============
        file = new File("data/message/encrypted.jpg");
        fileData = new byte[(int)file.length()];
        in = new FileInputStream(file);
        in.read(fileData);
        in.close();
        encrypted = CipherTools.decryptFileVigenere(fileData, "yoga");
        outputStream = new FileOutputStream(new File("data/message/decrypted.jpg"));
        outputStream.write(encrypted);
        outputStream.close();
//        byte[] strBin = "Hello".getBytes();
//        for(byte b : strBin) {
//            for(int i=0; i<8; i++) {
//                System.out.print(Integer.toBinaryString((b & 0xff) & (1 << 7-i))+' ');
//            }
//            System.out.println();
//        }
    }
    
}
