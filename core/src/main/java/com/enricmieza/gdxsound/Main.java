package com.enricmieza.gdxsound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

    public static float[] generateSineWave(float frequency, float sampleRate, int durationInSeconds) {
        int numSamples = (int) (sampleRate * durationInSeconds);
        float[] samples = new float[numSamples];
        for (int i = 0; i < numSamples; i++) {
            float t = i / sampleRate;
            samples[i] = (float) Math.sin(2 * Math.PI * frequency * t);
        }
        return samples;
    }

    AudioDevice audioDevice;
    Thread soundThread = new Thread(() -> {
        float sampleRate = 44100; // 44.1 kHz
        float frequency = 440; // 440 Hz (La4)
        int durationInSeconds = 2; // 2 segons

        float[] sineWave = generateSineWave(frequency, sampleRate, durationInSeconds);

        audioDevice.writeSamples(sineWave, 0, sineWave.length);
    });


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        //int durationInSeconds = 5;
        //float[] sineWave = generateSineWave(freq, sampleRate, durationInSeconds);

        // Crear un dispositiu d'àudio i reproduir l'ona sinusoïdal
        audioDevice = Gdx.audio.newAudioDevice((int) sampleRate, true);
        //audioDevice.writeSamples(sineWave, 0, sineWave.length);

        soundThread.start();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
