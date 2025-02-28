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
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();

        // al detectar un touch apaguem o engeguem
        if( Gdx.input.isTouched() ) {
            synth.sound = true;
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            synth.freq = 440 + x;
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
