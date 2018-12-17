package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.UpdateController;
import jade.core.behaviours.OneShotBehaviour;
import model.pojo.ServiceRelationAgent;
import org.apache.log4j.Logger;

public class UpdateServiceRelationAgent extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(UpdateServiceRelationAgent.class);


  private BCAgent bcAgent;
  private String serviceId;
  private String agentId;
  private String newValue;
  private String fieldToUpdate;

  private static final long serialVersionUID = -1338959206287507672L;

  public UpdateServiceRelationAgent(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public UpdateServiceRelationAgent(BCAgent agent, String serviceId, String agentId, String newValue, String fieldToUpdate) {
    super(agent);
    bcAgent = agent;
    this.serviceId = serviceId;
    this.agentId = agentId;
    this.newValue = newValue;
    this.fieldToUpdate = fieldToUpdate;
  }

  @Override public void action() {

    Object lockWaitingHFNetworkUpdatePropagation = new Object();

    boolean updateSucceed = false;
    // update from the real BCT Ledger
    synchronized (lockWaitingHFNetworkUpdatePropagation) {
      try {
        updateSucceed = UpdateController
            .updateServiceRelationAgent(serviceId, agentId, bcAgent.getHfClient(),
                bcAgent.getUser(), bcAgent.getHfServiceChannel(), newValue, fieldToUpdate);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    if (updateSucceed) {
      // now update in the GUI, I keep things off chain with StructService
      switch (fieldToUpdate) {
        case ServiceRelationAgent.COST:
          bcAgent = BCAgentController.updateStructServiceCostInAgent(serviceId, bcAgent, newValue);
          break;
        case ServiceRelationAgent.TIME:
          bcAgent = BCAgentController.updateStructServiceTimeInAgent(serviceId, bcAgent, newValue);
          break;
        case ServiceRelationAgent.DESCRIPTION:
          bcAgent =
              BCAgentController.updateStructServiceDescriptionInAgent(serviceId, bcAgent, newValue);
          break;
        default: // should be unreachable
          IllegalStateException illegalStateException =
              new IllegalStateException("Wrong field to update, it's not in the expected ones");
          log.error(illegalStateException);
          throw illegalStateException;
      }
      bcAgent.addBehaviour(new RefreshBcAgentGui(bcAgent));
    }



  }
}
