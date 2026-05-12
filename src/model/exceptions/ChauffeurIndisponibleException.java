package model.exceptions;

public class ChauffeurIndisponibleException extends RuntimeException {
    private final String chauffeurId;
    public ChauffeurIndisponibleException(String chauffeurId) {
        super("Le chauffeur " + chauffeurId + " n'est pas disponible.");
        this.chauffeurId = chauffeurId;
    }
    public String getChauffeurId() { return chauffeurId; }
}
