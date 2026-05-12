package model.exceptions;

public class MissionDejaAffecteeException extends RuntimeException {
    public MissionDejaAffecteeException(String missionId) {
        super("La mission " + missionId + " est déjà affectée.");
    }
}
