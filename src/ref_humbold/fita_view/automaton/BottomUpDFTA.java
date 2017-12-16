package ref_humbold.fita_view.automaton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import ref_humbold.fita_view.Pair;
import ref_humbold.fita_view.Triple;
import ref_humbold.fita_view.automaton.transition.BottomUpTransitions;
import ref_humbold.fita_view.automaton.transition.DuplicatedTransitionException;
import ref_humbold.fita_view.automaton.transition.IllegalTransitionException;
import ref_humbold.fita_view.automaton.transition.NoSuchTransitionException;
import ref_humbold.fita_view.automaton.traversing.BottomUpTraversing;
import ref_humbold.fita_view.automaton.traversing.IncorrectTraversingException;
import ref_humbold.fita_view.automaton.traversing.TopDownTraversing;
import ref_humbold.fita_view.automaton.traversing.TraversingFactory;
import ref_humbold.fita_view.automaton.traversing.TraversingMode;
import ref_humbold.fita_view.tree.TreeVertex;

public class BottomUpDFTA
    extends SimpleTreeAutomaton
{
    private BottomUpTraversing traversing;
    private BottomUpTransitions transitions = new BottomUpTransitions();
    private Set<Map<Variable, String>> acceptingStates = new HashSet<>();
    private List<TreeVertex> leaves = new ArrayList<>();

    public BottomUpDFTA(Collection<String> alphabet, Collection<Variable> variables)
    {
        super(alphabet, variables);
    }

    @Override
    public void setTree(TreeVertex tree)
    {
        super.setTree(tree);
        this.findLeaves();
        this.isRunning = false;
    }

    @Override
    protected BottomUpTraversing getTraversing()
    {
        return traversing;
    }

    @Override
    public void setTraversing(TraversingMode traversingMode)
        throws IncorrectTraversingException
    {
        this.traversing = TraversingFactory.getInstance().getBottomUpTraversing(traversingMode);
    }

    @Override
    public boolean isAccepted()
        throws UndefinedAcceptanceException
    {
        if(acceptingStates.isEmpty())
            throw new UndefinedAcceptanceException("Automaton has no acccepting states defined.");

        return acceptingStates.contains(tree.getFullState());
    }

    @Override
    public void makeStepForward()
        throws NoSuchTransitionException, IllegalVariableValueException, NoTraversingException
    {
        if(traversing == null)
        {
            isRunning = false;
            throw new NoTraversingException("Automaton has no traversing strategy.");
        }

        if(!isRunning)
            initialize();

        if(!traversing.hasNext())
        {
            isRunning = false;
            return;
        }

        for(TreeVertex vertex : traversing.next())
            for(Variable var : variables)
                try
                {
                    String leftValue = vertex.getLeft() == null ? var.getInitValue()
                                                                : vertex.getLeft().getState(var);
                    String rightValue = vertex.getRight() == null ? var.getInitValue()
                                                                  : vertex.getRight().getState(var);
                    String result = doTransition(var, leftValue, rightValue, vertex.getLabel());

                    vertex.setState(var, result);
                }
                catch(Exception e)
                {
                    isRunning = false;
                    throw e;
                }
    }

    @Override
    public TreeVertex generateTree()
    {
        return null;
    }

    @Override
    protected void initialize()
        throws IllegalVariableValueException
    {
        super.initialize();

        List<Pair<TreeVertex, Integer>> indexedLeaves = new ArrayList<>();

        for(TreeVertex v : leaves)
            indexedLeaves.add(Pair.make(v, v.getIndex()));

        traversing.initialize(indexedLeaves);
        isRunning = true;
    }

    /**
     * Adding an accepting state of automaton
     * @param accept mapping from variables to their accepting values
     */
    void addAcceptingState(Map<Variable, String> accept)
    {
        acceptingStates.add(accept);
    }

    /**
     * Adding new transition entry to transition function of automaton.
     * @param var variable
     * @param leftValue variable value in left son
     * @param rightValue variable value in right son
     * @param label tree label of node
     * @param result variable value in node
     */
    void addTransition(Variable var, String leftValue, String rightValue, String label,
                       String result)
        throws DuplicatedTransitionException, IllegalTransitionException
    {
        transitions.add(var, Triple.make(leftValue, rightValue, label), result);
    }

    private String doTransition(Variable var, String leftValue, String rightValue, String label)
        throws NoSuchTransitionException
    {
        String result = transitions.get(var, Triple.make(leftValue, rightValue, label));

        if(result.equals(Wildcard.LEFT_VALUE))
            return leftValue;

        if(result.equals(Wildcard.RIGHT_VALUE))
            return rightValue;

        return result;
    }

    private void findLeaves()
    {
        leaves.clear();

        TopDownTraversing t =
            TraversingFactory.getInstance().getTopDownTraversing(TraversingMode.DFS);

        t.initialize(tree);

        while(t.hasNext())
            for(TreeVertex v : t.next())
                if(!v.hasChildren())
                    leaves.add(v);
    }

    @Override
    public String toString()
    {
        return "BottomUpDFTA of " + alphabet.toString() + " & " + variables.toString() + " & "
            + transitions.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o)
            return true;

        if(o == null || !(o instanceof BottomUpDFTA))
            return false;

        BottomUpDFTA other = (BottomUpDFTA)o;

        return Objects.equals(this.alphabet, other.alphabet) && Objects.equals(this.variables,
                                                                               other.variables)
            && Objects.equals(this.acceptingStates, other.acceptingStates) && Objects.equals(
            this.transitions, other.transitions);
    }
}
