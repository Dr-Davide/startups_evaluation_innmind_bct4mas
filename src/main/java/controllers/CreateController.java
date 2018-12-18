package controllers;

import agents.BCAgent;
import model.dao.*;
import model.pojo.*;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.UUID;

public class CreateController {
    private static final Logger log =
        Logger.getLogger(CreateController.class);


    /**
     * Wrapper of Create Review called from the demander Agent using the ReviewDAO
     *
     * @param bcAgent
     * @param executerAgentId
     * @param executedFeatureId
     * @param timestamp
     * @param value
     * @return
     */
    public static boolean createDemanderWriterActivity(BCAgent bcAgent, String executerAgentId,
        String executedFeatureId, String timestamp, String value) {
        boolean isCreatedActivity = false;
        ReviewDAO reviewDAO = new ReviewDAO();
        HFClient client = bcAgent.getHfClient();
        User userHF = bcAgent.getUser();
        Channel channelHF = bcAgent.getHfTransactionChannel();
        String writerAgentId = bcAgent.getMyName();
        // String demanderAgentId = demanderAgentId.getSender().getLocalName();
        String demanderAgentId = bcAgent.getMyName();
        try {
          Review reviewPojo =
              setActivity(executerAgentId, executedFeatureId, timestamp, value, writerAgentId,
                  demanderAgentId);

          isCreatedActivity = reviewDAO.create(client, userHF, channelHF, reviewPojo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreatedActivity;

    }



  /**
     * Wrapper of Create Review called from the executer Agent using the ReviewDAO
     *
     * @param bcAgent
     * @param demanderAgentId
     * @param executedFeatureId
     * @param timestamp
     * @param value
     * @return
     */
    public static boolean createExecuterWriterActivity(BCAgent bcAgent, String demanderAgentId,
        String executedFeatureId, String timestamp, String value) {
        boolean isCreatedActivity = false;
        ReviewDAO reviewDAO = new ReviewDAO();
        HFClient client = bcAgent.getHfClient();
        User userHF = bcAgent.getUser();
        Channel channelHF = bcAgent.getHfTransactionChannel();
        String writerAgentId = bcAgent.getMyName();
        // String demanderAgentId = demanderAgentId.getSender().getLocalName();
        String executerAgentId = bcAgent.getMyName();
        try {
            // create service TxId
          Review reviewPojo =
              setActivity(executerAgentId, executedFeatureId, timestamp, value, writerAgentId,
                  demanderAgentId);

          isCreatedActivity = reviewDAO.create(client, userHF, channelHF, reviewPojo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreatedActivity;

    }

  private static Review setActivity(String executerAgentId, String executedFeatureId,
                                    String timestamp, String value, String writerAgentId, String demanderAgentId) {
    // create service TxId
    String executedFeatureTxId = UUID.randomUUID().toString();
    String evaluationId = writerAgentId + demanderAgentId + executerAgentId + executedFeatureTxId;
    Review reviewPojo = new Review();

    reviewPojo.setEvaluationId(evaluationId);
    reviewPojo.setWriterAgentId(writerAgentId);
    reviewPojo.setStartupAgentId(demanderAgentId);
    reviewPojo.setExpertAgentId(executerAgentId);
    reviewPojo.setReviewedFeatureId(executedFeatureId);
    reviewPojo.setReviewedFeatureTxId(executedFeatureTxId);
    reviewPojo.setReviewedFeatureTimestamp(timestamp);
    reviewPojo.setValue(value);
    return reviewPojo;
  }

    /**
     * Create a new agent in the ledger using AgentDAO
     *
     * @param clientHF
     * @param userHF
     * @param channel
     * @param agentName
     * @param agentAddress
     * @return
     * @throws Exception
     */
    public static boolean createAgent(HFClient clientHF, User userHF, Channel channel,
        String agentName, String agentAddress) throws Exception {
        AgentDAO agentDAO = new AgentDAO();

        Agent newAgent = new Agent();

        newAgent.setName(agentName);
        newAgent.setAddress(agentAddress);

        // TODO: Gestire creazione ID(incrementale)
        newAgent.setAgentId(agentName);

        boolean allPeerSuccess = agentDAO.create(clientHF, userHF, channel, newAgent);

        return allPeerSuccess;
    }

    /**
     * Create a new reputation record in the ledger using ReputationDAO
     * @param agentId
     * @param serviceId
     * @param agentRole
     * @param reputationId
     * @param clientHF
     * @param userHF
     * @param channel
     * @param stringInitialReputationValue
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    static boolean createReputation(String agentId, String serviceId, String agentRole,
        String reputationId, HFClient clientHF, User userHF, Channel channel,
        String stringInitialReputationValue) throws ProposalException, InvalidArgumentException {
        boolean allPeerSuccess;
        ReputationDAO reputationDAO = new ReputationDAO();
        InnMindReputation innMindReputationPojo = new InnMindReputation();

        innMindReputationPojo.setInnMindReputationId(reputationId);
        innMindReputationPojo.setAgentId(agentId);
        innMindReputationPojo.setFeatureId(serviceId);
        innMindReputationPojo.setAgentRole(agentRole);
        innMindReputationPojo.setValue(stringInitialReputationValue);

        allPeerSuccess = reputationDAO.create(clientHF, userHF, channel, innMindReputationPojo);

        return allPeerSuccess;
    }

    /**
     * Create only the service NB: not coupled with an agent
     *
     * @param clientHF
     * @param userHF
     * @param channel
     * @param serviceName
     * @param serviceDesc
     * @param serviceComposition
     * @return
     * @throws Exception
     */
    public static boolean createFeature(HFClient clientHF, User userHF, Channel channel,
        String serviceId, String serviceName, String serviceDesc, String serviceComposition)
        throws Exception {
        FeatureDAO featureDAO = new FeatureDAO();

        Feature featurePojo = new Feature();

        // TODO: Gestire creazione ID(incrementale)
        featurePojo.setFeatureId(serviceId);
        featurePojo.setName(serviceName);
//        featurePojo.setDescription(serviceDesc);
      featurePojo.setFeatureComposition(serviceComposition);


        boolean allPeerSuccess = featureDAO.create(clientHF, userHF, channel, featurePojo);

        return allPeerSuccess;

    }

    /**
     * @param clientHF
     * @param userHF
     * @param channel
     * @param serviceId
     * @param agentId
     * @param cost
     * @param time
     * @param description
     * @return
     */
    public static boolean createFeatureRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String serviceId, String agentId, String cost, String time,
        String description)
        throws Exception {

      FeatureRelationAgentDAO serviceRelationAgentDAO = new FeatureRelationAgentDAO();

      FeatureRelationAgent featureRelationAgentPojo = new FeatureRelationAgent();

      String relationId = serviceId + agentId;

      featureRelationAgentPojo.setRelationId(relationId);
      featureRelationAgentPojo.setFeatureId(serviceId);
      featureRelationAgentPojo.setAgentId(agentId);
      featureRelationAgentPojo.setCost(cost);
      featureRelationAgentPojo.setTime(time);
      featureRelationAgentPojo.setDescription(description);

      boolean allPeerSuccess =
            serviceRelationAgentDAO.create(clientHF, userHF, channel, featureRelationAgentPojo);

      return allPeerSuccess;
    }

    /**
     * @param clientHF
     * @param userHF
     * @param channel
     * @param relationId
     * @param serviceId
     * @param agentId
     * @param cost
     * @param time
     * @param description
     * @return
     */
    public static boolean createFeatureRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String relationId, String serviceId, String agentId, String cost,
        String time, String description) throws Exception {

        FeatureRelationAgentDAO serviceRelationAgentDAO = new FeatureRelationAgentDAO();

        FeatureRelationAgent featureRelationAgentPojo = new FeatureRelationAgent();

        featureRelationAgentPojo.setRelationId(relationId);
        featureRelationAgentPojo.setFeatureId(serviceId);
        featureRelationAgentPojo.setAgentId(agentId);
        featureRelationAgentPojo.setCost(cost);
        featureRelationAgentPojo.setTime(time);

        boolean allPeerSuccess =
            serviceRelationAgentDAO.create(clientHF, userHF, channel, featureRelationAgentPojo);

        return allPeerSuccess;
    }
}
