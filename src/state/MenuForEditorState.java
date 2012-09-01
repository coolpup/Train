package state;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import other.LevelController;
import other.LevelPackage;
import other.Translator;
import app.Game;

public class MenuForEditorState extends BasicGameState {

    private boolean isMouseOverPackageArrowUp, isMouseOverPackageArrowDown,
            isMouseOverLevelArrowUp, isMouseOverLevelArrowDown, isMouseOverReturn,
            isLevelArrowDownDisabled, isCreatingNewPackage, isRenamingPackage, isDeletingPackage,
            isCreatingNewLevel, isRenamingLevel, isResizingLevel, isDeletingLevel,
            isMouseOverPackageNames[], isMouseOverLevelNames[], isMouseOverPackageActions[],
            isPackageActionsDisabled[], isMouseOverLevelActions[], isLevelActionsDisabled[];
    private int stateId, width, height, packageIndex, levelIndex, packageBaseIndex, levelBaseIndex,
            inputState;
    private UnicodeFont ubuntuSmall, ubuntuMedium, ubuntuLarge;

    private Input input;
    private Point mouse;
    private Translator translator;
    private Rectangle packageArrowUpRectangle, packageArrowDownRectangle, levelArrowUpRectangle,
            levelArrowDownRectangle, returnRectangle, packageNameRectangles[],
            levelNameRectangles[], packageActionRectangles[], levelActionRectangles[];
    private ArrayList<LevelPackage> levelPackages;
    private Image arrowUp, arrowDown, arrowMouseOverUp, arrowMouseOverDown, arrowDisabledUp,
            arrowDisabledDown;
    private String packageActions[], levelActions[], newLevelName;
    private TextField textField;
    private Dimension levelSize;
    private LevelController levelController;

