package items.equipment.armour;

public class eternalArmour extends Armour{

    private final double helmetDurability = 100;
    private final double chestplateDurability = 100;
    private final double leggingsDurability = 100;

    @Override
    public double getHelmetDurability() {
        return helmetDurability;
    }

    @Override
    public double getChestplateDurability() {
        return chestplateDurability;
    }

    @Override
    public double getLeggingsDurability() {
        return leggingsDurability;
    }
}
