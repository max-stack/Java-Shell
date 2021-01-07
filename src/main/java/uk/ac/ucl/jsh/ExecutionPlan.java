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

    public void join(ExecutionPlan joinPlan) {
        String topElement = joinPlan.getCommandQueue().peek();
        if (topElement.equals(" ")) {
            return;
        }
        if (
            topElement == ConnectionType.SEQUENCE.toString() ||
            topElement == ConnectionType.PIPE.toString() ||
            topElement == ConnectionType.REDIRECT_FROM.toString() ||
            topElement == ConnectionType.REDIRECT_TO.toString()
        ) {
            if ( findNextQuote || !subCommands.isEmpty() && (StringUtils.countMatches(subCommands.getLast(), "\"") == 1 || StringUtils.countMatches(subCommands.getLast(), "'") == 1))
            {
                findNextQuote = true;
                subCommands.add(subCommands.removeLast() + topElement);
            } else {
                commands.addAll(subCommands);
                subCommands.clear();
                commands.addAll(joinPlan.getCommandQueue());
            }
        } else if (topElement == ConnectionType.SUBSTITUTION.toString()) {
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
        } else if (topElement == ConnectionType.END_COMMAND.toString()) {
            commands.addAll(subCommands);
            subCommands.clear();
        } else {
            if (findNextQuote) {
                subCommands.add(subCommands.removeLast() + topElement);
                if (
                    StringUtils.countMatches(topElement, "\"") == 1 ||
                    StringUtils.countMatches(topElement, "'") == 1
                ) {
                    findNextQuote = false;
                }
            } else {
                subCommands.addAll(joinPlan.getCommandQueue());
            }
        }
    }
}
