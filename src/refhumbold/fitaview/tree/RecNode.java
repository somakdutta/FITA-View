package refhumbold.fitaview.tree;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import refhumbold.fitaview.automaton.IllegalVariableValueException;
import refhumbold.fitaview.automaton.Variable;

public class RecNode
    extends TreeNode
{
    private RepeatNode recursive;
    private TreeNode parent;
    private Map<Variable, String> stateCopy;

    public RecNode(RepeatNode recursive, int index)
    {
        super(index);

        if(recursive == null)
            throw new IllegalArgumentException("Recursive node is null");

        this.recursive = recursive;
        this.stateCopy = recursive.getStateWithNulls();
    }

    @Override
    public NodeType getType()
    {
        return NodeType.REC;
    }

    @Override
    public TreeNode getLeft()
    {
        return recursive.getLeft();
    }

    @Override
    public TreeNode getRight()
    {
        return recursive.getRight();
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    protected void setParent(TreeNode node)
    {
        parent = node;
    }

    @Override
    public String getLabel()
    {
        return recursive.getLabel();
    }

    @Override
    public Map<Variable, String> getStateWithNulls()
    {
        return stateCopy;
    }

    @Override
    public void setState(Map<Variable, String> state)
        throws IllegalVariableValueException
    {
        recursive.setState(state);
        stateCopy = state;
    }

    @Override
    public void setInitialState(Collection<Variable> vars)
    {
        recursive.setInitialState(vars);
        stateCopy = recursive.getStateWithNulls();
    }

    @Override
    public String getStateValueOrNull(Variable var)
    {
        return stateCopy.get(var);
    }

    @Override
    public void setStateValue(Variable var, String value)
        throws IllegalVariableValueException
    {
        recursive.setStateValue(var, value);
        stateCopy = recursive.getStateWithNulls();
    }

    @Override
    public void deleteState()
    {
        recursive.deleteState();
        stateCopy.clear();
    }

    @Override
    public String toString()
    {
        return "<@ REC @>";
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;

        if(o == null || !(o instanceof RecNode))
            return false;

        RecNode other = (RecNode)o;

        return this.recursive.index == other.recursive.index && this.index == other.index;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.index, recursive.index, getLabel());
    }
}