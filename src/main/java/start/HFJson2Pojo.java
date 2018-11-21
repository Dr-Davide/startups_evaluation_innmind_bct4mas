package start;

import java.util.List;

/**
 * POJO Class that model the structure of the related JSON (StartClass.HF_CONFIG_FILE)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class HFJson2Pojo {

  private String orgName;
  private String domain;
  private String fabricUserPath;
  private String certificationAuthorityName;
  String caUrl;
  private String mspId;
  private String adminCertName;
  private List<String> channelNames;
  private List<String> chaincodeNames;
  private List<String> ordererNames, ordererGrpcURLs;
  private List<String> peerNames, peerGrpcURLs;

  /**
   * @return the orgName
   */
  public String getOrgName() {
    return orgName;
  }

  /**
   * @param orgName the orgName to set
   */
  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  /**
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * @param domain the domain to set
   */
  public void setDomain(String domain) {
    this.domain = domain;
  }

  /**
   * @return the fabricUserPath
   */
  public String getFabricUserPath() {
    return fabricUserPath;
  }

  /**
   * @param fabricUserPath the fabricUserPath to set
   */
  public void setFabricUserPath(String fabricUserPath) {
    this.fabricUserPath = fabricUserPath;
  }

  /**
   * @return the certificationAuthorityName
   */
  public String getCertificationAuthorityName() {
    return certificationAuthorityName;
  }

  /**
   * @param certificationAuthorityName the certificationAuthorityName to set
   */
  public void setCertificationAuthorityName(String certificationAuthorityName) {
    this.certificationAuthorityName = certificationAuthorityName;
  }

  /**
   * @return the caUrl
   */
  public String getCaUrl() {
    return caUrl;
  }

  /**
   * @param caUrl the caUrl to set
   */
  public void setCaUrl(String caUrl) {
    this.caUrl = caUrl;
  }

  /**
   * @return the mspId
   */
  public String getMspId() {
    return mspId;
  }

  /**
   * @param mspId the mspId to set
   */
  public void setMspId(String mspId) {
    this.mspId = mspId;
  }

  /**
   * @return the adminCertName
   */
  public String getAdminCertName() {
    return adminCertName;
  }

  /**
   * @param adminCertName the adminCertName to set
   */
  public void setAdminCertName(String adminCertName) {
    this.adminCertName = adminCertName;
  }

  /**
   * @return the channelNames
   */
  public List<String> getChannelNames() {
    return channelNames;
  }

  /**
   * @param channelNames the channelNames to set
   */
  public void setChannelNames(List<String> channelNames) {
    this.channelNames = channelNames;
  }

  /**
   * @return the chaincodeNames
   */
  public List<String> getChaincodeNames() {
    return chaincodeNames;
  }

  /**
   * @param chaincodeNames the chaincodeNames to set
   */
  public void setChaincodeNames(List<String> chaincodeNames) {
    this.chaincodeNames = chaincodeNames;
  }

  /**
   * @return the ordererNames
   */
  public List<String> getOrdererNames() {
    return ordererNames;
  }

  /**
   * @param ordererNames the ordererNames to set
   */
  public void setOrdererNames(List<String> ordererNames) {
    this.ordererNames = ordererNames;
  }

  /**
   * @return the ordererGrpcURLs
   */
  public List<String> getOrdererGrpcURLs() {
    return ordererGrpcURLs;
  }

  /**
   * @param ordererGrpcURLs the ordererGrpcURLs to set
   */
  public void setOrdererGrpcURLs(List<String> ordererGrpcURLs) {
    this.ordererGrpcURLs = ordererGrpcURLs;
  }

  /**
   * @return the peerNames
   */
  public List<String> getPeerNames() {
    return peerNames;
  }

  /**
   * @param peerNames the peerNames to set
   */
  public void setPeerNames(List<String> peerNames) {
    this.peerNames = peerNames;
  }

  /**
   * @return the peerGrpcURLs
   */
  public List<String> getPeerGrpcURLs() {
    return peerGrpcURLs;
  }

  /**
   * @param peerGrpcURLs the peerGrpcURLs to set
   */
  public void setPeerGrpcURLs(List<String> peerGrpcURLs) {
    this.peerGrpcURLs = peerGrpcURLs;
  }

}
