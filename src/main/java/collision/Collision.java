package collision;

import entities.Entity;
import frame.Panel;
import map.Settings;
import map.Tile;

import java.awt.*;

public class Collision {

    Panel gamePanel;

    public Collision(Panel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /* checks if the player collides with any tile */
    public void checkTileCollision(Entity entity) {
        entity.setCollisionUp(false);
        entity.setCollisionDown(false);
        entity.setCollisionLeft(false);
        entity.setCollisionRight(false);

        entity.updateHitbox();

        // rectangle representing entity's collisionBox
        Rectangle playerRect = new Rectangle(entity.getEntityX() + entity.getCollisionBox().x, entity.getEntityY() + entity.getCollisionBox().y, entity.getCollisionBox().width, entity.getCollisionBox().height);

        int tileSize = Settings.getTileSize();

        // range of tiles to check based on the player's position and speed
        int startX = Math.max((entity.getEntityX() + entity.getCollisionBox().x - entity.getSpeed()) / tileSize, 0);
        int endX = Math.min((entity.getEntityX() + entity.getCollisionBox().x + entity.getCollisionBox().width + entity.getSpeed()) / tileSize, Settings.getMaxTilesHorizontally() - 1);
        int startY = Math.max((entity.getEntityY() + entity.getCollisionBox().y - entity.getSpeed()) / tileSize, 0);
        int endY = Math.min((entity.getEntityY() + entity.getCollisionBox().y + entity.getCollisionBox().height + entity.getSpeed()) / tileSize, Settings.getMaxTilesVertically() - 1);

        // check for collision in the optimized range
        for (int tileY = startY; tileY <= endY; tileY++) {
            for (int tileX = startX; tileX <= endX; tileX++) {
                int tileNum = gamePanel.map.getMapTileMatrix()[tileX][tileY]; // current tile
                if (tileNum >= 0 && tileNum < gamePanel.map.getTile().length) { // check if tile number is valid
                    Tile tile = gamePanel.map.getTile()[tileNum]; // get the tile from the map
                    if (tile != null && tile.getTileCollision()) { // if tile has collision will loop through all the collision areas
                        for (Rectangle rect : tile.getCollisionArea()) { // create new rectangle representing tile collision area
                            Rectangle tileRect = new Rectangle(tileX * tileSize + rect.x, tileY * tileSize + rect.y, rect.width, rect.height);

                            // buffer value for acceptable overlap
                            int buffer = 5;

                            // check collision if player moves up INTERSECTS with tile collision area (+ buffer)
                            Rectangle upRect = new Rectangle(playerRect.x + buffer, playerRect.y - entity.getSpeed(), playerRect.width - 2 * buffer, entity.getSpeed());
                            if (upRect.intersects(tileRect)) entity.setCollisionUp(true);


                            // check collision if player moves down INTERSECTS with tile collision area (+ buffer)
                            Rectangle downRect = new Rectangle(playerRect.x + buffer, playerRect.y + playerRect.height, playerRect.width - 2 * buffer, entity.getSpeed());
                            if (downRect.intersects(tileRect)) entity.setCollisionDown(true);


                            // check collision if player moves left INTERSECTS with tile collision area (+ buffer)
                            Rectangle leftRect = new Rectangle(playerRect.x - entity.getSpeed(), playerRect.y + buffer, entity.getSpeed(), playerRect.height - 2 * buffer);
                            if (leftRect.intersects(tileRect)) entity.setCollisionLeft(true);


                            // check collision if player moves right INTERSECTS with tile collision area (+ buffer)
                            Rectangle rightRect = new Rectangle(playerRect.x + playerRect.width, playerRect.y + buffer, entity.getSpeed(), playerRect.height - 2 * buffer);
                            if (rightRect.intersects(tileRect)) entity.setCollisionRight(true);
                        }
                    }
                }
            }
        }
    }

    /* checks if two entities collide */
    public void checkEntityCollision(Entity entity1, Entity entity2) {
        entity1.updateHitbox();
        entity2.updateHitbox();

        // rectangle representing entity1's collisionBox
        Rectangle rect1 = new Rectangle(entity1.getEntityX() + entity1.getCollisionBox().x, entity1.getEntityY() + entity1.getCollisionBox().y, entity1.getCollisionBox().width, entity1.getCollisionBox().height);

        // rectangle representing entity2's collisionBox
        Rectangle rect2 = new Rectangle(entity2.getEntityX() + entity2.getCollisionBox().x, entity2.getEntityY() + entity2.getCollisionBox().y, entity2.getCollisionBox().width, entity2.getCollisionBox().height);

        // buffer value for acceptable overlap
        int buffer = 5;

        // check collision if entity1 moves up INTERSECTS with entity2's collisionBox (+ buffer)
        Rectangle upRect1 = new Rectangle(rect1.x + buffer, rect1.y - entity1.getSpeed(), rect1.width - 2 * buffer, entity1.getSpeed());
        if (upRect1.intersects(rect2)) entity1.setCollisionUp(true);

        // check collision if entity1 moves down INTERSECTS with entity2's collisionBox (+ buffer)
        Rectangle downRect1 = new Rectangle(rect1.x + buffer, rect1.y + rect1.height, rect1.width - 2 * buffer, entity1.getSpeed());
        if (downRect1.intersects(rect2)) entity1.setCollisionDown(true);

        // check collision if entity1 moves left INTERSECTS with entity2's collisionBox (+ buffer)
        Rectangle leftRect1 = new Rectangle(rect1.x - entity1.getSpeed(), rect1.y + buffer, entity1.getSpeed(), rect1.height - 2 * buffer);
        if (leftRect1.intersects(rect2)) entity1.setCollisionLeft(true);

        // check collision if entity1 moves right INTERSECTS with entity2's collisionBox (+ buffer)
        Rectangle rightRect1 = new Rectangle(rect1.x + rect1.width, rect1.y + buffer, entity1.getSpeed(), rect1.height - 2 * buffer);
        if (rightRect1.intersects(rect2)) entity1.setCollisionRight(true);
    }

    /* checks if the attackHitbox of the attacker entity collides with the hit boxes of the target entity */
    public void checkEntityAttack(Entity attacker, Entity target) {

        Rectangle targetVerticalHitBox = new Rectangle(target.getEntityX() + target.getVerticalHitBox().x, target.getEntityY() + target.getVerticalHitBox().y, target.getVerticalHitBox().width,target.getVerticalHitBox().height);
        Rectangle targetHorizontalHitBox = new Rectangle(target.getEntityX() + target.getHorizontalHitBox().x, target.getEntityY() + target.getHorizontalHitBox().y, target.getHorizontalHitBox().width,target.getHorizontalHitBox().height);

        Rectangle attackBox;

        int verticalCenterX = attacker.getVerticalHitBox().x + attacker.getVerticalHitBox().width / 2;
        int verticalCenterY = attacker.getVerticalHitBox().y + attacker.getVerticalHitBox().height / 2;
        int horizontalCenterX = attacker.getHorizontalHitBox().x + attacker.getHorizontalHitBox().width / 2;
        int horizontalCenterY = attacker.getHorizontalHitBox().y + attacker.getHorizontalHitBox().height / 2;

        switch (attacker.getDirection()) {
            case "attackUp":
                attackBox = new Rectangle(attacker.getEntityX() + verticalCenterX - 11 * Settings.getScale() / 2, attacker.getEntityY() + verticalCenterY - 20 * Settings.getScale(), 11 * Settings.getScale(), 20 * Settings.getScale());
                break;
            case "attackDown":
                attackBox = new Rectangle(attacker.getEntityX() + verticalCenterX - 11 * Settings.getScale() / 2, attacker.getEntityY() + verticalCenterY, 11 * Settings.getScale(), 20 * Settings.getScale());
                break;
            case "attackLeft":
                attackBox = new Rectangle(attacker.getEntityX() + horizontalCenterX - 20 * Settings.getScale(), attacker.getEntityY() + horizontalCenterY - 11 * Settings.getScale() / 2, 20 * Settings.getScale(), 11 * Settings.getScale());
                break;
            case "attackRight":
                attackBox = new Rectangle(attacker.getEntityX() + horizontalCenterX, attacker.getEntityY() + horizontalCenterY - 11 * Settings.getScale() / 2, 20 * Settings.getScale(), 11 * Settings.getScale());
                break;
            default:
                return;
        }

        if (attackBox.intersects(targetVerticalHitBox) || attackBox.intersects(targetHorizontalHitBox)) {
            System.out.println("Entity hit!");
            target.health -= 4;
            System.out.println(target + ":" + target.health);
        }
    }
}
