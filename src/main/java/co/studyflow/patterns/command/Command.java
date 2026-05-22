package co.studyflow.patterns.command;

/**
 * Interfaz Command para operaciones reversibles
 */
public interface Command {
    void execute();
    void undo();
    String getDescripcion();
}
