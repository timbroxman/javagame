package ru.samsung.gamestudio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import ru.samsung.gamestudio.GameResources;
import ru.samsung.gamestudio.MyGdxGame;
import ru.samsung.gamestudio.components.ButtonView;
import ru.samsung.gamestudio.components.ImageView;
import ru.samsung.gamestudio.components.MovingBackgroundView;
import ru.samsung.gamestudio.components.TextView;
import ru.samsung.gamestudio.GameSettings;

public class AboutScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    MovingBackgroundView backgroundView;
    TextView titleTextView;
    ImageView authorImageView;
    TextView authorNameTextView;
    TextView authorInfoTextView;
    TextView gameInfoTextView;
    ButtonView returnButton;

    public AboutScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new MovingBackgroundView(GameResources.BACKGROUND_IMG_PATH);
        titleTextView = new TextView(myGdxGame.largeWhiteFont, 256, 1100, "About Author");
        titleTextView.setCentered(true);
        
        // Изображение автора (центрировано, размер 180x180)
        float imageSize = 180;
        float imageX = (GameSettings.SCREEN_WIDTH - imageSize) / 2f;
        authorImageView = new ImageView(imageX, 850, GameResources.AUTHOR_IMAGE_PATH, imageSize, imageSize);
        
        authorNameTextView = new TextView(myGdxGame.russianWhiteFont, 0, 700, "Меня зовут Тимур");
        authorNameTextView.setCentered(true);
        authorInfoTextView = new TextView(myGdxGame.russianWhiteFont, 0, 630, "Разработчик игры");
        authorInfoTextView.setCentered(true);
        gameInfoTextView = new TextView(myGdxGame.russianWhiteFont, 0, 560, "Спасибо за игру!");
        gameInfoTextView.setCentered(true);

        returnButton = new ButtonView(
                280, 447,
                160, 70,
                myGdxGame.commonBlackFont,
                GameResources.BUTTON_SHORT_BG_IMG_PATH,
                "return");
    }

    @Override
    public void render(float delta) {

        handleInput();

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        authorImageView.draw(myGdxGame.batch);
        authorNameTextView.draw(myGdxGame.batch);
        authorInfoTextView.draw(myGdxGame.batch);
        gameInfoTextView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                if (myGdxGame.audioManager.isSoundOn)
                    myGdxGame.audioManager.buttonClickSound.play(0.3f);
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        }
    }
}
