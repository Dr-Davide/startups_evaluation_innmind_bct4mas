package model.pojo;

/**
 * POJO representation of the blockchain's asset Review
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Review {
    private Object evaluationId;
    // 	evaluationId = writerAgentId + startupAgentId + expertAgentId + reviewedFeatureTxId
  private Object writerAgentId;
  private Object startupAgentId;
  private Object expertAgentId;
  private Object reviewedFeatureId;
  private Object reviewedFeatureTxId;
  private Object reviewedFeatureTimestamp;
  private Object value;

  /**
   * @return the evaluationId
   */
  public Object getEvaluationId() {
    return evaluationId;
  }

  /**
   * @param evaluationId the evaluationId to set
   */
  public void setEvaluationId(Object evaluationId) {
    this.evaluationId = evaluationId;
  }

  /**
   * @return the writerAgentId
   */
  public Object getWriterAgentId() {
    return writerAgentId;
  }

  /**
   * @param writerAgentId the writerAgentId to set
   */
  public void setWriterAgentId(Object writerAgentId) {
    this.writerAgentId = writerAgentId;
  }

  /**
   * @return the startupAgentId
   */
  public Object getStartupAgentId() {
    return startupAgentId;
  }

  /**
   * @param startupAgentId the startupAgentId to set
   */
  public void setStartupAgentId(Object startupAgentId) {
    this.startupAgentId = startupAgentId;
  }

  /**
   * @return the expertAgentId
   */
  public Object getExpertAgentId() {
    return expertAgentId;
  }

  /**
   * @param expertAgentId the expertAgentId to set
   */
  public void setExpertAgentId(Object expertAgentId) {
    this.expertAgentId = expertAgentId;
  }

  /**
   * @return the reviewedFeatureId
   */
  public Object getReviewedFeatureId() {
    return reviewedFeatureId;
  }

  /**
   * @param reviewedFeatureId the reviewedFeatureId to set
   */
  public void setReviewedFeatureId(Object reviewedFeatureId) {
    this.reviewedFeatureId = reviewedFeatureId;
  }

  /**
   * @return the reviewedFeatureTxId
   */
  public Object getReviewedFeatureTxId() {
    return reviewedFeatureTxId;
  }

  /**
   * @param reviewedFeatureTxId the reviewedFeatureTxId to set
   */
  public void setReviewedFeatureTxId(Object reviewedFeatureTxId) {
    this.reviewedFeatureTxId = reviewedFeatureTxId;
  }

  /**
   * @return the reviewedFeatureTimestamp
   */
  public Object getReviewedFeatureTimestamp() {
    return reviewedFeatureTimestamp;
  }

  /**
   * @param reviewedFeatureTimestamp the reviewedFeatureTimestamp to set
   */
  public void setReviewedFeatureTimestamp(Object reviewedFeatureTimestamp) {
    this.reviewedFeatureTimestamp = reviewedFeatureTimestamp;
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
