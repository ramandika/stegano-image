package com.company;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ramandika on 18/02/16.
 */
public class SteganoAlgorithm {
    private static final boolean F=false;
    private static final boolean T=true;
    private final static int M=8,N=8; //M col N row
    private final static int max8x8changing=112;
    private static double alpha=0.3;
    private static List<int[][]> bitplanes; // list of 8x8 matriks bp
    private static final boolean[][] chessBoard={
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
    };

    public static class BitPlane{
        public List<boolean[][]> bp;
    }


    public static List<int[][]> to8x8(Image img){
        bitplanes=new ArrayList<>();
        int width=img.getWidth(); int height=img.getHeigth();
        int size=width*height;
        int[] pixels=img.getPixels();
        int wh=((img.getWidth()-1)/M)+1; int he=((img.getHeigth()-1)/N)+1;
        System.out.println(wh+"||"+he);
        //iterate all square
        for(int i=0;i<he;i++)
            for(int j=0;j<wh;j++){
                //Access all pixels inside
                int start=i*(width*N)+j*M;
                int end=start+(width*(N-1));
                int[][] tempix=new int[M][N];
                int absis=0;
                for(int x=start;x<=end;x+=width) {
                    int ordinat=0;
                    for (int y = x; y < x + M; y++){
                        if(y/width==x/width && y<size)tempix[absis][ordinat] = pixels[y];
                        else tempix[absis][ordinat]= 0xffffff;
                        ordinat++;
                    }
                    absis++;
                }
                bitplanes.add(tempix);
            }
        return bitplanes;
    }

    private static List<BitPlane> toPBC(List<int[][]> input,char c){
        int size=input.size();
        List<BitPlane> res=new ArrayList<>();
        for(int i=0;i<size;i++){
            BitPlane pbc=new BitPlane();boolean[][] bit8x8;
            int[][] pixels8x8=input.get(i);
            for(int layer=0;layer<8;layer++){
                bit8x8=new boolean[M][N];
                for(int a=0;a<M;a++)
                    for(int b=0;b<N;b++){
                        if(c=='R') bit8x8[a][b]=(((pixels8x8[a][b] >> 16) & 0xff) & (1 << layer))!=0;
                        else if(c=='G') bit8x8[a][b]=(((pixels8x8[a][b] >> 8) & 0xff) & (1 << layer))!=0;
                        else if(c=='B') bit8x8[a][b]=((pixels8x8[a][b] & 0xff) & (1 << layer))!=0;
                    }
                pbc.bp.add(bit8x8);
            }
            res.add(pbc);
        }
        return res;
    }

    private static List<BitPlane> XOR(List<BitPlane> input){ //PB to CGC Vice Versa
        int size=input.size();
        List<BitPlane> cgcres=new ArrayList<>();
        for(int i=0;i<size;i++){
            BitPlane temp=input.get(i);
            BitPlane cgc=new BitPlane();
            for(int a=0;a<temp.bp.size();a++){//layer [0,7]
                boolean[][] bp8x8=temp.bp.get(a); //a=layer bp
                boolean[][] res8x8=new boolean[M][N];
                for(int x=0;x<M;x++)
                    for(int y=0;y<N;y++){
                        if(x==0) res8x8[x][y]=bp8x8[x][y];
                        else{
                            res8x8[x][y]=bp8x8[x][y]^bp8x8[x-1][y];
                        }
                    }
                cgc.bp.add(res8x8);
            }
            cgcres.add(cgc);
        }
        return cgcres;
    }

    private static boolean isComplexEnough(boolean[][] input){
        int counter=0;
        boolean enough;
        double result;
        for(int i=0;i<M;i++)
            for(int j=0;j<N;j++){
                if(i+1<M){
                    if(j+1<M) counter+=(input[i][j]^input[i][j+1]) ? 1:0;//xor kanan
                    counter+=(input[i][j]^input[i+1][j]) ? 1:0;//xor bawah
                }else{
                    if(j+1<M) counter+=(input[i][j]^input[i][j+1]) ? 1:0;//xor kanan
                }
            }
        result=counter/max8x8changing;
        if(result>=alpha) enough= true;
        else enough=false;
        return enough;
    }

    private static boolean[][] toComplex(boolean[][] input){
        boolean[][] boolres=new boolean[M][N];
        for(int i=0;i<N;i++)
            for(int j=0;j<M;j++)
                boolres[i][j]=input[i][j]^chessBoard[i][j];
        return boolres;
    }


    public static Image Insert(String cipher){
    }

    public static String Extract(Image i,String key){
        //TO BE IMPLEMENTED
        return null;
    }

}
