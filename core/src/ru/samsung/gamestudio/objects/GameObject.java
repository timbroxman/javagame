package ru.samsung.gamestudio.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static ru.samsung.gamestudio.GameSettings.SCALE;

public class GameObject {

    public short cBits;

    public int width;
    public int height;

    public Body body;
    protected Texture texture;

    GameObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;

        texture = new Texture(texturePath);
        body = createBody(x, y, world, (short) 0xFFFF); // По умолчанию сталкивается со всеми
    }

    GameObject(String texturePath, int x, int y, int width, int height, short cBits, short maskBits, World world) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;

        texture = new Texture(texturePath);
        body = createBody(x, y, world, maskBits);
    }

    protected float rotation = 0f; // Вращение для анимации
    
    public void draw(SpriteBatch batch) {
        draw(batch, 0f);
    }
    
    public void draw(SpriteBatch batch, float rotation) {
        float originX = width / 2f;
        float originY = height / 2f;
        batch.draw(texture,
                getX() - originX,
                getY() - originY,
                originX, originY,
                width, height,
                1f, 1f,
                rotation,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
    }

    public void hit() {
        // all physics objects could be hit
    }

    public int getX() {
        return (int) (body.getPosition().x / SCALE);
    }

    public int getY() {
        return (int) (body.getPosition().y / SCALE);
    }

    public void setX(int x) {
        body.setTransform(x * SCALE, body.getPosition().y, 0);
    }

    public void setY(int y) {
        body.setTransform(body.getPosition().x, y * SCALE, 0);
    }

    private Body createBody(float x, float y, World world, short maskBits) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.fixedRotation = true;
        Body body = world.createBody(def);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(Math.max(width, height) * SCALE / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = cBits;
        fixtureDef.filter.maskBits = maskBits; // Устанавливаем маску столкновений

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        circleShape.dispose();

        body.setTransform(x * SCALE, y * SCALE, 0);
        return body;
    }

}
