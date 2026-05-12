package view;

import model.impl.Chauffeur;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ChauffeurTableModel extends AbstractTableModel {

    private static final String[] COLONNES = {
            "ID", "Nom", "Prénom", "N° Permis", "Catégorie", "Disponible", "Missions effectuées"
    };

    private List<Chauffeur> chauffeurs;

    public ChauffeurTableModel(List<Chauffeur> chauffeurs) {
        this.chauffeurs = chauffeurs;
    }

    public void setChauffeurs(List<Chauffeur> chauffeurs) {
        this.chauffeurs = chauffeurs;
        fireTableDataChanged();
    }

    public Chauffeur getChauffeurAt(int row) {
        return chauffeurs.get(row);
    }

    @Override public int getRowCount()    { return chauffeurs.size(); }
    @Override public int getColumnCount() { return COLONNES.length; }
    @Override public String getColumnName(int col) { return COLONNES[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Chauffeur c = chauffeurs.get(row);
        return switch (col) {
            case 0 -> c.getId();
            case 1 -> c.getNom();
            case 2 -> c.getPrenom();
            case 3 -> c.getNumeroPermis();
            case 4 -> c.getCategoriePermis();
            case 5 -> c.estDisponible() ? "✓ Oui" : "✗ Non";
            case 6 -> c.getNombreMissions();
            default -> "";
        };
    }
}
