package start;

import java.util.List;

/**
 * POJO Class that model the structure of the related JSON (StartClass.JADE_CONFIG_FILE)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class JadeJson2Pojo {

  private String port;
  private String agentImagePath;
  private String noAvatarExpertImagePath;
  private String noAvatarStartupImagePath;
  private String evaluateImagePath;
  private String okIconPath;
  private String messageIconPath;
  private List<String> agentImagesHotelsPath;

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

  public String getNoAvatarExpertImagePath() {
    return noAvatarExpertImagePath;
  }

  public void setNoAvatarExpertImagePath(String noAvatarExpertImagePath) {
    this.noAvatarExpertImagePath = noAvatarExpertImagePath;
  }

  public String getNoAvatarStartupImagePath() {
    return noAvatarStartupImagePath;
  }

  public void setNoAvatarStartupImagePath(String noAvatarStartupImagePath) {
    this.noAvatarStartupImagePath = noAvatarStartupImagePath;
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

  public String getMessageIconPath() {
    return messageIconPath;
  }

  public void setMessageIconPath(String messageIconPath) {
    this.messageIconPath = messageIconPath;
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
