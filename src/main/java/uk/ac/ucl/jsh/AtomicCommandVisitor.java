package uk.ac.ucl.jsh;

import java.util.ArrayList;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class AtomicCommandVisitor
    extends AbstractParseTreeVisitor<ArrayList<String>> {

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
