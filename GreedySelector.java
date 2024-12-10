import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

// This is an example strategy which could not solve many cases.
// Try develop other strategies

public class GreedySelector implements SpellsSelector {
    @Override
    public void optimize(SpellTableModel spells, int mana, int bossMaxHP) {
        List<Integer> indices = new ArrayList<>();

        // populate indices
        for (int i = 0; i < spells.getRowCount(); ++i)
            indices.add(i);
        
        // sort indices by DMG dealt descending
        indices.sort(new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                return spells.getSpell(b).dmgDeal() - spells.getSpell(a).dmgDeal();
            }
        });

        // greedily choose spells
        int accumulatedCost = 0;
        for (Integer idx : indices) {
            Spell theSpell = spells.getSpell(idx);
            if (theSpell.cost() <= mana - accumulatedCost) { // if we have some rooms left
                spells.select(idx); // mark the spell as selected
                accumulatedCost += theSpell.cost(); // accumulate cost of the spell
                System.out.println("Selected " + theSpell.name() + " at " + idx);
            }
            else
                spells.unselect(idx); // unselect the rest
        }
    }
}
