package uk.ac.ucl.jsh;

import java.util.ArrayList; 
import java.util.Queue;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;

class ExecutionPlan {
    Queue<String> commands = new LinkedList<>();
    LinkedList<String> subCommands = new LinkedList<>();
    boolean prevTerminal = false;
<<<<<<< HEAD
    boolean findNextQuote = false;
=======
>>>>>>> cdc64fd61f6e83d3d0938dee2b2417b53a3c9750
    String substitutionCommand = null;

    public ExecutionPlan(String[] args) {
        for (String arg : args) {
            commands.add(arg);
        }
    }

    public ExecutionPlan(ConnectionType connection) {
        commands.add(connection.toString());
    }

    public ExecutionPlan() {

    }

    public Queue<String> getCommandQueue() {
        return commands;
    }

    public void join(ExecutionPlan joinPlan) {
        String topElement = joinPlan.getCommandQueue().peek();
        if(topElement.equals(" ")){
            return;
        }
        // ConnectionType type = ConnectionType.valueOf(topElement);
        // switch (type) {
        //     case SEQUENCE:
        //         commands.addAll(subCommands);
        //         subCommands.clear();
        //         commands.addAll(joinPlan.getCommandQueue());
        //         break;
        //     case PIPE:
        //     case REDIRECT_FROM:
        //     case REDIRECT_TO:
        //         commands.addAll(joinPlan.getCommandQueue());
        //         commands.addAll(subCommands);
        //         subCommands.clear();
        //         break;
        //     case END_COMMAND:
        //         commands.addAll(subCommands);
        //         subCommands.clear();
        //         break;
        //     default:
        //         subCommands.addAll(joinPlan.getCommandQueue());
        // }
        if(topElement == ConnectionType.SEQUENCE.toString() ||
           topElement == ConnectionType.PIPE.toString() ||
           topElement == ConnectionType.REDIRECT_FROM.toString() ||
<<<<<<< HEAD
           topElement == ConnectionType.REDIRECT_TO.toString()){          
            if(findNextQuote || ( !subCommands.isEmpty() && 
               (StringUtils.countMatches(subCommands.getLast(), "\"") == 1 ||
                StringUtils.countMatches(subCommands.getLast(), "'") == 1))){
                findNextQuote = true;
                subCommands.add(subCommands.removeLast() + topElement);
            }
            else{
                commands.addAll(subCommands);
                subCommands.clear();
                commands.addAll(joinPlan.getCommandQueue());
            }
        }
        else if(topElement == ConnectionType.SUBSTITUTION.toString()){
            if(substitutionCommand == null){
                if(!subCommands.isEmpty()){
                    substitutionCommand = subCommands.remove();
                }
                else{
                    commands.add("appsub");
                    substitutionCommand = "";
                }
=======
           topElement == ConnectionType.REDIRECT_TO.toString()){
            commands.addAll(subCommands);
            subCommands.clear();
            commands.addAll(joinPlan.getCommandQueue());
        }
        else if(topElement == ConnectionType.SUBSTITUTION.toString()){
            if(substitutionCommand == null){
                substitutionCommand = subCommands.remove();
>>>>>>> cdc64fd61f6e83d3d0938dee2b2417b53a3c9750
                commands.addAll(joinPlan.getCommandQueue());
            }
            else{
                commands.addAll(subCommands);
                subCommands.clear();
                commands.addAll(joinPlan.getCommandQueue());
<<<<<<< HEAD
                if(substitutionCommand != ""){
                    commands.add(substitutionCommand);
                }
=======
                commands.add(substitutionCommand);
>>>>>>> cdc64fd61f6e83d3d0938dee2b2417b53a3c9750
                substitutionCommand = null;
            }
        }
        else if(topElement == ConnectionType.END_COMMAND.toString()){
            commands.addAll(subCommands);
            subCommands.clear();
        }
        else {
            if(findNextQuote){
                subCommands.add(subCommands.removeLast() + topElement);
                if(StringUtils.countMatches(topElement, "\"") == 1 ||
                   StringUtils.countMatches(topElement, "'") == 1){
                    findNextQuote = false;
                }
            }
            else{
                subCommands.addAll(joinPlan.getCommandQueue());
            }
        }
    }
}