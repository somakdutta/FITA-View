package ref_humbold.fita_view.automaton;

import java.util.*;

import ref_humbold.fita_view.Pair;
import ref_humbold.fita_view.automaton.transition.NoSuchTransitionException;
import ref_humbold.fita_view.automaton.traversing.TopDownDFS;
import ref_humbold.fita_view.automaton.traversing.TopDownTraversing;
import ref_humbold.fita_view.tree.NodeType;
import ref_humbold.fita_view.tree.TreeNode;
import ref_humbold.fita_view.tree.UndefinedTreeStateException;

public abstract class AbstractTreeAutomaton
    implements TreeAutomaton
{
    protected TreeNode tree;
    protected Set<String> alphabet;
    protected List<Variable> variables;
    protected AcceptingConditions acceptingConditions = new AcceptingConditions();
    protected AutomatonRunningMode runningMode = AutomatonRunningMode.STOPPED;
    protected boolean isSendingMessages = false;

    public AbstractTreeAutomaton(Collection<Variable> variables, Collection<String> alphabet)
    {
        this.variables = new ArrayList<>(variables);
        this.alphabet = new HashSet<>(alphabet);
    }

    @Override
    public AcceptingConditions getAcceptingConditions()
    {
        return this.acceptingConditions;
    }

    @Override
    public Set<String> getAlphabet()
    {
        return this.alphabet;
    }

    @Override
    public List<Variable> getVariables()
    {
        return this.variables;
    }

    @Override
    public AutomatonRunningMode getRunningMode()
    {
        return this.runningMode;
    }

    protected void setRunningMode(AutomatonRunningMode runningMode)
    {
        this.runningMode = runningMode;

        if(isSendingMessages)
            AutomatonRunningModeSender.getInstance().send(runningMode);
    }

    @Override
    public void setTree(TreeNode tree)
        throws TreeFinitenessException, EmptyTreeException
    {
        if(tree == null)
            throw new EmptyTreeException("Tree is empty.");

        assertFiniteness(tree);

        this.tree = tree;
        this.setRunningMode(AutomatonRunningMode.STOPPED);
    }

    @Override
    public void setSendingMessages(boolean sendingMessages)
    {
        this.isSendingMessages = sendingMessages;
    }

    @Override
    public void addAcceptingConditions(Map<Variable, Pair<String, Boolean>> accept)
    {
        acceptingConditions.add(accept);
    }

    @Override
    public boolean isInAlphabet(String label)
    {
        return alphabet.contains(label);
    }

    @Override
    public void run()
        throws IllegalVariableValueException, NoSuchTransitionException,
               NoTraversingStrategyException, UndefinedTreeStateException, EmptyTreeException,
               NoNonDeterministicStrategyException
    {
        if(getTraversing() == null)
        {
            stopTraversing();
            throw new NoTraversingStrategyException("Automaton has no traversing strategy.");
        }

        if(runningMode == AutomatonRunningMode.STOPPED
            || runningMode == AutomatonRunningMode.FINISHED)
            initialize();

        while(getTraversing().hasNext())
            makeStepForward();
    }

    @Override
    public void makeStepForward()
        throws NoSuchTransitionException, IllegalVariableValueException,
               NoTraversingStrategyException, UndefinedTreeStateException, EmptyTreeException,
               NoNonDeterministicStrategyException
    {
        if(getTraversing() == null)
        {
            stopTraversing();
            throw new NoTraversingStrategyException("Automaton has no traversing strategy.");
        }

        if(runningMode == AutomatonRunningMode.STOPPED)
            initialize();

        Iterable<TreeNode> nextNodes;

        try
        {
            nextNodes = getTraversing().next();

            for(TreeNode node : nextNodes)
                processNode(node);
        }
        catch(Exception e)
        {
            stopTraversing();
            throw e;
        }

        if(isSendingMessages)
            AutomatonCurrentNodesSender.getInstance().send(nextNodes);

        changeRunningMode();
    }

    @Override
    public void stopTraversing()
    {
        setRunningMode(AutomatonRunningMode.STOPPED);

        if(getTraversing() != null)
            getTraversing().clear();

        if(tree != null)
            deleteTreeStates();

        if(isSendingMessages)
            AutomatonCurrentNodesSender.getInstance().send(Collections::emptyIterator);
    }

    protected abstract void changeRunningMode();

    /**
     * Verifying finiteness of the tree.
     * @param tree tree to verify
     * @throws TreeFinitenessException if tree finiteness is violated
     */
    protected abstract void assertFiniteness(TreeNode tree)
        throws TreeFinitenessException;

    /**
     * Initializing automaton and tree before running on tree.
     */
    protected void initialize()
        throws IllegalVariableValueException, EmptyTreeException, NoTraversingStrategyException,
               NoNonDeterministicStrategyException
    {
        if(getTraversing() == null)
            throw new NoTraversingStrategyException("Automaton has no traversing strategy.");

        if(tree == null)
            throw new EmptyTreeException("No tree specified.");

        deleteTreeStates();
        setRunningMode(AutomatonRunningMode.RUNNING);
    }

    /**
     * Testing if specified tree contains a recursive node.
     * @param node tree node
     * @return {@code true} if tree has a recursive node, otherwise {@code false}
     */
    protected boolean containsRecursiveNode(TreeNode node)
    {
        return node != null && (node.getType() == NodeType.REC || containsRecursiveNode(
            node.getLeft()) || containsRecursiveNode(node.getRight()));
    }

    /**
     * Processing tree nodes in each step.
     * @param node nodes to process
     */
    protected abstract void processNode(TreeNode node)
        throws NoSuchTransitionException, IllegalVariableValueException,
               UndefinedTreeStateException;

    private void deleteTreeStates()
    {
        TopDownTraversing t = new TopDownDFS();

        t.initialize(tree);
        t.forEachRemaining(iterable -> iterable.forEach(TreeNode::deleteState));
    }
}
