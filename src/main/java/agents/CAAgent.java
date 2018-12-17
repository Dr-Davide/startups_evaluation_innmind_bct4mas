package agents;

import behaviours.CAAgentCyclicBehaviour;
import jade.core.Agent;
import view.CAAgentGui;

/**
 * Agente (CA-agent) che si occupa della comunicazione con il server addetto ai certificati nella
 * Fabrc network (Fabric-CA)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class CAAgent extends Agent {

    public static final String AGENT_NAME = "main";
    static final String AGENT_TYPE = "CAAgent";
    private static final long serialVersionUID = 5405872353996101079L;

    private CAAgentGui caAgentGui;

  @Override
  protected void setup() {
    caAgentGui = new CAAgentGui(this);
    caAgentGui.showGui();

    // addBehaviour(new CAagentCyclicBehaviourRetaggi(this));
    addBehaviour(new CAAgentCyclicBehaviour(this));

  }

}
