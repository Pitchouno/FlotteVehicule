package util;

import java.io.Serializable;

/**
 * Classe de base pour toute entité métier avec un identifiant unique.
 * Toutes les classes stockées dans un Registre doivent hériter de cette classe.
 */
public abstract class Entite implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;

    public Entite(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant ne peut pas être vide.");
        }
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entite)) return false;
        return id.equals(((Entite) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
