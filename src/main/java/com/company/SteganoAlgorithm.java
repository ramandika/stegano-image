package com.company;


import java.util.*;

/**
 * Created by ramandika on 18/02/16.
 */
public class SteganoAlgorithm {
    private static final boolean F=false;
    private static final boolean T=true;
    private final static int M=8,N=8;               // M col N row
    private final static int max8x8changing=112;    // jumlah maksimal kemungkinan perubahan warna pada 8x8
    private static double alpha;                    // default treshold
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
    private static int wh;
    private static int he;
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

    /**
     * converting from PBC to CGC system and vise versa
     * @param input
     * @return 
     */
    public static List<BitPlane> XOR(List<BitPlane> input) {
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
        System.out.println(counter+"/"+max8x8changing+"="+result);
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
        List<boolean[][]> messageBitplane = null;
        boolean[][] messageMatrix;
        
        for(int x=0; x<message.size(); x++) {
            messageMatrix = new boolean[8][8];
            for(int i=0; i<messageMatrix.length; i++) {
                if(x<message.size()) {
                    for(int j=0; j<messageMatrix[i].length; j++) {
                        if(i==0&&j==0) {
                            messageMatrix[i][j] = false;
                        }
                        else {
                            messageMatrix[i][j] = message.get(x);
                            x++;
                        }
                    }
                }
                else {
                    for(int j=0; j<messageMatrix[i].length; j++) {
                        messageMatrix[i][j] = false;
                    }
                }
            }
            messageBitplane.add(messageMatrix);
        }
        return messageBitplane;
    }
    
    // INI GUE MASIH BINGUNG GIMANA NGECONVERTNYA, TOLONG KERJAIN YA.. :V
    static boolean[][] convertIntToMatrix(int num) {
        boolean[][] matrix = new boolean[8][8];
        byte[] byteNum = new byte[8];
        for(int i=0; i<8; i++) {
            byteNum[i] = (byte) ((num >> 7-i) & 0xff);
            for(int j=0; j<8; j++) {
                
            }
        }
        return matrix;
    }
    
    /**
     * menghitung jumlah byte yg dapat disimpan
     * @param imgPath
     * @param cipher
     * @return 
     */
    public static int countPayloadByte(String imgPath, String cipher) {
        int NbBit = 0;     // jumlah char yang dapat disisipkan pada gambar
        bitplanes = to8x8(new Image(imgPath));
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = XOR(redPBC);
        List<BitPlane> greenCGC = XOR(greenPBC);
        List<BitPlane> blueCGC = XOR(bluePBC);
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
    
    public static Image insertFile(String imgPath, String headerFile, byte[] binaryFile, String key) {
        int idxSeq = -1;
        byte[] cipher = CipherTools.encryptFileVigenere(binaryFile, key);
        List<Boolean> binaryMsg = binaryToListBoolean(cipher);
        List<boolean[][]> msgMatrix = new ArrayList();
        msgMatrix.add(convertIntToMatrix(headerFile.length()));                       // parameternya info size header, 0 karena bukan nyisipin file
        msgMatrix.add(convertIntToMatrix(binaryFile.length));                         // parameternya info size dr text yang akan di-embed
        msgMatrix.addAll(convertMessageToMatrix(binaryMsg));
        List<Integer> randSeq = getUniqRandSeq(getSeedFromKey(key), msgMatrix.size());
        
        bitplanes = to8x8(new Image(imgPath));
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = XOR(redPBC);
        List<BitPlane> greenCGC = XOR(greenPBC);
        List<BitPlane> blueCGC = XOR(bluePBC);
        for(int i=0; i<redCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<redCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(redCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                redCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                redCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<greenCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<greenCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(greenCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                greenCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                greenCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<blueCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<blueCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(blueCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                blueCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                blueCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        redPBC = XOR(redCGC);
        greenPBC = XOR(greenCGC);
        bluePBC = XOR(blueCGC);
        
        // BALIKIN KE GAMBAR
        // MASIH BINGUNG
        // TOLONG LANJUTIN YEEE
        
        return null;
    }
    
    public static Image insertText(String imgPath, String message, String key) {
        int idxSeq = -1;
        String cipher = CipherTools.encryptVigenereExtended(message, key);
        List<Boolean> binaryMsg = convertMessageToBinary(cipher);
        List<boolean[][]> msgMatrix = new ArrayList();
        msgMatrix.add(convertIntToMatrix(0));                       // parameternya info size header, 0 karena bukan nyisipin file
        msgMatrix.add(convertIntToMatrix(message.length()));        // parameternya info size dr text yang akan di-embed
        msgMatrix.addAll(convertMessageToMatrix(binaryMsg));
        List<Integer> randSeq = getUniqRandSeq(getSeedFromKey(key), msgMatrix.size());
        
        bitplanes = to8x8(new Image(imgPath));
        List<BitPlane> redPBC = toPBC(bitplanes, 'R');
        List<BitPlane> greenPBC = toPBC(bitplanes, 'G');
        List<BitPlane> bluePBC = toPBC(bitplanes, 'B');
        List<BitPlane> redCGC = XOR(redPBC);
        List<BitPlane> greenCGC = XOR(greenPBC);
        List<BitPlane> blueCGC = XOR(bluePBC);
        for(int i=0; i<redCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<redCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(redCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                redCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                redCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<greenCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<greenCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(greenCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                greenCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                greenCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<blueCGC.size(); i++) {                        // seluruh blok 8x8
            if(((i+1)%wh!=0)&&((i/wh)!=he-1)) {
                for(int j=0; j<blueCGC.get(i).bp.size(); j++) {      // 8 layer bit plane
                    if(isComplexEnough(blueCGC.get(i).bp.get(j))) {
                        idxSeq++;
                        if(randSeq.contains(idxSeq)) {
                            if(isComplexEnough(msgMatrix.get(randSeq.indexOf(idxSeq)))) {
                                blueCGC.get(i).bp.set(j, msgMatrix.get(randSeq.indexOf(idxSeq)));
                            }
                            else {
                                blueCGC.get(i).bp.set(j, conjugate(msgMatrix.get(randSeq.indexOf(idxSeq))));
                            }
                        }
                    }
                }
            }
        }
        redPBC = XOR(redCGC);
        greenPBC = XOR(greenCGC);
        bluePBC = XOR(blueCGC);
        
        // BALIKIN KE GAMBAR
        // MASIH BINGUNG
        // TOLONG LANJUTIN YEEE
        
        return null;
    }

    static List<Integer> getUniqRandSeq(int seed, int size){
        List<Integer> list=new ArrayList();
        for(int i=0;i<size;i++)
            list.add(i);
        Collections.shuffle(list, new Random(seed));
        return list;
    }

    public static String Extract(Image i,String key){
        //TO BE IMPLEMENTED
        return null;
    }

}
