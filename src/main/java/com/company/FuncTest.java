package com.company;


import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ramandika on 26/02/16.
 */
public class FuncTest {
    private static String fileName= "pixels_example.txt";
    public static int row=18,col=16;

    public static void testToRGB() throws Exception{
        Scanner scanner = new Scanner(new File(fileName));
        int[] pixels=new int[row*col];
        int idx=0;
        while(scanner.hasNextInt())
        {
            pixels[idx++]=scanner.nextInt();
        }
        Image img=new Image(pixels,row,col);
        Image.RGB[] rgbpixels=img.convertToRGB();
        //Print red
        PrintWriter writer = new PrintWriter("red.txt", "UTF-8");
        System.out.println(rgbpixels.length);
        for(int i=0;i<rgbpixels.length;i++){
            if((i+1)%col==0) writer.println(rgbpixels[i].getRed());
            else writer.print(rgbpixels[i].getRed()+" ");

        }
        writer.close();
        //Print green
        writer = new PrintWriter("green.txt", "UTF-8");
        for(int i=0;i<rgbpixels.length;i++){
            if((i+1)%col==0) writer.println(rgbpixels[i].getGreen());
            else writer.print(rgbpixels[i].getGreen()+" ");

        }
        writer.close();
        //Print blue
        writer=new PrintWriter("blue.txt","UTF-8");
        for(int i=0;i<rgbpixels.length;i++){
            if((i+1)%col==0) writer.println(rgbpixels[i].getBlue());
            else writer.print(rgbpixels[i].getBlue()+" ");

        }
        writer.close();
    }

    public static void testTo8x8() throws Exception{
        Scanner scanner = new Scanner(new File(fileName));
        int[] pixels=new int[row*col];
        int idx=0;
        while(scanner.hasNextInt())
        {
            pixels[idx++]=scanner.nextInt();
        }
        Image img=new Image(pixels,row,col);
        List<int[][]> temp=SteganoAlgorithm.to8x8(img);
        PrintWriter writer = new PrintWriter("8x8.txt", "UTF-8");
        for(int i=0;i<temp.size();i++){
            int arraytemp[][]=temp.get(i);
            for(int a=0;a<8;a++) {
                for (int b = 0; b < 8; b++)
                    writer.print(arraytemp[a][b] + " ");
                writer.println();
            }
            writer.println("======================================");
        }
        writer.close();
    }

    public static void testToPBC() throws Exception{
        Scanner scanner = new Scanner(new File(fileName));
        int[] pixels=new int[row*col];
        int idx=0;
        while(scanner.hasNextInt())
        {
            pixels[idx++]=scanner.nextInt();
        }
        Image img=new Image(pixels,row,col);
        List<int[][]> pixels8x8=SteganoAlgorithm.to8x8(img);;
        List<SteganoAlgorithm.BitPlane> redPlane=SteganoAlgorithm.toPBC(pixels8x8,'R');
        List<SteganoAlgorithm.BitPlane> greenPlane=SteganoAlgorithm.toPBC(pixels8x8,'G');
        List<SteganoAlgorithm.BitPlane> bluePlane=SteganoAlgorithm.toPBC(pixels8x8,'B');

        PrintWriter writer;
        //Print redPlan
        writer=new PrintWriter("redPlane.txt", "UTF-8");
        for(int i=0;i<redPlane.size();i++){
            writer.println("##########PIXELS 8x8 Ke-"+i+"##########");
            List<boolean[][]> temp=redPlane.get(i).bp;
            for(int j=0;j<temp.size();j++){
                writer.println("-----Bit ke-"+j+"-----");
                boolean[][] boolTemp=temp.get(j);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++) {
                        writer.print((boolTemp[a][b] ? 1 : 0) + " ");
                    }
                    writer.println();
                }
            }
        }
        writer.close();
        //Print greenPlane
        writer=new PrintWriter("greenPlane.txt", "UTF-8");
        for(int i=0;i<greenPlane.size();i++){
            writer.println("##########PIXELS 8x8 Ke-"+i+"##########");
            List<boolean[][]> temp=greenPlane.get(i).bp;
            for(int j=0;j<temp.size();j++){
                writer.println("-----Bit ke-"+j+"-----");
                boolean[][] boolTemp=temp.get(j);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++) {
                        writer.print((boolTemp[a][b] ? 1 : 0) + " ");
                    }
                    writer.println();
                }
            }
        }
        writer.close();
        //Print bluePlane
        writer=new PrintWriter("bluePlane.txt", "UTF-8");
        for(int i=0;i<bluePlane.size();i++){
            writer.println("##########PIXELS 8x8 Ke-"+i+"##########");
            List<boolean[][]> temp=bluePlane.get(i).bp;
            for(int j=0;j<temp.size();j++){
                writer.println("-----Bit ke-"+j+"-----");
                boolean[][] boolTemp=temp.get(j);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++) {
                        writer.print((boolTemp[a][b] ? 1 : 0) + " ");
                    }
                    writer.println();
                }
            }
        }
        writer.close();
    }

