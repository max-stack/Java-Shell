package uk.ac.ucl.jsh;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.commons.lang3.StringUtils;

class ExecutionPlan {

    Queue<String> commands = new LinkedList<>();
    LinkedList<String> subCommands = new LinkedList<>();
    boolean prevTerminal = false;
    boolean findNextQuote = false;
    String substitutionCommand = null;

    public ExecutionPlan(String[] args) {
        for (String arg : args) {
            commands.add(arg);
        }
    }

    public ExecutionPlan(ConnectionType connection) {
        commands.add(connection.toString());
    }

    public ExecutionPlan() {}

    public Queue<String> getCommandQueue() {
        return commands;
    }

    /**
     * This method is invoked by ANTLR when it is walking the tree of a
     * command and collating it's arguments together until a terminal is found.
     * The method then takes that command and adds it to the command queue based
     * on what type of command it is.
     *
     * @param joinPlan group arguments from ANTLR which need to be added to the
     * command queue.
     *
     */
    public void join(ExecutionPlan joinPlan) {
        String topElement = joinPlan.getCommandQueue().peek();
        if (topElement.equals(" ")) {
            return;
        }
        if (topElement.equals(ConnectionType.SEQUENCE.toString()) ||
            topElement.equals(ConnectionType.PIPE.toString()) ||
            topElement.equals(ConnectionType.REDIRECT_FROM.toString()) ||
            topElement.equals(ConnectionType.REDIRECT_TO.toString())) {

            if ( findNextQuote || !subCommands.isEmpty() && 
                (StringUtils.countMatches(subCommands.getLast(), "\"") == 1 || 
                StringUtils.countMatches(subCommands.getLast(), "'") == 1)){

                findNextQuote = true;
                subCommands.add(subCommands.removeLast() + topElement);
            } else {
                commands.addAll(subCommands);
                subCommands.clear();
                commands.addAll(joinPlan.getCommandQueue());
            }
        } else if (topElement.equals(ConnectionType.SUBSTITUTION.toString())) {
            if (substitutionCommand == null) {
                if (!subCommands.isEmpty()) {
                    substitutionCommand = subCommands.remove();
                } else {
                    commands.add("appsub");
                    substitutionCommand = "";
                }
                commands.addAll(joinPlan.getCommandQueue());
            } else {
                commands.addAll(subCommands);
                subCommands.clear();
                commands.addAll(joinPlan.getCommandQueue());
                if (substitutionCommand != "") {
                    commands.add(substitutionCommand);
                }
                substitutionCommand = null;
            }
        } else if (topElement.equals(ConnectionType.END_COMMAND.toString())) {
            commands.addAll(subCommands);
            subCommands.clear();
        } else {
            if (findNextQuote) {
                subCommands.add(subCommands.removeLast() + topElement);
                if (StringUtils.countMatches(topElement, "\"") == 1 ||
                    StringUtils.countMatches(topElement, "'") == 1) {
                        
                    findNextQuote = false;
                }
            } else {
                subCommands.addAll(joinPlan.getCommandQueue());
            }
        }
    }
}
