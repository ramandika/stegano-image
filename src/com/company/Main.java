package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;

public class Main {
    private static String url="../../binary.png";

    public Image readImage(String url){
        Image img=null;
        try{
            img=new Image();
            BufferedImage image =
                    ImageIO.read(this.getClass().getResource(url));
            int width=image.getWidth();
            int height=image.getHeight();
            img.setWidth(width);
            img.setHeigth(height);
            img.setPixels(image.getRGB(0, 0, width, height, img.getPixels(), 0,width));
        }catch (Exception e){
            e.printStackTrace();
        }
        return img;
    }
    public static void main(String[] args) {
        try{
            Main m=new Main();
            Image img=new Image();
            Image.RGB[] rgb=new Image.RGB[100];
            img.setWidth(5);img.setHeigth(20);
            for(int i=0;i<100;i++){
                Image.RGB temp=new Image.RGB();
                temp.setRed(i);
                temp.setGreen(i);
                temp.setBlue(i);
                rgb[i]=temp;
            }
            img.setPixelsRGB(rgb);
            List<int[][]> res8x8=SteganoAlgorithm.to8x8(img);
            for(int i=0;i<res8x8.size();i++){
                int[][] rgbtemp=res8x8.get(i);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++) System.out.print(((rgbtemp[a][b] >> 16)&0xff) +" ");
                    System.out.println();
                }
                System.out.println("=======================================================");

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
