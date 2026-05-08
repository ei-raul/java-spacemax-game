package com.spacemax;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {

    private final SpaceMaxGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    // Assets
    private Texture shipTexture;
    private BitmapFont hudFont;
    private FreeTypeFontGenerator fontGenerator;

    // Game state
    private float shipX;
    private float shipY;
    private float shipSpeed = 400f; // pixels per second
    private float shipWidth = 80;
    private float shipHeight = 80;
    private int score = 0;
    private int lives = 3;

    // Stars
    private float[] starX;
    private float[] starY;
    private final int NUM_STARS = 100;

    // Lasers
    private Array<Laser> lasers;
    private float laserCooldown = 0.2f;
    private float timeSinceLastShot = 0;

    private static class Laser {
        float x, y;
        float width = 6;
        float height = 30;
        float speed = 800f;
        boolean active = true;

        public Laser(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void update(float delta) {
            y += speed * delta;
            if (y > Gdx.graphics.getHeight()) {
                active = false;
            }
        }
    }

    public GameScreen(SpaceMaxGame game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Load textures
        shipTexture = new Texture(Gdx.files.internal("spaceship.png"));

        // Set initial ship position (bottom center)
        shipX = Gdx.graphics.getWidth() / 2f - shipWidth / 2f;
        shipY = 20; // 20 pixels from bottom

        // Generate HUD Font
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.shadowColor = Color.BLACK;
        hudFont = fontGenerator.generateFont(parameter);

        // Initialize stars
        starX = new float[NUM_STARS];
        starY = new float[NUM_STARS];
        for (int i = 0; i < NUM_STARS; i++) {
            starX[i] = MathUtils.random(0, Gdx.graphics.getWidth());
            starY[i] = MathUtils.random(0, Gdx.graphics.getHeight());
        }

        lasers = new Array<>();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        timeSinceLastShot += delta;

        // Update logic
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            shipX -= shipSpeed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            shipX += shipSpeed * delta;
        }

        // Shooting logic
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && timeSinceLastShot >= laserCooldown) {
            lasers.add(new Laser(shipX + shipWidth / 2f - 3f, shipY + shipHeight));
            timeSinceLastShot = 0;
        }

        // Clamp to screen bounds
        if (shipX < 0) {
            shipX = 0;
        }
        if (shipX > Gdx.graphics.getWidth() - shipWidth) {
            shipX = Gdx.graphics.getWidth() - shipWidth;
        }

        // Update stars falling
        for (int i = 0; i < NUM_STARS; i++) {
            starY[i] -= 50 * delta; // slow fall
            if (starY[i] < 0) {
                starY[i] = Gdx.graphics.getHeight();
                starX[i] = MathUtils.random(0, Gdx.graphics.getWidth());
            }
        }

        // Update lasers
        for (int i = lasers.size - 1; i >= 0; i--) {
            Laser laser = lasers.get(i);
            laser.update(delta);
            if (!laser.active) {
                lasers.removeIndex(i);
            }
        }

        // Clear screen (Black space)
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw stars
        shapeRenderer.begin(ShapeRenderer.ShapeType.Point);
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < NUM_STARS; i++) {
            shapeRenderer.point(starX[i], starY[i], 0);
        }
        shapeRenderer.end();

        // Draw lasers (Additive blending for glow effect)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Laser laser : lasers) {
            // Outer glow (cyan, transparent)
            shapeRenderer.setColor(0f, 1f, 1f, 0.4f);
            shapeRenderer.rect(laser.x - 4, laser.y - 4, laser.width + 8, laser.height + 8);
            // Inner core (white)
            shapeRenderer.setColor(1f, 1f, 1f, 1f);
            shapeRenderer.rect(laser.x, laser.y, laser.width, laser.height);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        
        // Draw spaceship (additive blending to hide black background if generated by AI)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        batch.draw(shipTexture, shipX, shipY, shipWidth, shipHeight);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); // Reset to default

        // Draw HUD
        String hudText = "Vidas: " + lives + "  Score: " + score;
        hudFont.draw(batch, hudText, Gdx.graphics.getWidth() - 250, Gdx.graphics.getHeight() - 20);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        shipTexture.dispose();
        hudFont.dispose();
        fontGenerator.dispose();
    }
}
