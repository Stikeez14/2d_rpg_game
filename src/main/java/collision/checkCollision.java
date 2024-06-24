package collision;

import frame.Panel;
import entities.malePlayer;
import map.mapSettings;

public class checkCollision {

    Panel gamePanel;

    public checkCollision(Panel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void checkTile(malePlayer player){

        int entityLeftX = player.x + player.hitbox.x;
        int entityRightX = player.x + player.hitbox.x + player.hitbox.width;
        int entityTopY = player.y + player.hitbox.y;
        int entityBottomY = player.y + player.hitbox.y + player.hitbox.height;

        int entityLeft = entityLeftX / mapSettings.getTileSize();
        int entityRight = entityRightX / mapSettings.getTileSize();
        int entityTop = entityTopY / mapSettings.getTileSize();
        int entityBottom = entityBottomY / mapSettings.getTileSize();

        int tileNum1, tileNum2;

        switch (player.direction) {
            case "down":
                entityBottom = (entityBottomY + player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityLeft][entityBottom];
                tileNum2 = gamePanel.map.mapTileArray[entityRight][entityBottom];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "up":
                entityTop = (entityTopY - player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityLeft][entityTop];
                tileNum2 = gamePanel.map.mapTileArray[entityRight][entityTop];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "left":
                entityLeft = (entityLeftX - player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityLeft][entityTop];
                tileNum2 = gamePanel.map.mapTileArray[entityLeft][entityBottom];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "right":
                entityRight = (entityRightX + player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityRight][entityTop];
                tileNum2 = gamePanel.map.mapTileArray[entityRight][entityBottom];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "down&left":
                entityBottom = (entityBottomY + player.Speed) / mapSettings.getTileSize();
                entityLeft = (entityLeftX - player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityLeft][entityBottom];
                tileNum2 = gamePanel.map.mapTileArray[entityLeft][entityTop];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "down&right":
                entityBottom = (entityBottomY + player.Speed) / mapSettings.getTileSize();
                entityRight = (entityRightX + player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityRight][entityBottom];
                tileNum2 = gamePanel.map.mapTileArray[entityRight][entityTop];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "up&left":
                entityTop = (entityTopY - player.Speed) / mapSettings.getTileSize();
                entityLeft = (entityLeftX - player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityLeft][entityTop];
                tileNum2 = gamePanel.map.mapTileArray[entityLeft][entityBottom];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
            case "up&right":
                entityTop = (entityTopY - player.Speed) / mapSettings.getTileSize();
                entityRight = (entityRightX + player.Speed) / mapSettings.getTileSize();
                tileNum1 = gamePanel.map.mapTileArray[entityRight][entityTop];
                tileNum2 = gamePanel.map.mapTileArray[entityRight][entityBottom];
                if (gamePanel.map.tiles[tileNum1].collision || gamePanel.map.tiles[tileNum2].collision) player.collision = true;
                break;
        }
    }
}
