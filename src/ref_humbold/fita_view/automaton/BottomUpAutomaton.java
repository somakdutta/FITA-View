package ref_humbold.fita_view.automaton;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import ref_humbold.fita_view.Triple;
import ref_humbold.fita_view.automaton.transition.DuplicatedTransitionException;
import ref_humbold.fita_view.automaton.transition.IllegalTransitionException;
import ref_humbold.fita_view.automaton.transition.NoSuchTransitionException;
import ref_humbold.fita_view.automaton.traversing.*;
import ref_humbold.fita_view.tree.TreeNode;
import ref_humbold.fita_view.tree.UndefinedTreeStateException;

public abstract class BottomUpAutomaton
    extends AbstractTreeAutomaton
{
    protected BottomUpTraversing traversing;
    private List<TreeNode> leaves = new ArrayList<>();

    public BottomUpAutomaton(Collection<Variable> variables, Collection<String> alphabet)
    {
        super(variables, alphabet);
    }

    @Override
    public AutomatonDirection getDirection()
    {
        return AutomatonDirection.BOTTOM_UP;
    }

    @Override
    public BottomUpTraversing getTraversing()
    {
        return traversing;
    }

    @Override
    public void setTraversing(TraversingMode mode)
        throws IncorrectTraversingException
    {
        this.traversing = TraversingFactory.getBottomUpTraversing(mode);
    }

    @Override
    public boolean isAccepted()
        throws UndefinedAcceptanceException, UndefinedTreeStateException, EmptyTreeException
    {
        if(tree == null)
            throw new EmptyTreeException("Tree is empty.");

        return acceptanceConditions.check(tree.getState());
    }

    @Override
    public void setTree(TreeNode tree)
        throws TreeFinitenessException, EmptyTreeException
    {
        super.setTree(tree);
        this.findLeaves();
    }

    /**
     * Adding new transition entry to transition function of automaton.
     * @param var variable
     * @param leftValue variable value in left son
     * @param rightValue variable value in right son
     * @param label tree label of node
     * @param result variable value in node
     */
    public abstract void addTransition(Variable var, String leftValue, String rightValue,
                                       String label, String result)
        throws DuplicatedTransitionException, IllegalTransitionException;

    @Override
    protected void changeRunningMode()
    {
        setRunningMode(
            traversing.hasNext() ? AutomatonRunningMode.RUNNING : AutomatonRunningMode.FINISHED);
    }

    @Override
    protected void initialize()
        throws IllegalVariableValueException, EmptyTreeException, NoTraversingStrategyException,
               NoNonDeterministicStrategyException
    {
        super.initialize();

        traversing.initialize(leaves.toArray(new TreeNode[0]));
    }

    @Override
    protected void assertFiniteness(TreeNode tree)
        throws TreeFinitenessException
    {
        if(containsRecursiveNode(tree))
            throw new TreeFinitenessException("Tree is infinite.");
    }

    @Override
    protected void processNode(TreeNode node)
        throws IllegalVariableValueException, UndefinedTreeStateException, NoSuchTransitionException
    {
        for(Variable var : variables)
        {
            String leftValue = node.getLeft() == null ? var.getInitValue()
                                                      : node.getLeft().getStateValue(var);
            String rightValue = node.getRight() == null ? var.getInitValue()
                                                        : node.getRight().getStateValue(var);
            String result = doTransition(var, leftValue, rightValue, node.getLabel());

            node.setStateValue(var, result);
        }

        if(isSendingMessages)
            if(node.hasChildren())
                TransitionSender.getInstance()
                                .send(Triple.make(node.getLeft().getState(), node.getState(),
                                                  node.getRight().getState()));
            else
                TransitionSender.getInstance()
                                .send(Triple.make(collectInitialStates(), node.getState(),
                                                  collectInitialStates()));
    }

    /**
     * Calling a transition function with specified arguments.
     * @param var variable
     * @param leftValue variable value in left son
     * @param rightValue variable value in right son
     * @param label tree label of node
     * @return variable value in node
     */
    protected abstract String applyTransition(Variable var, String leftValue, String rightValue,
                                              String label)
        throws NoSuchTransitionException;

    /**
     * Converting transition key to its string representation.
     * @param key transition key
     * @return string representation
     */
    protected String keyToString(Triple<String, String, String> key)
    {
        return "LEFT VALUE = \'" + key.getFirst() + "\', RIGHT VALUE = \'" + key.getSecond()
            + "\', LABEL = \'" + key.getThird() + "\'";
    }

    /**
     * Converting transition value to its string representation.
     * @param value transition value
     * @return string representation
     */
    protected String valueToString(String value)
    {
        return "VALUE = \'" + value + "\'";
    }

    private String doTransition(Variable var, String leftValue, String rightValue, String label)
        throws NoSuchTransitionException
    {
        String result = applyTransition(var, leftValue, rightValue, label);

        if(Objects.equals(result, Wildcard.LEFT_VALUE))
            return leftValue;

        if(Objects.equals(result, Wildcard.RIGHT_VALUE))
            return rightValue;

        return result;
    }

    private Map<Variable, String> collectInitialStates()
    {
        return variables.stream()
                        .collect(Collectors.toMap(Function.identity(), Variable::getInitValue,
                                                  (a, b) -> b));
    }

    private void findLeaves()
    {
        TopDownTraversing t = new TopDownDFS();

        leaves.clear();
        t.initialize(tree);

        t.forEachRemaining(iterator -> iterator.forEach(v -> {
            if(!v.hasChildren())
                leaves.add(v);
        }));
    }
}