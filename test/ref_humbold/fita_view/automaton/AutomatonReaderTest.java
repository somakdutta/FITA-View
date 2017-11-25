package ref_humbold.fita_view.automaton;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class AutomatonReaderTest
{
    private AutomatonReader testObject;

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
    public void testReadTopDownDFTA()
    {
        TreeAutomaton result = null;

        try
        {
            testObject = new AutomatonReader(
                "test/ref_humbold/fita_view/automaton/testReadTopDownDFTA.tda.xml");
            result = testObject.read();
        }
        catch(SAXException | IOException e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception " + e.getClass().getSimpleName());
        }

        Variable v = new Variable("A", "B", "C");
        TopDownDFTA expected =
            new TopDownDFTA(new HashSet<>(Arrays.asList("0", "1")), Collections.singletonList(v));

        expected.addTransition(v, "A", "0", "B", "C");
        expected.addTransition(v, "A", "1", "A", "A");
        expected.addTransition(v, "B", "0", "C", "A");
        expected.addTransition(v, "B", "1", "B", "B");
        expected.addTransition(v, "C", "0", "A", "B");
        expected.addTransition(v, "C", "1", "C", "C");

        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof TopDownDFTA);
        Assert.assertEquals(expected, result);
    }
}