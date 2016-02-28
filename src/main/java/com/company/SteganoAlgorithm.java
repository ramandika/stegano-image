package com.company;


import com.company.Image.RGB;
import javafx.util.Pair;

import java.io.File;
import java.io.PrintWriter;
import java.text.CollationElementIterator;
import java.util.*;

/**
 * Created by ramandika on 18/02/16.
 */
public class SteganoAlgorithm {
    private static final boolean F=false;
    private static final boolean T=true;
    public static int M=8,N=8;               // M col N row
    private final static int max8x8changing=112;    // jumlah maksimal kemungkinan perubahan warna pada 8x8
    public static double alpha=0.48;                    // default treshold
    private static List<int[][]> bitplanes;         // kumpulan blok 8x8 sebuah gambar
    private static final boolean[][] chessBoard = {   // buat conjugate
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},
            {F,T,F,T,F,T,F,T},
            {T,F,T,F,T,F,T,F},

    };
    public static int wh;
    public static int he;
    private static List<boolean[][]> messageBlock;
    public static class BitPlane {// 8 bit plane utk 1 blok dengan
        public BitPlane(){
            bp=new ArrayList();
        };
        public List<boolean[][]> bp;        // sizenya pasti 8 (bitplane [0,7])
    }
    
    /**
     * convert complete image to sets of 8x8 blocks of pixel
     * @param img
     * @return 
     */
    public static List<int[][]> to8x8(Image img){
        bitplanes=new ArrayList();
        int width=img.getWidth();
        int height=img.getHeigth();
        int size=width*height;
        int[] pixels=img.getPixels();
        wh=((img.getWidth()-1)/M)+1;        // jumlah blok 8x8 menyamping
        he=((img.getHeigth()-1)/N)+1;       // jumlah blok 8x8 menurun
        
        //System.out.println(wh+"||"+he);
        //iterate all square
        for(int i=0;i<he;i++) {
            for(int j=0;j<wh;j++){
                //Access all pixels inside
                int start=i*(width*N)+j*M;
                int end=start+(width*(N-1));
                int[][] tempix=new int[M][N];
                int absis=0;
                for(int x=start;x<=end;x+=width) {
                    int ordinat=0;
                    for (int y = x; y < x + M; y++){
                        if(y/width==x/width && y<size)
                            tempix[absis][ordinat] = pixels[y];
                        else
                            tempix[absis][ordinat]= 0xffffff;
                        ordinat++;
                    }
                    absis++;
                }
                bitplanes.add(tempix);
            }
        }
        return bitplanes;
    }

    public static List<int[][]> pbcTo8x8(List<List<BitPlane>> rgbBp){
        List<int[][]> listPixels=new ArrayList();
        for(int i=0;i<rgbBp.size();i++){//bp[0]=red;bp[1]=green;bp[2]==blue
            List<BitPlane> bitplanes = rgbBp.get(i);
            for(int j=0;j<bitplanes.size();j++){
                List<boolean[][]> listbool=bitplanes.get(j).bp; // j posisi 8x8
                int[][] pixels=null;
                if(i>0) pixels=listPixels.get(j);
                else pixels=new int[8][8];
                for(int k=0;k<listbool.size();k++){ // k urutan bit
                    boolean[][] temp=listbool.get(k);
                    for(int a=0;a<8;a++)
                        for(int b=0;b<8;b++)
                            pixels[a][b]+=((temp[a][b]? 1:0) <<(8*(2-i)+k)); //redd slide 16+j, green slide 8+j, blue slide j
                }
                if(i==0)listPixels.add(pixels);
            }
/*            if(i==0)
                for(int idx=0;idx<listPixels.size();idx++){
                    System.out.println("Index 8x8 ke-"+idx);
                    for(int x=0;x<8;x++) {
                        for (int y = 0; y < 8; y++)
                            System.out.print(listPixels.get(idx)[x][y] + " ");
                        System.out.println();
                    }
                    System.out.println("=========================");
                }*/
        }
        return listPixels;
    }

    public static int[] convert8x8ToPixels(List<int[][]> lb,int w,int h){
        int size=w*h;//,row,col,i8x8;
        int[] pixels=new int[size];
        for(int i=0;i<size;i++) {
            pixels[i] = lb.get((i % w) / M + (i / (M * w)) * wh)[(i / w) % M][(i % w) % N];
        }
        return pixels;
    }
    
    /**
     * 
     * @param input blok 8x8 dengan nilai RGB masing-masing pixel
     * @param c kode warna [R,G,B]
     * @return kumpulan BitPlane utk satu warna tertentu
     */
    public static List<BitPlane> toPBC(List<int[][]> input,char c){
        int size=input.size();
        List<BitPlane> res=new ArrayList();
        for(int i=0;i<size;i++) {
            BitPlane pbc=new BitPlane();
            boolean[][] bit8x8;
            int[][] pixels8x8=input.get(i);
            for(int layer=0;layer<8;layer++) {
                bit8x8=new boolean[M][N];
                for(int a=0;a<M;a++) {
                    for(int b=0;b<N;b++){
                        if(c=='R')
                            bit8x8[a][b] = (((pixels8x8[a][b] >> 16) & 0xff) & (1 << layer))!=0;
                        else if(c=='G')
                            bit8x8[a][b] = (((pixels8x8[a][b] >> 8) & 0xff) & (1 << layer))!=0;
                        else if(c=='B')
                            bit8x8[a][b] = ((pixels8x8[a][b] & 0xff) & (1 << layer))!=0;
                    }
                }
                pbc.bp.add(bit8x8);             // menambah elemen blok bit 8x8
            }
            res.add(pbc);
        }
        return res;
    }

    public static List<BitPlane> cgcToPBC(List<BitPlane> input){
        int size=input.size();                                      // jumlah blok 8x8 dalam sebuah gambar
        List<BitPlane> cgcres=new ArrayList();
        for(int i=0;i<size;i++){
            BitPlane temp=input.get(i);
            BitPlane cgc=new BitPlane();
            for(int a=0;a<temp.bp.size();a++){                      //layer [0,7]
                boolean[][] bp8x8=temp.bp.get(a);                   //a=layer bp
                boolean[][] res8x8=new boolean[M][N];
                for(int x=0;x<M;x++)
                    for(int y=0;y<N;y++){
                        if(y==0) res8x8[x][y]=bp8x8[x][y];
                        else{
                            res8x8[x][y]=bp8x8[x][y]^res8x8[x][y-1];
                        }
                    }
                cgc.bp.add(res8x8);
            }
            cgcres.add(cgc);
        }
        return cgcres;
    }
    /**
     * converting from PBC to CGC system and vise versa
     * @param input
     * @return 
     */
    public static List<BitPlane> pbcToCGC(List<BitPlane> input) {
        int size=input.size();                                      // jumlah blok 8x8 dalam sebuah gambar
        List<BitPlane> cgcres=new ArrayList();
        for(int i=0;i<size;i++){
            BitPlane temp=input.get(i);
            BitPlane cgc=new BitPlane();
            for(int a=0;a<temp.bp.size();a++){                      //layer [0,7]
                boolean[][] bp8x8=temp.bp.get(a);                   //a=layer bp
                boolean[][] res8x8=new boolean[M][N];
                for(int x=0;x<M;x++)
                    for(int y=0;y<N;y++){
                        if(y==0) res8x8[x][y]=bp8x8[x][y];
                        else{
                            res8x8[x][y]=bp8x8[x][y]^bp8x8[x][y-1];
                        }
                    }
                cgc.bp.add(res8x8);
            }
            cgcres.add(cgc);
        }
        return cgcres;
    }
    
    /**
     * untuk mengukur kompleksitas, apakah blok 8x8 dapat disisipi pesan atau tidak
     * @param input
     * @return 
     */
    public static boolean isComplexEnough(boolean[][] input){
        int counter=0;
        boolean enough;
        double result;
        for(int i=0;i<M;i++)
            for(int j=0;j<N;j++){
                if(i+1<M){ // bukan pixel paling bawah
                    if(j+1<M)   // bukan pixel paling kanan
                        counter+=(input[i][j]^input[i][j+1]) ? 1:0;//xor kanan
                    counter+=(input[i][j]^input[i+1][j]) ? 1:0;//xor bawah
                }
                else {
                    if(j+1<M) counter+=(input[i][j]^input[i][j+1]) ? 1:0;//xor kanan
                }
            }
        result=(double)counter/max8x8changing;
        //System.out.println(counter+"/"+max8x8changing+"="+result);
        return result>=alpha;
    }
    
    /**
     * conjugate a message block to make it more complex
     * @param input
     * @return 
     */
    public static boolean[][] conjugate(boolean[][] input){            // convert bit pesan ke dalam bentuk yang lebih kompleks
        boolean[][] boolres=new boolean[M][N];

        for(int i=0;i<N;i++)
            for(int j=0;j<M;j++)
                boolres[i][j]=input[i][j]^chessBoard[i][j];
        boolres[0][0]=true; //Flag conjugated
        return boolres;
    }
    
    /**
     * convert secret message into a binary array
     * @param message
     * @return 
     */
    public static List<Boolean> convertMessageToBinary(String message) {
        byte[] messageBin = message.getBytes();
        List<Boolean> messageBitplane = new ArrayList<Boolean>();
        
        for(int x=0; x<messageBin.length; x++) {
            boolean bit;
            for(int i=0; i<8; i++) {
                bit = ((messageBin[x] & 0xff) & (1 << 7-i))!=0;
                messageBitplane.add(bit);
            }
        }
        return messageBitplane;
    }
    
    /**
     * convert binary secret message into a binary bitplane which replaces image bitplane
     * @param message
     * @return 
     */
    public static List<boolean[][]> convertMessageToMatrix(List<Boolean> message) {
        List<boolean[][]> messageBitplane = new ArrayList();
        System.out.println("ukuran message : "+ message.size());
        boolean[][] messageMatrix;
        
        for(int x=0; x<message.size(); x++) {
            messageMatrix = new boolean[8][8];
            for(int i=0; i<messageMatrix.length; i++) {
                for(int j=0; j<messageMatrix[i].length; j++) {
                    if(i==0&&j==0) {
                        messageMatrix[i][j] = false;
                    }
                    else {
                        if(x<message.size()) {
                            messageMatrix[i][j] = message.get(x);
                            x++;
                        }
                        else
                            messageMatrix[i][j] = false;
                    }
                }
            }
            if(isComplexEnough(messageMatrix))
                messageMatrix = conjugate(messageMatrix);
            messageBitplane.add(messageMatrix);
        }
        return messageBitplane;
    }
    
    // INI GUE MASIH BINGUNG GIMANA NGECONVERTNYA, TOLONG KERJAIN YA.. :V
    static boolean[][] convertIntToMatrix(long num) throws Exception {
        boolean[][] matrix;
        byte[] byteNum = new byte[8];
        if(num < 0) throw new Exception("Bit overflow > 63 bit");
        else{
            matrix = new boolean[8][8];
            for(int i=0; i<8; i++) {
                byteNum[i] = (byte) (num >> 8*(7-i));
            }
            for(int a=0;a<8;a++)
                for (int b = 0; b < 8; b++) {
                    matrix[a][b] = (((byteNum[a] >> (7 - b)) & 1) != 0);
                }
        }
/*        matrix[0][0]=false;
        for(int i=0;i<8;i++) {
            for (int j = 0; j < 8; j++)
                System.out.print((matrix[i][j]?1:0) + " ");
            System.out.println();
        }*/
        return matrix;
    }
    
    /**
     * menghitung jumlah byte yg dapat disimpan
     * @param imgPath
     * @param cipher
     * @return 
     */
    public static int countPayloadByte(String imgPath) {
        int NbBit = 0;     // jumlah char yang dapat disisipkan pada gambar
        bitplanes = to8x8(new Image(imgPath));
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = pbcToCGC(redPBC);
        List<BitPlane> greenCGC = pbcToCGC(greenPBC);
        List<BitPlane> blueCGC = pbcToCGC(bluePBC);
        for(int i=0; i<redCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int n=0; n<redCGC.get(i).bp.size(); n++) {      // 8 layer bit plane
                    if(isComplexEnough(redCGC.get(i).bp.get(n)))
                        NbBit += 63;
                }
            }
        }
        for(int i=0; i<greenCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int n=0; n<greenCGC.get(i).bp.size(); n++) {      // 8 layer bit plane
                    if(isComplexEnough(greenCGC.get(i).bp.get(n)))
                        NbBit += 63;
                }
            }
        }
        for(int i=0; i<blueCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int n=0; n<blueCGC.get(i).bp.size(); n++) {      // 8 layer bit plane
                    if(isComplexEnough(blueCGC.get(i).bp.get(n)))
                        NbBit += 63;
                }
            }
        }
        return (NbBit-126)/8;
    }
    
    static int getSeedFromKey(String key) {
        int seed = 0;
        for(int i=0; i<key.length(); i++)
            seed += (int) key.charAt(i);
        return seed;
    }
    
    static List<Boolean> binaryToListBoolean(byte[] binaryFile) {
        List<Boolean> messageBitplane = new ArrayList<Boolean>();
        
        for(int x=0; x<binaryFile.length; x++) {
            boolean bit;
            for(int i=0; i<8; i++) {
                bit = ((binaryFile[x] & 0xff) & (1 << 7-i))!=0;
                messageBitplane.add(bit);
            }
        }
        return messageBitplane;
    }

    private static Map<Integer,List<Integer>> getComplexPlanes(List<BitPlane> bitplanes) throws Exception{
        Map<Integer,List<Integer>> map=new HashMap();
        for(int i=0;i<bitplanes.size();i++){
            List<boolean[][]> bp=bitplanes.get(i).bp;
            List<Integer> bitComplex=new ArrayList();
            for(int bit=0;bit<bp.size();bit++) // [0..7] bit
                if(isComplexEnough(bp.get(bit)))bitComplex.add(bit);
            map.put(i,bitComplex);
        }
        return map;
    }
    
    public static Image insertFile(String imgPath, String headerFile, byte[] binaryFile, String key) throws Exception{
        int idxSeq = -1;
        byte[] cipher = CipherTools.encryptFileVigenere(binaryFile, key);
        List<Boolean> binaryMsg = binaryToListBoolean(cipher);
        List<boolean[][]> msgMatrix = new ArrayList();
        msgMatrix.add(convertIntToMatrix(headerFile.length()));                       // parameternya info size header, 0 karena bukan nyisipin file
        msgMatrix.add(convertIntToMatrix(binaryFile.length));                         // parameternya info size dr text yang akan di-embed
        msgMatrix.addAll(convertMessageToMatrix(binaryMsg));
        
        bitplanes = to8x8(new Image(imgPath));
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = pbcToCGC(redPBC);
        List<BitPlane> greenCGC = pbcToCGC(greenPBC);
        List<BitPlane> blueCGC = pbcToCGC(bluePBC);

        Map<Integer,List<Integer>> redComplex=getComplexPlanes(redCGC);
        Map<Integer,List<Integer>> greenComplex=getComplexPlanes(greenCGC);
        Map<Integer,List<Integer>> blueComplex=getComplexPlanes(blueCGC);
        int seed=getSeedFromKey(key);

        /*RED shuffle*/
        List keys = new ArrayList(redComplex.keySet());
        List templist;
        Collections.shuffle(keys,new Random(seed));
        List<Pair<Integer, List<Integer>>> redComplexShuf=null;
        for (Object o : keys) {
            redComplexShuf = new ArrayList();
            templist=redComplex.get(o);
            if(templist!=null)Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, redComplex.get(o));
            redComplexShuf.add(p);
        }

        /*Green Shuffle*/
        keys=new ArrayList(greenComplex.keySet());
        templist=null;
        Collections.shuffle(keys,new Random(seed));
        List<Pair<Integer, List<Integer>>> greenComplexShuf=null;
        for (Object o : keys) {
            greenComplexShuf = new ArrayList();
            templist=greenComplex.get(o);
            Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, greenComplex.get(o));
            greenComplexShuf.add(p);
        }
        /*Blue shuffle*/
        keys=new ArrayList(blueComplex.keySet());
        List<Pair<Integer, List<Integer>>> blueComplexShuf=null;
        Collections.shuffle(keys,new Random(seed));
        for (Object o : keys) {
            blueComplexShuf = new ArrayList();
            templist=blueComplex.get(o);
            Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, blueComplex.get(o));
            blueComplexShuf.add(p);
        }

        //Replace redComplex
        for(int i=0;i<redComplexShuf.size();i++){
            Pair<Integer,List<Integer>> temp=redComplexShuf.get(i);
            int pos= temp.getKey();
            List<Integer> bits=temp.getValue();
            if(((pos+1)%wh!=0)&&((pos/wh)!=he-1)){
                for(int j=0;i<bits.size();j++){
                    boolean[][] imgbool=redCGC.get(pos).bp.get(j);
                    idxSeq++;
                    for(int a=0;a<8;a++)
                        for(int b=0;b<8;b++)
                            imgbool[a][b]=messageBlock.get(idxSeq)[a][b];
                }
            }
        }
        //Replace greenComplex

        //Replace blueComplex

        redPBC = cgcToPBC(redCGC);
        greenPBC = cgcToPBC(greenCGC);
        bluePBC = cgcToPBC(blueCGC);
        
        // BALIKIN KE GAMBAR
        // MASIH BINGUNG
        // TOLONG LANJUTIN YEEE
        
        return null;
    }
    
    /*//public static Image insertText(String imgPath, String message, String key) throws Exception {
    public static void insertText() throws Exception {
       *//* int idxSeq = -1;
        String cipher = CipherTools.encryptVigenereExtended(message, key);
        List<Boolean> binaryMsg = convertMessageToBinary(cipher);
        List<boolean[][]> msgMatrix = new ArrayList();
        msgMatrix.add(convertIntToMatrix(0));                       // parameternya info size header, 0 karena bukan nyisipin file
        msgMatrix.add(convertIntToMatrix(message.length()));        // parameternya info size dr text yang akan di-embed
        msgMatrix.addAll(convertMessageToMatrix(binaryMsg));*//*
        Scanner scanner = new Scanner(new File("pixels_example.txt"));
        int[] pixels=new int[18*16];
        int idx=0;
        while(scanner.hasNextInt())
        {
            pixels[idx++]=scanner.nextInt();
        }
        Image img=new Image(pixels,18,16);
        //bitplanes = to8x8(new Image(imgPath));
        bitplanes = to8x8(img);
        //Print 8x8
        PrintWriter writer=new PrintWriter("8x8suram.txt");
        for(int i=0;i<bitplanes.size();i++){
            System.out.println("Posisi ke-"+i);
            for(int a=0;a<8;a++) {
                for (int b = 0; b < 8; b++)
                    writer.print(bitplanes.get(i)[a][b]+" ");
                writer.println();
            }
            writer.println("======================================");
        }
        writer.close();
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<List<BitPlane>> rgbPBC=new ArrayList();
        rgbPBC.add(redPBC);
        rgbPBC.add(greenPBC);
        rgbPBC.add(bluePBC);
        List<int[][]> pixel8x8=pbcTo8x8(rgbPBC);
        writer=new PrintWriter("8x8test.txt");
        for(int i=0;i<pixel8x8.size();i++){
            System.out.println("Posisi ke-"+i);
            for(int a=0;a<8;a++) {
                for (int b = 0; b < 8; b++)
                    writer.print(pixel8x8.get(i)[a][b]+" ");
                writer.println();
            }
            writer.println("======================================");
        }
        writer.close();
*//*        List<BitPlane> redCGC = XOR(redPBC);
        List<BitPlane> greenCGC = XOR(greenPBC);
        List<BitPlane> blueCGC = XOR(bluePBC);

        Map<Integer,List<Integer>> redComplex=getComplexPlanes(redCGC);
        Map<Integer,List<Integer>> greenComplex=getComplexPlanes(greenCGC);
        Map<Integer,List<Integer>> blueComplex=getComplexPlanes(blueCGC);
        int seed=getSeedFromKey(key);

        *//**//*RED shuffle*//**//*
        List keys = new ArrayList(redComplex.keySet());
        List templist;
        Collections.shuffle(keys,new Random(seed));
        List<Pair<Integer, List<Integer>>> redComplexShuf=null;
        for (Object o : keys) {
            redComplexShuf = new ArrayList();
            templist=redComplex.get(o);
            if(templist!=null)Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, redComplex.get(o));
            redComplexShuf.add(p);
        }

        *//**//*Green Shuffle*//**//*
        keys=new ArrayList(greenComplex.keySet());
        List<Pair<Integer, List<Integer>>> greenComplexShuf=null;
        Collections.shuffle(keys,new Random(seed));
        for (Object o : keys) {
            greenComplexShuf = new ArrayList();
            templist=greenComplex.get(o);
            Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, greenComplex.get(o));
            greenComplexShuf.add(p);
        }
        *//**//*Blue shuffle*//**//*
        keys=new ArrayList(blueComplex.keySet());
        List<Pair<Integer, List<Integer>>> blueComplexShuf=null;
        Collections.shuffle(keys,new Random(seed));
        for (Object o : keys) {
            blueComplexShuf = new ArrayList();
            templist=blueComplex.get(o);
            Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, blueComplex.get(o));
            blueComplexShuf.add(p);
        }*//*
//        redPBC = XOR(redCGC);
//        greenPBC = XOR(greenCGC);
//        bluePBC = XOR(blueCGC);
//
//        // BALIKIN KE GAMBAR
//        // MASIH BINGUNG
//        // TOLONG LANJUTIN YEEE
//
    }*/

    public static Image insertText(String imgPath, String message, String key, boolean isEncrypted) throws Exception {
        int idxSeq = 0;
        
        if(isEncrypted)
            message = CipherTools.encryptVigenereExtended(message, key);
        List<Boolean> binaryMsg = convertMessageToBinary(message);
        List<boolean[][]> msgMatrix = new ArrayList();
        msgMatrix.add(convertIntToMatrix(0));                       // parameternya info size header, 0 karena bukan nyisipin file
        msgMatrix.add(convertIntToMatrix(message.length()));        // parameternya info size dr text yang akan di-embed
        msgMatrix.addAll(convertMessageToMatrix(binaryMsg));
        Image image=new Image(imgPath);
        bitplanes = to8x8(image);
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = pbcToCGC(redPBC);
        List<BitPlane> greenCGC = pbcToCGC(greenPBC);
        List<BitPlane> blueCGC = pbcToCGC(bluePBC);

        Map<Integer,List<Integer>> redComplex=getComplexPlanes(redCGC);
        Map<Integer,List<Integer>> greenComplex=getComplexPlanes(greenCGC);
        Map<Integer,List<Integer>> blueComplex=getComplexPlanes(blueCGC);
        int seed=getSeedFromKey(key);

        //RED shuffle
        List keys = new ArrayList(redComplex.keySet());
        List templist;
        Collections.shuffle(keys,new Random(seed));
        List<Pair<Integer, List<Integer>>> redComplexShuf=null;
        for (Object o : keys) {
            redComplexShuf = new ArrayList();
            templist=redComplex.get(o);
            if(templist!=null)Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, redComplex.get(o));
            redComplexShuf.add(p);
        }

        //Green Shuffle
        keys=new ArrayList(greenComplex.keySet());
        templist=null;
        Collections.shuffle(keys,new Random(seed));
        List<Pair<Integer, List<Integer>>> greenComplexShuf=null;
        for (Object o : keys) {
            greenComplexShuf = new ArrayList();
            templist=greenComplex.get(o);
            if(templist!=null)Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, greenComplex.get(o));
            greenComplexShuf.add(p);
        }
        //Blue shuffle
        keys=new ArrayList(blueComplex.keySet());
        templist=null;
        List<Pair<Integer, List<Integer>>> blueComplexShuf=null;
        Collections.shuffle(keys,new Random(seed));
        for (Object o : keys) {
            blueComplexShuf = new ArrayList();
            templist=blueComplex.get(o);
            if(templist!= null)Collections.shuffle(templist, new Random(seed));
            Pair p = new Pair(o, blueComplex.get(o));
            blueComplexShuf.add(p);
        }

        //Replace redComplex
        for(int i=0;i<redComplexShuf.size() && idxSeq<msgMatrix.size();i++){
            Pair<Integer,List<Integer>> temp=redComplexShuf.get(i);
            int pos= temp.getKey();
            List<Integer> bits=temp.getValue();
            if(((pos+1)%wh!=0)&&((pos/wh)!=he-1)){
                for(int j=0;j<bits.size() && idxSeq<msgMatrix.size();j++){
                    boolean[][] imgbool=redCGC.get(pos).bp.get(bits.get(j));
                    for(int a=0;a<8;a++)
                        for(int b=0;b<8;b++)
                            imgbool[a][b]=msgMatrix.get(idxSeq)[a][b];
                    idxSeq++;
                }
            }
        }
        //Replace greemCompex
        for(int i=0;i<greenComplexShuf.size() && idxSeq<msgMatrix.size();i++){
            Pair<Integer,List<Integer>> temp=greenComplexShuf.get(i);
            int pos= temp.getKey();
            List<Integer> bits=temp.getValue();
            if(((pos+1)%wh!=0)&&((pos/wh)!=he-1)){
                for(int j=0;j<bits.size() && idxSeq<msgMatrix.size();j++){
                    boolean[][] imgbool=greenCGC.get(pos).bp.get(bits.get(j));
                    for(int a=0;a<8;a++)
                        for(int b=0;b<8;b++)
                            imgbool[a][b]=msgMatrix.get(idxSeq)[a][b];
                    idxSeq++;
                }
            }
        }

        //Replace blueComplex
        for(int i=0;i<blueComplexShuf.size() && idxSeq<msgMatrix.size();i++){
            Pair<Integer,List<Integer>> temp=blueComplexShuf.get(i);
            int pos= temp.getKey();
            List<Integer> bits=temp.getValue();
            if(((pos+1)%wh!=0)&&((pos/wh)!=he-1)){
                for(int j=0;j<bits.size() && idxSeq<msgMatrix.size();j++){
                    boolean[][] imgbool=blueCGC.get(pos).bp.get(bits.get(j));
                    for(int a=0;a<8;a++)
                        for(int b=0;b<8;b++)
                            imgbool[a][b]=msgMatrix.get(idxSeq)[a][b];
                    idxSeq++;
                }
            }
        }
        redPBC=cgcToPBC(redCGC);
        greenPBC=cgcToPBC(greenCGC);
        bluePBC=cgcToPBC(blueCGC);
