package controllers;

import model.dao.ReputationDAO;
import model.dao.ServiceRelationAgentDAO;
import model.pojo.Reputation;
import model.pojo.ServiceRelationAgent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class UpdateController {
    // for now only Reputation.value

    // TODO: Service (name, description)
    // TODO: Agent (name)
    
    private static final Logger log = Logger.getLogger(UpdateController.class);

    /**
     * Update the reputation from the given ID (reputationId)
     *
     * @param reputationId
     * @param clientHF
     * @param userHF
     * @param channel
     * @param stringNewReputationValue
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    static boolean updateReputation(String reputationId, HFClient clientHF, User userHF,
        Channel channel, String stringNewReputationValue)
        throws ProposalException, InvalidArgumentException {
        boolean allPeerSuccess;
        Reputation reputationPojo = ReadController.getReputation(clientHF, channel, reputationId);
        allPeerSuccess =
            updateReputation(reputationPojo, clientHF, userHF, channel, stringNewReputationValue);
        return allPeerSuccess;
    }

    /**
     * Update the reputation from the given model.pojo.Reputation
     *
     * @param reputationPojo
     * @param clientHF
     * @param userHF
     * @param channel
     * @param stringNewReputationValue
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    private static boolean updateReputation(Reputation reputationPojo, HFClient clientHF,
        User userHF, Channel channel, String stringNewReputationValue)
        throws ProposalException, InvalidArgumentException {
        boolean allPeerSuccess;
        String[] updateParams = {stringNewReputationValue};
        ReputationDAO reputationDAO = new ReputationDAO();
        allPeerSuccess =
            reputationDAO.update(clientHF, userHF, channel, reputationPojo, updateParams);
        return allPeerSuccess;
    }

  public static boolean updateServiceRelationAgent(String serviceId, String agentId,
      HFClient clientHF, User userHF, Channel channel, String newValue, String fieldToUpdate)
      throws ProposalException, InvalidArgumentException {
    boolean allPeerSuccess;

    String relationId = serviceId + agentId;

    ServiceRelationAgent serviceRelationAgentPojo =
        ReadController.getServiceRelationAgent(clientHF, channel, relationId);
    allPeerSuccess =
        updateServiceRelationAgent(serviceRelationAgentPojo, clientHF, userHF, channel, newValue,
            fieldToUpdate);
    return allPeerSuccess;
  }

  private static boolean updateServiceRelationAgent(ServiceRelationAgent serviceRelationAgentPojo,
      HFClient clientHF, User userHF, Channel channel, String newValue, String fieldToUpdate)
      throws ProposalException, InvalidArgumentException {
    boolean allPeerSuccess;
    String[] updateParams = {newValue, fieldToUpdate};
    ServiceRelationAgentDAO reputationDAO = new ServiceRelationAgentDAO();
    allPeerSuccess =
        reputationDAO.update(clientHF, userHF, channel, serviceRelationAgentPojo, updateParams);
    return allPeerSuccess;
  }
}
