package ref_humbold.fita_view.tree;

import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class TreeReaderTest
{
    public static final String DIRECTORY = "test_files/TreeReaderTest/";
    private TreeReader testObject;

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
        testObject = null;
    }

    @Test
    public void testReadEmptyTree()
    {
        TreeVertex result = null;

        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadEmptyTree.tree.xml"));
            result = testObject.read();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        Assert.assertNull(result);
    }

    @Test
    public void testReadFiniteTree()
    {
        TreeVertex result = null;

        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadFiniteTree.tree.xml"));
            result = testObject.read();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        TreeVertex expected = new NodeVertex("1", 1, new NodeVertex("2", 3, new NodeVertex("3", 7),
                                                                    new NodeVertex("4", 6)),
                                             new NodeVertex("5", 2));

        Assert.assertNotNull(result);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testReadWhenSingleRepeat()
    {
        TreeVertex result = null;

        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenSingleRepeat.tree.xml"));
            result = testObject.read();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        TreeVertex repeat = new RepeatVertex("5", 2);

        repeat.setLeft(new NodeVertex("6", 5));
        repeat.setRight(new NodeVertex("7", 4, new RecVertex(repeat, 9), new NodeVertex("9", 8)));

        TreeVertex expected = new NodeVertex("1", 1, new NodeVertex("2", 3, new NodeVertex("3", 7),
                                                                    new NodeVertex("4", 6)),
                                             repeat);

        Assert.assertNotNull(result);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testReadWhenNestedRepeats()
    {
        TreeVertex result = null;

        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenNestedRepeats.tree.xml"));
            result = testObject.read();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        TreeVertex repeat5 = new RepeatVertex("5", 2);
        TreeVertex repeat7 = new RepeatVertex("7", 11);

        repeat7.setLeft(
            new NodeVertex("8", 23, new RecVertex(repeat7, 47), new RecVertex(repeat7, 46)));
        repeat7.setRight(new NodeVertex("11", 22));

        repeat5.setLeft(new NodeVertex("6", 5, repeat7, new RecVertex(repeat5, 10)));
        repeat5.setRight(
            new NodeVertex("13", 4, new RecVertex(repeat5, 9), new NodeVertex("15", 8)));

        TreeVertex expected = new NodeVertex("1", 1, new NodeVertex("2", 3, new NodeVertex("3", 7),
                                                                    new NodeVertex("4", 6)),
                                             repeat5);

        Assert.assertNotNull(result);
        Assert.assertEquals(expected, result);
    }

    @Test(expected = TreeParsingException.class)
    public void testReadWhenRecOutOfScope()
        throws SAXException
    {
        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenRecOutOfScope.tree.xml"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        try
        {
            testObject.read();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }
    }

    @Test(expected = OneChildException.class)
    public void testReadWhenOneChild()
        throws SAXException
    {
        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenOneChild.tree.xml"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        try
        {
            testObject.read();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }
    }

    @Test(expected = TreeParsingException.class)
    public void testReadWhenNullIsChild()
        throws SAXException
    {
        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenNullIsChild.tree.xml"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        try
        {
            testObject.read();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }
    }

    @Test(expected = TreeParsingException.class)
    public void testReadWhenThreeChildren()
        throws SAXException
    {
        try
        {
            testObject = new TreeReader(new File(DIRECTORY + "testReadWhenThreeChildren.tree.xml"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        try
        {
            testObject.read();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }
    }
}
