/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.audiorecordapp;

/**
 *
 * @author Noppayut
 */
public class mainTester {
    public static void main(String[] args) throws Exception{
        
        BurstExtraction b;
        String path = System.getProperty("user.dir")+"\\";
        String sourcewav = "testwav"+".wav";
        String savetext = "Textfile";                
        WavReader w = new WavReader(path, sourcewav);
        int fileno = 0;
        int framesRead = w.dmax;
        long starttime;
        while (framesRead == w.dmax){
            System.out.println("File no : "+(fileno+1));
            w.operate();
            double[] signal = w.getBuffer();
            framesRead = w.getFramesRead();
            starttime = w.starttime;
            System.out.println("Finished reading wav");
            b = new BurstExtraction(signal, starttime);
            b.operateInMem(savetext+(fileno+1)+".txt");
            System.out.println("Finished event extraction");
            System.out.println("----------------------------------");
            b = null;
            System.gc();
            fileno += 1;
        }
        /*
        for(int i = 0 ; i < noOfFile ; i++){
            b = new BurstExtraction();
            b.operate(savetext+(i+1)+".txt");
        }
        */
        
        w.finish();
    }
    
}
