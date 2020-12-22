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
        if(ConnectionType.connectionExists(joinPlan.getCommandQueue().peek())){
            commands.addAll(joinPlan.getCommandQueue());
            commands.addAll(subCommands);
            subCommands.clear();
        }
        else if(joinPlan.getCommandQueue().peek().equals("£")){
            commands.addAll(subCommands);
            subCommands.clear();
        }
        else{
            subCommands.addAll(joinPlan.getCommandQueue());
        }
    }
}