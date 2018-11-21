package model.pojo;

/**
 * POJO representation of the blockchain's asset Activity
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class Activity {
    private Object evaluationId;
    // 	evaluationId = writerAgentId + demanderAgentId + executerAgentId + executedServiceTxId
  private Object writerAgentId;
  private Object demanderAgentId;
  private Object executerAgentId;
  private Object executedServiceId;
  private Object executedServiceTxId;
  private Object executedServiceTimestamp;
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
   * @return the demanderAgentId
   */
  public Object getDemanderAgentId() {
    return demanderAgentId;
  }

  /**
   * @param demanderAgentId the demanderAgentId to set
   */
  public void setDemanderAgentId(Object demanderAgentId) {
    this.demanderAgentId = demanderAgentId;
  }

  /**
   * @return the executerAgentId
   */
  public Object getExecuterAgentId() {
    return executerAgentId;
  }

  /**
   * @param executerAgentId the executerAgentId to set
   */
  public void setExecuterAgentId(Object executerAgentId) {
    this.executerAgentId = executerAgentId;
  }

  /**
   * @return the executedServiceId
   */
  public Object getExecutedServiceId() {
    return executedServiceId;
  }

  /**
   * @param executedServiceId the executedServiceId to set
   */
  public void setExecutedServiceId(Object executedServiceId) {
    this.executedServiceId = executedServiceId;
  }

  /**
   * @return the executedServiceTxId
   */
  public Object getExecutedServiceTxId() {
    return executedServiceTxId;
  }

  /**
   * @param executedServiceTxId the executedServiceTxId to set
   */
  public void setExecutedServiceTxId(Object executedServiceTxId) {
    this.executedServiceTxId = executedServiceTxId;
  }

  /**
   * @return the executedServiceTimestamp
   */
  public Object getExecutedServiceTimestamp() {
    return executedServiceTimestamp;
  }

  /**
   * @param executedServiceTimestamp the executedServiceTimestamp to set
   */
  public void setExecutedServiceTimestamp(Object executedServiceTimestamp) {
    this.executedServiceTimestamp = executedServiceTimestamp;
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
