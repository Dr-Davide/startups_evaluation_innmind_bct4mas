package model.pojo;

/**
 * POJO representation of the blockchain's asset Feature
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Feature {
  private Object featureId;
  private Object name;
  private Object featureComposition;

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


  public Object getFeatureComposition() {
    return featureComposition;
  }

  public void setFeatureComposition(Object featureComposition) {
    this.featureComposition = featureComposition;
  }
}
