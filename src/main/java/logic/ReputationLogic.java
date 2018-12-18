package logic;

import agents.BCAgent;
import controllers.ComplexWorkflowController;
import controllers.RangeQueriesController;
import controllers.ReadController;
import model.pojo.Review;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class ReputationLogic {
    private static final Logger log = Logger.getLogger(ReputationLogic.class);

  private BCAgent bcAgent;
  // TODO: Levare String e lavorare direttamente su Review
  private String demanderAgentId;
  private String executerAgentId;
  private Review demanderReview;
  private Review executerReview;

    private static String demanderRole = "DEMANDER";
    private static String executerRole = "EXECUTER";

  /**
   * Initialize with empty strings demander and executer
   */
  public ReputationLogic(BCAgent bcAgentInput) {
    bcAgent = bcAgentInput;
    demanderAgentId = "";
    executerAgentId = "";
    demanderReview = new Review();
    executerReview = new Review();
  }

  public void deltaBasedWorkflow() {
    Double demanderEvaluation = Double.parseDouble(demanderReview.getValue().toString());
    Double executerEvaluation = Double.parseDouble(executerReview.getValue().toString());
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
      // TODO: Complex InnMindReputation Algorithm
    }

  }

  /**
   * Compute the mean Value of reputation history adding the lastEvaluation to the calculation
   *
   * meanValue = ((lastReputationValue * reputationHistoryListSize) + lastEvaluation) / (reputationHistoryListSize + 1)
   *
   * @param innMindReputationHistoryList
   * @param lastEvaluation
   * @return
   */
  private Double computeMeanValue(ArrayList<InnMindReputation> innMindReputationHistoryList,
      Double lastEvaluation) {
    Double sum = 0.0;
    Double meanValue;

    Integer reputationHistorySize = innMindReputationHistoryList.size();

    if (reputationHistorySize > 0) {

      // correct real mean value of evaluations
      sum = Double
          .parseDouble(innMindReputationHistoryList.get(reputationHistorySize - 1).getValue().toString())
          * reputationHistorySize;
    }

    // Add the lastEvaluation in the InnMindReputation computation
    sum = sum + lastEvaluation;

    //    meanValue = sum / i;
    meanValue = sum / (reputationHistorySize + 1);

    log.info("MEAN VALUE: " + meanValue);

    return meanValue;
  }


  /**
   * Compute the InnMindReputation of <code>agentId</code> relative of <code>serviceId</code> with the <code>agentRole</code>
   * now the computation of the reputation is the mean value of all the Evaluations given from the other.
   * In other words: Executer InnMindReputation = Mean Value of all the evaluations given by the Demanders
   * Demander InnMindReputation = Mean Value of all the evaluations given by the Executers (for that service)
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

    ArrayList<InnMindReputation> innMindReputationHistoryList;

    String reputationId = agentId + serviceId + agentRole;

    // Get the actual innMindReputation
    InnMindReputation innMindReputation = ReadController
        .getReputation(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), reputationId);

    log.info("REPUTATION ACTUAL VALUE: " + innMindReputation.getInnMindReputationId());
    // UPDATE REPUTATION

      // Get InnMindReputation History
    innMindReputationHistoryList = RangeQueriesController.getReputationHistory(bcAgent, reputationId);

    log.info("REPUTATION HISTORY SIZE: " + innMindReputationHistoryList.size());

    // Get the Last Evaluation depending on the agentRole
    Double lastEvaluation =
        0.0; // put a value only to let it compile because of the if else, normally have to be assigned by the if or if else closures
    if (agentRole.equals(ReputationLogic.executerRole)) {
      // GET THE DEMANDER EVALUATION
      lastEvaluation = Double.parseDouble(demanderReview.getValue().toString());
    } else if (agentRole.equals(ReputationLogic.demanderRole)) {
      // TODO: Think of logic of Demander InnMindReputation, for now take the value from executerReview
      // GET THE EXECUTER EVALUATION
      lastEvaluation = Double.parseDouble(executerReview.getValue().toString());
    }

    // Compute Mean Value
    computedReputationValue = computeMeanValue(innMindReputationHistoryList, lastEvaluation);
    //    }



    return computedReputationValue;
  }

  /**
   * Wrapper of computeReputation input demanderAgentId and Update (or Create) InnMindReputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateDemanderReputation() throws Exception {
    boolean isUpdatedDemanderReputation;

    String serviceId = demanderReview.getReviewedFeatureId().toString();
    String demanderAgentId = demanderReview.getStartupAgentId().toString();

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
   * Wrapper of computeReputation input executerAgentId and Update (or Create) InnMindReputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateExecuterReputation() throws Exception {
    boolean isUpdatedExecuterReputation;

    String serviceId = executerReview.getReviewedFeatureId().toString();
    String executerAgentId = executerReview.getExpertAgentId().toString();

    Double newReputationValue =
        computeReputation(executerAgentId, serviceId, ReputationLogic.executerRole);

    isUpdatedExecuterReputation = ComplexWorkflowController
        .updateOrCreateReputation(bcAgent, executerAgentId, serviceId, ReputationLogic.executerRole,
            newReputationValue);

    return isUpdatedExecuterReputation;
  }

  public void setDemanderAndExecuter(Review firstReview, Review secondReview) {
    if (firstReview.getWriterAgentId().toString()
        .equals(firstReview.getStartupAgentId().toString())) {
      // Writer Demander
      demanderAgentId = firstReview.getStartupAgentId().toString();
      demanderReview = firstReview;

    } else if (firstReview.getWriterAgentId().toString()
        .equals(firstReview.getExpertAgentId().toString())) {
      // Writer Executer
        executerAgentId = firstReview.getExpertAgentId().toString();
        executerReview = firstReview;

    }

    if (secondReview.getWriterAgentId().toString()
        .equals(secondReview.getStartupAgentId().toString())) {
      // Writer Demander
      demanderAgentId = secondReview.getStartupAgentId().toString();
      demanderReview = secondReview;

    } else if (secondReview.getWriterAgentId().toString()
        .equals(secondReview.getExpertAgentId().toString())) {
      // Writer Executer

      executerAgentId = secondReview.getExpertAgentId().toString();
      executerReview = secondReview;
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
   * @return the demanderReview
   */
  public Review getDemanderReview() {
    return demanderReview;
  }

  /**
   * @param demanderReview the demanderReview to set
   */
  public void setDemanderReview(Review demanderReview) {
    this.demanderReview = demanderReview;
  }

  /**
   * @return the executerReview
   */
  public Review getExecuterReview() {
    return executerReview;
  }

  /**
   * @param executerReview the executerReview to set
   */
  public void setExecuterReview(Review executerReview) {
    this.executerReview = executerReview;
  }
}
