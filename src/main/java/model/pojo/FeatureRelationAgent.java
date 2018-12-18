package model.pojo;

/**
 * POJO representation of the blockchain's asset FeatureRelationAgent
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class FeatureRelationAgent {
  public static final String COST = "COST";
  public static final String TIME = "TIME";
  public static final String DESCRIPTION = "DESCRIPTION";

    private Object relationId; // relationId = featureId + agentId
  private Object featureId;
  private Object agentId;
  private Object cost;
  private Object time;
  private Object description;

  /**
   * @return the relationId
   */
  public Object getRelationId() {
    return relationId;
  }

  /**
   * @param relationId the relationId to set
   */
  public void setRelationId(Object relationId) {
    this.relationId = relationId;
  }

  /**
   * @return the featureId
   */
  public Object getFeatureId() {
    return featureId;
  }

  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(Object featureId) {
    this.featureId = featureId;
  }

  /**
   * @return the agentId
   */
  public Object getAgentId() {
    return agentId;
  }

  /**
   * @param agentId the agentId to set
   */
  public void setAgentId(Object agentId) {
    this.agentId = agentId;
  }

  /**
   * @return the cost
   */
  public Object getCost() {
    return cost;
  }

  /**
   * @param cost the cost to set
   */
  public void setCost(Object cost) {
    this.cost = cost;
  }

  /**
   * @return the time
   */
  public Object getTime() {
    return time;
  }

  /**
   * @param time the time to set
   */
  public void setTime(Object time) {
    this.time = time;
  }

  /**
   * @return the description
   */
  public Object getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(Object description) {
    this.description = description;
  }


}
