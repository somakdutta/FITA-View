package ref_humbold.fita_view.automaton.nondeterminism;

import java.util.Collection;

public class FirstElementChoice<K, R>
    implements StateChoice<K, R>
{
    @Override
    public StateChoiceMode getMode()
    {
        return StateChoiceMode.FIRST;
    }

    /**
     * Non-deterministically choosing variable values.
     * @param key transition key in node.
     * @param states set of possible state variable values
     * @return variable value chosen as first value return by set iterator
     */
    @Override
    public R chooseState(K key, Collection<R> states)
    {
        return states.iterator().next();
    }
}
