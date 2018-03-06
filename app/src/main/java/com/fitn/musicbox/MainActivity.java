package com.fitn.musicbox;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView leftTime;
    private TextView rightTime;
    private SeekBar seekBar;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                leftTime.setText(dateFormat.format(new Date(currentPosition)));
                rightTime.setText(dateFormat.format(new Date(duration - currentPosition)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setupUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.yeba);
        artistImage = (ImageView) findViewById(R.id.music_image);
        leftTime = (TextView) findViewById(R.id.leftTime);
        rightTime = (TextView) findViewById(R.id.rightTime);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        prevButton = (Button) findViewById(R.id.prevButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        playButton = (Button) findViewById(R.id.playButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.prevButton:
                prevMusic();
                break;

            case R.id.playButton:
                if (mediaPlayer.isPlaying()){
                    pauseMusic();
                } else {
                    startMusic();
                }
                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    public void startMusic(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            playButton.setBackgroundResource(android.R.drawable.ic_media_pause);
            updateThread();
        }
    }

    public void prevMusic(){
        if (mediaPlayer.isPlaying())
        mediaPlayer.seekTo(0);
    }

    public void nextMusic(){
        if (mediaPlayer.isPlaying())
        mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
        playButton.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    public void updateThread(){
        thread = new Thread() {
            @Override
            public void run(){
               try {

                   while(mediaPlayer != null && mediaPlayer.isPlaying()) {
                       Thread.sleep(50);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                                seekBar.setMax(mediaPlayer.getDuration());
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());

                                leftTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                .format(new Date(mediaPlayer.getCurrentPosition()))));

                               rightTime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                       .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));
                           }
                       });
                   }
               }
               catch(InterruptedException ie){
                   ie.printStackTrace();
               }
            }
        };
        thread.start();
    }

    @Override
    // make sure the music stops playing when the app is closed
    //Note the music doesnt stop if the app window is not focused on
    protected void onDestroy() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        thread.interrupt();
        thread = null;
        super.onDestroy();
    }

}
