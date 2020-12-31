package uk.ac.ucl.jsh;

import java.util.ArrayList; 
import java.util.Queue;
import java.util.LinkedList;

class ExecutionPlan{
    Queue<String> commands = new LinkedList<>();
    LinkedList<String> subCommands = new LinkedList<>();
    boolean prevTerminal = false;

    public ExecutionPlan(String[] args){
        for(String arg : args){
            commands.add(arg);
        }
    }

    public ExecutionPlan(ConnectionType connection){
        commands.add(connection.toString());
    }

    public ExecutionPlan(){

    }

    public Queue<String> getCommandQueue(){
        return commands;
    }

    public void join(ExecutionPlan joinPlan){
        String topElement = joinPlan.getCommandQueue().peek();
        // ConnectionType type = ConnectionType.valueOf(topElement);
        // switch(type){
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

        if(topElement == ConnectionType.SEQUENCE.toString()){
            commands.addAll(subCommands);
            subCommands.clear();
            commands.addAll(joinPlan.getCommandQueue());
        }
<<<<<<< HEAD
        else if(topElement == ConnectionType.PIPE.toString() ||
=======
        else if(topElement == ConnectionType.PIPE.toString() || 
>>>>>>> ff8a819fb563fcdb246ba49a44d2a3c173d3a449
                topElement == ConnectionType.REDIRECT_FROM.toString() ||
                topElement == ConnectionType.REDIRECT_TO.toString()){
            commands.addAll(joinPlan.getCommandQueue());
            commands.addAll(subCommands);
            subCommands.clear();
        }
        else if(topElement == ConnectionType.END_COMMAND.toString()){
            commands.addAll(subCommands);
            subCommands.clear();
        }
        else{
            subCommands.addAll(joinPlan.getCommandQueue());
        }
    }
}