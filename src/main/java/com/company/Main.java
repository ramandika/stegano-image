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
    public static final boolean T = true;
    public static final boolean F = false;
    public static void main(String[] args) throws FileNotFoundException, IOException,Exception {
            Boolean[] messages=
            {
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
                F,T,T,F,F,T,T,F,
            };
           List<boolean[][]> result=SteganoAlgorithm.convertMessageToMatrix(Arrays.asList(messages));
           for(int size=0;size<result.size();size++){
               for(int a=0;a<8;a++){
                   for(int b=0;b<8;b++)
                       System.out.print((result.get(size)[a][b]?1:0)+" ");
                   System.out.println();
               }
               System.out.println("Matriks ke-"+size);
           }
    }
}
