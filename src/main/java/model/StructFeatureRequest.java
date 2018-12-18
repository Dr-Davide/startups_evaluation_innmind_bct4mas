package model;

/**
 * POJO maps Agent Structure risposta query di ricerca servizio su SL agente {@link agentName} offre
 * il servizio cercato al costo {@link serviceCost}, di durata {@link serviceTime}
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class StructFeatureRequest {
  // TODO: Refactoring con agentName e StructFeature

  private String agentName;
  private Integer serviceCost;
  private Integer serviceTime;
  private Float reputation;

  public StructFeatureRequest() {

  }

  public StructFeatureRequest(String agentName, Integer cost, Integer time, Float reputation) {
    this.agentName = agentName;
    serviceCost = cost;
    serviceTime = time;
    this.reputation = reputation;
  }

  public void setAgentName(String name) {
    agentName = name;
  }

  public void setFeatureCost(Integer cost) {
    serviceCost = cost;
  }

  public void setFeatureTime(Integer time) {
    serviceTime = time;
  }

  public String getAgentName() {
    return agentName;
  }

  public Integer getFeatureCost() {
    return serviceCost;
  }

  public Integer getFeatureTime() {
    return serviceTime;
  }

  /**
   * @return the reputation
   */
  public Float getReputation() {
    return reputation;
  }

  /**
   * @param reputation the reputation to set
   */
  public void setReputation(Float reputation) {
    this.reputation = reputation;
  }
}
