package ref_humbold.fita_view.automaton.traversing;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ref_humbold.fita_view.tree.NodeVertex;
import ref_humbold.fita_view.tree.TreeVertex;

public class BottomUpLevelTest
{
    private BottomUpLevel testObject;
    private TreeVertex node13 = new NodeVertex("13", 13);
    private TreeVertex node12 = new NodeVertex("12", 12);
    private TreeVertex node11 = new NodeVertex("11", 11);
    private TreeVertex node10 = new NodeVertex("10", 10);
    private TreeVertex node7 = new NodeVertex("7", 7);
    private TreeVertex node6 = new NodeVertex("6", 6, node13, node12);
    private TreeVertex node5 = new NodeVertex("5", 5, node11, node10);
    private TreeVertex node4 = new NodeVertex("4", 4);
    private TreeVertex node3 = new NodeVertex("3", 3, node7, node6);
    private TreeVertex node2 = new NodeVertex("2", 2, node5, node4);
    private TreeVertex node1 = new NodeVertex("1", 1, node3, node2);

    @Before
    public void setUp()
    {
        testObject = new BottomUpLevel();
    }

    @After
    public void tearDown()
    {
        testObject = null;
    }

    @Test
    public void testNext()
    {
        ArrayList<Object[]> result = new ArrayList<>();

        testObject.initialize(node4, node7, node10, node11, node12, node13);

        while(testObject.hasNext())
        {
            ArrayList<TreeVertex> vertices = new ArrayList<>();

            for(TreeVertex v : testObject.next())
                vertices.add(v);

            Assert.assertTrue(vertices.size() > 0);

            result.add(vertices.toArray());
        }

        TreeVertex[][] expected =
            {{node13, node12, node11, node10}, {node7, node6, node5, node4}, {node3, node2},
             {node1}};

        Assert.assertArrayEquals(expected, result.toArray());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextWhenOutOfBounds()
    {
        testObject.initialize(node4, node7, node10, node11, node12, node13);

        while(testObject.hasNext())
        {
            testObject.next();
        }

        testObject.next();
    }

    @Test
    public void testInitialize()
    {
        testObject.initialize(node7, node4);

        Queue<TreeVertex> queue = testObject.vertexQueue;

        Assert.assertEquals(2, queue.size());
        Assert.assertTrue(queue.contains(node7));
        Assert.assertTrue(queue.contains(node4));
        Assert.assertEquals(2, testObject.currentDepth);
    }

    @Test
    public void testInitializeWhenDoubleInvoke()
    {
        testObject.initialize(node7, node4);
        testObject.initialize(node10, node11, node12);

        Queue<TreeVertex> queue = testObject.vertexQueue;

        Assert.assertEquals(3, queue.size());
        Assert.assertFalse(queue.contains(node7));
        Assert.assertFalse(queue.contains(node4));
        Assert.assertTrue(queue.contains(node12));
        Assert.assertTrue(queue.contains(node11));
        Assert.assertTrue(queue.contains(node10));
        Assert.assertEquals(3, testObject.currentDepth);
    }

    @Test
    public void testHasNextWhenEmpty()
    {
        boolean result = testObject.hasNext();

        Assert.assertFalse(result);
    }

    @Test
    public void testHasNextWhenNotEmpty()
    {
        testObject.initialize(node13);

        boolean result = testObject.hasNext();

        Assert.assertTrue(result);
    }
}
