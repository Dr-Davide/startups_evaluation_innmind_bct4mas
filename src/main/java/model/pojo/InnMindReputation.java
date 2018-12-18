package model.pojo;

/**
 * POJO representation of the blockchain's asset InnMindReputation
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class InnMindReputation {
  public static final String STARTUP_ROLE = "STARTUP";
  public static final String EXPERT_ROLE = "EXPERT";

  private Object innMindReputationId; // innMindReputationId = agentId + featureId + agentRole
  private Object agentId;
  private Object featureId;
  private Object agentRole;
  private Object value;

  /**
   * @return the innMindReputationId
   */
  public Object getInnMindReputationId() {
    return innMindReputationId;
  }

  /**
   * @param innMindReputationId the innMindReputationId to set
   */
  public void setInnMindReputationId(Object innMindReputationId) {
    this.innMindReputationId = innMindReputationId;
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
   * @return the agentRole
   */
  public Object getAgentRole() {
    return agentRole;
  }

  /**
   * @param agentRole the agentRole to set
   */
  public void setAgentRole(Object agentRole) {
    this.agentRole = agentRole;
  }

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }


}
