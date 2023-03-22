package com.mygdx.shapewars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.shapewars.model.ShapeWarsModel;
import com.mygdx.shapewars.controller.ShapeWarsController;
import com.mygdx.shapewars.view.UIBuilder;

public class MainMenuView implements Screen {
    private final Stage stage;
    private final ShapeWarsModel model;
    private final UIBuilder uiBuilder;
    private ShapeWarsController controller;
    private TextButton startButton;
    private TextButton hostButton;
    private TextButton joinButton;

    public MainMenuView(ShapeWarsModel model) {
        this.model = model;
        this.stage = new Stage();
        this.uiBuilder = new UIBuilder(this.stage);

        Gdx.input.setInputProcessor(stage);

        buildUI();
    }

    public void setController(ShapeWarsController controller) {
        this.controller = controller;
    }
    @Override
    public void show() {
        System.out.println("Main menu view showing");
        Gdx.input.setInputProcessor(stage);
        render(0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_STENCIL_BACK_VALUE_MASK);

        model.batch.begin();
        model.batch.end();

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
        float allButtonsWidth = 750f;
        float allButtonsHeight = 200f;
        float startButtonXPos = Gdx.graphics.getWidth() / 2 - allButtonsWidth / 2;
        float startButtonYPos = Gdx.graphics.getHeight() / 2 - allButtonsHeight / 2 + 200;
        float hostButtonXPos = Gdx.graphics.getWidth() / 2 - allButtonsWidth / 2;
        float hostButtonYPos = Gdx.graphics.getHeight() / 2 - allButtonsHeight / 2 - 50f;
        float joinButtonXPos = Gdx.graphics.getWidth() / 2 - allButtonsWidth / 2;
        float joinButtonYPos = Gdx.graphics.getHeight() / 2 - allButtonsHeight / 2 - 300;

        startButton = uiBuilder.buildButton("Start Game", allButtonsWidth, allButtonsHeight, startButtonXPos, startButtonYPos);
        hostButton = uiBuilder.buildButton("Join", allButtonsWidth, allButtonsHeight, hostButtonXPos, hostButtonYPos);
        joinButton = uiBuilder.buildButton("Host", allButtonsWidth, allButtonsHeight, joinButtonXPos, joinButtonYPos);

        addActionsToUI();
    }

    private void addActionsToUI()
    {
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                try {
                    controller.setScreen(controller.getShapeWarsView());
                } catch (NullPointerException nullPointerException) {
                    System.out.println("No Controller found");
                }
            }
        });

        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                try {
                    controller.setScreen(controller.getHostView());
                } catch (NullPointerException nullPointerException) {
                    System.out.println("No Controller found");
                }
            }
        }); 

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to JoinView (has to be decided and implemented)
            }
        });
    }
}
