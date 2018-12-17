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
     * Wrapper of Create Activity called from the demander Agent using the ActivityDAO
     *
     * @param bcAgent
     * @param executerAgentId
     * @param executedServiceId
     * @param timestamp
     * @param value
     * @return
     */
    public static boolean createDemanderWriterActivity(BCAgent bcAgent, String executerAgentId,
        String executedServiceId, String timestamp, String value) {
        boolean isCreatedActivity = false;
        ActivityDAO activityDAO = new ActivityDAO();
        HFClient client = bcAgent.getHfClient();
        User userHF = bcAgent.getUser();
        Channel channelHF = bcAgent.getHfServiceChannel();
        String writerAgentId = bcAgent.getMyName();
        // String demanderAgentId = demanderAgentId.getSender().getLocalName();
        String demanderAgentId = bcAgent.getMyName();
        try {
          Activity activityPojo =
              setActivity(executerAgentId, executedServiceId, timestamp, value, writerAgentId,
                  demanderAgentId);

          isCreatedActivity = activityDAO.create(client, userHF, channelHF, activityPojo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreatedActivity;

    }



  /**
     * Wrapper of Create Activity called from the executer Agent using the ActivityDAO
     *
     * @param bcAgent
     * @param demanderAgentId
     * @param executedServiceId
     * @param timestamp
     * @param value
     * @return
     */
    public static boolean createExecuterWriterActivity(BCAgent bcAgent, String demanderAgentId,
        String executedServiceId, String timestamp, String value) {
        boolean isCreatedActivity = false;
        ActivityDAO activityDAO = new ActivityDAO();
        HFClient client = bcAgent.getHfClient();
        User userHF = bcAgent.getUser();
        Channel channelHF = bcAgent.getHfServiceChannel();
        String writerAgentId = bcAgent.getMyName();
        // String demanderAgentId = demanderAgentId.getSender().getLocalName();
        String executerAgentId = bcAgent.getMyName();
        try {
            // create service TxId
          Activity activityPojo =
              setActivity(executerAgentId, executedServiceId, timestamp, value, writerAgentId,
                  demanderAgentId);

          isCreatedActivity = activityDAO.create(client, userHF, channelHF, activityPojo);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreatedActivity;

    }

  private static Activity setActivity(String executerAgentId, String executedServiceId,
      String timestamp, String value, String writerAgentId, String demanderAgentId) {
    // create service TxId
    String executedServiceTxId = UUID.randomUUID().toString();
    String evaluationId = writerAgentId + demanderAgentId + executerAgentId + executedServiceTxId;
    Activity activityPojo = new Activity();

    activityPojo.setEvaluationId(evaluationId);
    activityPojo.setWriterAgentId(writerAgentId);
    activityPojo.setDemanderAgentId(demanderAgentId);
    activityPojo.setExecuterAgentId(executerAgentId);
    activityPojo.setExecutedServiceId(executedServiceId);
    activityPojo.setExecutedServiceTxId(executedServiceTxId);
    activityPojo.setExecutedServiceTimestamp(timestamp);
    activityPojo.setValue(value);
    return activityPojo;
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
        Reputation reputationPojo = new Reputation();

        reputationPojo.setReputationId(reputationId);
        reputationPojo.setAgentId(agentId);
        reputationPojo.setServiceId(serviceId);
        reputationPojo.setAgentRole(agentRole);
        reputationPojo.setValue(stringInitialReputationValue);

        allPeerSuccess = reputationDAO.create(clientHF, userHF, channel, reputationPojo);

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
    public static boolean createService(HFClient clientHF, User userHF, Channel channel,
        String serviceId, String serviceName, String serviceDesc, String serviceComposition)
        throws Exception {
        ServiceDAO serviceDAO = new ServiceDAO();

        Service servicePojo = new Service();

        // TODO: Gestire creazione ID(incrementale)
        servicePojo.setServiceId(serviceId);
        servicePojo.setName(serviceName);
        servicePojo.setDescription(serviceDesc);
      servicePojo.setServiceComposition(serviceComposition);


        boolean allPeerSuccess = serviceDAO.create(clientHF, userHF, channel, servicePojo);

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
    public static boolean createServiceRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String serviceId, String agentId, String cost, String time,
        String description)
        throws Exception {

      ServiceRelationAgentDAO serviceRelationAgentDAO = new ServiceRelationAgentDAO();

      ServiceRelationAgent serviceRelationAgentPojo = new ServiceRelationAgent();

      String relationId = serviceId + agentId;

      serviceRelationAgentPojo.setRelationId(relationId);
      serviceRelationAgentPojo.setServiceId(serviceId);
      serviceRelationAgentPojo.setAgentId(agentId);
      serviceRelationAgentPojo.setCost(cost);
      serviceRelationAgentPojo.setTime(time);
      serviceRelationAgentPojo.setDescription(description);

      boolean allPeerSuccess =
            serviceRelationAgentDAO.create(clientHF, userHF, channel, serviceRelationAgentPojo);

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
    public static boolean createServiceRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String relationId, String serviceId, String agentId, String cost,
        String time, String description) throws Exception {

        ServiceRelationAgentDAO serviceRelationAgentDAO = new ServiceRelationAgentDAO();

        ServiceRelationAgent serviceRelationAgentPojo = new ServiceRelationAgent();

        serviceRelationAgentPojo.setRelationId(relationId);
        serviceRelationAgentPojo.setServiceId(serviceId);
        serviceRelationAgentPojo.setAgentId(agentId);
        serviceRelationAgentPojo.setCost(cost);
        serviceRelationAgentPojo.setTime(time);

        boolean allPeerSuccess =
            serviceRelationAgentDAO.create(clientHF, userHF, channel, serviceRelationAgentPojo);

        return allPeerSuccess;
    }
}
