package model;

/**
 * It's a view that recollect the result of the 3 query on the chaincode that have to be done to
 * have all the information contained in this POJO class: Service(serviceId, serviceComposition), ServiceRelationAgent(cost, time, agentId), Reputation(reputation)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class ServiceView {

  private String serviceId;
  private String name;
  private String description;
  private String serviceComposition;
  private String cost;
  private String time;
  private String reputation;

  /**
   * Empty Constructor
   */
  public ServiceView() {

  }

  /**
   * Initalizer Costructor
   *  @param serviceId
   * @param name
   * @param description
   * @param serviceComposition
   * @param cost
   * @param time
   * @param reputation
   */
  public ServiceView(String serviceId, String name, String description, String serviceComposition,
      String cost, String time,
      String reputation) {
    this.serviceId = serviceId;
    this.name = name;
    this.description = description;
    this.serviceComposition = serviceComposition;
    this.cost = cost;
    this.time = time;
    this.reputation = reputation;
  }

    /**
   * @return the serviceId
   */
  public String getServiceId() {
    return serviceId;
  }

  /**
   * @param serviceId the serviceId to set
   */
  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
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

  public String getServiceComposition() {
    return serviceComposition;
  }

  public void setServiceComposition(String serviceComposition) {
    this.serviceComposition = serviceComposition;
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
