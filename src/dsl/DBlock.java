package dsl;

import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

import java.util.ArrayList;
import java.util.List;

public class DBlock extends DStatement {

    final String node = "DBlock";
    final List<DStatement> statements;

    private DBlock(List<DStatement> statements) {
        this.statements = statements;
    }

    @Override
    public void updateSequences(List<Sequence> soFar) {
        for (DStatement statement : statements)
            statement.updateSequences(soFar);
    }

    @Override
    public String sketch() {
        String s = "{";
        for (DStatement statement : statements)
            s += statement == null? HOLE() : statement.sketch();
        s += "}";
        return s;
    }

    public static class Handle extends Handler {
        Block block;

        public Handle(Block block, Visitor visitor) {
            super(visitor);
            this.block = block;
        }

        @Override
        public DBlock handle() {
            if (block == null)
                return null;
            List<DStatement> statements = new ArrayList<DStatement>();
            for (Object o : block.statements()) {
                Statement s = (Statement) o;
                DStatement statement = new DStatement.Handle(s, visitor).handle();
                if (statement == null)
                    continue;
                if (statement instanceof DBlock)
                    statements.addAll(((DBlock) statement).statements);
                else
                    statements.add(statement);
            }

            if (! statements.isEmpty())
                return new DBlock(statements);

            return null;
        }
    }
}