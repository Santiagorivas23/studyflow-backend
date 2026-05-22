package co.studyflow.patterns.command;

import java.util.Stack;

/**
 * Gestor de comandos que mantiene un stack de Undo/Redo
 * Implementa Singleton para garantizar una única instancia por sesión
 */
public class CommandManager {
    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;
    private static final int MAX_COMMANDS = 50;
    
    public CommandManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }
    
    public void execute(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Limpiar redo cuando ejecutamos un nuevo comando
        
        // Limitar tamaño del stack
        if (undoStack.size() > MAX_COMMANDS) {
            undoStack.remove(0);
        }
    }
    
    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
    
    public boolean puedoUndo() {
        return !undoStack.isEmpty();
    }
    
    public boolean puedoRedo() {
        return !redoStack.isEmpty();
    }
    
    public String obtenerDescripcionUltimo() {
        return undoStack.isEmpty() ? "" : undoStack.peek().getDescripcion();
    }
    
    public void limpiar() {
        undoStack.clear();
        redoStack.clear();
    }
}
