//_____________________________________________________________
//____________ © Nader Trabelsi - December 2016 _______________
//_____________________________________________________________
//_____________________________________________________________
package com.nader_trabelsi.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.nader_trabelsi.game.Constants.ACCELERATION;
import static com.nader_trabelsi.game.Constants.ACCELERATION_OF_GRAVITY;
import static com.nader_trabelsi.game.Constants.ACCELEROMETER_SENSITIVITY;
import static com.nader_trabelsi.game.Constants.BASE_RADIUS;
import static com.nader_trabelsi.game.Constants.COILS;
import static com.nader_trabelsi.game.Constants.COLOR;
import static com.nader_trabelsi.game.Constants.DRAG;
import static com.nader_trabelsi.game.Constants.END_HOLE_RADIUS;
import static com.nader_trabelsi.game.Constants.END_HOLE_SEGMENTS;
import static com.nader_trabelsi.game.Constants.MAX_SPEED;
import static com.nader_trabelsi.game.Constants.MAX_TIME;
import static com.nader_trabelsi.game.Constants.WORLD_SIZE;


public class BallSpiralPathAndroidInput extends BallSpiralPathInput {

    Viewport viewport;
    float radius = BASE_RADIUS;
    Vector2 ballPosition = new Vector2(((WORLD_SIZE + (WORLD_SIZE / 3)) - 60) / 2, WORLD_SIZE / 2);
    Vector2 velocity = new Vector2();
    int screenWidth = WORLD_SIZE + WORLD_SIZE / 3 -60;
    int screenHeight = WORLD_SIZE;
    int xStep = screenWidth / 2 / COILS;
    int yStep = screenHeight / 2 / COILS;
    SpriteBatch spriteBatch = new SpriteBatch();
    BitmapFont font = new BitmapFont();
    GlyphLayout g = new GlyphLayout();
    int time, min;
    String sec, t;
    Sound ballCollision = Gdx.audio.newSound(Gdx.files.internal("BallCollision.mp3"));
    Sound Win = Gdx.audio.newSound(Gdx.files.internal("Win.mp3"));
    Music Time = Gdx.audio.newMusic(Gdx.files.internal("TimeIsRunningOut.mp3"));
    Sound Loss = Gdx.audio.newSound(Gdx.files.internal("Loss.mp3"));
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/FontByRedan.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    boolean alreadyPlayingT = false;
    boolean alreadyPlaying = false;
    float remainingTime = MAX_TIME;

    public BallSpiralPathAndroidInput(Viewport v) {
        viewport = v;
    }

    public void playSound(char role) {
        if (role == 'x') {
            ballCollision.play((Math.abs(velocity.x)) / MAX_SPEED);
        }
        if (role == 'y') {
            ballCollision.play((Math.abs(velocity.y)) / MAX_SPEED);
        }
        if (role == 'w') {
            Win.play(1.0f);
        }
        if (role == 'l') {
            Loss.play(1.0f);
        }
        if (role == 't') {
            Time.play();
        }

    }

