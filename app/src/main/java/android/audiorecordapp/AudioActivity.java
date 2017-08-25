package android.audiorecordapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static android.media.MediaRecorder.*;

public class AudioActivity extends AppCompatActivity {
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private MediaRecorder mediaRecorder;
    private String mFileName;
    private File ff;
    String realfilename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }


    public void record(View v){
        Button btnRecord = (Button) v;
        // Start the recording

        if (v.getTag() == "start" || v.getTag() == null) {
            v.setTag("stop");
            btnRecord.setText("Stop");
            //Toast.makeText(this, "Start recording", Toast.LENGTH_LONG).show();
            //mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFileName = "audiorecordtest.3gp";

            ff = new File(this.getApplicationContext().getFilesDir(), mFileName);
            realfilename = ff.toString();
            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.setOutputFile(realfilename);
                Toast.makeText(this, "Record : "+realfilename, Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, "1 "+e.toString(), Toast.LENGTH_LONG).show();
            }

            try {
                mediaRecorder.prepare();
            }
            catch (Exception e){
                Toast.makeText(this, "2 "+e.toString(), Toast.LENGTH_LONG).show();
            }

            try {
                mediaRecorder.start();
            }
            catch (Exception e){
                Toast.makeText(this, "3 "+e.toString(), Toast.LENGTH_LONG).show();
            }

        }
        else if (v.getTag() == "stop") {
            try {
                Toast.makeText(this, "Stop recording", Toast.LENGTH_LONG).show();
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

            v.setTag("start");
            btnRecord.setText("Record");

            //System.out.println("STOPPED");

        }
        //Toast.makeText(this, mFileName+"", Toast.LENGTH_LONG).show();
    }

    public void playback(View v){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(realfilename);
            mediaPlayer.prepare(); // must call prepare first
            Toast.makeText(this, realfilename+"", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start(); // then start
        Toast.makeText(this, "Start playing", Toast.LENGTH_LONG).show();

    }

}
