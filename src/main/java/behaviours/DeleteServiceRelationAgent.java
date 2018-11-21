package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.DeleteController;
import jade.core.behaviours.OneShotBehaviour;

public class DeleteServiceRelationAgent extends OneShotBehaviour {

  private BCAgent bcAgent;
  private String serviceId;
  private String agentId;

  private static final long serialVersionUID = -1338959206287507672L;

  public DeleteServiceRelationAgent(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public DeleteServiceRelationAgent(BCAgent agent, String serviceId, String agentId) {
    super(agent);
    bcAgent = agent;
    this.serviceId = serviceId;
    this.agentId = agentId;
  }

  @Override public void action() {

    Object lockWaitingHFNetworkUpdatePropagation = new Object();

    boolean deleteSucceed = false;

    // delete from the real BCT Ledger
    synchronized (lockWaitingHFNetworkUpdatePropagation) {
      try {
        deleteSucceed = DeleteController
            .deleteServiceRelationAgent(bcAgent.getHfClient(), bcAgent.getUser(),
                bcAgent.getHfServiceChannel(), serviceId, agentId);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }


    // now delete in the GUI, I keep things off chain with StructService
    if (deleteSucceed) {
      bcAgent = BCAgentController.deleteStructServiceInAgent(serviceId, bcAgent);

      bcAgent.addBehaviour(new RefreshBcAgentGui(bcAgent));
    }

  }
}
