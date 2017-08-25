/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.audiorecordapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Noppayut
 */
public class WavReader {
    BufferedWriter bufferedWriter;
    WavFile wavFile;
    int dmax;
    long nf;
    long count;
    long starttime;
    long sp;
    int framesRead;
    double[] buffer;
    
    WavReader(String path, String wav) throws Exception{
        this.wavFile = WavFile.openWavFile(new File(path + wav));
        this.dmax = 28800000;
        this.nf = this.wavFile.getNumFrames()/dmax + 1;        
        this.sp = wavFile.getSampleRate();
        this.starttime = 0;
        count = 0;
        this.framesRead=-1;
        this.buffer = new double[this.dmax];
    }
    public double[] getBuffer(){
        double[] tmp = this.buffer.clone();
        this.buffer = null;
        //System.gc();
        return tmp;
    }
    public int getFramesRead(){
        return this.framesRead;
    }
    
    public void operate(){
        
        try
        {
           //bufferedWriter = new BufferedWriter(new FileWriter(path + text));
           // Open the wav file specified as the first argument
           

           // Display information about the wav file
           this.wavFile.display();
           // Get the number of audio channels in the wav file
           //int numChannels = this.wavFile.getNumChannels();
           // Create a buffer of 100 frames
           //double[] buffer = new double[1];
              
           /*
           while (framesRead != 0) {
               if (count % dmax == 0){
                   System.out.println("Writing "+nooffile+" th file");
                   if (bufferedWriter == null){
                       bufferedWriter = new BufferedWriter(new FileWriter(path + text + nooffile + ".txt"));
                   }
                   else {
                       bufferedWriter.close();
                       bufferedWriter = new BufferedWriter(new FileWriter(path + text + nooffile + ".txt"));                             
                   }
                   nooffile += 1;
               }
               framesRead = wavFile.readFrames(buffer, 1);
               //System.out.println(count+" "+buffer[0]);
               time = (1.0*count)/sp;
               writestring = String.format("%.10f,%10f", time,buffer[0]);
               bufferedWriter.write(writestring);
               bufferedWriter.newLine();
               bufferedWriter.flush();
               count += 1;
           }
            */
           this.buffer = new double[this.dmax];
           this.framesRead = this.wavFile.readFrames(this.buffer, this.dmax);
           /*
           while (framesRead != 0){
               if (count % dmax == 0 && count > 0){
                   this.starttime = count;
                   return amp;
               }
               framesRead = wavFile.readFrames(buffer, 1);
               //System.out.println(count+" "+buffer[0]);               
               amp[(int)count%dmax] = buffer[0];
               count += 1;
           }
            */
           // Close the wavFile           
           //bufferedWriter.close();


           // Output the minimum and maximum value

        }
        catch (Exception e)
        {
           System.err.println(e);
        }      
   }
    
    public void finish() throws Exception{
        this.wavFile.close();
    }
   public static void main(String[] args)
   {
       System.out.println("Just test");
   }    
}
