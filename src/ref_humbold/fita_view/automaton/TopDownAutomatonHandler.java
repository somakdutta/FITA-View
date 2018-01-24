package ref_humbold.fita_view.automaton;

import java.util.Objects;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class TopDownAutomatonHandler
    extends AutomatonHandler
{
    private TopDownAutomaton automaton;
    private boolean isInfiniteAccept = false;
    private String nodeValue;
    private String label;
    private String leftResult;
    private String rightResult;

    @Override
    public TreeAutomaton getAutomaton()
    {
        return automaton;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
    {
        tagName = qName;
        content = new StringBuilder();

        switch(qName)
        {
            case "buchi-accepting":
                isInfiniteAccept = true;
                automaton = isDeterministic ? new TopDownDITA(variables.values(), alphabet)
                                            : new TopDownNITA(variables.values(), alphabet);
                break;

            case "leaf-accepting":
                if(!isInfiniteAccept)
                    automaton = isDeterministic ? new TopDownDFTA(variables.values(), alphabet)
                                                : new TopDownNFTA(variables.values(), alphabet);

                isInfiniteAccept = false;
                break;

            case "label":
            case "node-value":
            case "left-result":
            case "right-result":
                break;

            default:
                super.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
        throws SAXException
    {
        switch(qName)
        {
            case "buchi-accepting":
            case "leaf-accepting":
            case "variables":
                break;

            case "trans":
                automaton.addTransition(variables.get(varID), nodeValue, label, leftResult,
                                        rightResult);
                break;

            case "conditions":
                for(Integer id : variables.keySet())
                {
                    if(conditions.get(variables.get(id)) == null)
                        throw new NoAcceptingForVariableException(
                            writePosition() + "Variable with ID " + id
                                + "has no accepting condition.");
                }

                if(isInfiniteAccept)
                    ((InfiniteTreeAutomaton)automaton).addInfinitelyAcceptingConditions(conditions);
                else
                    automaton.addAcceptingConditions(conditions);
                break;

            case "label":
                label = content.toString();
                content = null;

                if(!Objects.equals(label, Wildcard.EVERY_VALUE) && !alphabet.contains(label))
                    throw new IllegalAlphabetWordException(
                        writePosition() + "Given label \'" + label
                            + "\' is not a part of automaton's alphabet.");
                break;

            case "node-value":
                nodeValue = content.toString();
                content = null;

                if(!Objects.equals(nodeValue, Wildcard.EVERY_VALUE) && !variables.get(varID)
                                                                                 .contains(
                                                                                     nodeValue))
                    throw new IllegalVariableValueException(
                        writePosition() + "Given node-value  \'" + nodeValue
                            + "\' is not a value of variable with ID " + varID + ".");
                break;

            case "left-result":
                leftResult = content.toString();
                content = null;

                if(!Objects.equals(leftResult, Wildcard.SAME_VALUE) && !variables.get(varID)
                                                                                 .contains(
                                                                                     leftResult))
                    throw new IllegalVariableValueException(
                        writePosition() + "Given left-result \'" + leftResult
                            + "\' is not a value of variable with ID " + varID + ".");
                break;

            case "right-result":
                rightResult = content.toString();
                content = null;

                if(!Objects.equals(rightResult, Wildcard.SAME_VALUE) && !variables.get(varID)
                                                                                  .contains(
                                                                                      rightResult))
                    throw new IllegalVariableValueException(
                        writePosition() + "Given right-result \'" + rightResult
                            + "\'is not a value of variable with ID " + varID + ".");
                break;

            default:
                super.endElement(uri, localName, qName);
        }
    }
}
