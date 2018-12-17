package model.pojo;

/**
 * POJO representation of the blockchain's asset Service
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Service {
  private Object serviceId;
  private Object name;
  private Object description;
  private Object serviceComposition;

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

  public Object getServiceComposition() {
    return serviceComposition;
  }

  public void setServiceComposition(Object serviceComposition) {
    this.serviceComposition = serviceComposition;
  }
}
