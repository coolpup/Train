package org.train.entity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class CenteredLayout implements LayoutInterface {

    private List<Rectangle> rectangles = new ArrayList<Rectangle>();
    private Container container;
    private GameContainer gameContainer;

    public CenteredLayout(GameContainer gameContainer, Container container) {
        this.container = container;
        this.gameContainer = gameContainer;
        this.recalculateRectangles();
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public List<Rectangle> getRectangles() {
        return this.rectangles;
    }

    private void calculateRectangles() {
        this.rectangles = new ArrayList<Rectangle>(this.container.getChildren().size());
        int menuHeight = this.getMenuHeight();
        int maxWidth = this.getMenuItemMaxWidth();

        int lastOffsetY = 0;
        for (Child item : this.container.getChildren()) {
            int width = item.getWidth();
            int height = item.getHeight();
            int x = this.gameContainer.getWidth() / 2 - width / 2;
            int y = this.gameContainer.getHeight() / 2 - menuHeight / 2 + lastOffsetY;
            this.rectangles
                    .add(new Rectangle(x - this.container.getMarginRight(), y, width, height));
            lastOffsetY += height;
        }

        this.container.setPosition(new Point(this.gameContainer.getWidth() / 2 - maxWidth / 2,
                this.gameContainer.getHeight() / 2 - menuHeight / 2));
        this.container.setWidth(maxWidth);
        this.container.setHeight(menuHeight);
    }

    private int getMenuHeight() {
        int menuHeight = 0;
        for (Child item : this.container.getChildren()) {
            menuHeight += item.getHeight();
        }
        return menuHeight;
    }

    private int getMenuItemMaxWidth() {
        int maxWidth = 0;
        for (Child item : this.container.getChildren()) {
            int width = item.getWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        return maxWidth;
    }

    private void applyItemMargin() {
        int marginHeight = 0;
        int marginWidth = 0;
        int counter = 0;
        int offsetY = 0;
        int lastMarginBottom = 0;
        for (Child item : this.container.getChildren()) {
            Rectangle addition = new Rectangle(item.getMarginLeft(), item.getMarginTop()
                    + lastMarginBottom, 0, 0);
            Rectangle r = this.rectangles.get(counter);
            r.setX(r.getX() + addition.getX());
            r.setY(r.getY() + addition.getY() + offsetY);
            marginHeight += item.getMarginTop();
            marginHeight += item.getMarginBottom();
            int itemMarginWidth = item.getMarginLeft() + item.getMarginRight();
            marginWidth = (marginWidth < itemMarginWidth) ? itemMarginWidth : marginWidth;
            lastMarginBottom = item.getMarginBottom();
            offsetY += addition.getY();
            counter++;
        }
        for (Rectangle r : this.rectangles) {
            r.setX(r.getX() - marginWidth / 2);
            r.setY(r.getY() - marginHeight / 2);
        }
        this.container.setWidth(this.container.getWidth() + marginWidth);
        this.container.setHeight(this.container.getHeight() + marginHeight);
        this.container.setPosition(new Point(this.container.getPosition().x - marginWidth / 2,
                this.container.getPosition().y - marginHeight / 2));
    }

    private void placeMenuItems() {
        this.calculateRectangles();
        this.applyItemMargin();
    }

    @Override
    public void render(Graphics g) {
        for (Child item : this.container.getChildren()) {
            item.render(g);
        }
    }

    @Override
    public void recalculateRectangles() {
        this.placeMenuItems();
        for (int i = 0; i < this.container.getChildren().size(); i++) {
            this.container.getChildren().get(i).setRectangle(this.rectangles.get(i));
        }
    }
}
