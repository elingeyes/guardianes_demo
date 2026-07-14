package io.univalle.guardianes_demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    //Variables del juego
    private Texture fondo;
    private Texture[] personajeWalk;
    private Texture[] personajeJump;
    private Texture[] personajeCrouch;
    private Texture[] bloques;
    private Texture personajeActual;

    //Posicion del personaje
    private float posXPersonaje = 100;
    private float posYPersonaje = 255;
    private float velocidad = 200f;
    private float ancho = 50;
    private float altoNormal = 70;
    private float altoCrouch = 70;

    //Animación caminata
    private int frameActual = 0;
    private float tiempoAnimacion = 0;
    private float tiempoEntreFrames = 0.15f;
    private boolean seEstaMoviendo = false;

    //Salto
    private boolean isJumping = false;
    private float velocityY = 0f;
    private float gravity = -1000f; // px/s^2
    private float jumpVelocity = 450f;
    private int jumpFrameIndex = 0;

    //Agacharse
    private boolean isCrouching = false;
    private int crouchFrameIndex = 0;

    //Plataformas
    private Array<Rectangle> plataformas;
    private Array<Integer> plataformasTipo;
    private boolean tocandoPlataforma = false;

    @Override
    public void create() {
        batch = new SpriteBatch();

        fondo = new Texture("background.png");

        // Walk frames
        personajeWalk = new Texture[4];
        personajeWalk[0] = new Texture("characters/player/guardian/walk/walk_1.png");
        personajeWalk[1] = new Texture("characters/player/guardian/walk/walk_2.png");
        personajeWalk[2] = new Texture("characters/player/guardian/walk/walk_3.png");
        personajeWalk[3] = new Texture("characters/player/guardian/walk/walk_4.png");

        // Jump frames
        personajeJump = new Texture[3];
        personajeJump[0] = new Texture("characters/player/guardian/jump/jump_1.png");
        personajeJump[1] = new Texture("characters/player/guardian/jump/jump_2.png");
        personajeJump[2] = new Texture("characters/player/guardian/jump/jump_3.png");

        // Crouch frames
        personajeCrouch = new Texture[2];
        personajeCrouch[0] = new Texture("characters/player/guardian/crouch/crouch_1.png");
        personajeCrouch[1] = new Texture("characters/player/guardian/crouch/crouch_2.png");

        // Bloques
        bloques = new Texture[4];
        bloques[0] = new Texture("blocks/block_1.png");
        bloques[1] = new Texture("blocks/block_2.png");
        bloques[2] = new Texture("blocks/block_3.png");
        bloques[3] = new Texture("blocks/block_4.png");

        personajeActual = personajeWalk[0];

        // Inicializar plataformas
        inicializarPlataformas();
    }

    private void inicializarPlataformas() {
        plataformas = new Array<>();
        plataformasTipo = new Array<>();

        // Plataforma base (suelo)
        plataformas.add(new Rectangle(0, 130, 600, 20));
        plataformasTipo.add(0);

        // Bloque 1 - posición x=80, y=250
        plataformas.add(new Rectangle(80, 250, 80, 80));
        plataformasTipo.add(0);

        // Bloque 2 - posición x=200, y=300
        plataformas.add(new Rectangle(200, 300, 80, 80));
        plataformasTipo.add(1);

        // Bloque 3 - posición x=320, y=280
        plataformas.add(new Rectangle(320, 280, 80, 80));
        plataformasTipo.add(2);

        // Bloque 4 - posición x=440, y=320
        plataformas.add(new Rectangle(440, 320, 80, 80));
        plataformasTipo.add(3);
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        handleInput(deltaTime);
        updatePhysics(deltaTime);
        updateAnimation(deltaTime);

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(fondo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Dibujar bloques desde el array de plataformas
        for (int i = 0; i < plataformas.size; i++) {
            Rectangle plat = plataformas.get(i);
            int tipo = plataformasTipo.get(i);
            batch.draw(bloques[tipo], plat.x, plat.y, plat.width, plat.height);
        }

        float altoActual = isCrouching ? altoCrouch : altoNormal;
        batch.draw(personajeActual, posXPersonaje, posYPersonaje, ancho, altoActual);
        batch.end();
    }

    private void handleInput(float deltaTime) {
        seEstaMoviendo = false;
        isCrouching = false;

        // Horizontal movement allowed always
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            posXPersonaje -= velocidad * deltaTime;
            seEstaMoviendo = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            posXPersonaje += velocidad * deltaTime;
            seEstaMoviendo = true;
        }

        // Jump: start only when key just pressed and not already jumping
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !isJumping) {
            isJumping = true;
            velocityY = jumpVelocity;
            jumpFrameIndex = 0;
            tiempoAnimacion = 0;
            personajeActual = personajeJump[0];
            personajeActual = personajeJump[0];
        }

        // Crouch when DOWN is pressed and not jumping
        if (!isJumping && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isCrouching = true;
            seEstaMoviendo = false; // stay in place while crouching
            // set initial crouch frame (use second frame as resting crouch)
            crouchFrameIndex = 1;
            tiempoAnimacion = 0;
            personajeActual = personajeCrouch[1];
        }

        if (!seEstaMoviendo && !isJumping && !isCrouching) {
            frameActual = 0;
            tiempoAnimacion = 0;
            personajeActual = personajeWalk[0];
        }
    }

    private void updatePhysics(float deltaTime) {
        if (isJumping) {
            velocityY += gravity * deltaTime;
            posYPersonaje += velocityY * deltaTime;
            // Ground collision
            if (posYPersonaje <= 255f) {
                posYPersonaje = 255f;
                isJumping = false;
                velocityY = 0f;
                jumpFrameIndex = 0;
                tiempoAnimacion = 0;
                personajeActual = personajeWalk[0];
            }
        }
    }

    private void updateAnimation(float deltaTime) {
        if (isJumping) {
            // animate jump frames (advance until last frame, then hold)
            tiempoAnimacion += deltaTime;
            if (tiempoAnimacion >= tiempoEntreFrames) {
                tiempoAnimacion = 0;
                if (jumpFrameIndex < personajeJump.length - 1) jumpFrameIndex++;
                personajeActual = personajeJump[jumpFrameIndex];
            }
        } else if (isCrouching) {
            // animate crouch (toggle frames or hold first)
            tiempoAnimacion += deltaTime;
            if (tiempoAnimacion >= tiempoEntreFrames * 2) {
                tiempoAnimacion = 0;
                crouchFrameIndex = (crouchFrameIndex + 1) % personajeCrouch.length;
                personajeActual = personajeCrouch[crouchFrameIndex];
            }
        } else if (seEstaMoviendo) {
            tiempoAnimacion += deltaTime;
            if (tiempoAnimacion >= tiempoEntreFrames) {
                tiempoAnimacion = 0;
                frameActual = (frameActual + 1) % personajeWalk.length;
                personajeActual = personajeWalk[frameActual];
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        fondo.dispose();
        for (Texture texture : personajeWalk) {
            texture.dispose();
        }
        for (Texture texture : personajeJump) {
            texture.dispose();
        }
        for (Texture texture : personajeCrouch) {
            texture.dispose();
        }
        for (Texture texture : bloques) {
            texture.dispose();
        }
    }
}
