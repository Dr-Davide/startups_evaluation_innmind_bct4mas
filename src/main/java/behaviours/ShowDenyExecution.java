package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class ShowDenyExecution extends OneShotBehaviour {
  private BCAgent bcAgent;
  private ACLMessage message;

  public ShowDenyExecution(BCAgent agent, ACLMessage message) {
    super(agent);
    bcAgent = agent;
    this.message = message;
  }

  public ShowDenyExecution(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {
    // TODO Auto-generated method stub
    String[] parsedMessage = parseMessageContent(message.getContent());
      String serviceId = parsedMessage[0];
      String denialExecuterAgent = parsedMessage[1];
      String showInputDialogMessage ="Demander: " + bcAgent.getLocalName() + "\nyour request has been denied\n"+
              "Denial Agent: "
              + denialExecuterAgent + "\nfor the service: " + serviceId + "\nretry the ask";
      String showInputDialogTitle = "401: Request Denied";


    bcAgent.bcAgentGui.showDenyExecution(showInputDialogMessage ,showInputDialogTitle );
  }

  /**
   * parse[0] -> serviceId parse[1] -> denialExecuterAgent
   * 
   * @param messageContent
   * @return
   */
  private String[] parseMessageContent(String messageContent) {
    String[] splittedContent = messageContent.split("%");
    return splittedContent;
  }
}
