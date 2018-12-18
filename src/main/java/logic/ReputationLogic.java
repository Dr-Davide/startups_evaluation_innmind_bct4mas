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
  private String startupAgentId;
  private String expertAgentId;
  private Review startupReview;
  private Review expertReview;

    private static String startupRole = "STARTUP";
    private static String expertRole = "EXPERT";

  /**
   * Initialize with empty strings demander and executer
   */
  public ReputationLogic(BCAgent bcAgentInput) {
    bcAgent = bcAgentInput;
    startupAgentId = "";
    expertAgentId = "";
    startupReview = new Review();
    expertReview = new Review();
  }

  public void deltaBasedWorkflow() {
    Double startupEvaluation = Double.parseDouble(startupReview.getValue().toString());
    Double expertEvaluation = Double.parseDouble(expertReview.getValue().toString());
    Double deltaEvaluations = 0.0;
    if (startupEvaluation > expertEvaluation) {
      deltaEvaluations = startupEvaluation - expertEvaluation;
    } else {
      deltaEvaluations = expertEvaluation - startupEvaluation;
    }
    Double insufficientValue = 5.0;
    Double deltaThreshold = 2.0;
    if ((startupEvaluation < insufficientValue && expertEvaluation < insufficientValue)
        || (startupEvaluation > insufficientValue && expertEvaluation > insufficientValue)) {
      // TODO: Calcola media reputazione con aggiunta evaluations

    }
    if ((startupEvaluation < insufficientValue || expertEvaluation < insufficientValue)
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
    if (agentRole.equals(ReputationLogic.expertRole)) {
      // GET THE DEMANDER EVALUATION
      lastEvaluation = Double.parseDouble(startupReview.getValue().toString());
    } else if (agentRole.equals(ReputationLogic.startupRole)) {
      // TODO: Think of logic of Demander InnMindReputation, for now take the value from expertReview
      // GET THE EXECUTER EVALUATION
      lastEvaluation = Double.parseDouble(expertReview.getValue().toString());
    }

    // Compute Mean Value
    // TODO: ADD HERE DISAGREEMENT RESOLUTION
    computedReputationValue = computeMeanValue(innMindReputationHistoryList, lastEvaluation);
    //    }



    return computedReputationValue;
  }

  /**
   * Wrapper of computeReputation input startupAgentId and Update (or Create) InnMindReputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateStartupReputation() throws Exception {
    boolean isUpdatedStartupReputation;

    String featureId = startupReview.getReviewedFeatureId().toString();
    String startupAgentId = startupReview.getStartupAgentId().toString();

    // Compute the new Value
    Double newReputationValue =
        computeReputation(startupAgentId, featureId, ReputationLogic.startupRole);

    // Save the new Value in the BlockChain
    isUpdatedStartupReputation = ComplexWorkflowController
        .updateOrCreateReputation(bcAgent, startupAgentId, featureId, ReputationLogic.startupRole,
            newReputationValue);

    return isUpdatedStartupReputation;
  }

  /**
   * Wrapper of computeReputation input expertAgentId and Update (or Create) InnMindReputation
   *
   * @return
   * @throws Exception
   */
  public boolean updateExpertReputation() throws Exception {
    boolean isUpdatedExpertReputation;

    String featureId = expertReview.getReviewedFeatureId().toString();
    String expertAgentId = expertReview.getExpertAgentId().toString();

    Double newReputationValue =
        computeReputation(expertAgentId, featureId, ReputationLogic.expertRole);

    isUpdatedExpertReputation = ComplexWorkflowController
        .updateOrCreateReputation(bcAgent, expertAgentId, featureId, ReputationLogic.expertRole,
            newReputationValue);

    return isUpdatedExpertReputation;
  }

  public void setDemanderAndExecuter(Review firstReview, Review secondReview) {
    if (firstReview.getWriterAgentId().toString()
        .equals(firstReview.getStartupAgentId().toString())) {
      // Writer Demander
      startupAgentId = firstReview.getStartupAgentId().toString();
      startupReview = firstReview;

    } else if (firstReview.getWriterAgentId().toString()
        .equals(firstReview.getExpertAgentId().toString())) {
      // Writer Executer
        expertAgentId = firstReview.getExpertAgentId().toString();
        expertReview = firstReview;

    }

    if (secondReview.getWriterAgentId().toString()
        .equals(secondReview.getStartupAgentId().toString())) {
      // Writer Demander
      startupAgentId = secondReview.getStartupAgentId().toString();
      startupReview = secondReview;

    } else if (secondReview.getWriterAgentId().toString()
        .equals(secondReview.getExpertAgentId().toString())) {
      // Writer Executer

      expertAgentId = secondReview.getExpertAgentId().toString();
      expertReview = secondReview;
    }
  }


  public boolean isDemanderSet() {
    boolean isDemanderSet = false;
    if (startupAgentId == "") {
      isDemanderSet = false;
    } else {
      isDemanderSet = true;
    }
    return isDemanderSet;
  }

  public boolean isExecuterSet() {
    boolean isExecuterSet = false;
    if (expertAgentId == "") {
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
    return startupAgentId;
  }

  /**
   * @param demanderAgent the demanderAgent to set
   */
  public void setDemanderAgent(String demanderAgent) {
      startupAgentId = demanderAgent;
  }

  /**
   * @return the executerAgent
   */
  public String getExecuterAgent() {
    return expertAgentId;
  }

  /**
   * @param executerAgent the executerAgent to set
   */
  public void setExecuterAgent(String executerAgent) {
      expertAgentId = executerAgent;
  }

  /**
   * @return the startupReview
   */
  public Review getStartupReview() {
    return startupReview;
  }

  /**
   * @param startupReview the startupReview to set
   */
  public void setStartupReview(Review startupReview) {
    this.startupReview = startupReview;
  }

  /**
   * @return the expertReview
   */
  public Review getExpertReview() {
    return expertReview;
  }

  /**
   * @param expertReview the expertReview to set
   */
  public void setExpertReview(Review expertReview) {
    this.expertReview = expertReview;
  }
}
