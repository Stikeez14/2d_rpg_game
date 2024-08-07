package entities;

import frame.Panel;
import map.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Bandit extends Entity {

    private static final int MIN_DIRECTION_CHANGE_INTERVAL = 240;
    private static final int MAX_DIRECTION_CHANGE_INTERVAL = 480;
    private static final int DETECT_RANGE = 3; // in tiles
    private static final int SPRITE_THRESHOLD = 20;
    private static final int HEALTH_BAR_WIDTH = 16;
    private static final int MAX_HEALTH = 20;
    private static final int STOP_RANGE = 100; // in pixels

    private int countCooldown;
    private final Random rand;
    private String lastCollisionDirection;
    private final String weaponType;

    public Bandit(int x, int y, Panel gamePanel, String banditType, String weaponType) {
        super(x, y, gamePanel);
        this.rand = new Random();
        this.direction = "standing";
        this.Speed = 1;
        this.lastCollisionDirection = "none";
        this.weaponType = weaponType;
        this.health = MAX_HEALTH;
        loadEntityVisuals(banditType);
    }

    @Override
    public void setEntity() {
        gamePanel.ck.checkTileCollision(this);

        for (Entity entity : gamePanel.getEntities()) {
            gamePanel.ck.checkEntityCollision(this, entity);
        }

        checkEntityHealth(this);

        if (detectEntity(gamePanel.player)) {
            moveToEntity(gamePanel.player);
        } else {
            moveEntity();
        }

        updateHitbox();
        updateSpriteFlag();
    }

    @Override
    protected void loadEntityVisuals(String banditType) {
        try {
            String path = banditType + File.separator;
            standing = loadImage(path + "standing.png");
            walkDown1 = loadImage(path + "walkDown1.png");
            walkDown2 = loadImage(path + "walkDown2.png");
            walkUp1 = loadImage(path + "walkUp1.png");
            walkUp2 = loadImage(path + "walkUp2.png");
            walkLeft1 = loadImage(path + "walkLeft1.png");
            walkLeft2 = loadImage(path + "walkLeft2.png");
            walkRight1 = loadImage(path + "walkRight1.png");
            walkRight2 = loadImage(path + "walkRight2.png");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Bandit Visuals!", e);
        }
    }

    @Override
    protected void moveEntity() {
        boolean collided = false;

        if (direction.equals("Up")) {
            if (!collisionUp) {
                y -= Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "Up";
            }
        }

        if (direction.equals("Down")) {
            if (!collisionDown) {
                y += Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "Down";
            }
        }

        if (direction.equals("Left")) {
            if (!collisionLeft) {
                x -= Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "Left";
            }
        }

        if (direction.equals("Right")) {
            if (!collisionRight) {
                x += Speed;
                lastCollisionDirection = "none";
            } else {
                collided = true;
                lastCollisionDirection = "Right";
            }
        }

        if (countCooldown > 0) countCooldown--;

        if (collided || countCooldown == 0) {
            if (rand.nextDouble() < 0.6) {
                direction = "standing";
            } else {
                direction = setDirection();
            }
            countCooldown = MIN_DIRECTION_CHANGE_INTERVAL + rand.nextInt(MAX_DIRECTION_CHANGE_INTERVAL - MIN_DIRECTION_CHANGE_INTERVAL + 1);
        }
    }

    private void updateSpriteFlag() {
        spriteCounter++;
        if (spriteCounter > SPRITE_THRESHOLD) {
            spriteFlag = (spriteFlag == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    private String setDirection() {
        List<String> validDirections = new ArrayList<>(Arrays.asList("Up" , "Down", "Left", "Right", "standing"));

        if (collisionUp) {
            validDirections.remove("Up");
        }
        if (collisionDown) {
            validDirections.remove("Down");
        }
        if (collisionLeft) {
            validDirections.remove("Left");
        }
        if (collisionRight) {
            validDirections.remove("Right");
        }

        validDirections.remove(lastCollisionDirection);

        if (validDirections.isEmpty()) validDirections.add("standing");

        return validDirections.get(rand.nextInt(validDirections.size()));
    }

    public boolean detectEntity(Entity target) {
        int tileSize = Settings.getTileSize();
        int targetX = target.getEntityX();
        int targetY = target.getEntityY();
        int banditX = this.getEntityX();
        int banditY = this.getEntityY();
        int rangeInPixels = DETECT_RANGE * tileSize;

        boolean isWithinRange = Math.abs(targetX - banditX) <= rangeInPixels && Math.abs(targetY - banditY) <= rangeInPixels;

        if (isWithinRange) {
            List<Node> path = findPath(target);
            return path != null;
        }
        return false;
    }

    private static class Node implements Comparable<Node> {
        int x, y;
        int g, h;
        Node parent;

        Node(int x, int y, int g, int h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }

        int f() {
            return g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f(), other.f());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    private List<Node> cachedPath = null;

    private int lastTargetX = -1;
    private int lastTargetY = -1;

    private int frameCounter = 0;
    private boolean noPathFound = false;
    private int pathfindingAttempts = 0;

    private static final int MAX_NODES_PER_FRAME = 100; // Limit nodes processed per frame

    private List<Node> findPath(Entity target) {
        int tileSize = Settings.getTileSize();
        int startX = x / tileSize;
        int startY = y / tileSize;
        int endX = target.getEntityX() / tileSize;
        int endY = target.getEntityY() / tileSize;

        if (startX < 0 || startY < 0 || endX < 0 || endY < 0 ||
                startX >= Settings.getMaxTilesVertically() || startY >= Settings.getMaxTilesHorizontally() ||
                endX >= Settings.getMaxTilesVertically() || endY >= Settings.getMaxTilesHorizontally()) {
            return null;
        }

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(startX, startY, 0, Math.abs(endX - startX) + Math.abs(endY - startY), null);
        openList.add(startNode);

        int nodesProcessed = 0;

        while (!openList.isEmpty()) {
            if (nodesProcessed > MAX_NODES_PER_FRAME) {
                return null;
            }
            nodesProcessed++;

            Node current = openList.poll();

            if (current.x == endX && current.y == endY) {
                List<Node> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path;
            }

            closedList.add(current);

            for (int[] direction : new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}) {
                int newX = current.x + direction[0];
                int newY = current.y + direction[1];

                if (newX < 0 || newX >= Settings.getMaxTilesVertically() || newY < 0 || newY >= Settings.getMaxTilesHorizontally()) continue;

                // Check if the tile is an obstacle or if another entity occupies the tile
                int tileNumber = gamePanel.map.getTileNumberAt(newX, newY);
                if (isObstacle(tileNumber, newX, newY)) continue;

                Node neighbor = new Node(newX, newY, current.g + 1, Math.abs(endX - newX) + Math.abs(endY - newY), current);
                if (closedList.contains(neighbor)) continue;

                if (!openList.contains(neighbor) || current.g + 1 < neighbor.g) {
                    openList.add(neighbor);
                }
            }
        }

        return null; // No path found
    }

    private boolean isObstacle(int tileNumber, int newX, int newY) {
        // Check if the tile is a solid obstacle based on tile numbers
        if (tileNumber == 5 || tileNumber == 6 || tileNumber == 7 || tileNumber == 8 || tileNumber == 9 || tileNumber == 10 || tileNumber == 11) {
            return true;
        }

        // Check if another entity is occupying the tile
        for (Entity entity : gamePanel.getEntities()) {
            if(entity.equals(gamePanel.player)) return false;
            int entityTileX = entity.getEntityX() / Settings.getTileSize();
            int entityTileY = entity.getEntityY() / Settings.getTileSize();
            if (entityTileX == newX && entityTileY == newY && entity != this) {
                return true; // Another entity is in the way
            }
        }

        return false;
    }

    private void moveToEntity(Entity target) {
        int targetTileX = target.getEntityX() / Settings.getTileSize();
        int targetTileY = target.getEntityY() / Settings.getTileSize();

        int distX = target.getEntityX() - x;
        int distY = target.getEntityY() - y;

        if (Math.abs(distX) <= STOP_RANGE && Math.abs(distY) <= STOP_RANGE) {
            direction = "standing";
            return;
        }

        if (targetMoved(targetTileX, targetTileY)) {
            noPathFound = false;
            pathfindingAttempts = 0;
        }

        int pathUpdateInterval = 10;
        if ((cachedPath == null || targetMoved(targetTileX, targetTileY) || frameCounter % pathUpdateInterval == 0) && !noPathFound) {
            int maxPathfindingAttempts = 5;
            int retryPathfindingInterval = 100;
            if (pathfindingAttempts < maxPathfindingAttempts) {
                List<Node> newPath = findPath(target);
                if (newPath != null) {
                    cachedPath = newPath;
                    lastTargetX = targetTileX;
                    lastTargetY = targetTileY;
                    pathfindingAttempts = 0;
                } else {
                    pathfindingAttempts++;
                    if (pathfindingAttempts >= maxPathfindingAttempts) {
                        noPathFound = true;
                        System.out.println("Max pathfinding attempts reached, no path found.");
                    }
                }
            } else if (frameCounter % retryPathfindingInterval == 0) {
                pathfindingAttempts = 0;
            }
            frameCounter = 0;
        }

        if (cachedPath == null || noPathFound) {
            noPathFound = true;
            System.out.println("No path found to the target. Falling back to random movement.");
            if (direction.equals("standing")) {
                direction = setDirection();
            }
            countCooldown = MIN_DIRECTION_CHANGE_INTERVAL + rand.nextInt(MAX_DIRECTION_CHANGE_INTERVAL - MIN_DIRECTION_CHANGE_INTERVAL + 1); // Add cooldown
            moveEntity();
            return;
        }

        if (cachedPath.size() > 1) {
            Node nextStep = cachedPath.get(1);
            int nextX = nextStep.x * Settings.getTileSize();
            int nextY = nextStep.y * Settings.getTileSize();

            if (nextX > x && !collisionRight) {
                x += Speed;
                direction = "Right";
                return;
            } else if (nextX < x && !collisionLeft) {
                x -= Speed;
                direction = "Left";
                return;
            }

            if (nextY > y && !collisionDown) {
                y += Speed;
                direction = "Down";
                return;
            } else if (nextY < y && !collisionUp) {
                y -= Speed;
                direction = "Up";
                return;
            }
        }

        System.out.println("Reverting to random movement.");
        countCooldown = MIN_DIRECTION_CHANGE_INTERVAL + rand.nextInt(MAX_DIRECTION_CHANGE_INTERVAL - MIN_DIRECTION_CHANGE_INTERVAL + 1); // Add cooldown
        moveEntity();
        frameCounter++;
    }

    private boolean targetMoved(int targetTileX, int targetTileY) {
        return lastTargetX != targetTileX || lastTargetY != targetTileY;
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = getCurrentSprite();
        int drawX = x - gamePanel.player.getEntityX() + gamePanel.player.getEntityScreenX();
        int drawY = y - gamePanel.player.getEntityY() + gamePanel.player.getEntityScreenY();
        g2.drawImage(image, drawX, drawY, width, height, null);

        drawWeapon(g2, drawX, drawY);
        if (detectEntity(gamePanel.player) && !noPathFound) drawDetectedStatus(g2,drawX,drawY);
        if (health < MAX_HEALTH) drawHealthBar(g2, drawX, drawY);

        if (gamePanel.player.drawBoxes) {
            drawEntityBox(g2, drawX, drawY, collisionBox, Color.red);
            drawEntityBox(g2, drawX, drawY, verticalHitBox, Color.blue);
            drawEntityBox(g2, drawX, drawY, horizontalHitBox, Color.blue);
        }
    }

    private void drawWeapon(Graphics2D g2, int drawX, int drawY) {
        BufferedImage weapon = null;

        if (weaponType != null) {
            switch (weaponType) {
                case "baseballBat" -> weapon = getWeaponImage(standingBat, walkDown1Bat, standingBat, walkUp1Bat, walkUp2Bat, walkLeft1Bat, walkLeft2Bat, walkRight1Bat, walkRight2Bat);
                case "dagger" -> weapon = getWeaponImage(standingDag, walkDown1Dag, standingDag, walkUp1Dag, walkUp2Dag, walkLeft1Dag, walkLeft2Dag, walkRight1Dag, walkRight2Dag);
            }
        }

        if (weapon != null) {
            g2.drawImage(weapon, drawX, drawY, width, height, null);
        }
    }

    private BufferedImage getWeaponImage(BufferedImage standingWeapon, BufferedImage walkDown1Weapon, BufferedImage walkDown2Weapon, BufferedImage walkUp1Weapon, BufferedImage walkUp2Weapon, BufferedImage walkLeft1Weapon, BufferedImage walkLeft2Weapon, BufferedImage walkRight1Weapon, BufferedImage walkRight2Weapon) {
        return switch (direction) {
            case "Down" -> (spriteFlag == 1) ? walkDown1Weapon : walkDown2Weapon;
            case "Up" -> (spriteFlag == 1) ? walkUp1Weapon : walkUp2Weapon;
            case "Left" -> (spriteFlag == 1) ? walkLeft1Weapon : walkLeft2Weapon;
            case "Right" -> (spriteFlag == 1) ? walkRight1Weapon : walkRight2Weapon;
            case "standing" -> standingWeapon;
            default -> null;
        };
    }

    private void drawEntityBox(Graphics2D g2, int drawX, int drawY, Rectangle box, Color color) {
        g2.setColor(color);
        g2.drawRect(drawX + box.x, drawY + box.y, box.width, box.height);
    }

    private void drawHealthBar(Graphics2D g2, int drawX, int drawY) {
        int barWidthFilled = (int) (HEALTH_BAR_WIDTH * health / MAX_HEALTH);
        Color barColor;

        if (barWidthFilled < 4) barColor = Color.RED;
        else if (barWidthFilled < 8) barColor = Color.ORANGE;
        else if (barWidthFilled < 12) barColor = Color.YELLOW;
        else barColor = Color.GREEN;

        g2.setColor(barColor);
        g2.fillRect(drawX + 7 * Settings.getScale(), drawY - 10, barWidthFilled * Settings.getScale(), Settings.getScale());
        g2.setColor(Color.BLACK);
        g2.drawRect(drawX + 7 * Settings.getScale(), drawY - 10, barWidthFilled * Settings.getScale(), Settings.getScale());
    }

    private void drawDetectedStatus(Graphics2D g2, int drawX, int drawY) {
        if (detectEntity(gamePanel.player) && !noPathFound) {
            g2.setColor(Color.red);
            g2.drawRect(drawX + width / 2 - 2, drawY - 10, 5, 5); // Adjusted position to be above the bandit
            g2.fillRect(drawX + width / 2 - 2, drawY - 10, 5, 5);
        }
    }
}
