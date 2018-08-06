//_____________________________________________________________
//____________ © Nader Trabelsi - December 2016 _______________
//_____________________________________________________________
//_____________________________________________________________


package com.nader_trabelsi.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.nader_trabelsi.game.Constants.WORLD_SIZE;


public class BallSpiralPathScreen extends ScreenAdapter {

    ShapeRenderer renderer;
    Viewport viewport;
    BallSpiralPathInput input;

    @Override
    public void show() {
        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        viewport = new FitViewport(WORLD_SIZE + WORLD_SIZE / 3, WORLD_SIZE);
        switch (Gdx.app.getType()) {
            case Android:
                input = new BallSpiralPathAndroidInput(viewport);
                Gdx.input.setInputProcessor(input);
                break;
            case Desktop:
                input = new BallSpiralPathDesktopInput(viewport);
                Gdx.input.setInputProcessor(input);
                break;
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }


    @Override
    public void render(float delta) {
        viewport.apply();

        renderer.begin();
        input.render(renderer);
        renderer.end();
        if (!input.w) {
            input.update(delta);
        }

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


}