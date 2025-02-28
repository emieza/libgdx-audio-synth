package com.enricmieza.gdxsound;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    GdxSynth synth;

    @Override
    public void create() {
        // ...elements GUI...
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        // creem i posem en marxa el thread d'audio
        synth = new GdxSynth();
        synth.start();
    }

    @Override
    public void render() {
        // ...activitat GUI...
        ScreenUtils.clear(0.25f, 0.15f, 0.35f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        // al detectar un touch apaguem o engeguem
        if( Gdx.input.isTouched() ) {
            synth.sound = true;
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();
            int height = Gdx.graphics.getHeight();
            int width = Gdx.graphics.getWidth();

            // l'amplitud la fem en funció (inversa) de la distància X
            // modificació logarítmica
            final float a = 999;
            float linx = x/width;
            float logx = (float) ( Math.log(1+a*linx)/Math.log(1+a) );
            synth.amplitude = 1-logx;
            //synth.amplitude = 1-linx; //lineal per test
            // la freqüència base és a l'extrem inferior (440 Hz)
            // augmenta al doble quan arriba al limit superior
            synth.freq = 440 * (1 + ((float)height - y) / (float)height );
        } else {
            synth.sound = false;
        }
    }

    @Override
    public void dispose() {
        // indiquem al thread de so que acabi
        synth.running = false;

        // destruim objectes GUI
        batch.dispose();
        image.dispose();

        // IMPORTANT: esperem a que acabi de sortir el thread d'audio
        try {
            synth.join();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
