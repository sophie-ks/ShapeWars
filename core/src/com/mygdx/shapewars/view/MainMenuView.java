package com.mygdx.shapewars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.shapewars.config.Role;
import com.mygdx.shapewars.controller.ShapeWarsController;

import java.net.UnknownHostException;

public class MainMenuView implements Screen {
    private final Stage stage;
    private final UIBuilder uiBuilder;
    private ShapeWarsController controller;
    private ImageButton startButton;
    private ImageButton hostButton;
    private ImageButton joinButton;
    private Sprite backgroundSprite;

    public MainMenuView(ShapeWarsController controller) {
        this.stage = new Stage();
        this.controller = controller;
        this.uiBuilder = new UIBuilder(this.stage);
        buildUI();
        Texture background = new Texture(Gdx.files.internal("mainMenu/background.png"));
        backgroundSprite = new Sprite(background);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        // make menu resizable
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        stage.setViewport(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
        Gdx.input.setInputProcessor(stage);
        render(0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.6f, 0.8f, 1f, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage.getViewport().apply();

        controller.gameModel.batch.begin();
        backgroundSprite.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        backgroundSprite.setPosition((stage.getViewport().getWorldWidth() - backgroundSprite.getWidth())/2, (stage.getViewport().getWorldHeight()- backgroundSprite.getHeight()) / 2);
        backgroundSprite.draw(controller.gameModel.batch);
        controller.gameModel.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void buildUI() {
        float allButtonsWidth = 768f;
        float allButtonsHeight = 192f;
        float startButtonXPos = Gdx.graphics.getWidth() / 2f - allButtonsWidth / 2;
        float startButtonYPos = Gdx.graphics.getHeight() / 2f - allButtonsHeight / 2 + 200;
        float hostButtonXPos = Gdx.graphics.getWidth() / 2f - allButtonsWidth / 2;
        float hostButtonYPos = Gdx.graphics.getHeight() / 2f - allButtonsHeight / 2 - 50f;
        float joinButtonXPos = Gdx.graphics.getWidth() / 2f - allButtonsWidth / 2;
        float joinButtonYPos = Gdx.graphics.getHeight() / 2f - allButtonsHeight / 2 - 300;

        // TODO delete unnecessary comments
        //startButton = uiBuilder.buildButton("Start Game", allButtonsWidth, allButtonsHeight, startButtonXPos,startButtonYPos);
        startButton = uiBuilder.buildImageButton(new Texture("mainMenu/startButton.png"), allButtonsWidth, allButtonsHeight, startButtonXPos, startButtonYPos);
        //joinButton = uiBuilder.buildImageButton(new Texture("mainMenu/joinButton.png"), allButtonsWidth, allButtonsHeight, joinButtonXPos, joinButtonYPos);

        joinButton = uiBuilder.buildImageButton(new Texture("mainMenu/JoinButton.png"), allButtonsWidth, allButtonsHeight, joinButtonXPos, joinButtonYPos);
        //hostButton = uiBuilder.buildButton("Host", allButtonsWidth, allButtonsHeight, joinButtonXPos, joinButtonYPos);

        hostButton = uiBuilder.buildImageButton(new Texture("mainMenu/hostButton.png"), allButtonsWidth, allButtonsHeight, hostButtonXPos, hostButtonYPos);


        addActionsToUI();
    }

    private void addActionsToUI() {
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    controller.generateShapeWarsModel(Role.Server, "");
                    controller.shapeWarsModel.generateEntities();
                    controller.setScreen(new ShapeWarsView(controller));
                } catch (NullPointerException nullPointerException) {
                    System.out.println("No Controller found");
                }
            }
        });

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    controller.setScreen(new ClientView(controller));
                } catch (NullPointerException nullPointerException) {
                    System.out.println("No Controller found");
                }
            }
        });

        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    controller.generateShapeWarsModel(Role.Server, "");
                    controller.setScreen(new HostView(controller));
                } catch (NullPointerException | UnknownHostException nullPointerException) {
                    System.out.println("No Controller found");
                }
            }
        });
    }
}
