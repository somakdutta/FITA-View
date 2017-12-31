package ref_humbold.fita_view.viewer.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;

import ref_humbold.fita_view.Pointer;
import ref_humbold.fita_view.automaton.FileFormatException;
import ref_humbold.fita_view.message.Message;
import ref_humbold.fita_view.message.ParameterizedMessageReceiver;
import ref_humbold.fita_view.tree.TreeNode;
import ref_humbold.fita_view.tree.TreeReader;
import ref_humbold.fita_view.viewer.EmptyPanel;
import ref_humbold.fita_view.viewer.MessageBox;
import ref_humbold.fita_view.viewer.TitlePanel;

public class TreeMainPanel
    extends JPanel
    implements ParameterizedMessageReceiver<String>
{
    private static final long serialVersionUID = 5944023926285119879L;

    private Pointer<TreeNode> treePointer = new Pointer<>();
    private JFileChooser fileChooser = new JFileChooser();
    private TitlePanel titlePanel = new TitlePanel("tree");
    private TreeDrawingPanel drawingPanel = new TreeDrawingPanel(treePointer);

    public TreeMainPanel()
    {
        super();

        this.initializeComponents();
        this.setBackground(Color.RED);
        this.setLayout(new BorderLayout(10, 10));

        this.add(titlePanel, BorderLayout.PAGE_START);
        this.add(new EmptyPanel(), BorderLayout.LINE_START);
        this.add(drawingPanel, BorderLayout.CENTER);
        this.add(new EmptyPanel(), BorderLayout.LINE_END);
        this.add(new EmptyPanel(), BorderLayout.PAGE_END);
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
                    TreeNode tree = loadTree(file);

                    treePointer.set(tree);
                    MessageBox.showInfoBox("SUCCESS", "Successfully loaded file " + file.getName());
                }
                catch(Exception e)
                {
                    MessageBox.showExceptionBox(e);
                }
        }
        else if(message.getParam().equals("removeButton"))
        {
            treePointer.delete();
        }
    }

    private File chooseFile()
    {
        int result = fileChooser.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();

        return null;
    }

    private TreeNode loadTree(File file)
        throws FileFormatException, SAXException, IOException
    {
        TreeReader reader = new TreeReader(file);

        return reader.read();
    }

    private void initializeComponents()
    {
        titlePanel.addReceiver(this);

        fileChooser.setFileFilter(new FileNameExtensionFilter("XML tree file", "tree.xml", "xml"));
        fileChooser.setMultiSelectionEnabled(false);
    }
}
