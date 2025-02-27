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

    public static float[] generaSinusoide(float frequency, float sampleRate, float durationInSeconds) {
        int numSamples = (int) (sampleRate * durationInSeconds);
        float[] samples = new float[numSamples];
        for (int i = 0; i < numSamples; i++) {
            float t = i / sampleRate;
            samples[i] = (float) Math.sin(2 * Math.PI * frequency * t);
        }
        return samples;
    }

    public static float[] generaSilenci(float sampleRate, float durationInSeconds) {
        int numSamples = (int) (sampleRate * durationInSeconds);
        float[] samples = new float[numSamples];
        for (int i = 0; i < numSamples; i++) {
            samples[i] = (float) 0.0f;
        }
        return samples;
    }

    Thread soundThread = new Thread(() -> {
        float[] sineWave = generaSinusoide(freq, sampleRate, step);
        float[] silenci = generaSilenci(sampleRate, step);

        while(running) {
            if( sona )
                audioDevice.writeSamples(sineWave, 0, sineWave.length);
            else {
                audioDevice.writeSamples(silenci, 0, silenci.length);
            }
        }
    });


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        // Crear un dispositiu d'àudio i reproduir l'ona sinusoïdal
        audioDevice = Gdx.audio.newAudioDevice((int) sampleRate, true);
        // posem en marxa el thread d'audio
        soundThread.start();
    }

    @Override
    public void render() {
        // GUI
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
        // esperem a que acabi de sortir el thread d'audio
        try {
            soundThread.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
