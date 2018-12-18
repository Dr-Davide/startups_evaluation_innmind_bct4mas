package controllers;

import model.dao.InnMindReputationDAO;
import model.dao.FeatureRelationAgentDAO;
import model.pojo.InnMindReputation;
import model.pojo.FeatureRelationAgent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

public class UpdateController {
    // for now only InnMindReputation.value

    // TODO: Feature (name, description)
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
        InnMindReputation innMindReputationPojo = ReadController.getReputation(clientHF, channel, reputationId);
        allPeerSuccess =
            updateReputation(innMindReputationPojo, clientHF, userHF, channel, stringNewReputationValue);
        return allPeerSuccess;
    }

    /**
     * Update the reputation from the given model.pojo.InnMindReputation
     *
     * @param innMindReputationPojo
     * @param clientHF
     * @param userHF
     * @param channel
     * @param stringNewReputationValue
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    private static boolean updateReputation(InnMindReputation innMindReputationPojo, HFClient clientHF,
                                            User userHF, Channel channel, String stringNewReputationValue)
        throws ProposalException, InvalidArgumentException {
        boolean allPeerSuccess;
        String[] updateParams = {stringNewReputationValue};
        InnMindReputationDAO innMindReputationDAO = new InnMindReputationDAO();
        allPeerSuccess =
            innMindReputationDAO.update(clientHF, userHF, channel, innMindReputationPojo, updateParams);
        return allPeerSuccess;
    }

  public static boolean updateFeatureRelationAgent(String serviceId, String agentId,
      HFClient clientHF, User userHF, Channel channel, String newValue, String fieldToUpdate)
      throws ProposalException, InvalidArgumentException {
    boolean allPeerSuccess;

    String relationId = serviceId + agentId;

    FeatureRelationAgent serviceRelationAgentPojo =
        ReadController.getFeatureRelationAgent(clientHF, channel, relationId);
    allPeerSuccess =
        updateFeatureRelationAgent(serviceRelationAgentPojo, clientHF, userHF, channel, newValue,
            fieldToUpdate);
    return allPeerSuccess;
  }

  private static boolean updateFeatureRelationAgent(FeatureRelationAgent serviceRelationAgentPojo,
      HFClient clientHF, User userHF, Channel channel, String newValue, String fieldToUpdate)
      throws ProposalException, InvalidArgumentException {
    boolean allPeerSuccess;
    String[] updateParams = {newValue, fieldToUpdate};
    FeatureRelationAgentDAO reputationDAO = new FeatureRelationAgentDAO();
    allPeerSuccess =
        reputationDAO.update(clientHF, userHF, channel, serviceRelationAgentPojo, updateParams);
    return allPeerSuccess;
  }
}