/*    public static void testXOR() throws Exception{
        Scanner scanner = new Scanner(new File(fileName));
        int[] pixels=new int[row*col];
        int idx=0;
        while(scanner.hasNextInt())
        {
            pixels[idx++]=scanner.nextInt();
        }
        Image img=new Image(pixels,row,col);
        List<int[][]> pixels8x8=SteganoAlgorithm.to8x8(img);
        List<SteganoAlgorithm.BitPlane> lb=SteganoAlgorithm.toPBC(pixels8x8,'R');
        List<SteganoAlgorithm.BitPlane> cgc=SteganoAlgorithm.XOR(lb);
        PrintWriter writer;
        //Print redPlanCGC
        writer=new PrintWriter("redPlaneCGC.txt", "UTF-8");
        for(int i=0;i<cgc.size();i++){
            writer.println("##########PIXELS CGC 8x8 Ke-"+i+"##########");
            List<boolean[][]> temp=cgc.get(i).bp;
            for(int j=0;j<temp.size();j++){
                writer.println("-----Bit ke-"+j+"-----");
                boolean[][] boolTemp=temp.get(j);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++) {
                        writer.print((boolTemp[a][b] ? 1 : 0) + " ");
                    }
                    writer.println();
                }
            }
        }
        writer.close();
    }*/

    public static void testIsComplexEnough(){
        final boolean T=true;
        final boolean F=false;
        boolean[][] bp={
            {T, T, F, F, T, T, F, T},
            {T, F, F, F, T, T, F, T},
            {F, F, F, F, T, F, F, T},
            {T, T, F, T, T, F, F, T},
            {F, F, T, F, T, F, F, T},
            {F, F, F, T, F, F, T, T},
            {T, F, F, T, T, F, T, F},
            {F, F, T, T, T, F, F, F}
        };
        System.out.println(SteganoAlgorithm.isComplexEnough(bp));
    }

    public static void testConjugate(){
        final boolean T=true;
        final boolean F=false;
        boolean[][] bp={
                {T, T, F, F, T, T, F, T},
                {T, F, F, F, T, T, F, T},
                {F, F, F, F, T, F, F, T},
                {T, T, F, T, T, F, F, T},
                {F, F, T, F, T, F, F, T},
                {F, F, F, T, F, F, T, T},
                {T, F, F, T, T, F, T, F},
                {F, F, T, T, T, F, F, F}
        };
        boolean[][] conjugate=SteganoAlgorithm.conjugate(bp);
        System.out.println(SteganoAlgorithm.isComplexEnough(conjugate));
        for(int a=0;a<8;a++) {
            for (int b = 0; b < 8; b++)
                System.out.print(conjugate[a][b] + " ");
            System.out.println();
        }
    }
}
