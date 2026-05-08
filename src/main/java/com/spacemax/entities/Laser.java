package com.spacemax.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Laser {
    private float x;
    private float y;
    private float width = 6;
    private float height = 30;
    private float speed = 800f;
    private boolean active = true;

    public Laser(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float delta) {
        y += speed * delta;
        if (y > Gdx.graphics.getHeight()) {
            active = false;
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        // Outer glow (cyan, transparent)
        shapeRenderer.setColor(0f, 1f, 1f, 0.4f);
        shapeRenderer.rect(x - 4, y - 4, width + 8, height + 8);
        
        // Inner core (white)
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
