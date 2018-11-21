package model.pojo;

/**
 * POJO representation of the blockchain's asset Agent
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Agent {
  private Object agentId;
  private Object name;
  private Object address;

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
   * @return the name
   */
  public Object getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(Object name) {
    this.name = name;
  }

  /**
   * @return the address
   */
  public Object getAddress() {
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(Object address) {
    this.address = address;
  }


}
