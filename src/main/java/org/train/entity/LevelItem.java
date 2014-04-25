package org.train.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

public class LevelItem extends Entity {
    protected Image image;
    private Level.Item type;

    public LevelItem(Image image, Level.Item type) {
        this.image = image;
        this.type = type;
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) {
        super.render(container, game, g);

        this.image.draw(this.getPosition().x, this.getPosition().y, this.getScale());
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Level.Item getType() {
        return type;
    }
}