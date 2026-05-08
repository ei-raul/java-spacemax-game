package com.spacemax;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private final SpaceMaxGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private FreeTypeFontGenerator fontGenerator;

    public MainMenuScreen(SpaceMaxGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load background
        backgroundTexture = new Texture(Gdx.files.internal("background_menu.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setScaling(Scaling.fill);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Generate fonts
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        
        // Title Font
        parameter.size = 72;
        parameter.color = Color.CYAN;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = 3;
        parameter.shadowOffsetY = 3;
        parameter.borderColor = Color.BLUE;
        parameter.borderWidth = 2;
        titleFont = fontGenerator.generateFont(parameter);

        // Button Font
        parameter.size = 28;
        parameter.color = Color.WHITE;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.borderWidth = 0;
        buttonFont = fontGenerator.generateFont(parameter);

        // UI Table
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Title Label
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = titleFont;
        Label titleLabel = new Label("SpaceMax", titleStyle);
        titleLabel.setAlignment(Align.center);

        // Button Styles
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.up = createColoredDrawable(new Color(0.2f, 0.2f, 0.6f, 0.8f));
        buttonStyle.down = createColoredDrawable(new Color(0.1f, 0.1f, 0.4f, 0.9f));
        buttonStyle.over = createColoredDrawable(new Color(0.3f, 0.3f, 0.8f, 0.9f));
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.LIGHT_GRAY;

        // Buttons
        TextButton newGameButton = new TextButton("Novo jogo", buttonStyle);
        TextButton continueButton = new TextButton("Continuar jogo salvo", buttonStyle);
        TextButton rankingButton = new TextButton("Ranking", buttonStyle);

        // Layout
        table.add(titleLabel).padBottom(80).row();
        table.add(newGameButton).width(300).height(60).padBottom(20).row();
        table.add(continueButton).width(300).height(60).padBottom(20).row();
        table.add(rankingButton).width(300).height(60).padBottom(20).row();

        // Listeners (For now just a log)
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Novo Jogo clicado!");
                // game.setScreen(new GameScreen(game));
            }
        });
    }

    private TextureRegionDrawable createColoredDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        titleFont.dispose();
        buttonFont.dispose();
        fontGenerator.dispose();
    }
}
