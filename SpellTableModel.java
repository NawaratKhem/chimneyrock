import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.table.*;

public class SpellTableModel extends AbstractTableModel {
    private List<SelectableSpell> data;
    private Set<Integer> checked = new TreeSet<Integer>();

    public SpellTableModel() {
        data = new ArrayList<>();
    }

    public void add(Spell spell) {
        int rowCount = getRowCount();
        data.add(new SelectableSpell(spell));
        fireTableRowsInserted(rowCount, rowCount);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 1:
                return "Name";
            case 2:
                return "Mana Cost";
            case 3:
                return "DMG";
            default:
                return "";
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col == 1) return data.get(row).getSpell().name();
        if (col == 2) return data.get(row).getSpell().cost();
        if (col == 3) return data.get(row).getSpell().dmgDeal();
        return data.get(row).isSelected();
    }

    public Spell getSpell(int row) {
        return data.get(row).getSpell();
    }

    public    void     select(int row) { setValueAt(true, row, row); }
    public    void   unselect(int row) { setValueAt(false, row, row); }
    public boolean isSelected(int row) { return data.get(row).isSelected(); }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
        boolean b = (Boolean) aValue;
        
        if (b) {
            data.get(row).select();
            checked.add(row);
        } else {
            data.get(row).unselect();
            checked.remove(row);
        }

        fireTableRowsUpdated(row, row);
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    public void clearSelection() {
        for (SelectableSpell spell : data) {
            spell.unselect();
        }
    }

    public void selectSpell(SelectableSpell spell) {
        for(SelectableSpell theSpell : data) {
            if(spell == theSpell){
                theSpell.select();
            }
        }
    }

    public List<SelectableSpell> getSpells() {
        return data;
    }
}