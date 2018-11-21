package logic;

import java.util.ArrayList;
import java.util.Comparator;
import org.w3c.dom.Document;
import model.StructServiceRequest;

/**
 * Class that implements the heuristic algorithms
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class Heuristic {

  // stub per config xml
  // TODO: Da levare
  private String searchHeuristic;

  public final static String COST = "cost";
  public final static String TIME = "time";
  public final static String REPUTATION = "reputation";

  public Heuristic() {

  }

  /**
   *
   * Heuristic @param documentXML
   */
  public Heuristic(Document documentXML) {
    searchHeuristic = documentXML.getElementsByTagName("heuristic").item(0).getTextContent();
  }

  /**
   * Return the lower cost StructAgent in the list
   *
   * @param myList
   * @return
   */
  public StructServiceRequest getLowerCost(ArrayList<StructServiceRequest> myList) {
    StructServiceRequest lowerCost;
    myList = this.costOrdering(myList);
    lowerCost = myList.get(0);
    return lowerCost;
  }

  /**
   * Return the lower time StructAgent in the list
   *
   * @param myList
   * @return
   */
  public StructServiceRequest getLowerTime(ArrayList<StructServiceRequest> myList) {
    StructServiceRequest lowerTime;
    myList = this.timeOrdering(myList);
    lowerTime = myList.get(0);
    return lowerTime;
  }

  /**
   * Implement the by Cost Ordering of myList
   *
   * @param myList
   * @return
   */
  public ArrayList<StructServiceRequest> costOrdering(ArrayList<StructServiceRequest> myList) {
    myList.sort(Comparator.comparing(StructServiceRequest::getServiceCost));
    return myList;
  }

  /**
   * Implement the by Time Ordering of myList
   *
   * @param myList
   * @return
   */
  public ArrayList<StructServiceRequest> timeOrdering(ArrayList<StructServiceRequest> myList) {
    myList.sort(Comparator.comparing(StructServiceRequest::getServiceTime));
    return myList;
  }

  /**
   * Implement the by Reputation Reverse Ordering (Higher value on top) of myList
   *
   * @param myList
   * @return
   */
  public ArrayList<StructServiceRequest> reputationOrdering(ArrayList<StructServiceRequest> myList) {
    myList.sort(Comparator.comparing(StructServiceRequest::getReputation));
    ArrayList<StructServiceRequest> descendingOrderedList = new ArrayList<StructServiceRequest>();
    for (int i = myList.size() - 1; i >= 0; i--) {
      descendingOrderedList.add(myList.get(i));
    }
    return descendingOrderedList;
  }

  /**
   * @return the searchHeuristic
   */
  public String getSearchHeuristic() {
    return searchHeuristic;
  }

  /**
   * @param searchHeuristic the searchHeuristic to set
   */
  public void setSearchHeuristic(String searchHeuristic) {
    this.searchHeuristic = searchHeuristic;
  }

  /**
   * @param documentXML the XML document
   */
  public void setSearchHeuristicFromXml(Document documentXML) {
    searchHeuristic = documentXML.getElementsByTagName("heuristic").item(0).getTextContent();
  }

}
