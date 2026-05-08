package com.spacemax;

import com.badlogic.gdx.Gdx;
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

import com.spacemax.entities.Laser;
import com.spacemax.entities.Meteor;
import com.spacemax.entities.Ship;

public class GameScreen implements Screen {

    private final SpaceMaxGame game;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    
    // Assets
    private Texture shipTexture;
    private Texture meteorTexture;
    private BitmapFont hudFont;
    private FreeTypeFontGenerator fontGenerator;

    // Game state
    private int score = 0;
    private int lives = 3;

    // Stars background
    private float[] starX;
    private float[] starY;
    private final int NUM_STARS = 100;

    // Entities
    private Ship ship;
    private Array<Laser> lasers;
    private Array<Meteor> meteors;
    private float meteorSpawnTimer = 0;
    private float meteorSpawnInterval = 1.0f; // spawn every 1 second

    public GameScreen(SpaceMaxGame game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Load textures
        shipTexture = new Texture(Gdx.files.internal("spaceship.png"));
        meteorTexture = new Texture(Gdx.files.internal("meteor.png"));

        // Initialize entities
        ship = new Ship();
        lasers = new Array<>();
        meteors = new Array<>();

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
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Update Ship
        ship.update(delta);
        
        Laser newLaser = ship.shoot();
        if (newLaser != null) {
            lasers.add(newLaser);
        }

        // Update stars falling
        for (int i = 0; i < NUM_STARS; i++) {
            starY[i] -= 50 * delta; // slow fall
            if (starY[i] < 0) {
                starY[i] = Gdx.graphics.getHeight();
                starX[i] = MathUtils.random(0, Gdx.graphics.getWidth());
            }
        }

        // Spawn meteors
        meteorSpawnTimer += delta;
        if (meteorSpawnTimer >= meteorSpawnInterval) {
            float spawnX = MathUtils.random(0, Gdx.graphics.getWidth() - 60);
            meteors.add(new Meteor(spawnX, Gdx.graphics.getHeight()));
            meteorSpawnTimer = 0;
        }

        // Update meteors and check collisions
        for (int i = meteors.size - 1; i >= 0; i--) {
            Meteor meteor = meteors.get(i);
            meteor.update(delta);
            
            if (!meteor.isActive()) {
                meteors.removeIndex(i);
                continue;
            }

            // Collision with ship
            if (meteor.getBounds().overlaps(ship.getBounds())) {
                meteor.setActive(false);
                lives--;
                System.out.println("Colisao com meteoro! Vidas: " + lives);
                if (lives <= 0) {
                    game.setScreen(new MainMenuScreen(game));
                    return; // Stop rendering this frame
                }
                meteors.removeIndex(i);
                continue;
            }

            // Collision with lasers
            for (int j = lasers.size - 1; j >= 0; j--) {
                Laser laser = lasers.get(j);
                if (laser.isActive() && meteor.getBounds().overlaps(laser.getBounds())) {
                    meteor.setActive(false);
                    laser.setActive(false);
                    score++;
                    break;
                }
            }

            if (!meteor.isActive()) {
                meteors.removeIndex(i);
            }
        }

        // Update lasers
        for (int i = lasers.size - 1; i >= 0; i--) {
            Laser laser = lasers.get(i);
            laser.update(delta);
            if (!laser.isActive()) {
                lasers.removeIndex(i);
            }
        }

        // --- DRAWING ---
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
            laser.draw(shapeRenderer);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        
        // Draw meteors
        for (Meteor meteor : meteors) {
            meteor.draw(batch, meteorTexture);
        }

        // Draw spaceship
        ship.draw(batch, shipTexture);

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
        meteorTexture.dispose();
        hudFont.dispose();
        fontGenerator.dispose();
    }
}
