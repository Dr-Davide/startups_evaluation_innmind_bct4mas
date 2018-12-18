package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;

public class RefreshBcAgentGui extends OneShotBehaviour {

  private static final long serialVersionUID = 4366703488300870798L;
  private BCAgent bcAgent;

  public RefreshBcAgentGui(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {
    bcAgent.updateManageFeaturesTableModelData();
    bcAgent.updateSelectLeafFeaturesTableModelData();
  }
}
