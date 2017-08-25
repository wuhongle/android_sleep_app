/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package android.audiorecordapp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import static java.lang.System.in;
import sun.applet.Main;
import java.util.Arrays;
/**
 *
 * @author Noppayut
 */
public class BurstExtraction {
    public static final int SAMPLE_RATE = 48000/2;
    public static final int DMAX = 28800000;
    public static final int SMAX = 6;
    public static final double S = 1.5d;
    public static final double GAMMA = 200.0d;

    private int[] state;
    private int[][] pre_state;
    private int waves;
    private int count;
    
    private double[] sigma;
    private double[] In;
    private double[] pre_cost;
    private double[] log_sigma;
    private long starttime;
    private String pwd;
    
    BurstExtraction(double[] signal, long starttime){
        this.state = new int[DMAX];
        this.pre_state = new int[SMAX][DMAX];
        this.waves = 0;
        this.count = 0;
        this.sigma = new double[SMAX];
        this.In = new double[DMAX];
        for (int i = 0 ; i < signal.length-5; i++){
            this.In[i] = signal[i+5];
        }
        //this.In = Arrays.copyOfRange(signal, 5, signal.length);
        this.pre_cost = new double[SMAX];
        this.log_sigma = new double[SMAX];                       
        this.pwd = System.getProperty("user.dir");
        this.starttime = starttime;
    }
    

    
    void operate(String filename) throws Exception{
        /*
         *  perform the algorithm by reading text file 
         */
        int current;  
        ReadFile(filename);
        init();
        state_transition();
        current = this.waves;
        System.out.println(current);
        while(current-- > 0){
            output(filename);
        }
    }
    
    void operateInMem(String filename) throws Exception{
        /*
         *  perform the algorithm in memory
         */
        int current;
        preprocess();
        init();
        state_transition();
        current = this.waves;
        System.out.println(current);
        while(current-- > 0){
            output(filename);
        }
    }
    
    void ReadFile(String filename) throws Exception{
        BufferedReader inputfile = null;
        
        try {
            inputfile = new BufferedReader(new FileReader(this.pwd + "\\"+filename));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //abandon first 5 lines
        //firstly, get start time at the first line
        //this.starttime = Double.parseDouble(inputfile.readLine().split(",")[0]);
        this.starttime = (int) Double.parseDouble(inputfile.readLine().split(",")[0]);
        //then, throw away the rest 4 lines
        int nope = 4;
        while(nope-- > 0){
            inputfile.readLine();
        }
        for (int i = 0 ; i < DMAX; i++){
            this.In[i] = 0;
        }
        String read;
        int linecount = 0;
        for (int i = 0 ; i < DMAX ; i++){
            read = inputfile.readLine();
            //System.out.println(read);
            
            if (read == null) {
                System.out.println("Print line count "+linecount);
                break;             
            }
           
            this.In[i] = Double.parseDouble(read.split(",")[1]);
            linecount++;
        }
        
        preprocess();
        inputfile.close();
    }
    
    void preprocess(){
        double v = 0.0;
        for (int i = 0 ; i < DMAX; i++){
            v += this.In[i];
        }
        v /= DMAX;
        for (int i = 0 ; i < DMAX; i++){
            this.In[i] = this.In[i] - v;
        }

        
    }
    
    void init(){
        this.sigma[0] = Math.sqrt(variance(0, DMAX));        
        this.log_sigma[0] = Math.log(Math.sqrt(2*Math.PI)*this.sigma[0]);
        this.pre_cost[0] = 0.0;
        this.state[0] = 0;
        for (int i = 1 ; i < SMAX ; i++){
            this.sigma[i] = S*this.sigma[i-1];
            this.log_sigma[i] = Math.log(Math.sqrt(2*Math.PI)*this.sigma[i]);
            this.pre_cost[i] = 99999.0;
        }
        
    }
    
    void state_transition(){
        int l,t,k;
        double min;
        double[] c = new double[SMAX];
        
        for(t = 1; t < DMAX; t++){
            for(l = 0; l < SMAX; l++){
                c[l] = costFunc(l, t);
            }
            for (l = 0 ; l < SMAX; l++){
                this.pre_cost[l] = c[l];
            }
        }
        min = this.pre_cost[0];
        k = 0;
        for(l = 1; l < SMAX; l++){
            if(this.pre_cost[l] < min){
                min = this.pre_cost[l];
                k = l;
            }
        }
        
        this.state[DMAX-1] = k;
        for (t = DMAX-2; t > 0; t--){
            this.state[t] = this.pre_state[this.state[t+1]][t+1];
            if(this.state[t+1] > 0 && this.state[t] == 0) {
                this.waves++;
            }
        }
        System.out.println(this.waves);
    }
    
    double costFunc(int j, int t){
        double min_c;
        double[] c = new double[SMAX];
        for (int l = 0 ; l < SMAX; l++ ){
            c[l] = this.pre_cost[l] + transitionCost(l, j);
        }
        
        min_c = c[0];
        int k = 0;
        for (int l = 1 ; l < SMAX ; l++){
            if(c[l] < min_c){
                min_c = c[l];
                k = l;
            }
        }
        this.pre_state[j][t] = k;
        
        return this.log_sigma[j] + (this.In[t]*this.In[t])/(2*this.sigma[j]*this.sigma[j]) + min_c;
    }
    
    double transitionCost(int l, int j){
        return (l < j) ? (j-l)*GAMMA*Math.log(DMAX):0;
    }
    
    double variance(int srt, int end){
        double mu = 0.0d;
        double v = 0.0d;
        for(int i = srt; i < end ; i++){
            v += this.In[i]*this.In[i];
        }
        v /= (double) (end - srt);
        return v;            
        
    }
    
    void output(String filename) throws IOException{
        int t,flag ,ttsec , sec, min, hr,timeintv;
        flag = 0;
        BufferedWriter outfile = null;
        for(t = this.count ; t < DMAX ; t++){
            if(this.state[t] > 0 && flag == 0){
                flag = 1;
                ttsec = (int) ((t+this.starttime)/ SAMPLE_RATE);
                hr = ttsec / 3600;
                min = (ttsec % 3600)/ 60;
                sec = ttsec%60;
                timeintv = (int)(((double)((t + this.starttime)% SAMPLE_RATE )/SAMPLE_RATE) * 1000);                
                String fname = String.format("%s_%06d_%02d%02d%02d_%03d_h.dat",filename,ttsec,hr,min,sec,timeintv);
                outfile = new BufferedWriter(new FileWriter(this.pwd+"\\result\\"+fname));                
            }
            if(flag==1){
                int arg1 = (int) (t+this.starttime);
                double arg2 = ((double)t + this.starttime)/SAMPLE_RATE;
                double arg3 =  this.In[t]*10;
                int arg4 = this.state[t];
                String writestring = String.format("%d\t%f\t%f\t%d\n",arg1,arg2,arg3,arg4);
                outfile.write(writestring);
                if(this.state[t+1]==0){
                    this.count = t+1;
                    break;
                }
            }
        }
        
        if(flag==1){
            outfile.close();
        }
    }
    public static void main(String[] args) throws Exception{
        /*
        BurstExtraction b = new BurstExtraction();
        b.operate("0");
        */
        System.out.println("Burst extraction");
    }
    
    
    
}
