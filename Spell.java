import java.util.Objects;

public record Spell(String name, int cost, int dmgDeal) {
    public Spell {
        Objects.requireNonNull(name);
        Objects.requireNonNull(cost);
        Objects.requireNonNull(dmgDeal);
    }
}
