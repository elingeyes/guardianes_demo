package io.univalle.guardianes_demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    //Variables del juego
    private Texture fondo;
    private Texture personaje;

    //Posicion del personaje
    private float posXPersonaje = 100;
    private float posYPersonaje = 170;
    private float velocidad = 200f;
    private float ancho = 50;
    private float alto = 70;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //image = new Texture("libgdx.png");

        fondo = new Texture("background.png");
        personaje = new Texture("characters/player/guardian/walk/walk_1.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(personaje, posXPersonaje, posYPersonaje, ancho, alto);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        personaje.dispose();
    }
}
