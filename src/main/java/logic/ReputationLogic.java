package logic;

import agents.BCAgent;
import controllers.ComplexWorkflowController;
import controllers.RangeQueriesController;
import controllers.ReadController;
import model.pojo.Activity;
import model.pojo.Reputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ReputationLogic {
    private static final Logger log = Logger.getLogger(ReputationLogic.class);

  private BCAgent bcAgent;
  // TODO: Levare String e lavorare direttamente su Activity
  private String demanderAgentId;
  private String executerAgentId;
  private Activity demanderActivity;
  private Activity executerActivity;

    private static String demanderRole = "DEMANDER";
    private static String executerRole = "EXECUTER";

  /**
   * Initialize with empty strings demander and executer
   */
  public ReputationLogic(BCAgent bcAgentInput) {
    bcAgent = bcAgentInput;
    demanderAgentId = "";
    executerAgentId = "";
    demanderActivity = new Activity();
    executerActivity = new Activity();
  }

  public void deltaBasedWorkflow() {
    Double demanderEvaluation = Double.parseDouble(demanderActivity.getValue().toString());
    Double executerEvaluation = Double.parseDouble(executerActivity.getValue().toString());
    Double deltaEvaluations = 0.0;
    if (demanderEvaluation > executerEvaluation) {
      deltaEvaluations = demanderEvaluation - executerEvaluation;
    } else {
      deltaEvaluations = executerEvaluation - demanderEvaluation;
    }
    Double insufficientValue = 5.0;
    Double deltaThreshold = 2.0;
    if ((demanderEvaluation < insufficientValue && executerEvaluation < insufficientValue)
        || (demanderEvaluation > insufficientValue && executerEvaluation > insufficientValue)) {
      // TODO: Calcola media reputazione con aggiunta evaluations

    }
    if ((demanderEvaluation < insufficientValue || executerEvaluation < insufficientValue)
        && deltaEvaluations > deltaThreshold) {
      // TODO: Complex Reputation Algorithm
    }

  }

  /**
   * Compute the mean Value of reputation history adding the lastEvaluation to the calculation
   *
   * meanValue = ((lastReputationValue * reputationHistoryListSize) + lastEvaluation) / (reputationHistoryListSize + 1)
   *
   * @param reputationHistoryList
   * @param lastEvaluation
   * @return
   */
  private Double computeMeanValue(ArrayList<model.pojo.Reputation> reputationHistoryList,
      Double lastEvaluation) {
    Double sum = 0.0;
    Double meanValue;

    Integer reputationHistorySize = reputationHistoryList.size();

    if (reputationHistorySize > 0) {

      // correct real mean value of evaluations
      sum = Double
          .parseDouble(reputationHistoryList.get(reputationHistorySize - 1).getValue().toString())
          * reputationHistorySize;
    }

    // Add the lastEvaluation in the Reputation computation
    sum = sum + lastEvaluation;

    //    meanValue = sum / i;
    meanValue = sum / (reputationHistorySize + 1);

    log.info("MEAN VALUE: " + meanValue);

    return meanValue;
  }


  /**
   * Compute the Reputation of <code>agentId</code> relative of <code>serviceId</code> with the <code>agentRole</code>
   * now the computation of the reputation is the mean value of all the Evaluations given from the other.
   * In other words: Executer Reputation = Mean Value of all the evaluations given by the Demanders
   * Demander Reputation = Mean Value of all the evaluations given by the Executers (for that service)
   *
   * @param agentId
   * @param serviceId
   * @param agentRole
   * @return
   * @throws ProposalException
   * @throws InvalidArgumentException
   * @throws ParseException
   */
  private Double computeReputation(String agentId, String serviceId, String agentRole)
      throws ProposalException, InvalidArgumentException, ParseException {
    String emptyString = "";
    Double computedReputationValue;

    ArrayList<model.pojo.Reputation> reputationHistoryList;

    String reputationId = agentId + serviceId + agentRole;

    // Get the actual reputation
    Reputation reputation = ReadController
        .getReputation(bcAgent.getHfClient(), bcAgent.getHfServiceChannel(), reputationId);

    log.info("REPUTATION ACTUAL VALUE: " + reputation.getReputationId());
    // UPDATE REPUTATION

      // Get Reputation History
    reputationHistoryList = RangeQueriesController.getReputationHistory(bcAgent, reputationId);

    log.info("REPUTATION HISTORY SIZE: " + reputationHistoryList.size());

    // Get the Last Evaluation depending on the agentRole
    Double lastEvaluation =
        0.0; // put a value only to let it compile because of the if else, normally have to be assigned by the if or if else closures
    if (agentRole.equals(ReputationLogic.executerRole)) {
      // GET THE DEMANDER EVALUATION
      lastEvaluation = Double.parseDouble(demanderActivity.getValue().toString());
    } else if (agentRole.equals(ReputationLogic.demanderRole)) {
      // TODO: Think of logic of Demander Reputation, for now take the value from executerActivity
      // GET THE EXECUTER EVALUATION
      lastEvaluation = Double.parseDouble(executerActivity.getValue().toString());
    }

    // Compute Mean Value
    computedReputationValue = computeMeanValue(reputationHistoryList, lastEvaluation);
    //    }



    return computedReputationValue;
  }

  /**
   * Wrapper of computeReputation input demanderAgentId and Update (or Create) Reputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateDemanderReputation() throws Exception {
    boolean isUpdatedDemanderReputation;

    String serviceId = demanderActivity.getExecutedServiceId().toString();
    String demanderAgentId = demanderActivity.getDemanderAgentId().toString();

    // Compute the new Value
    Double newReputationValue =
        computeReputation(demanderAgentId, serviceId, ReputationLogic.demanderRole);

    // Save the new Value in the BlockChain
    isUpdatedDemanderReputation = ComplexWorkflowController
        .updateOrCreateReputation(bcAgent, demanderAgentId, serviceId, ReputationLogic.demanderRole,
            newReputationValue);

    return isUpdatedDemanderReputation;
  }

  /**
   * Wrapper of computeReputation input executerAgentId and Update (or Create) Reputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateExecuterReputation() throws Exception {
    boolean isUpdatedExecuterReputation;

    String serviceId = executerActivity.getExecutedServiceId().toString();
    String executerAgentId = executerActivity.getExecuterAgentId().toString();

    Double newReputationValue =
        computeReputation(executerAgentId, serviceId, ReputationLogic.executerRole);

    isUpdatedExecuterReputation = ComplexWorkflowController
        .updateOrCreateReputation(bcAgent, executerAgentId, serviceId, ReputationLogic.executerRole,
            newReputationValue);

    return isUpdatedExecuterReputation;
  }

  public void setDemanderAndExecuter(Activity firstActivity, Activity secondActivity) {
    if (firstActivity.getWriterAgentId().toString()
        .equals(firstActivity.getDemanderAgentId().toString())) {
      // Writer Demander
      demanderAgentId = firstActivity.getDemanderAgentId().toString();
      demanderActivity = firstActivity;

    } else if (firstActivity.getWriterAgentId().toString()
        .equals(firstActivity.getExecuterAgentId().toString())) {
      // Writer Executer
        executerAgentId = firstActivity.getExecuterAgentId().toString();
        executerActivity = firstActivity;

    }

    if (secondActivity.getWriterAgentId().toString()
        .equals(secondActivity.getDemanderAgentId().toString())) {
      // Writer Demander
      demanderAgentId = secondActivity.getDemanderAgentId().toString();
      demanderActivity = secondActivity;

    } else if (secondActivity.getWriterAgentId().toString()
        .equals(secondActivity.getExecuterAgentId().toString())) {
      // Writer Executer

      executerAgentId = secondActivity.getExecuterAgentId().toString();
      executerActivity = secondActivity;
    }
  }


  public boolean isDemanderSet() {
    boolean isDemanderSet = false;
    if (demanderAgentId == "") {
      isDemanderSet = false;
    } else {
      isDemanderSet = true;
    }
    return isDemanderSet;
  }

  public boolean isExecuterSet() {
    boolean isExecuterSet = false;
    if (executerAgentId == "") {
      isExecuterSet = false;
    } else {
      isExecuterSet = true;
    }
    return isExecuterSet;
  }

  /**
   * @return the demanderAgent
   */
  public String getDemanderAgent() {
    return demanderAgentId;
  }

  /**
   * @param demanderAgent the demanderAgent to set
   */
  public void setDemanderAgent(String demanderAgent) {
      demanderAgentId = demanderAgent;
  }

  /**
   * @return the executerAgent
   */
  public String getExecuterAgent() {
    return executerAgentId;
  }

  /**
   * @param executerAgent the executerAgent to set
   */
  public void setExecuterAgent(String executerAgent) {
      executerAgentId = executerAgent;
  }

  /**
   * @return the demanderActivity
   */
  public Activity getDemanderActivity() {
    return demanderActivity;
  }

  /**
   * @param demanderActivity the demanderActivity to set
   */
  public void setDemanderActivity(Activity demanderActivity) {
    this.demanderActivity = demanderActivity;
  }

  /**
   * @return the executerActivity
   */
  public Activity getExecuterActivity() {
    return executerActivity;
  }

  /**
   * @param executerActivity the executerActivity to set
   */
  public void setExecuterActivity(Activity executerActivity) {
    this.executerActivity = executerActivity;
  }
}