    public MenuForEditorState(int stateId) {
        this.stateId = stateId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        translator = Translator.getInstance();
        width = container.getWidth();
        height = container.getHeight();
        String fontPath = Game.CONTENT_PATH + "fonts/ubuntu.ttf";

        ubuntuSmall = new UnicodeFont(fontPath, width / 36, false, false);
        ubuntuSmall.addGlyphs(32, 800);
        ubuntuSmall.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        ubuntuSmall.loadGlyphs();

        ubuntuMedium = new UnicodeFont(fontPath, width / 20, false, false);
        ubuntuMedium.addGlyphs(32, 800);
        ubuntuMedium.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        ubuntuMedium.loadGlyphs();

        ubuntuLarge = new UnicodeFont(fontPath, width / 16, false, false);
        ubuntuLarge.addGlyphs(32, 800);
        ubuntuLarge.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        ubuntuLarge.loadGlyphs();

        levelController = new LevelController();
        levelPackages = levelController.getLevels();

        arrowUp = new Image(Game.CONTENT_PATH + "graphics/arrow.png").getScaledCopy(width / 2000f);
        arrowDown = arrowUp.getFlippedCopy(false, true);
        arrowDisabledUp = new Image(Game.CONTENT_PATH + "graphics/arrowDisabled.png")
                .getScaledCopy(width / 2000f);
        arrowDisabledDown = arrowDisabledUp.getFlippedCopy(false, true);
        arrowMouseOverUp = new Image(Game.CONTENT_PATH + "graphics/arrowMouseOver.png")
                .getScaledCopy(width / 2000f);
        arrowMouseOverDown = arrowMouseOverUp.getFlippedCopy(false, true);

        packageArrowUpRectangle = new Rectangle();
        packageArrowUpRectangle.width = arrowUp.getWidth();
        packageArrowUpRectangle.height = arrowUp.getHeight();
        packageArrowUpRectangle.x = width / 12;
        packageArrowUpRectangle.y = height * 3 / 12;

        packageArrowDownRectangle = new Rectangle();
        packageArrowDownRectangle.width = arrowDown.getWidth();
        packageArrowDownRectangle.height = arrowDown.getHeight();
        packageArrowDownRectangle.x = width / 12;
        packageArrowDownRectangle.y = height * 9 / 12;

        levelArrowUpRectangle = new Rectangle();
        levelArrowUpRectangle.width = arrowUp.getWidth();
        levelArrowUpRectangle.height = arrowUp.getHeight();
        levelArrowUpRectangle.x = width * 7 / 12;
        levelArrowUpRectangle.y = height * 3 / 12;

        levelArrowDownRectangle = new Rectangle();
        levelArrowDownRectangle.width = arrowDown.getWidth();
        levelArrowDownRectangle.height = arrowDown.getHeight();
        levelArrowDownRectangle.x = width * 7 / 12;
        levelArrowDownRectangle.y = height * 9 / 12;

        returnRectangle = new Rectangle();
        returnRectangle.width = ubuntuSmall.getWidth(translator.translate("return"));
        returnRectangle.height = ubuntuSmall.getHeight(translator.translate("return"));
        returnRectangle.x = width / 100;
        returnRectangle.y = (int) (height - returnRectangle.height * 1.1f);

        packageActions = new String[5];
        packageActions[0] = translator.translate("Create");
        packageActions[1] = translator.translate("Move Up");
        packageActions[2] = translator.translate("Move Down");
        packageActions[3] = translator.translate("Rename");
        packageActions[4] = translator.translate("Delete");

        levelActions = new String[7];
        levelActions[0] = translator.translate("Create");
        levelActions[1] = translator.translate("Edit");
        levelActions[2] = translator.translate("Move Up");
        levelActions[3] = translator.translate("Move Down");
        levelActions[4] = translator.translate("Rename");
        levelActions[5] = translator.translate("Resize");
        levelActions[6] = translator.translate("Delete");

        packageIndex = -1;
        packageBaseIndex = 0;
        levelIndex = -1;
        levelBaseIndex = 0;

        setPackageNameRectangles();
        isMouseOverPackageNames = new boolean[packageNameRectangles.length];
        setPackageActionRectangles();
        isMouseOverPackageActions = new boolean[packageActionRectangles.length];
        isPackageActionsDisabled = new boolean[packageActionRectangles.length];

        isMouseOverLevelNames = new boolean[5];
        setLevelActionRectangles();
        isMouseOverLevelActions = new boolean[levelActionRectangles.length];
        isLevelActionsDisabled = new boolean[levelActionRectangles.length];

        isCreatingNewPackage = false;
        isRenamingPackage = false;

        textField = new TextField(container, ubuntuSmall, width / 3, height - width / 26,
                width / 3, width / 30);
        textField.setBackgroundColor(Color.darkGray);
        textField.setMaxLength(15);
        textField.setTextColor(Color.white);
        textField.setText("");
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        String text = translator.translate("Packages");
        String text2 = translator.translate("Levels");
        g.setFont(ubuntuMedium);
        g.setColor(Color.gray);
        g.drawString(text, width / 500, height / 6 - ubuntuMedium.getHeight(text) / 2 + height
                / 750);
        g.drawString(text2, width / 2 + width / 500, height / 6 - ubuntuMedium.getHeight(text) / 2
                + height / 750);
        g.setColor(Color.white);
        g.drawString(text, 0, height / 6 - ubuntuMedium.getHeight(text) / 2);
        g.drawString(text2, width / 2, height / 6 - ubuntuMedium.getHeight(text) / 2);
        g.setFont(ubuntuSmall);
        g.setColor((isMouseOverReturn) ? Color.red : Color.white);
        g.drawString(translator.translate("return"), width / 100, height - returnRectangle.height
                * 1.1f);
        for (int i = 0; i < levelPackages.size() && i < 5; i++) {
            g.setColor((packageIndex == packageBaseIndex + i) ? Color.blue
                    : ((isMouseOverPackageNames[i]) ? Color.red : Color.white));
            g.drawString(levelPackages.get(packageBaseIndex + i).getName(), width / 100, height
                    * (4 + i) / 13);
        }
        if (packageIndex >= 0) {
            LevelPackage levelPackage = levelPackages.get(packageIndex);
            ArrayList<String> levelNames = levelPackage.getLevelNames();
            for (int i = 0; i < levelNames.size() && i < 5; i++) {
                g.setColor((levelIndex == levelBaseIndex + i) ? Color.blue
                        : ((isMouseOverLevelNames[i]) ? Color.red : Color.white));
                g.drawString(levelNames.get(levelBaseIndex + i), width / 2 + width / 100, height
                        * (4 + i) / 13);
            }
        }
        g.setColor(Color.red);
        text = String.format("%4$s %1$d - %2$d %5$s %3$d", packageBaseIndex + 1,
                packageBaseIndex + 5, levelPackages.size(), translator.translate("showing"),
                translator.translate("of"));
        g.drawString(text, width / 200, height * 9 / 11);
        text = String.format("%4$s %1$d - %2$d %5$s %3$d", levelBaseIndex + 1, levelBaseIndex + 5,
                (packageIndex >= 0) ? levelPackages.get(packageIndex).getLevelNames().size() : 0,
                translator.translate("showing"), translator.translate("of"));
        g.drawString(text, width / 2 + width / 200, height * 9 / 11);
        if (isDeletingPackage || isDeletingLevel) {
            drawString(g, ubuntuSmall,
                    translator.translate("Really") + " " + translator.translate("delete") + "? "
                            + translator.translate("Yes") + "(Enter)/" + translator.translate("No")
                            + "(Escape)", width / 2, height - height / 24);
        }
        for (int i = 0; i < packageActions.length; i++) {
            g.setColor((isPackageActionsDisabled[i]) ? Color.darkGray
                    : ((isMouseOverPackageActions[i]) ? Color.blue : Color.red));
            g.drawString(packageActions[i],
                    width / 3 - ubuntuSmall.getWidth(packageActions[i]) / 2, height * (4 + i) / 13);
        }
        for (int i = 0; i < levelActions.length; i++) {
            g.setColor((isLevelActionsDisabled[i]) ? Color.darkGray
                    : ((isMouseOverLevelActions[i]) ? Color.blue : Color.red));
            g.drawString(levelActions[i], width - width / 9 - ubuntuSmall.getWidth(levelActions[i])
                    / 2, height * (3 + i) / 13);
        }

        Image arrowImageToBeDrawn = (packageBaseIndex == 0) ? arrowDisabledUp
                : ((isMouseOverPackageArrowUp) ? arrowMouseOverUp : arrowUp);
        drawImage(arrowImageToBeDrawn, width / 12, height * 3 / 12);
        arrowImageToBeDrawn = (packageBaseIndex + 5 == levelPackages.size()) ? arrowDisabledDown
                : ((isMouseOverPackageArrowDown) ? arrowMouseOverDown : arrowDown);
        drawImage(arrowImageToBeDrawn, width / 12, height * 9 / 12);

        arrowImageToBeDrawn = (levelBaseIndex == 0) ? arrowDisabledUp
                : ((isMouseOverLevelArrowUp) ? arrowMouseOverUp : arrowUp);
        drawImage(arrowImageToBeDrawn, width * 7 / 12, height * 3 / 12);

        arrowImageToBeDrawn = (isLevelArrowDownDisabled) ? arrowDisabledDown
                : ((isMouseOverLevelArrowDown) ? arrowMouseOverDown : arrowDown);
        drawImage(arrowImageToBeDrawn, width * 7 / 12, height * 9 / 12);

        g.setColor(Color.white);
        if (isCreatingNewPackage || isRenamingPackage || isCreatingNewLevel || isRenamingLevel
                || isResizingLevel) {
            textField.render(container, g);
            textField.setFocus(true);
            if (isCreatingNewPackage || isRenamingPackage
                    || (isCreatingNewLevel && inputState == 0) || isRenamingLevel) {
                text = translator.translate("Name") + ":";
            }
            if ((isCreatingNewLevel && inputState == 1) || (isResizingLevel && inputState == 0)) {
                text = translator.translate("Width") + ":";
            }
            if ((isCreatingNewLevel && inputState == 2) || (isResizingLevel && inputState == 1)) {
                text = translator.translate("Height") + ":";
            }
            g.drawString(text, width / 3 - ubuntuSmall.getWidth(text) * 1.1f, height - width / 26);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        input = container.getInput();
        mouse = new Point(input.getMouseX(), input.getMouseY());

        isPackageActionsDisabled[0] = levelPackages.size() >= 1000;
        isPackageActionsDisabled[1] = packageIndex < 4;
        isPackageActionsDisabled[2] = packageIndex < 3 || packageIndex >= levelPackages.size() - 1;
        isPackageActionsDisabled[3] = packageIndex < 3;
        isPackageActionsDisabled[4] = packageIndex < 3;

        isLevelActionsDisabled[0] = packageIndex < 0
                || levelPackages.get(packageIndex).getLevelNames().size() >= 100;
        isLevelActionsDisabled[1] = packageIndex < 0 || levelIndex < 0;
        isLevelActionsDisabled[2] = packageIndex < 0 || levelIndex < 1;
        isLevelActionsDisabled[3] = packageIndex < 0 || levelIndex < 0
                || levelIndex >= levelPackages.get(packageIndex).getLevelNames().size() - 1;
        isLevelActionsDisabled[4] = packageIndex < 0 || levelIndex < 0;
        isLevelActionsDisabled[5] = packageIndex < 0 || levelIndex < 0;
        isLevelActionsDisabled[6] = packageIndex < 0 || levelIndex < 0;

        isMouseOverPackageArrowUp = packageArrowUpRectangle.contains(mouse);
        isMouseOverPackageArrowDown = packageArrowDownRectangle.contains(mouse);
        isMouseOverLevelArrowUp = levelArrowUpRectangle.contains(mouse);
        isMouseOverLevelArrowDown = levelArrowDownRectangle.contains(mouse);

        isMouseOverReturn = returnRectangle.contains(mouse);

        for (int i = 0; i < packageNameRectangles.length && i < levelPackages.size(); i++) {
            isMouseOverPackageNames[i] = packageNameRectangles[i].contains(mouse);
        }
        for (int i = 0; i < packageActionRectangles.length; i++) {
            isMouseOverPackageActions[i] = packageActionRectangles[i].contains(mouse);
        }

        if (packageIndex >= 0) {
            for (int i = 0; i < levelNameRectangles.length
                    && i < levelPackages.get(packageIndex).getLevelNames().size(); i++) {
                isMouseOverLevelNames[i] = levelNameRectangles[i].contains(mouse);
            }
            for (int i = 0; i < levelActionRectangles.length; i++) {
                isMouseOverLevelActions[i] = levelActionRectangles[i].contains(mouse);
            }
        }

        isLevelArrowDownDisabled = packageIndex < 0
                || (levelBaseIndex + 5 >= levelPackages.get(packageIndex).getLevelNames().size());

        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            if (isCreatingNewPackage || isRenamingPackage || isDeletingPackage
                    || isCreatingNewLevel || isRenamingLevel || isResizingLevel || isDeletingLevel) {
                isCreatingNewPackage = false;
                isRenamingPackage = false;
                isDeletingPackage = false;
                isCreatingNewLevel = false;
                isRenamingLevel = false;
                isResizingLevel = false;
                isDeletingLevel = false;
                textField.setText("");
            } else {
                game.enterState(Game.MENU_STATE);
            }
        }
        if (input.isKeyPressed(Input.KEY_ENTER)) {
            if (isCreatingNewPackage) {
                String name = textField.getText();
                boolean isPackageAlreadyExist = false;
                for (LevelPackage levelPackage : levelPackages) {
                    if (name.equals(levelPackage.getName())) {
                        isPackageAlreadyExist = true;
                        break;
                    }
                }
                if (!isPackageAlreadyExist) {
                    isCreatingNewPackage = false;
                    LevelPackage levelPackage = new LevelPackage(name, new ArrayList<String>());
                    levelPackages.add(levelPackage);
                    String path = String.format("%1$slevels/%2$03d_%3$s", Game.CONTENT_PATH,
                            levelPackages.size() - 1, textField.getText());
                    File newPackage = new File(path);
                    newPackage.mkdir();
                    textField.setText("");
                }
            }
            if (isRenamingPackage) {
                isRenamingPackage = false;

                String oldName = levelPackages.get(packageIndex).getName();
                String newName = textField.getText();
                boolean isPackageAlreadyExist = false;
                for (LevelPackage levelPackage : levelPackages) {
                    if (newName.equals(levelPackage.getName())) {
                        isPackageAlreadyExist = true;
                        break;
                    }
                }
                if (!isPackageAlreadyExist) {
                    levelPackages.get(packageIndex).setName(newName);

                    renamePackage(packageIndex, oldName, packageIndex, newName);
                    textField.setText("");
                }
            }
            if (isDeletingPackage) {
                isDeletingPackage = false;
                String path = String.format("%1$slevels/%2$03d_%3$s", Game.CONTENT_PATH,
                        packageIndex, levelPackages.get(packageIndex).getName());
                File beingDeletedPackage = new File(path);
                for (File file : beingDeletedPackage.listFiles()) {
                    file.delete();
                }
                beingDeletedPackage.delete();
                levelPackages.remove(packageIndex);
                if (packageBaseIndex > 0) {
                    packageBaseIndex--;
                }
                for (int i = packageIndex; i < levelPackages.size(); i++) {
                    renamePackage(i + 1, levelPackages.get(i).getName(), i, levelPackages.get(i)
                            .getName());
                }
                packageIndex--;
            }

            if (isCreatingNewLevel) {
                if (inputState == 2) {
                    try {
                        int levelHeight = Integer.parseInt(textField.getText());
                        if (levelHeight >= 7 && levelHeight <= 100) {
                            isCreatingNewLevel = false;
                            levelSize.height = levelHeight;

                            LevelPackage levelPackage = levelPackages.get(packageIndex);
                            levelPackage.getLevelNames().add(newLevelName);
                            String path = String.format("%1$slevels/%2$03d_%3$s/%4$02d_%5$s",
                                    Game.CONTENT_PATH, packageIndex, levelPackage.getName(),
                                    levelPackage.getLevelNames().size() - 1, newLevelName);
                            File newLevel = new File(path);

                            newLevel.createNewFile();

                            levelController.createNewLevel(packageIndex, levelPackage
                                    .getLevelNames().size() - 1, levelSize.width, levelSize.height);
                            textField.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (inputState == 1) {
                    try {
                        int levelWidth = Integer.parseInt(textField.getText());
                        if (levelWidth >= 7 && levelWidth <= 100) {
                            inputState = 2;
                            levelSize = new Dimension(levelWidth, 0);
                            textField.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (inputState == 0) {
                    String name = textField.getText();
                    boolean isLevelAlreadyExist = false;
                    LevelPackage levelPackage = levelPackages.get(packageIndex);
                    for (String string : levelPackage.getLevelNames()) {
                        if (name.equals(string)) {
                            isLevelAlreadyExist = true;
                            break;
                        }
                    }
                    if (!isLevelAlreadyExist) {
                        inputState = 1;
                        newLevelName = name;
                        textField.setText("");
                    }
                }
            }
            if (isRenamingLevel) {
                isRenamingLevel = false;
                LevelPackage levelPackage = levelPackages.get(packageIndex);
                String oldName = levelPackage.getLevelNames().get(levelIndex);
                String newName = textField.getText();
                boolean isLevelAlreadyExist = false;
                for (String string : levelPackage.getLevelNames()) {
                    if (newName.equals(string)) {
                        isLevelAlreadyExist = true;
                        break;
                    }
                }
                if (!isLevelAlreadyExist) {
                    levelPackage.getLevelNames().set(levelIndex, newName);

                    renameLevel(packageIndex, levelIndex, oldName, levelIndex, newName);
                    textField.setText("");
                }
            }
            if (isResizingLevel) {
                try {
                    if (inputState == 1) {
                        int levelHeight = Integer.parseInt(textField.getText());
                        if (levelHeight >= 7 && levelHeight <= 100) {
                            isResizingLevel = false;
                            levelSize.height = levelHeight;
                            levelController.resizeLevel(packageIndex, levelIndex, levelSize.width,
                                    levelSize.height);
                            textField.setText("");
                        }
                    }
                    if (inputState == 0) {
                        int levelWidth = Integer.parseInt(textField.getText());
                        if (levelWidth >= 7 && levelWidth <= 100) {
                            inputState = 1;
                            levelSize.width = levelWidth;
                            textField.setText(String.valueOf(levelSize.height));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (isDeletingLevel) {
                isDeletingLevel = false;
                LevelPackage levelPackage = levelPackages.get(packageIndex);
                ArrayList<String> names = levelPackage.getLevelNames();
                String path = String.format("%1$slevels/%2$03d_%3$s/%4$02d_%5$s",
                        Game.CONTENT_PATH, packageIndex, levelPackage.getName(), levelIndex,
                        names.get(levelIndex));
                File beingDeletedLevel = new File(path);
                beingDeletedLevel.delete();
                names.remove(levelIndex);
                if (levelBaseIndex > 0) {
                    levelBaseIndex--;
                }
                for (int i = levelIndex; i < names.size(); i++) {
                    renameLevel(packageIndex, i + 1, names.get(i), i, names.get(i));
                }
                levelIndex--;
            }
        }

        if (input.isMousePressed(0)) {
            if (isMouseOverPackageArrowUp && packageBaseIndex > 0) {
                packageBaseIndex--;
                setPackageNameRectangles();
            }
            if (isMouseOverPackageArrowDown && packageBaseIndex + 5 < levelPackages.size()) {
                packageBaseIndex++;
                setPackageNameRectangles();
            }

            if (isMouseOverLevelArrowUp && levelBaseIndex > 0) {
                levelBaseIndex--;
                setLevelNameRectangles();
            }
            if (isMouseOverLevelArrowDown && !isLevelArrowDownDisabled) {
                levelBaseIndex++;
                setLevelNameRectangles();
            }
            if (isMouseOverReturn) {
                game.enterState(Game.MENU_STATE);
            }
            if (!isCreatingNewPackage && !isRenamingPackage && !isDeletingPackage
                    && !isCreatingNewLevel && !isRenamingLevel && !isResizingLevel
                    && !isDeletingLevel) {
                for (int i = 0; i < packageNameRectangles.length && i < levelPackages.size(); i++) {
                    if (isMouseOverPackageNames[i]) {
                        packageIndex = packageBaseIndex + i;
                        levelBaseIndex = 0;
                        levelIndex = -1;
                        setLevelNameRectangles();
                    }
                }

                if (isMouseOverPackageActions[0] && !isPackageActionsDisabled[0]) { // CREATE
                    isCreatingNewPackage = true;
                }
                if (isMouseOverPackageActions[1] && !isPackageActionsDisabled[1]) { // MOVEUP
                    String firstName = levelPackages.get(packageIndex).getName();
                    String secondName = levelPackages.get(packageIndex - 1).getName();
                    LevelPackage levelPackage = levelPackages.remove(packageIndex - 1);
                    levelPackages.add(packageIndex, levelPackage);

                    renamePackage(packageIndex, firstName, packageIndex - 1, firstName);
                    renamePackage(packageIndex - 1, secondName, packageIndex, secondName);
                    packageIndex--;
                }
                if (isMouseOverPackageActions[2] && !isPackageActionsDisabled[2]) { // MOVEDOWN
                    String firstName = levelPackages.get(packageIndex).getName();
                    String secondName = levelPackages.get(packageIndex + 1).getName();
                    LevelPackage levelPackage = levelPackages.remove(packageIndex);
                    levelPackages.add(packageIndex + 1, levelPackage);

                    renamePackage(packageIndex, firstName, packageIndex + 1, firstName);
                    renamePackage(packageIndex + 1, secondName, packageIndex, secondName);
                    packageIndex++;
                }
                if (isMouseOverPackageActions[3] && !isPackageActionsDisabled[3]) { // RENAME
                    isRenamingPackage = true;
                    textField.setText(levelPackages.get(packageIndex).getName());
                }
                if (isMouseOverPackageActions[4] && !isPackageActionsDisabled[4]) { // DELETE
                    isDeletingPackage = true;
                }

                if (packageIndex >= 0) {
                    for (int i = 0; i < levelNameRectangles.length
                            && i < levelPackages.get(packageIndex).getLevelNames().size(); i++) {
                        if (isMouseOverLevelNames[i]) {
                            levelIndex = levelBaseIndex + i;
                        }
                    }
                    if (isMouseOverLevelActions[0] && !isLevelActionsDisabled[0]) { // CREATE
                        isCreatingNewLevel = true;
                        inputState = 0;
                    }
                    if (isMouseOverLevelActions[1] && !isLevelActionsDisabled[1]) { // EDIT
                        try {
                            levelController.loadLevel(packageIndex, levelIndex);
                            game.enterState(Game.EDITOR_STATE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (isMouseOverLevelActions[2] && !isLevelActionsDisabled[2]) { // MOVEUP
                        LevelPackage levelPackage = levelPackages.get(packageIndex);
                        String firstName = levelPackage.getLevelNames().get(levelIndex);
                        String secondName = levelPackage.getLevelNames().get(levelIndex - 1);
                        String level = levelPackage.getLevelNames().remove(levelIndex - 1);
                        levelPackage.getLevelNames().add(levelIndex, level);

                        renameLevel(packageIndex, levelIndex, firstName, levelIndex - 1, firstName);
                        renameLevel(packageIndex, levelIndex - 1, secondName, levelIndex,
                                secondName);
                        levelIndex--;
                    }
                    if (isMouseOverLevelActions[3] && !isLevelActionsDisabled[3]) { // MOVEDOWN
                        LevelPackage levelPackage = levelPackages.get(packageIndex);
                        String firstName = levelPackage.getLevelNames().get(levelIndex);
                        String secondName = levelPackage.getLevelNames().get(levelIndex + 1);
                        String level = levelPackage.getLevelNames().remove(levelIndex);
                        levelPackage.getLevelNames().add(levelIndex + 1, level);

                        renameLevel(packageIndex, levelIndex, firstName, levelIndex + 1, firstName);
                        renameLevel(packageIndex, levelIndex + 1, secondName, levelIndex,
                                secondName);
                        levelIndex++;
                    }
                    if (isMouseOverLevelActions[4] && !isLevelActionsDisabled[4]) { // RENAME
                        isRenamingLevel = true;
                        textField.setText(levelPackages.get(packageIndex).getLevelNames()
                                .get(levelIndex));
                    }
                    if (isMouseOverLevelActions[5] && !isLevelActionsDisabled[5]) { // RESIZE
                        isResizingLevel = true;
                        inputState = 0;
                        levelSize = levelController.getLevelSize(packageIndex, levelIndex);
                        textField.setText(String.valueOf(levelSize.width));
                    }
                    if (isMouseOverLevelActions[6] && !isLevelActionsDisabled[6]) { // DELETE
                        isDeletingLevel = true;
                    }
                }
            }
        }
    }

    @Override
    public int getID() {
        return this.stateId;
    }

    private void drawString(Graphics g, UnicodeFont font, String text, float x, float y) {
        int width = font.getWidth(text);
        int height = font.getHeight(text);
        g.drawString(text, x - width / 2, y - height / 2);
    }

    private void drawImage(Image image, float x, float y) {
        image.draw(x, y);
    }

    private void setPackageNameRectangles() {
        packageNameRectangles = new Rectangle[5];
        for (int i = 0; i < packageNameRectangles.length && i < levelPackages.size(); i++) {
            packageNameRectangles[i] = new Rectangle();
            packageNameRectangles[i].x = width / 100;
            packageNameRectangles[i].y = height * (4 + i) / 13;
            packageNameRectangles[i].width = ubuntuSmall.getWidth(levelPackages.get(
                    i + packageBaseIndex).getName());
            packageNameRectangles[i].height = ubuntuSmall.getHeight(levelPackages.get(
                    i + packageBaseIndex).getName());
        }
    }

    private void setLevelNameRectangles() {
        levelNameRectangles = new Rectangle[5];
        LevelPackage levelPackage = levelPackages.get(packageIndex);
        ArrayList<String> levelNames = levelPackage.getLevelNames();
        for (int i = 0; i < levelNameRectangles.length && i < levelNames.size(); i++) {
            levelNameRectangles[i] = new Rectangle();
            levelNameRectangles[i].x = width / 2 + width / 100;
            levelNameRectangles[i].y = height * (4 + i) / 13;
            levelNameRectangles[i].width = ubuntuSmall.getWidth(levelNames.get(i + levelBaseIndex));
            levelNameRectangles[i].height = ubuntuSmall.getHeight(levelNames
                    .get(i + levelBaseIndex));
        }
    }

    private void setPackageActionRectangles() {
        packageActionRectangles = new Rectangle[5];
        for (int i = 0; i < packageActionRectangles.length; i++) {
            packageActionRectangles[i] = new Rectangle();
            packageActionRectangles[i].width = ubuntuSmall.getWidth(packageActions[i]);
            packageActionRectangles[i].height = ubuntuSmall.getHeight(packageActions[i]);
            packageActionRectangles[i].x = width / 3 - packageActionRectangles[i].width / 2;
            packageActionRectangles[i].y = height * (4 + i) / 13;
        }
    }

    private void setLevelActionRectangles() {
        levelActionRectangles = new Rectangle[7];
        for (int i = 0; i < levelActionRectangles.length; i++) {
            levelActionRectangles[i] = new Rectangle();
            levelActionRectangles[i].width = ubuntuSmall.getWidth(levelActions[i]);
            levelActionRectangles[i].height = ubuntuSmall.getHeight(levelActions[i]);
            levelActionRectangles[i].x = width - width / 9 - levelActionRectangles[i].width / 2;
            levelActionRectangles[i].y = height * (3 + i) / 13;
        }
    }

    private void renamePackage(int oldNumber, String oldName, int newNumber, String newName) {
        String path = String
                .format("%1$slevels/%2$03d_%3$s", Game.CONTENT_PATH, oldNumber, oldName);
        File beingRenamedPackage = new File(path);
        path = String.format("%1$slevels/%2$03d_%3$s", Game.CONTENT_PATH, newNumber, newName);
        File renamedPackage = new File(path);
        beingRenamedPackage.renameTo(renamedPackage);
    }

    private void renameLevel(int packageIndex, int oldNumber, String oldName, int newNumber,
            String newName) {
        String base = String.format("%1$slevels/%2$03d_%3$s/", Game.CONTENT_PATH, packageIndex,
                levelPackages.get(packageIndex).getName());
        String path = String.format("%1$02d_%2$s", oldNumber, oldName);
        File level = new File(base + path);
        path = String.format("%1$02d_%2$s", newNumber, newName);
        File renamedLevel = new File(base + path);
        level.renameTo(renamedLevel);
    }
}