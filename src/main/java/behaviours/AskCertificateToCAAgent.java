package behaviours;

import agents.BCAgent;
import agents.CAAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

public class AskCertificateToCAAgent extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(AskCertificateToCAAgent.class);


    private static final long serialVersionUID = -2998600208928485834L;
    BCAgent bcAgent;

    public AskCertificateToCAAgent(BCAgent a) {
    super(a); // call the ticker behaviour every second (why not a CyclicBehaviour directly?)
    bcAgent = a;
  }

  @Override
  public void action() {
    // TODO Auto-generated method stub
    askCertificate();
  }

  /**
   * the BCAgent ask certificate sending a ACLMessage to CAAgent
   */
  private void askCertificate() {
    try {
      log.info(bcAgent.getLocalName() + " Asking for a certificate...");

      // ask to CAagent to get a user certificate
        sendAskCertificateRequestMessage();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

    private void sendAskCertificateRequestMessage() {
        ACLMessage askCertificateMsg = new ACLMessage(ACLMessage.REQUEST);
        askCertificateMsg.setContent(bcAgent.getLocalName() + "/" + "askCertificate");
        askCertificateMsg.addReceiver(new AID(CAAgent.AGENT_NAME, AID.ISLOCALNAME));
        myAgent.send(askCertificateMsg);
    }

}
