public interface SpellsSelector {
    /** This method should mark spell as selected based on specific strategy.
     * 
     * @param spells contains spells, could be marked as selected or unselected using its related methods
     * @param mana the mana your character have, this method should ensure accumulated cost is not exceed
     * @param bossMaxHP max health point of the boss
     */
    public void optimize(SpellTableModel spells, int mana, int bossMaxHP);
}