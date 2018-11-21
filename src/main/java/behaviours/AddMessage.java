package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class AddMessage extends OneShotBehaviour {
  private BCAgent bcAgent;
  private ACLMessage message;

  public AddMessage(BCAgent agent, ACLMessage message) {
    super(agent);
    bcAgent = agent;
    this.message = message;
  }

  public AddMessage(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {
    // TODO Auto-generated method stub
    bcAgent.addMessage(message);
  }
}
