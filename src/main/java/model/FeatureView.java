package model;

/**
 * It's a view that recollect the result of the 3 query on the chaincode that have to be done to
 * have all the information contained in this POJO class: Feature(featureId, featureComposition), FeatureRelationAgent(cost, time, agentId), InnMindReputation(reputation)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class FeatureView {

  private String featureId;
  private String name;
  private String description;
  private String featureComposition;
  private String cost;
  private String time;
  private String reputation;

  /**
   * Empty Constructor
   */
  public FeatureView() {

  }

  /**
   * Initalizer Costructor
   *  @param featureId
   * @param name
   * @param description
   * @param featureComposition
   * @param cost
   * @param time
   * @param reputation
   */
  public FeatureView(String featureId, String name, String description, String featureComposition,
                     String cost, String time,
                     String reputation) {
    this.featureId = featureId;
    this.name = name;
    this.description = description;
    this.featureComposition = featureComposition;
    this.cost = cost;
    this.time = time;
    this.reputation = reputation;
  }

    /**
   * @return the featureId
   */
  public String getFeatureId() {
    return featureId;
  }

  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(String featureId) {
    this.featureId = featureId;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public String getFeatureComposition() {
    return featureComposition;
  }

  public void setFeatureComposition(String featureComposition) {
    this.featureComposition = featureComposition;
  }

  /**
   * @return the cost
   */
  public String getCost() {
    return cost;
  }

  /**
   * @param cost the cost to set
   */
  public void setCost(String cost) {
    this.cost = cost;
  }

  /**
   * @return the time
   */
  public String getTime() {
    return time;
  }

  /**
   * @param time the time to set
   */
  public void setTime(String time) {
    this.time = time;
  }

  /**
   * @return the reputation
   */
  public String getReputation() {
    return reputation;
  }

  /**
   * @param reputation the reputation to set
   */
  public void setReputation(String reputation) {
    this.reputation = reputation;
  }


}
