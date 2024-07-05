package items.equipment.armour;

public class EternalArmour extends Armour{

    private final double helmetDurability = 100;
    private final double chestPlateDurability = 100;
    private final double leggingsDurability = 100;

    @Override
    public double getHelmetDurability() {
        return helmetDurability;
    }

    @Override
    public double getChestplateDurability() {
        return chestPlateDurability;
    }

    @Override
    public double getLeggingsDurability() {
        return leggingsDurability;
    }
}
