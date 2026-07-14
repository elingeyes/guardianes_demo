package io.univalle.guardianes_demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    // =====================================================
    // TAMAÑO DEL MUNDO
    // =====================================================
    private static final int ANCHO_MUNDO = 1820;
    private static final int ALTO_MUNDO = 1040;

    // =====================================================
    // OBJETOS DE DIBUJO
    // =====================================================

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport viewport;

    // =====================================================
    // RECURSOS GRÁFICOS
    // =====================================================

    private Texture fondo;

    private Texture[] personajeWalk;
    private Texture[] personajeJump;
    private Texture[] personajeCrouch;

    private Texture[] bloques;

    private Texture personajeActual;

    // =====================================================
    // POSICIÓN Y TAMAÑO DEL PERSONAJE
    // =====================================================

    //El personaje inicia en la coordenada (150,370)
    private float posXPersonaje = 150;
    private float posYPersonaje = 370;

    //El personaje mide 50 de ancho por 70 de alto
    private float ancho = 50;
    private float altoNormal = 70;
    //tamaño del personaje al agacharse
    private float altoCrouch = 70;

    //velocidad a la que se mueve el personaje 200 frames por seg.
    private float velocidadMovimiento = 200f;

    // =====================================================
    // CONSTANTES DE FÍSICA
    // =====================================================

    private static final float GRAVEDAD = -1000f;
    private static final float VELOCIDAD_SALTO = 450f;

    // =====================================================
    // CONTROL DE SALTO
    // =====================================================

    private boolean estaSaltando = false;
    private float velocidadY = 0f;

    // =====================================================
    // CONTROL DE AGACHARSE
    // =====================================================

    private boolean estaAgachado = false;

    // =====================================================
    // CONTROL DE MOVIMIENTO
    // =====================================================

    private boolean seEstaMoviendo = false;

    // =====================================================
    // ANIMACIÓN
    // =====================================================

    private int frameActual = 0;

    private float tiempoAnimacion = 0;
    private float tiempoEntreFrames = 0.15f;

    private int jumpFrameIndex = 0;
    private int crouchFrameIndex = 0;

    // =====================================================
    // PLATAFORMAS
    // =====================================================

    private Array<Rectangle> plataformas;
    private Array<Integer> plataformasTipo;

    // =====================================================
    // CREATE
    // =====================================================

    @Override
    public void create() {

        batch = new SpriteBatch();

        camera = new OrthographicCamera();

        viewport = new FitViewport(
            ANCHO_MUNDO,
            ALTO_MUNDO,
            camera
        );

        viewport.apply();

        camera.position.set(
            ANCHO_MUNDO / 2f,
            ALTO_MUNDO / 2f,
            0
        );

        camera.update();

        fondo = new Texture("background.png");

        // ---------------------------------------------
        // Animación caminar
        // ---------------------------------------------

        personajeWalk = new Texture[4];

        personajeWalk[0] = new Texture("characters/player/guardian/walk/walk_1.png");
        personajeWalk[1] = new Texture("characters/player/guardian/walk/walk_2.png");
        personajeWalk[2] = new Texture("characters/player/guardian/walk/walk_3.png");
        personajeWalk[3] = new Texture("characters/player/guardian/walk/walk_4.png");

        // ---------------------------------------------
        // Animación salto
        // ---------------------------------------------

        personajeJump = new Texture[3];

        personajeJump[0] = new Texture("characters/player/guardian/jump/jump_1.png");
        personajeJump[1] = new Texture("characters/player/guardian/jump/jump_2.png");
        personajeJump[2] = new Texture("characters/player/guardian/jump/jump_3.png");

        // ---------------------------------------------
        // Animación agacharse
        // ---------------------------------------------

        personajeCrouch = new Texture[2];

        personajeCrouch[0] = new Texture("characters/player/guardian/crouch/crouch_1.png");
        personajeCrouch[1] = new Texture("characters/player/guardian/crouch/crouch_2.png");

        // ---------------------------------------------
        // Texturas de bloques
        // ---------------------------------------------

        bloques = new Texture[4];

        bloques[0] = new Texture("blocks/block_1.png");
        bloques[1] = new Texture("blocks/block_2.png");
        bloques[2] = new Texture("blocks/block_3.png");
        bloques[3] = new Texture("blocks/block_4.png");

        personajeActual = personajeWalk[0];

        inicializarPlataformas();
    }

    // =====================================================
    // CREAR PLATAFORMAS
    // =====================================================

    private void inicializarPlataformas() {

        plataformas = new Array<>();
        plataformasTipo = new Array<>();

        // Plataforma principal (suelo)

        //plataformas.add(new Rectangle(0, 400, 600, 20));
        //plataformasTipo.add(0);

        // Bloques elevados

        plataformas.add(new Rectangle(700, 400, 50, 50));
        plataformasTipo.add(0);

        plataformas.add(new Rectangle(800, 450, 50, 50));
        plataformasTipo.add(1);

        plataformas.add(new Rectangle(900, 500, 50, 50));
        plataformasTipo.add(2);

        plataformas.add(new Rectangle(1000, 370, 50, 50));
        plataformasTipo.add(3);
    }

    // =====================================================
    // CICLO PRINCIPAL DEL JUEGO
    // =====================================================

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        // 1. Leer teclado
        handleInput(deltaTime);

        // 2. Actualizar física
        updatePhysics(deltaTime);

        // 3. Actualizar animaciones
        updateAnimation(deltaTime);

        // 4. Dibujar pantalla
        dibujar();
    }

    // =====================================================
    // DIBUJAR
    // =====================================================


    @Override
    public void resize(int width, int height) {

        viewport.update(width, height);
    }

    private void dibujar() {

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        camera.update();

        batch.setProjectionMatrix(
            camera.combined
        );

        batch.begin();

        // Fondo

        batch.draw(
            fondo,
            0,
            0,
            ANCHO_MUNDO,
            ALTO_MUNDO
        );

        // Plataformas

        for (int i = 0; i < plataformas.size; i++) {

            Rectangle plataforma = plataformas.get(i);
            int tipo = plataformasTipo.get(i);

            batch.draw(
                bloques[tipo],
                plataforma.x,
                plataforma.y,
                plataforma.width,
                plataforma.height
            );
        }

        // Personaje

        // Determinar la altura que tendrá el personaje

        float altoActual;

        if (estaAgachado) {

            // Si está agachado usamos la altura de agachado
            altoActual = altoCrouch;

        } else {

            // Si está de pie usamos la altura normal
            altoActual = altoNormal;
        }

        batch.draw(
            personajeActual,
            posXPersonaje,
            posYPersonaje,
            ancho,
            altoActual
        );

        batch.end();
    }

    // =====================================================
    // ENTRADAS DEL TECLADO
    // =====================================================

    private void handleInput(float deltaTime) {

        seEstaMoviendo = false;
        estaAgachado = false;

        // Mover izquierda

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            posXPersonaje -= velocidadMovimiento * deltaTime;
            seEstaMoviendo = true;
        }

        // Mover derecha

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            posXPersonaje += velocidadMovimiento * deltaTime;
            seEstaMoviendo = true;
        }

        // Saltar

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !estaSaltando) {

            estaSaltando = true;
            velocidadY = VELOCIDAD_SALTO;

            jumpFrameIndex = 0;
            tiempoAnimacion = 0;

            personajeActual = personajeJump[0];
        }

        // Agacharse

        if (!estaSaltando && Gdx.input.isKeyPressed(Input.Keys.DOWN)) {

            estaAgachado = true;

            crouchFrameIndex = 1;
            tiempoAnimacion = 0;

            personajeActual = personajeCrouch[1];
        }

        // Quieto

        if (!seEstaMoviendo && !estaSaltando && !estaAgachado) {

            frameActual = 0;
            tiempoAnimacion = 0;

            personajeActual = personajeWalk[0];
        }
    }

    // =====================================================
    // FÍSICA Y COLISIONES
    // =====================================================

    private void updatePhysics(float deltaTime) {

        //System.out.println("Saltando = " + estaSaltando);
        // --------------------------------------------------
        // Verificar si existe una plataforma debajo
        // del personaje
        // --------------------------------------------------

        boolean haySueloDebajo = false;

        // Calcular el centro de los pies del personaje
        float centroJugador = posXPersonaje + (ancho / 2);

        // Recorrer todas las plataformas
        for (int i = 0; i < plataformas.size; i++) {

            Rectangle plataforma = plataformas.get(i);

            // Parte superior de la plataforma
            float parteSuperiorPlataforma =
                plataforma.y + plataforma.height;

            // Verificar si el centro del personaje
            // está dentro del ancho de la plataforma

            boolean estaDentroDelAncho =
                centroJugador >= plataforma.x
                    &&
                    centroJugador <= plataforma.x + plataforma.width;

            // Verificar si los pies están sobre la plataforma

            boolean estaSobreLaPlataforma =
                Math.abs(posYPersonaje - parteSuperiorPlataforma) < 2;

            if (estaDentroDelAncho && estaSobreLaPlataforma) {

                haySueloDebajo = true;
                break;
            }
        }

        // Si ya no existe suelo debajo,
        // el personaje debe comenzar a caer

        // Si está por encima del suelo principal
        if (posYPersonaje > 370) {

            // Si ya no hay plataforma debajo
            if (!haySueloDebajo) {

                // Comienza a caer
                estaSaltando = true;
            }
        }

        // Si no está saltando ni cayendo,
        // no es necesario continuar

        if (!estaSaltando) {
            return;
        }

        // --------------------------------------------------
        // Guardar la posición anterior de los pies
        // --------------------------------------------------

        float posicionAnteriorY = posYPersonaje;

        // --------------------------------------------------
        // Aplicar gravedad
        // --------------------------------------------------

        velocidadY += GRAVEDAD * deltaTime;

        // --------------------------------------------------
        // Mover personaje verticalmente
        // --------------------------------------------------

        posYPersonaje += velocidadY * deltaTime;

        // --------------------------------------------------
        // Rectángulo de colisión del personaje
        // --------------------------------------------------

        Rectangle jugador = new Rectangle(
            posXPersonaje,
            posYPersonaje,
            ancho,
            altoNormal
        );

        // --------------------------------------------------
        // Verificar colisiones con plataformas
        // --------------------------------------------------

        for (int i = 0; i < plataformas.size; i++) {

            Rectangle plataforma = plataformas.get(i);

            // Parte superior de la plataforma

            float parteSuperiorPlataforma =
                plataforma.y + plataforma.height;

            // Verificar si el personaje coincide con el ancho
            // de la plataforma

            boolean entraPorLaIzquierda =
                jugador.x + jugador.width > plataforma.x;

            boolean entraPorLaDerecha =
                jugador.x < plataforma.x + plataforma.width;

            boolean coincideEnX =
                entraPorLaIzquierda && entraPorLaDerecha;

            // Verificar que el personaje esté cayendo

            boolean estaCayendo =
                velocidadY <= 0;

            // Antes del movimiento los pies estaban arriba

            boolean estabaArriba =
                posicionAnteriorY >= parteSuperiorPlataforma;

            // Después del movimiento los pies quedaron abajo

            boolean ahoraEstaAbajo =
                posYPersonaje <= parteSuperiorPlataforma;

            // Si estaba arriba y ahora abajo,
            // cruzó la superficie de la plataforma

            if (coincideEnX
                && estaCayendo
                && estabaArriba
                && ahoraEstaAbajo) {

                // Colocar los pies sobre la plataforma

                posYPersonaje = parteSuperiorPlataforma;

                // Detener la caída

                velocidadY = 0;

                // Ya no está saltando

                estaSaltando = false;

                // Reiniciar animación

                jumpFrameIndex = 0;
                tiempoAnimacion = 0;

                personajeActual = personajeWalk[0];

                break;
            }
        }

        // --------------------------------------------------
        // Evitar que caiga fuera de la pantalla
        // --------------------------------------------------

        if (posYPersonaje < 370) {

            posYPersonaje = 370;

            velocidadY = 0;

            estaSaltando = false;

            personajeActual = personajeWalk[0];
        }
    }

    // =====================================================
    // ANIMACIONES
    // =====================================================

    private void updateAnimation(float deltaTime) {

        if (estaSaltando) {

            tiempoAnimacion += deltaTime;

            if (tiempoAnimacion >= tiempoEntreFrames) {

                tiempoAnimacion = 0;

                if (jumpFrameIndex < personajeJump.length - 1) {
                    jumpFrameIndex++;
                }

                personajeActual = personajeJump[jumpFrameIndex];
            }

        } else if (estaAgachado) {

            tiempoAnimacion += deltaTime;

            if (tiempoAnimacion >= tiempoEntreFrames * 2) {

                tiempoAnimacion = 0;

                crouchFrameIndex =
                    (crouchFrameIndex + 1)
                        % personajeCrouch.length;

                personajeActual =
                    personajeCrouch[crouchFrameIndex];
            }

        } else if (seEstaMoviendo) {

            tiempoAnimacion += deltaTime;

            if (tiempoAnimacion >= tiempoEntreFrames) {

                tiempoAnimacion = 0;

                frameActual =
                    (frameActual + 1)
                        % personajeWalk.length;

                personajeActual =
                    personajeWalk[frameActual];
            }
        }
    }

    // =====================================================
    // LIBERAR MEMORIA
    // =====================================================

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