    public void render(ShapeRenderer renderer) {
        viewport.apply(true);
        Gdx.gl.glClearColor(Constants.BACKGROUND_COLOR.r, Constants.BACKGROUND_COLOR.g, Constants.BACKGROUND_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setProjectionMatrix(viewport.getCamera().combined);

        if ((int) remainingTime <= 0) {
            if (!alreadyPlaying) {
                playSound('l');
                alreadyPlaying = true;
            }
            parameter.size = 47;
            parameter.color = Color.RED;
            font = generator.generateFont(parameter);
            spriteBatch.begin();
            g.setText(font, "Sorry, You Lost!");
            float wid = g.width;
            font.draw(spriteBatch, g, (viewport.getScreenWidth() - wid) / 2, viewport.getScreenHeight() / 2);

            spriteBatch.end();
        } else {

            if (ballPosition.x <= (WORLD_SIZE + (WORLD_SIZE / 3)) / 2 / COILS && ballPosition.y <= WORLD_SIZE / 2 / COILS + 2 * END_HOLE_RADIUS) {
                if (!alreadyPlaying) {
                    playSound('w');
                    alreadyPlaying = true;
                }
                parameter.size = 47;
                parameter.color = Color.GREEN;
                font = generator.generateFont(parameter);
                spriteBatch.begin();
                g.setText(font, "Congratulations, You Won!");
                float wid = g.width;
                font.draw(spriteBatch, g, (viewport.getScreenWidth() - wid) / 2, viewport.getScreenHeight() / 2);
                spriteBatch.end();
                w = true;

            } else {

                if (remainingTime <= 9.0f && !alreadyPlayingT) {
                    playSound('t');
                    alreadyPlayingT = true;
                }
                if (remainingTime < 10.0f) {
                    font.setColor(Color.RED);
                }
                time = (int) remainingTime;
                min = time / 60;
                if (time % 60 < 10) {
                    sec = "0" + time % 60;
                } else {
                    sec = "" + time % 60;
                }
                t = min + ":" + sec + " left";
                font.getData().setScale(4);
                spriteBatch.begin();
                font.draw(spriteBatch, t, viewport.getScreenWidth(), viewport.getScreenHeight() - 10);
                spriteBatch.end();
                remainingTime -= Gdx.graphics.getRawDeltaTime();

                renderer.setColor(Color.BLACK);


                for (int i = 0; i < COILS - 1; i++) {

                    renderer.set(ShapeRenderer.ShapeType.Line);

                    int xOffset = xStep * (i + 1);
                    int yOffset = yStep * (i + 1);

                    Vector2 point1 = new Vector2(xOffset - xStep, yOffset);
                    Vector2 point2 = new Vector2(screenWidth - xOffset, yOffset);
                    Vector2 point3 = new Vector2(screenWidth - xOffset, screenHeight - yOffset);
                    Vector2 point4 = new Vector2(xOffset, screenHeight - yOffset);
                    Vector2 point5 = new Vector2(xOffset, yOffset + yStep);

                    renderer.set(ShapeRenderer.ShapeType.Filled);
                    renderer.rectLine(point1.x, point1.y, point2.x, point2.y, 3);
                    renderer.rectLine(point2.x, point2.y, point3.x, point3.y, 3);
                    renderer.rectLine(point3.x, point3.y, point4.x, point4.y, 3);
                    renderer.rectLine(point4.x, point4.y, point5.x, point5.y, 3);

                }
                renderer.circle(xStep - END_HOLE_RADIUS, yStep + END_HOLE_RADIUS, END_HOLE_RADIUS, END_HOLE_SEGMENTS);
                renderer.setColor(COLOR);
                renderer.circle(ballPosition.x, ballPosition.y, radius, 100);
            }
        }

    }

    public void update(float delta) {

        float xAxis = -Gdx.input.getAccelerometerY();
        float yAxis = Gdx.input.getAccelerometerX();

        float accelerationX = -ACCELERATION * xAxis / (ACCELEROMETER_SENSITIVITY * ACCELERATION_OF_GRAVITY);
        float accelerationY = -ACCELERATION * yAxis / (ACCELEROMETER_SENSITIVITY * ACCELERATION_OF_GRAVITY);

        velocity.x += delta * accelerationX;
        velocity.y += delta * accelerationY;


        velocity.clamp(0, MAX_SPEED);

        velocity.x -= delta * DRAG * velocity.x;
        velocity.y -= delta * DRAG * velocity.y;

        ballPosition.x += delta * velocity.x;
        ballPosition.y += delta * velocity.y;


        Vector2 point2 = null, point4 = null, point5 = null;

        int k = COILS;
        boolean findCOIL = false;
        while (k > 0 && findCOIL == false) {

            int xOffset = xStep * (k - 1);
            int yOffset = yStep * (k - 1);

            point2 = new Vector2(screenWidth - xOffset, yOffset);
            point4 = new Vector2(xOffset, screenHeight - yOffset);
            point5 = new Vector2(xOffset, yOffset + yStep);


            if (ballPosition.x > point5.x && ballPosition.x < point2.x && ballPosition.y > point5.y && ballPosition.y < point4.y) {
                switch (k) {
                    case COILS:
                        findCOIL = true;
                        break;
                    case COILS - 1:
                        if (ballPosition.x < point5.x + xStep || (ballPosition.y < point5.y + yStep && ballPosition.x < point2.x - xStep) || ballPosition.y > point4.y - yStep) {
                            findCOIL = true;
                        }
                        break;
                    default:
                        if (ballPosition.x < point5.x + xStep || (ballPosition.y < point5.y + yStep && ballPosition.x < point2.x - xStep) || ballPosition.y > point4.y - yStep || (ballPosition.x > point2.x - 2 * xStep && ballPosition.x < point2.x - xStep && ballPosition.y < point4.y - 2 * yStep)) {
                            findCOIL = true;
                        }
                }
            }

            k--;
        }

        if (findCOIL) {
            switch (k) {
                case COILS - 1:
                    if (ballPosition.x + radius > point2.x) {
                        ballPosition.x = point2.x - radius;
                        velocity.x = -velocity.x;
                        playSound('x');
                    }

                    if (ballPosition.x - radius < point5.x) {
                        ballPosition.x = point5.x + radius;
                        velocity.x = -velocity.x;
                        playSound('x');
                    }

                    if (ballPosition.y + radius > point4.y) {
                        ballPosition.y = point4.y - radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }

                    break;

                case COILS - 2:
                    if (ballPosition.x + radius > point2.x) {
                        if (ballPosition.y > point4.y - yStep && ballPosition.y < point4.y) {
                            ballPosition.x = point2.x - radius;
                            velocity.x = -velocity.x;
                            playSound('x');
                        }
                    }

                    if (ballPosition.x - radius < point5.x) {
                        ballPosition.x = point5.x + radius;
                        velocity.x = -velocity.x;
                        playSound('x');

                    }

                    if (ballPosition.y - radius < point5.y) {
                        ballPosition.y = point5.y + radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }

                    if (ballPosition.y + radius > point4.y) {
                        ballPosition.y = point4.y - radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }

                    if ((ballPosition.x + radius > point5.x + xStep) && !(ballPosition.x - radius > point5.x + xStep)) {
                        if ((ballPosition.y > point5.y + yStep) && (ballPosition.y < point4.y - yStep)) {
                            ballPosition.x = point5.x + xStep - radius;
                            velocity.x = -velocity.x;
                            playSound('x');
                        }
                    }

                    if ((ballPosition.y - radius < point4.y - yStep) && !(ballPosition.y + radius < point4.y - yStep)) {
                        if ((ballPosition.x > point5.x + xStep) && (ballPosition.x < point2.x - xStep)) {
                            ballPosition.y = point4.y - yStep + radius;
                            velocity.y = -velocity.y;
                            playSound('y');
                        }
                    }

                    if (ballPosition.x + radius > point2.x - xStep && ballPosition.y > point5.y && ballPosition.y < point5.y + yStep) {
                        ballPosition.x = point2.x - xStep - radius;
                        velocity.x = -velocity.x;
                        playSound('x');
                    }
                    break;
                default:
                    if (ballPosition.x - radius < point5.x) {
                        ballPosition.x = point5.x + radius;
                        velocity.x = -velocity.x;
                        playSound('x');

                    }
                    if (ballPosition.x + radius > point2.x) {
                        if (ballPosition.y > point4.y - yStep && ballPosition.y < point4.y) {
                            ballPosition.x = point2.x - radius;
                            velocity.x = -velocity.x;
                            playSound('x');
                        }
                    }
                    if (ballPosition.y - radius < point5.y) {
                        ballPosition.y = point5.y + radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }
                    if (ballPosition.y + radius > point4.y) {
                        ballPosition.y = point4.y - radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }

                    if (ballPosition.x + radius > point2.x - xStep && ballPosition.y > point5.y && ballPosition.y < point4.y - 2 * yStep) {
                        ballPosition.x = point2.x - xStep - radius;
                        velocity.x = -velocity.x;
                        playSound('x');
                    }

                    if ((ballPosition.y - radius < point4.y - yStep) && !(ballPosition.y + radius < point4.y - yStep)) {
                        if ((ballPosition.x > point5.x + xStep) && (ballPosition.x < point2.x - xStep)) {
                            ballPosition.y = point4.y - yStep + radius;
                            velocity.y = -velocity.y;
                            playSound('y');
                        }
                    }

                    if ((ballPosition.x + radius > point5.x + xStep) && !(ballPosition.x - radius > point5.x + xStep)) {
                        if ((ballPosition.y > point5.y + yStep) && (ballPosition.y < point4.y - yStep)) {
                            ballPosition.x = point5.x + xStep - radius;
                            velocity.x = -velocity.x;
                            playSound('x');
                        }
                    }

                    if (ballPosition.y + radius > point5.y + yStep && !(ballPosition.y - radius > point5.y + yStep) && ballPosition.x > point5.x + xStep && ballPosition.x < point2.x - 2 * xStep) {
                        ballPosition.y = point5.y + yStep - radius;
                        velocity.y = -velocity.y;
                        playSound('y');
                    }

                    if (ballPosition.x - radius < point2.x - 2 * xStep && !(ballPosition.x + radius < point2.x - 2 * xStep) && ballPosition.y > point5.y + yStep && ballPosition.y < point4.y - yStep) {
                        ballPosition.x = point2.x - 2 * xStep + radius;
                        velocity.x = -velocity.x;
                        playSound('x');
                    }

            }
        }
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