/*        System.out.println("redPBC size :"+redPBC.size());
        PrintWriter writer=new PrintWriter("pbccgcsuram.txt");
        for(int idx=0;idx<redPBC.size();idx++){
            writer.println("Posisi ke"+idx);
            List<boolean[][]> temp=redPBC.get(idx).bp;
            for(int idxs=0;idxs<temp.size();idxs++){
                writer.println("Bit ke "+idxs);
                for(int a=0;a<8;a++) {
                    for (int b = 0; b < 8; b++)
                        writer.print((temp.get(idxs)[a][b]?1:0) + " ");
                    writer.println();
                }
            }
            writer.close();
        }*/
        List<List<BitPlane>> lbp=new ArrayList();
        lbp.add(redPBC); lbp.add(greenPBC);lbp.add(bluePBC);
        bitplanes=pbcTo8x8(lbp);
        image.setPixels(convert8x8ToPixels(bitplanes,image.getWidth(),image.getHeigth()));
        return image;
    }

    public static String Extract(Image i,String key){
        //TO BE IMPLEMENTED
        return null;
    }
    
    public static double countPSNR(Image cover, Image stego) {
        double rms = 0.0;
        double redRMS, greenRMS, blueRMS;
        double squareDiff = 0.0;
        RGB[] coverPixel = cover.getPixelsRGB();
        RGB[] stegoPixel = stego.getPixelsRGB();
        
        for(int i=0; i<coverPixel.length; i++) {
            squareDiff += Math.pow((coverPixel[i].getRed()-stegoPixel[i].getRed()), 2);
        }
        redRMS = Math.sqrt(squareDiff/coverPixel.length);
        squareDiff = 0;
        for(int i=0; i<coverPixel.length; i++) {
            squareDiff += Math.pow((coverPixel[i].getGreen()-stegoPixel[i].getGreen()), 2);
        }
        greenRMS = Math.sqrt(squareDiff/coverPixel.length);
        squareDiff = 0;
        for(int i=0; i<coverPixel.length; i++) {
            squareDiff += Math.pow((coverPixel[i].getBlue()-stegoPixel[i].getBlue()), 2);
        }
        blueRMS = Math.sqrt(squareDiff/coverPixel.length);
        rms = (redRMS + greenRMS + blueRMS)/3;
        return 20*Math.log10((double)256.0/rms);
    }
    
}
