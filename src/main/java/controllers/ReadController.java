package controllers;

import model.dao.*;
import model.pojo.*;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.Optional;

public class ReadController {
    private static final Logger log = Logger.getLogger(ReadController.class);

    /**
     * Get the activity directly from the given ID (activityId)
     *
     * @param clientHF
     * @param channel
     * @param activityId
     * @return
     */
    static Review getActivity(HFClient clientHF, Channel channel, String activityId) {
        ReviewDAO reviewDAO = new ReviewDAO();
        Optional<Review> optionalAgent = reviewDAO.get(clientHF, channel, activityId);
        Review reviewPojo = optionalAgent.get();
        return reviewPojo;
    }


    /**
     * Get the agent directly from the given ID (agentId)
     *
     * @param clientHF
     * @param channel
     * @param agentId
     * @return
     */
    public static Agent getAgent(HFClient clientHF, Channel channel, String agentId) {
        AgentDAO agentDAO = new AgentDAO();
        Optional<Agent> optionalAgent = agentDAO.get(clientHF, channel, agentId);
        Agent agentPojo = optionalAgent.get();
        return agentPojo;
    }

    /**
     * Get the reputation from the already decomposed composite key (agentId, serviceId, agentRole)
     *
     * @param clientHF
     * @param channel
     * @param agentId
     * @param serviceId
     * @param agentRole
     * @return
     */
    public static InnMindReputation getReputation(HFClient clientHF, Channel channel, String agentId,
                                                  String serviceId, String agentRole) {

        InnMindReputationDAO innMindReputationDAO = new InnMindReputationDAO();

        String reputationId = agentId + serviceId + agentRole;

        Optional<InnMindReputation> optionalReputation =
            innMindReputationDAO.get(clientHF, channel, reputationId);

        InnMindReputation innMindReputationPojo = optionalReputation.get();
        return innMindReputationPojo;
    }

    /**
     * Get the reputation directly from the given composite key
     *
     * @param clientHF
     * @param channel
     * @param reputationId
     * @return
     */
    public static InnMindReputation getReputation(HFClient clientHF, Channel channel,
                                                  String reputationId) {
        InnMindReputationDAO innMindReputationDAO = new InnMindReputationDAO();

        Optional<InnMindReputation> optionalReputation =
            innMindReputationDAO.get(clientHF, channel, reputationId);

        InnMindReputation innMindReputationPojo = optionalReputation.get();

        return innMindReputationPojo;
    }

    /**
     * Get the service directly from the given ID (serviceId)
     *
     * @param clientHF
     * @param channel
     * @param serviceId
     * @return
     */
    public static Feature getFeature(HFClient clientHF, Channel channel, String serviceId) {
        FeatureDAO featureDAO = new FeatureDAO();
        Optional<Feature> optionalFeature = featureDAO.get(clientHF, channel, serviceId);
        Feature featurePojo = optionalFeature.get();
        return featurePojo;
    }

    /**
     * Get the serviceRelationAgent directly from the given ID (relationId)
     *
     * @param clientHF
     * @param channel
     * @param relationId
     * @return
     */
    public static FeatureRelationAgent getFeatureRelationAgent(HFClient clientHF, Channel channel,
                                                               String relationId) {
        FeatureRelationAgentDAO serviceRelationAgentDAO = new FeatureRelationAgentDAO();
        Optional<FeatureRelationAgent> optionalFeatureRelationAgent =
            serviceRelationAgentDAO.get(clientHF, channel, relationId);
        FeatureRelationAgent featureRelationAgentPojo = optionalFeatureRelationAgent.get();
        return featureRelationAgentPojo;
    }
}
