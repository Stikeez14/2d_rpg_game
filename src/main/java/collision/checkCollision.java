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

        for (int row = -1; row <= 1; row++) { // check for collision in each of the directions
            for (int col = -1; col <= 1; col++) {
                int tileX = (player.x + player.hitbox.x) / tileSize + col;
                int tileY = (player.y + player.hitbox.y) / tileSize + row;

                if (tileX >= 0 && tileX < mapSettings.getMaxTilesHorizontally() && tileY >= 0 && tileY < mapSettings.getMaxTilesVertically()) {
                    int tileNum = gamePanel.map.mapTileArray[tileX][tileY];

                    if (tileNum >= 0 && tileNum < gamePanel.map.tiles.length) {
                        Tile tile = gamePanel.map.tiles[tileNum];

                        if (tile != null && tile.collision) {
                            for (Rectangle rect : tile.collisionAreas) {
                                Rectangle tileRect = new Rectangle(tileX * tileSize + rect.x, tileY * tileSize + rect.y, rect.width, rect.height);

                                // check collision if player moves up
                                if (playerRect.y - player.Speed < tileRect.y + tileRect.height && playerRect.y >= tileRect.y + tileRect.height &&
                                        playerRect.x + playerRect.width > tileRect.x && playerRect.x < tileRect.x + tileRect.width) {
                                    player.collisionUp = true;
                                }

                                // check collision if player moves down
                                if (playerRect.y + playerRect.height + player.Speed > tileRect.y && playerRect.y + playerRect.height <= tileRect.y &&
                                        playerRect.x + playerRect.width > tileRect.x && playerRect.x < tileRect.x + tileRect.width) {
                                    player.collisionDown = true;
                                }

                                // check collision if player moves left
                                if (playerRect.x - player.Speed < tileRect.x + tileRect.width && playerRect.x >= tileRect.x + tileRect.width &&
                                        playerRect.y + playerRect.height > tileRect.y && playerRect.y < tileRect.y + tileRect.height) {
                                    player.collisionLeft = true;
                                }

                                // check collision if player moves right
                                if (playerRect.x + playerRect.width + player.Speed > tileRect.x && playerRect.x + playerRect.width <= tileRect.x &&
                                        playerRect.y + playerRect.height > tileRect.y && playerRect.y < tileRect.y + tileRect.height) {
                                    player.collisionRight = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
