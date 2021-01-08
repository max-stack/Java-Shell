package uk.ac.ucl.jsh;

import java.util.ArrayList;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class AtomicCommandVisitor
    extends AbstractParseTreeVisitor<ArrayList<String>> {

    /**
     * This method takes the current node when the rule from the grammar
     * is an atomic command. It returns an ArrayList which will form the arguments
     * of the command.
     *
     * @param node Current node of the tree when visiting.
     *
     * @return ArrayList of arguments for specific command.
     *
     */
    @Override
    public ArrayList<String> visitTerminal(TerminalNode node) {
        ArrayList<String> list = new ArrayList();
        list.add(node.getText());
        return list;
    }

    @Override
    public ArrayList<String> visitChildren(RuleNode node) {
        ArrayList<String> args = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            ArrayList<String> toVisit = new ArrayList<>();
            if (toVisit != null) {
                args.addAll(this.visit(node.getChild(i)));
            }
        }
        return args;
    }
}
