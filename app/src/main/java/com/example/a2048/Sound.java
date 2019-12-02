package com.example.a2048;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class Sound
{
    Context context;
    private SoundPool soundPool;
    private int swipe, merge, win, lose;

    public Sound(Context context)
    {
        this.context = context;

        AudioAttributes audioAttributes = new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).
                setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setMaxStreams(4).setAudioAttributes(audioAttributes).build();

        swipe = soundPool.load(context, R.raw.click, 1);
        merge = soundPool.load(context, R.raw.merge, 1);
        win = soundPool.load(context, R.raw.win, 1);
        lose = soundPool.load(context, R.raw.lose, 1);
    }

    public void playSwipe()
    {
        soundPool.play(swipe, 1, 1, 0, 0,  1);
    }

    public void playMerge()
    {
        soundPool.play(merge, 1, 1, 0, 0,  1);
    }

    public void playWin()
    {
        soundPool.play(win, 1, 1, 0, 0,  1);
    }

    public void playLose()
    {
        soundPool.play(lose, 1, 1, 0, 0,  1);
    }

}
