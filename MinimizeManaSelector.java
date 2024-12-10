import java.util.ArrayList;
import java.util.List;

public class MinimizeManaSelector implements SpellsSelector {

    @Override
    public void optimize(SpellTableModel spells, int mana, int bossMaxHP) {
        // Step 1: Get the spells from the model
        List<Spell> spellList = new ArrayList<>();
        int totalSpells = spells.getRowCount();
        for (int i = 0; i < totalSpells; i++) {
            spellList.add(spells.getSpell(i));
        }

        // Step 2: Apply the selection algorithm to minimize mana cost
        List<Spell> selectedSpells = minimizeMana(spellList, mana, bossMaxHP);

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
     * Algorithm to minimize mana cost while meeting or exceeding boss's HP
     *
     * @param spells    List of available spells
     * @param manaCap   The maximum mana available
     * @param bossMaxHP The boss's maximum HP to reach
     * @return List of selected spells to minimize mana cost
     */
    private List<Spell> minimizeMana(List<Spell> spells, int manaCap, int bossMaxHP) {
        int n = spells.size();
        if (n == 0 || bossMaxHP <= 0) {
            return new ArrayList<>();
        }
    
        // 2D DP array: rows are spells, columns are damage points
        // dp[i][j] represents the minimum mana to deal j damage using first i spells
        int[][] dp = new int[n + 1][bossMaxHP + 1];
        
        // Initialize with a large value
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= bossMaxHP; j++) {
                dp[i][j] = Integer.MAX_VALUE / 2;
            }
            dp[i][0] = 0; // 0 mana to deal 0 damage
        }
    
        // Build the DP table
        for (int i = 1; i <= n; i++) {
            Spell spell = spells.get(i - 1);
            int spellDamage = spell.dmgDeal();
            int spellCost = spell.cost();
    
            for (int j = 0; j <= bossMaxHP; j++) {
                // Option 1: Don't use this spell
                dp[i][j] = dp[i-1][j];
    
                // Option 2: Use this spell if it helps
                if (j >= spellDamage) {
                    // Compare current value with mana cost of using this spell
                    int prevDamagePointMana = dp[i-1][Math.max(0, j - spellDamage)];
                    if (prevDamagePointMana != Integer.MAX_VALUE / 2) {
                        dp[i][j] = Math.min(dp[i][j], prevDamagePointMana + spellCost);
                    }
                }
            }
        }
    
        // Find the minimum mana to deal at least bossMaxHP damage
        int minMana = Integer.MAX_VALUE;
        int minManaIndex = -1;
        for (int j = bossMaxHP; j <= bossMaxHP; j++) {
            if (dp[n][j] < minMana && dp[n][j] <= manaCap) {
                minMana = dp[n][j];
                minManaIndex = j;
            }
        }
    
        // If no solution found
        if (minManaIndex == -1) {
            return new ArrayList<>();
        }
    
        // Backtrack to find selected spells
        List<Spell> selectedSpells = new ArrayList<>();
        int remainingDamage = minManaIndex;
        for (int i = n; i > 0 && remainingDamage > 0; i--) {
            Spell spell = spells.get(i - 1);
            int spellDamage = spell.dmgDeal();
            int spellCost = spell.cost();
    
            // Check if this spell was used
            if (remainingDamage >= spellDamage && 
                dp[i][remainingDamage] == 
                dp[i-1][Math.max(0, remainingDamage - spellDamage)] + spellCost) {
                
                selectedSpells.add(0, spell);
                remainingDamage -= spellDamage;
            }
        }
    
        return selectedSpells;
    }
}