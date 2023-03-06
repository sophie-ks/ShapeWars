package com.mygdx.shapewars.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.shapewars.model.ShapeWarsModel;

public class ShapeWarsView implements Screen {
    private final Stage stage;
    private final ShapeWarsModel model;

    public ShapeWarsView(ShapeWarsModel model) {
        this.model = model;
        this.stage = new Stage(); // todo check if we need to change that
    }

    @Override
    public void show() {
        System.out.println("Game view showing");
        Gdx.input.setInputProcessor(stage);
        render(0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        model.batch.begin();
        model.tankSprite.draw(model.batch);
        model.batch.end();
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }
}
