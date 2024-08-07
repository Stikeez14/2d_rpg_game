package entities;

import frame.Panel;
import map.Map;
import map.Settings;
import inputs.KeyboardKeys;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player extends Entity {

    private final KeyboardKeys key = new KeyboardKeys(); // key handler for taking keyboard inputs

    // player visuals
    private BufferedImage standingEternalHelmet, standingEternalChestPlate, standingEternalLeggings;
    private BufferedImage walkUpEternalHelmet, walkUp1EternalChestPlate, walkUp2EternalChestPlate, walkUp1EternalLeggings, walkUp2EternalLeggings;
    private BufferedImage walkDownEternalHelmet, walkDown1EternalChestPlate, walkDown2EternalChestPlate, walkDown1EternalLeggings, walkDown2EternalLeggings;
    private BufferedImage walkLeftEternalHelmet, walkLeft1EternalChestPlate, walkLeft2EternalChestPlate, walkLeft1EternalLeggings, walkLeft2EternalLeggings;
    private BufferedImage walkRightEternalHelmet, walkRight1EternalChestPlate, walkRight2EternalChestPlate, walkRight1EternalLeggings, walkRight2EternalLeggings;

    public boolean isSprinting = false; // flag used to determine player sprinting

    // timestamps for sprint start & end
    private long sprintStartTime = 0;
    private long lastSprintTime = 0;

    // timestamps for attack start & end
    private long attackStartTime = 0;
    private long lastAttackTime = 0;

    // flags to check equipped armour
    private boolean hasHelmet = false;
    private boolean hasChestPlate = false;
    private boolean hasLeggings = false;
    private boolean hasWeapon = false;

    // track current & previous state of 'F3' key
    private boolean currentF3Pressed = false;
    private boolean previousF3Pressed = false;

    // track current & previous state of 'F2' key
    private boolean currentF2Pressed = false;
    private boolean previousF2Pressed = false;

    private static boolean isDrawInfo = false; // flag used to determine if debug info should be drawn on screen

    public Player(int x, int y, Panel gamePanel, String playerType) {
        super(x, y, gamePanel);
        gamePanel.addKeyListener(key);
        gamePanel.setFocusable(true); // helps with receiving key events
        gamePanel.requestFocusInWindow();
        loadEntityVisuals(playerType);
        health = 20; // starting health is 20
    }

    @Override
    public void setEntity() {

        sprintAndScale(); // handles the sprint logic & scale change based on it

        setDirection(); // update player direction

        handleAttack(); // attack direction

        if(isAttacking) { // if the player is attacking will check collision between his attackBox and all entities HitBoxes
            for (Entity entity : gamePanel.getEntities()) {
                if (!entity.equals(gamePanel.player)) {
                    gamePanel.ck.checkEntityAttack(this, entity);
                }
            }
        }

        checkEntityHealth(this); // checks health

        setDrawStatus(); // update flags for drawing information on screen

        gamePanel.ck.checkTileCollision(this); // check if the player is colliding with the tiles collision areas

        for (Entity entity : gamePanel.getEntities()) {
            gamePanel.ck.checkEntityCollision(this, entity); // check if the player is colliding with other entities
        }

        moveEntity(); // move the player based on collision flags

        updateSpriteFlag(); // update sprite flag based on counter
    }

    @Override
    protected void moveEntity() {
        super.moveEntity(); // call the parent method to avoid code duplication
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite(); // gets the sprite based on direction
        g2.drawImage(image, getEntityScreenX(), getEntityScreenY(), width, height, null); // draws player on screen

        drawPlayerEquipment(g2); // draws player on top of the player

        if(isDrawInfo){ // draw debug info only when 'F2' is pressed
            gamePanel.drawInfo(g2, "fps: ", gamePanel.getFPS(), 20, 40); // draws the fps
            gamePanel.drawTimeToDraw(g2, "draw time: ", gamePanel.getDrawTime(), 20, 85); // draws the time required to draw everything
            gamePanel.drawInfo(g2, "x: ",  getOriginalX(),20,130); // draws the player x coordinate in the world
            gamePanel.drawInfo(g2, "y: ",  getOriginalY(),20,175); // draws the player y coordinate in the world
        }
        if(drawBoxes) { // draws player boxes
            drawEntityBox(g2, collisionBox, Color.red);
            drawEntityBox(g2, verticalHitBox, Color.blue);
            drawEntityBox(g2, horizontalHitBox, Color.blue);
            if(attackBox !=null) drawEntityBox(g2, attackBox, Color.yellow);
        }
    }

    @Override
    public void loadEntityVisuals(String playerType) {
        try {
            /* PLAYER DEFAULT SKIN */
            standing = loadImage("malePlayer" + File.separator + "standing.png");
            walkDown1 = loadImage("malePlayer" + File.separator + "walkDown1.png");
            walkDown2 = loadImage("malePlayer" + File.separator + "walkDown2.png");
            walkUp1 = loadImage("malePlayer" + File.separator + "walkUp1.png");
            walkUp2 = loadImage("malePlayer" + File.separator + "walkUp2.png");
            walkLeft1 = loadImage("malePlayer" + File.separator + "walkLeft1.png");
            walkLeft2 = loadImage("malePlayer" + File.separator + "walkLeft2.png");
            walkRight1 = loadImage("malePlayer" + File.separator + "walkRight1.png");
            walkRight2 = loadImage("malePlayer" + File.separator + "walkRight2.png");

            /* ETERNAL ARMOUR SKIN */
            standingEternalHelmet = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalHelmet.png");
            standingEternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalChestPlate.png");
            standingEternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "standing" + File.separator + "standingEternalLeggings.png");

            walkUpEternalHelmet = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUpEternalHelmet.png");
            walkUp1EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp1EternalChestPlate.png");
            walkUp2EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp2EternalChestPlate.png");
            walkUp1EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp1EternalLeggings.png");
            walkUp2EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkUp" + File.separator + "walkUp2EternalLeggings.png");

            walkDownEternalHelmet = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDownEternalHelmet.png");
            walkDown1EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown1EternalChestPlate.png");
            walkDown2EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown2EternalChestPlate.png");
            walkDown1EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown1EternalLeggings.png");
            walkDown2EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkDown" + File.separator + "walkDown2EternalLeggings.png");

            walkLeftEternalHelmet = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeftEternalHelmet.png");
            walkLeft1EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft1EternalChestPlate.png");
            walkLeft2EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft2EternalChestPlate.png");
            walkLeft1EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft1EternalLeggings.png");
            walkLeft2EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkLeft" + File.separator + "walkLeft2EternalLeggings.png");

            walkRightEternalHelmet = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRightEternalHelmet.png");
            walkRight1EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight1EternalChestPlate.png");
            walkRight2EternalChestPlate = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight2EternalChestPlate.png");
            walkRight1EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight1EternalLeggings.png");
            walkRight2EternalLeggings = loadImage("equipment" + File.separator + "armours" + File.separator + "eternal" + File.separator + "walkRight" + File.separator + "walkRight2EternalLeggings.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Player Visuals!", e);
        }
    }

    /** SPRINT HANDLING & SCALE CHANGE */
    private void sprintAndScale(){
        int runSpeed;

        Speed = 1;
        runSpeed = Speed + 1;

        long currentTime = System.currentTimeMillis();

        //* SPRINTING */
        if (isSprinting) { // checking if the player is allowed to sprint
            long sprintDuration = 3000; // 3 sec sprint duration

            if (currentTime - sprintStartTime < sprintDuration) {
                Speed = runSpeed;
            } else {
                isSprinting = false;
                int oldScale = Settings.getScale(); // store the scale before changing it
                Settings.setWalkScale(); // change the scale to a bigger value (=6)
                Map.setScale(Settings.getScale()); // update in Map Class the scale value for tiles & collision areas

                // adjust player position to be the same as before scaling
                for(int i=0; i<gamePanel.getEntities().size();i++) gamePanel.getEntities().get(i).adjustPosition(oldScale,Settings.getScale());

                updateHitbox();  // scale entity collisionBox
                lastSprintTime = currentTime; // get the time when the sprint ended
            }
        } else {
            long sprintCooldown = 12000; // 12 sec cooldown after sprinting was used

            // check if cooldown ended and activate sprinting again
            if (currentTime - lastSprintTime >= sprintCooldown &&
                    ((key.upPressed && key.shiftPressed) ||
                            (key.downPressed && key.shiftPressed) ||
                            (key.leftPressed && key.shiftPressed) ||
                            (key.rightPressed && key.shiftPressed) ||
                            key.shiftPressed)) {
                isSprinting = true;
                int oldScale = Settings.getScale(); // store the scale before changing it
                Settings.setSprintScale(); // change the scale to a smaller value (=5)
                Map.setScale(Settings.getScale()); // update in Map Class the scale value for tiles & collision areas

                // adjust player position to be the same as before scaling
                for(int i=0; i<gamePanel.getEntities().size();i++) gamePanel.getEntities().get(i).adjustPosition(oldScale,Settings.getScale());

                updateHitbox(); // scale entity collisionBox
                sprintStartTime = currentTime;
                Speed = runSpeed; // update speed
            }
        }
    }

    /** GET DIRECTION BASED ON PRESSED KEYS */
    private void setDirection() {
        direction = "standing"; // direction initialized with "standing"

        long currentTime = System.currentTimeMillis();
        long attackCooldown = 1000;

        /* ATTACK DIRECTIONS */
        if (key.spacePressed && (currentTime - lastAttackTime >= attackCooldown)) {
            // if space is pressed & cooldown is attack cooldown is over, the player can attack again
            isAttacking = true; // activate flag
            lastAttackTime = currentTime;

            if (key.upPressed) {
                if (key.leftPressed) {
                    direction = "attackUp&Left";
                } else if (key.rightPressed) {
                    direction = "attackUp&Right";
                } else {
                    direction = "attackUp";
                }
            } else if (key.downPressed) {
                if (key.leftPressed) {
                    direction = "attackDown&Left";
                } else if (key.rightPressed) {
                    direction = "attackDown&Right";
                } else {
                    direction = "attackDown";
                }
            } else if (key.leftPressed) {
                direction = "attackLeft";
            } else if (key.rightPressed) {
                direction = "attackRight";
            } else {
                direction = "attackDown"; // if only space is pressed, the default attack is "attackDown"
            }
        } /* MOVEMENT DIRECTIONS */
        else {
            if (key.upPressed) {
                if (key.leftPressed) {
                    direction = "Up&Left";
                } else if (key.rightPressed) {
                    direction = "Up&Right";
                } else {
                    direction = "Up";
                }
            } else if (key.downPressed) {
                if (key.leftPressed) {
                    direction = "Down&Left";
                } else if (key.rightPressed) {
                    direction = "Down&Right";
                } else {
                    direction = "Down";
                }
            } else if (key.leftPressed) {
                direction = "Left";
            } else if (key.rightPressed) {
                direction = "Right";
            }
        }
    }

    /** CREATES ATTACK BOXES BASED ON DIRECTION */
    private void handleAttack() {
        if (isAttacking) {
            // start the attack
            if (attackStartTime == 0) attackStartTime = System.currentTimeMillis();
            long elapsedTime = System.currentTimeMillis() - attackStartTime;

            long attackDuration = 500; // 0.5 sec

            if (elapsedTime > attackDuration) {
                isAttacking = false; // deactivate flag
                attackBox = null;  // clear attackBox
                attackStartTime = 0; // reset startTime
            } else {
                // attackBox coordinates for starting in the middle of the vertical or horizontal hit boxes
                int verticalCenterX = verticalHitBox.x + verticalHitBox.width / 2;
                int verticalCenterY = verticalHitBox.y + verticalHitBox.height / 2;
                int horizontalCenterX = horizontalHitBox.x + horizontalHitBox.width / 2;
                int horizontalCenterY = horizontalHitBox.y + horizontalHitBox.height / 2;

                switch (direction) {
                    case "attackUp":
                        attackBox = new Rectangle(verticalCenterX - 11 * Settings.getScale() / 2, verticalCenterY - 20 * Settings.getScale(), 11 * Settings.getScale(), 20 * Settings.getScale());
                        break;
                    case "attackDown":
                        attackBox = new Rectangle(verticalCenterX - 11 * Settings.getScale() / 2, verticalCenterY, 11 * Settings.getScale(), 20 * Settings.getScale());
                        break;
                    case "attackLeft":
                        attackBox = new Rectangle(horizontalCenterX - 20 * Settings.getScale(), horizontalCenterY - 11 * Settings.getScale() / 2, 20 * Settings.getScale(), 11 * Settings.getScale());
                        break;
                    case "attackRight":
                        attackBox = new Rectangle(horizontalCenterX, horizontalCenterY - 11 * Settings.getScale() / 2, 20 * Settings.getScale(), 11 * Settings.getScale());
                        break;
                    default:
                        attackBox = null;
                        break;
                }
            }
        } else {
            attackBox = null; // clear attackBox if not attacking
            attackStartTime = 0; // reset startTime
        }
    }

    /** SET FLAGS FOR DRAWING INFORMATION ON SCREEN */
    private void setDrawStatus(){
        if (key.f3Pressed) { // handle 'F3' key press for toggling collision debug info
            if (!previousF3Pressed) currentF3Pressed = !currentF3Pressed;
            previousF3Pressed = true;
        } else previousF3Pressed = false;

        gamePanel.map.setDrawCollisionStatus(currentF3Pressed);
        setDrawHitboxStatus(currentF3Pressed);

        if (key.f2Pressed) { // handle 'F2' key press for toggling additional debug info
            if (!previousF2Pressed) currentF2Pressed = !currentF2Pressed;
            previousF2Pressed = true;
        } else previousF2Pressed = false;

        setIsDrawInfo(currentF2Pressed);
    }

    /** UPDATES THE FLAG RESPONSIBLE FOR SPRITE ANIMATIONS */
    private void updateSpriteFlag() {
        spriteCounter++;
        int spriteThreshold = isSprinting ? 10 : 20; // faster sprite change when sprinting

        if (spriteCounter > spriteThreshold) { // toggle sprite flag between 1 & 2 and reset counter
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    /** DRAW EQUIPMENT */
    private void drawPlayerEquipment(Graphics2D g2){
        BufferedImage helmet = null;
        BufferedImage chestPlate = null;
        BufferedImage leggings = null;
        BufferedImage weapon = null;

        if(!isAttacking){
            switch (direction) { // draws player armour based on direction and sprite flag
                case "Down", "Down&Left", "Down&Right":
                    if (hasHelmet) helmet = walkDownEternalHelmet;
                    if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkDown1EternalChestPlate : walkDown2EternalChestPlate;
                    if (hasLeggings) leggings = (spriteFlag == 1) ? walkDown1EternalLeggings : walkDown2EternalLeggings;
                    if (hasWeapon) weapon = (spriteFlag == 1) ? walkDown1Bat : standingBat;
                    break;
                case "Up", "Up&Left", "Up&Right":
                    if (hasHelmet) helmet = walkUpEternalHelmet;
                    if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkUp1EternalChestPlate : walkUp2EternalChestPlate;
                    if (hasLeggings) leggings = (spriteFlag == 1) ? walkUp1EternalLeggings : walkUp2EternalLeggings;
                    if (hasWeapon) weapon = (spriteFlag == 1) ? walkUp1Bat : walkUp2Bat;
                    break;
                case "Left":
                    if (hasHelmet) helmet = walkLeftEternalHelmet;
                    if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkLeft1EternalChestPlate : walkLeft2EternalChestPlate;
                    if (hasLeggings) leggings = (spriteFlag == 1) ? walkLeft1EternalLeggings : walkLeft2EternalLeggings;
                    if (hasWeapon) weapon = (spriteFlag == 1) ? walkLeft1Bat : walkLeft2Bat;
                    break;
                case "Right":
                    if (hasHelmet) helmet = walkRightEternalHelmet;
                    if (hasChestPlate) chestPlate = (spriteFlag == 1) ? walkRight1EternalChestPlate : walkRight2EternalChestPlate;
                    if (hasLeggings) leggings = (spriteFlag == 1) ? walkRight1EternalLeggings : walkRight2EternalLeggings;
                    if (hasWeapon) weapon = (spriteFlag == 1) ? walkRight1Bat : walkRight2Bat;
                    break;
                case "standing":
                    if (hasHelmet) helmet = standingEternalHelmet;
                    if (hasChestPlate) chestPlate = standingEternalChestPlate;
                    if (hasLeggings) leggings = standingEternalLeggings;
                    if (hasWeapon) weapon = standingBat;
                    break;
            }}

        if (helmet != null) g2.drawImage(helmet, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        if (chestPlate != null) g2.drawImage(chestPlate, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        if (leggings != null) g2.drawImage(leggings, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
        if (weapon != null) g2.drawImage(weapon, getEntityScreenX(), getEntityScreenY(), getEntityWidth(), getEntityHeight(), null);
    }

    /* JUST FOR TESTING EQUIPMENT */
    public void setEquipment(boolean hasHelmet, boolean hasChestPlate, boolean hasLeggings, boolean hasWeapon) {
        this.hasHelmet=hasHelmet;
        this.hasChestPlate=hasChestPlate;
        this.hasLeggings=hasLeggings;
        this.hasWeapon=hasWeapon;
    }

    /** DRAW INFORMATION */
    /* flag for debug information status */
    private void setIsDrawInfo(boolean status){ isDrawInfo = status; } // activated if player presses 'F2'
}
