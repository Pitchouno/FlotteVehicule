package view;

import model.abstracts.Mission;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class MissionTableModel extends AbstractTableModel {

    private static final String[] COLONNES = {
            "ID", "Départ", "Destination", "Distance", "Type", "Statut", "Coût estimé", "Véhicule", "Chauffeur"
    };

    private List<Mission> missions;

    public MissionTableModel(List<Mission> missions) {
        this.missions = missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
        fireTableDataChanged();
    }

    public Mission getMissionAt(int row) {
        return missions.get(row);
    }

    @Override public int getRowCount()    { return missions.size(); }
    @Override public int getColumnCount() { return COLONNES.length; }
    @Override public String getColumnName(int col) { return COLONNES[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Mission m = missions.get(row);
        return switch (col) {
            case 0 -> m.getId();
            case 1 -> m.getDepart();
            case 2 -> m.getDestination();
            case 3 -> String.format("%.0f km", m.getDistanceKm());
            case 4 -> (m instanceof model.impl.MissionLongue) ? "Longue" : "Courte";
            case 5 -> m.getStatut().name();
            case 6 -> String.format("%.2f €", m.calculerCout());
            case 7 -> m.getVehiculeAssigneId()  != null ? m.getVehiculeAssigneId()  : "—";
            case 8 -> m.getChauffeurAssigneId() != null ? m.getChauffeurAssigneId() : "—";
            default -> "";
        };
    }
}
