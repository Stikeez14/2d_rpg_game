package collision;

import frame.Panel;
import entities.malePlayer;
import map.mapSettings;
import map.Tile;

import java.awt.*;

public class checkCollision {

    Panel gamePanel;

    public checkCollision(Panel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void checkTile(malePlayer player) {
        player.collisionUp = false;
        player.collisionDown = false;
        player.collisionLeft = false;
        player.collisionRight = false;

        Rectangle playerRect = new Rectangle(player.x + player.hitbox.x, player.y + player.hitbox.y, player.hitbox.width, player.hitbox.height);
        int tileSize = mapSettings.getTileSize();

        // calculate the range of tiles to check based on the player's position and speed
        int startX = Math.max((player.x + player.hitbox.x - player.Speed) / tileSize, 0);
        int endX = Math.min((player.x + player.hitbox.x + player.hitbox.width + player.Speed) / tileSize, mapSettings.getMaxTilesHorizontally() - 1);
        int startY = Math.max((player.y + player.hitbox.y - player.Speed) / tileSize, 0);
        int endY = Math.min((player.y + player.hitbox.y + player.hitbox.height + player.Speed) / tileSize, mapSettings.getMaxTilesVertically() - 1);

        // check for collision in the optimized range
        for (int tileY = startY; tileY <= endY; tileY++) {
            for (int tileX = startX; tileX <= endX; tileX++) {
                int tileNum = gamePanel.map.mapTileArray[tileX][tileY];

                if (tileNum >= 0 && tileNum < gamePanel.map.tiles.length) {
                    Tile tile = gamePanel.map.tiles[tileNum];

                    if (tile != null && tile.collision) {
                        for (Rectangle rect : tile.collisionAreas) {
                            Rectangle tileRect = new Rectangle(tileX * tileSize + rect.x, tileY * tileSize + rect.y, rect.width, rect.height);

                            // check collision if player moves up
                            Rectangle upRect = new Rectangle(playerRect.x, playerRect.y - player.Speed, playerRect.width, player.Speed);
                            if (upRect.intersects(tileRect)) {
                                player.collisionUp = true;
                            }

                            // check collision if player moves down
                            Rectangle downRect = new Rectangle(playerRect.x, playerRect.y + playerRect.height, playerRect.width, player.Speed);
                            if (downRect.intersects(tileRect)) {
                                player.collisionDown = true;
                            }

                            // check collision if player moves left
                            Rectangle leftRect = new Rectangle(playerRect.x - player.Speed, playerRect.y, player.Speed, playerRect.height);
                            if (leftRect.intersects(tileRect)) {
                                player.collisionLeft = true;
                            }

                            // check collision if player moves right
                            Rectangle rightRect = new Rectangle(playerRect.x + playerRect.width, playerRect.y, player.Speed, playerRect.height);
                            if (rightRect.intersects(tileRect)) {
                                player.collisionRight = true;
                            }
                        }
                    }
                }
            }
        }
    }
}
