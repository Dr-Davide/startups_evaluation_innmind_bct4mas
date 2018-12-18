package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.UpdateController;
import jade.core.behaviours.OneShotBehaviour;
import model.pojo.FeatureRelationAgent;
import org.apache.log4j.Logger;

public class UpdateFeatureRelationAgent extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(UpdateFeatureRelationAgent.class);


  private BCAgent bcAgent;
  private String serviceId;
  private String agentId;
  private String newValue;
  private String fieldToUpdate;

  private static final long serialVersionUID = -1338959206287507672L;

  public UpdateFeatureRelationAgent(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public UpdateFeatureRelationAgent(BCAgent agent, String serviceId, String agentId, String newValue, String fieldToUpdate) {
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
            .updateFeatureRelationAgent(serviceId, agentId, bcAgent.getHfClient(),
                bcAgent.getUser(), bcAgent.getHfTransactionChannel(), newValue, fieldToUpdate);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    if (updateSucceed) {
      // now update in the GUI, I keep things off chain with StructFeature
      switch (fieldToUpdate) {
        case FeatureRelationAgent.COST:
          bcAgent = BCAgentController.updateStructFeatureCostInAgent(serviceId, bcAgent, newValue);
          break;
        case FeatureRelationAgent.TIME:
          bcAgent = BCAgentController.updateStructFeatureTimeInAgent(serviceId, bcAgent, newValue);
          break;
        case FeatureRelationAgent.DESCRIPTION:
          bcAgent =
              BCAgentController.updateStructFeatureDescriptionInAgent(serviceId, bcAgent, newValue);
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
