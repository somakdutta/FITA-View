package ref_humbold.fita_view.viewer.automaton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;

import ref_humbold.fita_view.Pointer;
import ref_humbold.fita_view.automaton.AutomatonReader;
import ref_humbold.fita_view.automaton.FileFormatException;
import ref_humbold.fita_view.automaton.TreeAutomaton;
import ref_humbold.fita_view.message.Message;
import ref_humbold.fita_view.message.ParameterizedMessageReceiver;
import ref_humbold.fita_view.viewer.EmptyPanel;
import ref_humbold.fita_view.viewer.TitlePanel;
import ref_humbold.fita_view.viewer.UserMessageBox;

public class AutomatonMainPanel
    extends JPanel
    implements ParameterizedMessageReceiver<String>
{
    private static final long serialVersionUID = -7678389910832412322L;

    private Pointer<TreeAutomaton> automatonPointer = new Pointer<>();
    private JFileChooser fileChooser = new JFileChooser();
    private TitlePanel titlePanel = new TitlePanel("automaton");
    private AutomatonScrollTreeView scrollTreeView = new AutomatonScrollTreeView(automatonPointer);
    private ModifyingButtonsPanel modifyingButtonsPanel = new ModifyingButtonsPanel(
        automatonPointer);
    private RunningButtonsPanel runningButtonsPanel = new RunningButtonsPanel();

    public AutomatonMainPanel()
    {
        super();

        this.initializeComponents();
        this.setBackground(Color.BLUE);
        this.setLayout(new BorderLayout(10, 10));

        this.add(titlePanel, BorderLayout.PAGE_START);
        this.add(new EmptyPanel(), BorderLayout.LINE_START);
        this.add(scrollTreeView, BorderLayout.CENTER);
        this.add(modifyingButtonsPanel, BorderLayout.LINE_END);
        this.add(runningButtonsPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void receiveParameterized(Message<String> message)
    {
        if(message.getParam().equals("openFileButton"))
        {
            File file = chooseFile();

            if(file != null)
                try
                {
                    TreeAutomaton automaton = loadAutomaton(file);

                    automatonPointer.set(automaton);
                    UserMessageBox.showInfo("SUCCESS",
                                            "Successfully loaded file " + file.getName());
                }
                catch(Exception e)
                {
                    UserMessageBox.showException(e);
                }
        }
        else if(message.getParam().equals("removeButton"))
        {
            automatonPointer.delete();
        }
    }

    private File chooseFile()
    {
        int result = fileChooser.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();

        return null;
    }

    private TreeAutomaton loadAutomaton(File file)
        throws IOException, SAXException, FileFormatException
    {
        AutomatonReader reader = new AutomatonReader(file);

        return reader.read();
    }

    private void initializeComponents()
    {
        titlePanel.addReceiver(this);

        fileChooser.addChoosableFileFilter(
            new FileNameExtensionFilter("XML bottom-up automaton file", "bua.xml", "xml"));
        fileChooser.setFileFilter(
            new FileNameExtensionFilter("XML top-down automaton file", "tda.xml", "xml"));
        fileChooser.setMultiSelectionEnabled(false);
    }
}
