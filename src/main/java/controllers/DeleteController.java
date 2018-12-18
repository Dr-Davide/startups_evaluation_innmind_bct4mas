package controllers;

import model.dao.FeatureRelationAgentDAO;
import model.pojo.FeatureRelationAgent;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

public class DeleteController {
    // for now no delete features

    // TODO: Delete FeatureRelationAgent
    public static boolean deleteFeatureRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String serviceId, String agentId) throws Exception {

      FeatureRelationAgentDAO serviceRelationAgentDAO = new FeatureRelationAgentDAO();

      String relationId = serviceId + agentId;

      // FIND the RECORD IN THE BCT
      FeatureRelationAgent featureRelationAgentPojo =
          ReadController.getFeatureRelationAgent(clientHF, channel, relationId);

      boolean allPeerSuccess =
          serviceRelationAgentDAO.delete(clientHF, userHF, channel, featureRelationAgentPojo);

      return allPeerSuccess;
    }
}
