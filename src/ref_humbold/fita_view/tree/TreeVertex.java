package ref_humbold.fita_view.tree;

import ref_humbold.fita_view.automaton.Variable;

public abstract class TreeVertex
{
    final int id;

    TreeVertex(int id)
    {
        this.id = id;
    }

    public abstract String getTypename();

    public abstract TreeVertex getLeft();

    protected abstract void setLeft(TreeVertex vertex);

    public abstract TreeVertex getRight();

    protected abstract void setRight(TreeVertex vertex);

    public abstract TreeVertex getParent();

    protected abstract void setParent(TreeVertex vertex);

    public abstract String getLabel();

    public abstract String getState(Variable var);

    public abstract void setState(Variable var, String value);
}
