package agents;

import behaviours.AddAgent;
import jade.core.Agent;
import view.AgentHandlerGui;

/**
 * Agent that implements the features of adding and managing agents
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class AgentHandler extends Agent {

    private static final long serialVersionUID = 8981344267613321407L;
    private AgentHandlerGui agentHandlerGui;

  @Override
  protected void setup() {
    agentHandlerGui = new AgentHandlerGui(this);
    agentHandlerGui.showGui();


      // trigger CAAgent in background
      addAgentTrigger(CAAgent.AGENT_NAME, CAAgent.AGENT_TYPE, "");
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param agentName
   * @param agentType
   * @param expirationCertificate
   */
  public void addAgentTrigger(String agentName, String agentType, String expirationCertificate) {
    addBehaviour(new AddAgent(agentName, agentType, expirationCertificate));
  }

}
