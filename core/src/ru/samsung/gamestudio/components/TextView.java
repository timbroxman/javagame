package ru.samsung.gamestudio.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import ru.samsung.gamestudio.GameSettings;

public class TextView extends View{

    protected BitmapFont font;
    protected String text;
    protected boolean centered = false;

    public TextView(BitmapFont font, float x, float y) {
        super(x, y);
        this.font = font;
    }

    public TextView(BitmapFont font, float x, float y, String text) {
        this(font, x, y);
        this.text = text;

        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }

    public void setText(String text) {
        this.text = text;
        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    @Override
    public void draw(SpriteBatch batch) {
        float drawX = x;
        if (centered) {
            drawX = (GameSettings.SCREEN_WIDTH - width) / 2f;
        }
        font.draw(batch, text, drawX, y + height);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

}