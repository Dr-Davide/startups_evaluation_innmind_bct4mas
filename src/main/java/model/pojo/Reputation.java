package model.pojo;

/**
 * POJO representation of the blockchain's asset Reputation
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Reputation {
  public static final String DEMANDER_ROLE = "DEMANDER";
  public static final String EXECUTER_ROLE = "EXECUTER";

  private Object reputationId; // reputationId = agentId + serviceId + agentRole
  private Object agentId;
  private Object serviceId;
  private Object agentRole;
  private Object value;

  /**
   * @return the reputationId
   */
  public Object getReputationId() {
    return reputationId;
  }

  /**
   * @param reputationId the reputationId to set
   */
  public void setReputationId(Object reputationId) {
    this.reputationId = reputationId;
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
   * @return the serviceId
   */
  public Object getServiceId() {
    return serviceId;
  }

  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(Object serviceId) {
    this.serviceId = serviceId;
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
