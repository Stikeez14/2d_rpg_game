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

        // rectangle representing player's hitbox
        Rectangle playerRect = new Rectangle(entity.getEntityX() + entity.getHitbox().x, entity.getEntityY() + entity.getHitbox().y, entity.getHitbox().width, entity.getHitbox().height);

        int tileSize = Settings.getTileSize();

        // range of tiles to check based on the player's position and speed
        int startX = Math.max((entity.getEntityX() + entity.getHitbox().x - entity.getSpeed()) / tileSize, 0);
        int endX = Math.min((entity.getEntityX() + entity.getHitbox().x + entity.getHitbox().width + entity.getSpeed()) / tileSize, Settings.getMaxTilesHorizontally() - 1);
        int startY = Math.max((entity.getEntityY() + entity.getHitbox().y - entity.getSpeed()) / tileSize, 0);
        int endY = Math.min((entity.getEntityY() + entity.getHitbox().y + entity.getHitbox().height + entity.getSpeed()) / tileSize, Settings.getMaxTilesVertically() - 1);

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
}
