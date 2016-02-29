package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ramandika on 18/02/16.
 */
public class Image {

    public static class RGB{
        //Property
        private int red=255;
        private int green=255;
        private int blue=255;

        //Method
        public void setRed(int r){this.red=r;}
        public void setGreen(int g){this.green=g;}
        public void setBlue(int b){this.blue=b;}
        public int getRed(){return this.red;}
        public int getGreen(){return this.green;}
        public int getBlue(){return this.blue;}
    }
    //Property
    private int[] pixels;
    private int width;
    private int heigth;
    private RGB[] pixelsRGB;

    //Method
    public Image(int pixels[],int row, int col){
        this.width=col;
        this.heigth=row;
        this.pixels=pixels;
    }
    public Image(String path) {
        try {
            //System.out.println(path);
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            setWidth(image.getWidth());
            setHeigth(image.getHeight());
            setPixels(image.getRGB(0, 0, image.getWidth(), image.getHeight(), getPixels(), 0,image.getWidth()));
            setPixelsRGB(convertToRGB());
        } catch (IOException ex) {
            Logger.getLogger(Image.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public RGB[] getPixelsRGB() {
        return pixelsRGB;
    }

    public void setPixelsRGB(RGB[] pixelsRGB) {
        this.pixelsRGB = pixelsRGB;
    }

    public RGB[] convertToRGB() throws NullPointerException{
        RGB[] rgb=null;
        int size=width*heigth;
        rgb=new RGB[size];
        for(int i=0;i<size;i++){
            RGB temp=new RGB();
            temp.setRed((pixels[i] >> 16) & 0xff);
            temp.setGreen((pixels[i] >> 8) & 0xff);
            temp.setBlue(pixels[i] & 0xff);
            rgb[i]=temp;
        }
        return rgb;
    }

    public int[] convertToPixel() throws NullPointerException{
        int[] pixels=null;
        int size=width*heigth;
        pixels=new int[size];
        for(int i=0;i<size;i++){
            pixels[i]=pixelsRGB[i].getRed() << 16;
            pixels[i]+=pixelsRGB[i].getGreen() << 8;
            pixels[i]+=pixelsRGB[i].getBlue();
        }
        return pixels;
    }

    public void saveImage(String filename) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, heigth, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0,0,width,heigth,pixels,0,width);
        File outputfile = new File(filename);
        String format=filename.substring(filename.indexOf('.')+1,filename.length());
        ImageIO.write(bufferedImage, format, outputfile);
    }
}
