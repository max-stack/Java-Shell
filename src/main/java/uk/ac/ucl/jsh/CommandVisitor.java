package uk.ac.ucl.jsh;

import java.util.ArrayList;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CommandVisitor extends AbstractParseTreeVisitor<ExecutionPlan> {

    /**
     * This method takes the current node when walking the tree and if
     * it is a terminal, an ExecutionPlan is returned so it can be built up
     * to form a command segment.
     *
     * @param node Current node of the tree when visiting.
     *
     * @return ExecutionPlan that will be joined to the command queue.
     *
     */

    @Override
    public ExecutionPlan visitTerminal(TerminalNode node) {
        switch (node.getText()) {
            case ";":
                return new ExecutionPlan(ConnectionType.SEQUENCE);
            case "|":
                return new ExecutionPlan(ConnectionType.PIPE);
            case ">":
                return new ExecutionPlan(ConnectionType.REDIRECT_TO);
            case "<":
                return new ExecutionPlan(ConnectionType.REDIRECT_FROM);
            case "`":
                return new ExecutionPlan(ConnectionType.SUBSTITUTION);
            default:
                throw new RuntimeException(
                    "Something is wrong with the grammar."
                );
        }
    }

    @Override
    public ExecutionPlan visitChildren(RuleNode node) {
        if (
            node.getRuleContext().getRuleIndex() ==
            JshGrammarParser.RULE_atomicCommand
        ) {
            AtomicCommandVisitor atomicVisitor = new AtomicCommandVisitor();
            ArrayList<String> args = atomicVisitor.visitChildren(node);
            return new ExecutionPlan(args.toArray(new String[args.size()]));
        }

        ExecutionPlan plan = new ExecutionPlan();
        for (int i = 0; i < node.getChildCount(); i++) {
            plan.join(this.visit(node.getChild(i)));
        }
        String[] endCommands = { "£" };
        plan.join(new ExecutionPlan(endCommands));
        return plan;
    }
}
