package start;

import java.util.List;

/**
 * POJO Class that model the structure of the related JSON (StartClass.JADE_CONFIG_FILE)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class JadeJson2Pojo {

  private List<String> agentTypes;
  private List<String> agentNames;
  private String port;
  private String agentImagePath;
  private String evaluateImagePath;
  private String okIconPath;
  private List<String> agentImagesHotelsPath;

  /**
   * @return the agentType
   */
  public List<String> getAgentTypes() {
    return agentTypes;
  }

  /**
   * @param agentType the agentType to set
   */
  public void setAgentTypes(List<String> agentType) {
    agentTypes = agentType;
  }

  /**
   * @return the agentName
   */
  public List<String> getAgentNames() {
    return agentNames;
  }

  /**
   * @param agentName the agentName to set
   */
  public void setAgentNames(List<String> agentName) {
    agentNames = agentName;
  }

  /**
   * @return the port
   */
  public String getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * @return the agentImagePath
   */
  public String getAgentImagePath() {
    return agentImagePath;
  }

  /**
   * @param agentImagePath the agentImagePath to set
   */
  public void setAgentImagePath(String agentImagePath) {
    this.agentImagePath = agentImagePath;
  }

  /**
   * @return the evaluateImagePath
   */
  public String getEvaluateImagePath() {
    return evaluateImagePath;
  }

  /**
   * @param evaluateImagePath the evaluateImagePath to set
   */
  public void setEvaluateImagePath(String evaluateImagePath) {
    this.evaluateImagePath = evaluateImagePath;
  }

  /**
   * @return the okIconPath
   */
  public String getOkIconPath() {
    return okIconPath;
  }

  /**
   * @param okIconPath the okIconPath to set
   */
  public void setOkIconPath(String okIconPath) {
    this.okIconPath = okIconPath;
  }

  /**
   * @return the agentImagesHotelsPath
   */
  public List<String> getAgentImagesHotelsPath() {
    return agentImagesHotelsPath;
  }

  /**
   * @param agentImagesHotelsPath the agentImagesHotelsPath to set
   */
  public void setAgentImagesHotelsPath(List<String> agentImagesHotelsPath) {
    this.agentImagesHotelsPath = agentImagesHotelsPath;
  }

}
