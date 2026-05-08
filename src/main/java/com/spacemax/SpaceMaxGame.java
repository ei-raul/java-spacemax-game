package com.spacemax;

import com.badlogic.gdx.Game;

public class SpaceMaxGame extends Game {

    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }
}
