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
    static Activity getActivity(HFClient clientHF, Channel channel, String activityId) {
        ActivityDAO activityDAO = new ActivityDAO();
        Optional<Activity> optionalAgent = activityDAO.get(clientHF, channel, activityId);
        Activity activityPojo = optionalAgent.get();
        return activityPojo;
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
    public static Reputation getReputation(HFClient clientHF, Channel channel, String agentId,
        String serviceId, String agentRole) {

        ReputationDAO reputationDAO = new ReputationDAO();

        String reputationId = agentId + serviceId + agentRole;

        Optional<Reputation> optionalReputation =
            reputationDAO.get(clientHF, channel, reputationId);

        Reputation reputationPojo = optionalReputation.get();
        return reputationPojo;
    }

    /**
     * Get the reputation directly from the given composite key
     *
     * @param clientHF
     * @param channel
     * @param reputationId
     * @return
     */
    public static Reputation getReputation(HFClient clientHF, Channel channel,
        String reputationId) {
        ReputationDAO reputationDAO = new ReputationDAO();

        Optional<Reputation> optionalReputation =
            reputationDAO.get(clientHF, channel, reputationId);

        Reputation reputationPojo = optionalReputation.get();

        return reputationPojo;
    }

    /**
     * Get the service directly from the given ID (serviceId)
     *
     * @param clientHF
     * @param channel
     * @param serviceId
     * @return
     */
    public static Service getService(HFClient clientHF, Channel channel, String serviceId) {
        ServiceDAO serviceDAO = new ServiceDAO();
        Optional<Service> optionalService = serviceDAO.get(clientHF, channel, serviceId);
        Service servicePojo = optionalService.get();
        return servicePojo;
    }

    /**
     * Get the serviceRelationAgent directly from the given ID (relationId)
     *
     * @param clientHF
     * @param channel
     * @param relationId
     * @return
     */
    public static ServiceRelationAgent getServiceRelationAgent(HFClient clientHF, Channel channel,
        String relationId) {
        ServiceRelationAgentDAO serviceRelationAgentDAO = new ServiceRelationAgentDAO();
        Optional<ServiceRelationAgent> optionalServiceRelationAgent =
            serviceRelationAgentDAO.get(clientHF, channel, relationId);
        ServiceRelationAgent serviceRelationAgentPojo = optionalServiceRelationAgent.get();
        return serviceRelationAgentPojo;
    }
}
