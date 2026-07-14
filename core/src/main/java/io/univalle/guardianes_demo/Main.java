package io.univalle.guardianes_demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    //Variables del juego
    private Texture fondo;
    private Texture[] personajeWalk;
    private Texture personajeActual;

    //Posicion del personaje
    private float posXPersonaje = 100;
    private float posYPersonaje = 170;
    private float velocidad = 200f;
    private float ancho = 50;
    private float alto = 70;

    //Animación
    private int frameActual = 0;
    private float tiempoAnimacion = 0;
    private float tiempoEntreFrames = 0.15f;

    @Override
    public void create() {
        batch = new SpriteBatch();

        fondo = new Texture("background.png");

        personajeWalk = new Texture[4];
        personajeWalk[0] = new Texture("characters/player/guardian/walk/walk_1.png");
        personajeWalk[1] = new Texture("characters/player/guardian/walk/walk_2.png");
        personajeWalk[2] = new Texture("characters/player/guardian/walk/walk_3.png");
        personajeWalk[3] = new Texture("characters/player/guardian/walk/walk_4.png");

        personajeActual = personajeWalk[0];
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updateAnimation(deltaTime);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(personajeActual, posXPersonaje, posYPersonaje, ancho, alto);
        batch.end();
    }

    private void handleInput(float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            posXPersonaje -= velocidad * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            posXPersonaje += velocidad * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            posYPersonaje += velocidad * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            posYPersonaje -= velocidad * deltaTime;
        }
    }

    private void updateAnimation(float deltaTime) {
        tiempoAnimacion += deltaTime;
        if (tiempoAnimacion >= tiempoEntreFrames) {
            tiempoAnimacion = 0;
            frameActual = (frameActual + 1) % 4;
            personajeActual = personajeWalk[frameActual];
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        for (Texture texture : personajeWalk) {
            texture.dispose();
        }
    }
}
