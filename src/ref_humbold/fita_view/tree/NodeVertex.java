package ref_humbold.fita_view.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ref_humbold.fita_view.automaton.IllegalVariableValueException;
import ref_humbold.fita_view.automaton.Variable;

public class NodeVertex
    extends TreeVertex
{
    private TreeVertex left = null;
    private TreeVertex right = null;
    private TreeVertex parent = null;
    private String label;
    private Map<Variable, String> state = new HashMap<>();

    public NodeVertex(String label)
    {
        this(label, 0);
    }

    public NodeVertex(String label, TreeVertex left, TreeVertex right)
    {
        this(label);
        this.setLeft(left);
        this.setRight(right);
    }

    public NodeVertex(String label, int index)
    {
        super(index);

        if(label == null)
            throw new IllegalArgumentException("Label is null");

        this.label = label;
    }

    public NodeVertex(String label, int index, TreeVertex left, TreeVertex right)
    {
        this(label, index);
        this.setLeft(left);
        this.setRight(right);
    }

    @Override
    public VertexType getType()
    {
        return VertexType.NODE;
    }

    @Override
    public TreeVertex getLeft()
    {
        return left;
    }

    @Override
    public void setLeft(TreeVertex vertex)
    {
        if(left != null)
            left.setParent(null);

        left = vertex;

        if(left != null)
            left.setParent(this);
    }

    @Override
    public TreeVertex getRight()
    {
        return right;
    }

    @Override
    public void setRight(TreeVertex vertex)
    {
        if(right != null)
            right.setParent(null);

        right = vertex;

        if(right != null)
            right.setParent(this);
    }

    @Override
    public TreeVertex getParent()
    {
        return parent;
    }

    @Override
    protected void setParent(TreeVertex vertex)
    {
        parent = vertex;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public Map<Variable, String> getFullState()
    {
        return state;
    }

    @Override
    public String getStateOrNull(Variable var)
    {
        return state.get(var);
    }

    @Override
    public void setState(Variable var, String value)
        throws IllegalVariableValueException
    {
        if(!var.contains(value))
            throw new IllegalVariableValueException(value);

        state.put(var, value);
    }

    @Override
    public void deleteFullState()
    {
        state.clear();
    }

    @Override
    public String toString()
    {
        String leftString = left == null ? "#" : left.toString();
        String rightString = right == null ? "#" : right.toString();

        return "<$ " + label + "," + leftString + ", " + rightString + " $>";
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;

        if(o == null || !(o instanceof NodeVertex))
            return false;

        NodeVertex other = (NodeVertex)o;

        return Objects.equals(this.label, other.label) && Objects.equals(this.left, other.left)
            && Objects.equals(this.right, other.right);
    }

    @Override
    public int hashCode()
    {
        return index * 37 + label.hashCode();
    }
}
