package refhumbold.fitaview.viewer.automaton;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import refhumbold.fitaview.Pointer;
import refhumbold.fitaview.automaton.TreeAutomaton;

public class AutomatonScrollTreeView
    extends JScrollPane
{
    private static final long serialVersionUID = 4304767885791508959L;

    public AutomatonScrollTreeView(Pointer<TreeAutomaton> pointer)
    {
        super();
        setViewportView(new AutomatonTreeView(pointer));
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    }
}
