package com.enricmieza.gdxsound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.AudioDevice;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    float freq = 440.0f;        // en Hz
    float sampleRate = 44100;   // 44.1 kHz - qualitat CD
    float step = 0.1f;          // fragment minim de so
    boolean sona = true;
    boolean running = true;
    AudioDevice audioDevice;
    AudioThread audioThread;

    public static float[] generaSinusoide(float frequency, float sampleRate, float durationInSeconds) {
        int numSamples = (int) (sampleRate * durationInSeconds);
        float[] samples = new float[numSamples];
        for (int i = 0; i < numSamples; i++) {
            float t = i / sampleRate;
            samples[i] = (float) Math.sin(2 * Math.PI * frequency * t);
        }
        return samples;
    }

    class AudioThread extends Thread {
        @Override
        public void run() {
            // creem sinusoide (un sol cop)
            float[] sineWave = generaSinusoide(freq, sampleRate, step);

            while(running) {
                if( sona )
                    audioDevice.writeSamples(sineWave, 0, sineWave.length);
                else {
                    // dormim una estona (la mateixa que el fragment d'àudio)
                    try {
                        Thread.sleep((long) (step*1000) );
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void create() {
        // ...elements GUI...
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        // Crear un dispositiu d'àudio
        audioDevice = Gdx.audio.newAudioDevice((int) sampleRate, true);

        // creem i posem en marxa el thread d'audio
        audioThread = new AudioThread();
        audioThread.start();
    }

    @Override
    public void render() {
        // ...activitat GUI...
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        // al detectar un touch apaguem o engeguem
        if( Gdx.input.justTouched() ) {
            sona = ! sona;
        }
    }

    @Override
    public void dispose() {
        // indiquem al thread de so que acabi
        running = false;

        // destruim objectes GUI
        batch.dispose();
        image.dispose();

        // IMPORTANT: esperem a que acabi de sortir el thread d'audio
        try {
            audioThread.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
