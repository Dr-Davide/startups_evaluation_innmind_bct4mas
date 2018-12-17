package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import jade.core.behaviours.OneShotBehaviour;

public class InitBcAgent extends OneShotBehaviour {

    private static final long serialVersionUID = -5824153372874943556L;
    BCAgent bcAgent;

  public InitBcAgent(BCAgent a) {
    super(a);
    bcAgent = a;
  }

  @Override
  public void action() {

      try {
          BCAgentController.setBCAgentValues(bcAgent);
      } catch (Exception e) {
          e.printStackTrace();
      }

  }

}
