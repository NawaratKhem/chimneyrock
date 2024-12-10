import java.util.ArrayList;
import java.util.List;

public class MaximizeDmgSelector implements SpellsSelector {

    @Override
    public void optimize(SpellTableModel spells, int mana, int bossMaxHP) {
        // Step 1: Get the spells from the model
        List<Spell> spellList = new ArrayList<>();
        int totalSpells = spells.getRowCount();
        for (int i = 0; i < totalSpells; i++) {
            spellList.add(spells.getSpell(i));
        }

        // Step 2: Apply the Knapsack algorithm to maximize damage
        List<Spell> selectedSpells = knapsackAlgorithm(spellList, mana);

        // Step 3: Clear current selections in the table
        spells.clearSelection();

        // Step 4: Mark the selected spells
        for (Spell selectedSpell : selectedSpells) {
            for (int i = 0; i < totalSpells; i++) {
                if (spells.getSpell(i).equals(selectedSpell)) {
                    spells.select(i); // Mark the spell as selected in the model
                    break;
                }
            }
        }
    }

    /**
     * Knapsack algorithm to maximize damage given mana cap
     *
     * @param spells  List of available spells
     * @param manaCap The maximum mana available
     * @return List of selected spells for max damage
     */
    private List<Spell> knapsackAlgorithm(List<Spell> spells, int manaCap) {
        int n = spells.size();
        if (n == 0 || manaCap <= 0) {
            return new ArrayList<>();
        }

        // Step 1: Initialize DP table and keep table
        int[][] dp = new int[n + 1][manaCap + 1];
        
        // Step 2: Fill the DP table
        for (int i = 1; i <= n; i++) {
            Spell currentSpell = spells.get(i - 1);
            int manaCost = currentSpell.cost();
            int damage = currentSpell.dmgDeal();

            for (int j = 0; j <= manaCap; j++) {
                // Default: don't use current spell
                dp[i][j] = dp[i - 1][j];

                // Try to use current spell if possible
                if (manaCost <= j) {
                    int potentialDamage = dp[i - 1][j - manaCost] + damage;
                    dp[i][j] = Math.max(dp[i][j], potentialDamage);
                }
            }
        }

        // Step 3: Backtrack to find the selected spells
        List<Spell> selectedSpells = new ArrayList<>();
        int remainingMana = manaCap;

        // Work backwards to find which spells were selected
        for (int i = n; i > 0 && remainingMana > 0; i--) {
            Spell currentSpell = spells.get(i - 1);
            int manaCost = currentSpell.cost();
            int damage = currentSpell.dmgDeal();

            // Check if this spell was key to achieving the max damage
            if (manaCost <= remainingMana && 
                dp[i][remainingMana] == dp[i-1][remainingMana - manaCost] + damage) {
                selectedSpells.add(0, currentSpell);
                remainingMana -= manaCost;
            }
        }

        return selectedSpells;
    }
}