public class SelectableSpell {
    private final Spell spell;
    boolean selected;

    public SelectableSpell(Spell spell) {
        this.spell = spell;
        this.selected = false;
    }

    public SelectableSpell(Spell spell, boolean selected) {
        this.spell = spell;
        this.selected = selected;
    }

    public Spell getSpell() {
        return this.spell;
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        this.selected = true;
    }

    public void unselect() {
        this.selected = false;
    }
}
