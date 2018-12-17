package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;

public class DeleteMessage extends OneShotBehaviour {
  private BCAgent bcAgent;
  private Integer deletingMessageIndex;

  public DeleteMessage(BCAgent agent, Integer deletingMessageIndex) {
    super(agent);
    bcAgent = agent;
    this.deletingMessageIndex = deletingMessageIndex;
  }

  public DeleteMessage(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {
    // TODO Auto-generated method stub
    bcAgent.deleteMessage(deletingMessageIndex);
  }
}
