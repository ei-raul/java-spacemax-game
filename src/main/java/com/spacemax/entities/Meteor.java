package com.spacemax.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Meteor {
    private float x;
    private float y;
    private float width = 60;
    private float height = 60;
    private float speed;
    private boolean active = true;

    public Meteor(float x, float y) {
        this.x = x;
        this.y = y;
        this.speed = MathUtils.random(150f, 350f);
    }

    public void update(float delta) {
        y -= speed * delta;
        if (y + height < 0) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch, Texture texture) {
        batch.draw(texture, x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x + 5, y + 5, width - 10, height - 10); // make hitbox slightly smaller
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
