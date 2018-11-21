package controllers;

import model.dao.ServiceRelationAgentDAO;
import model.pojo.ServiceRelationAgent;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

public class DeleteController {
    // for now no delete features

    // TODO: Delete ServiceRelationAgent
    public static boolean deleteServiceRelationAgent(HFClient clientHF, User userHF,
        Channel channel, String serviceId, String agentId) throws Exception {

      ServiceRelationAgentDAO serviceRelationAgentDAO = new ServiceRelationAgentDAO();

      String relationId = serviceId + agentId;

      // FIND the RECORD IN THE BCT
      ServiceRelationAgent serviceRelationAgentPojo =
          ReadController.getServiceRelationAgent(clientHF, channel, relationId);

      boolean allPeerSuccess =
          serviceRelationAgentDAO.delete(clientHF, userHF, channel, serviceRelationAgentPojo);

      return allPeerSuccess;
    }
}
