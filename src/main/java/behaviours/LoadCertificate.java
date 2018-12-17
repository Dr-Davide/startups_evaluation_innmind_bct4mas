package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

/**
 * Ogni secondo verifica l'arrivo di un "interact" message, se sì
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class LoadCertificate extends TickerBehaviour {
    private static final Logger log = Logger.getLogger(LoadCertificate.class);


    private static final long serialVersionUID = 6213001010728019562L;
    BCAgent bcAgent;

  public LoadCertificate(BCAgent a) {
      super(a, 100); // call the ticker behaviour every 0.1 second
	bcAgent = a;

  }

  @Override
  public void onTick() {

      ACLMessage message = myAgent.receive();

      if (message != null) {
          readInteractMessage(message);
      } else {
          log.info("NO INCOMING MESSAGE");
      }
  }

  /**
   *
   *
   * @param message
   */
  private void readInteractMessage(ACLMessage message) {
      {
          switch (message.getPerformative()) {
              case ACLMessage.INFORM:
                  String[] split = message.getContent().split("%");

                  if (split[0].equals("interact")) {
                      // register user and set in bcAgent fields
                      BCAgentController
                          .registerUserAndSetInBcAgent(bcAgent, bcAgent.getLocalName());
                      // initialize channels
                      BCAgentController.initializeChannelsAndSetInBcAgent(bcAgent);
                      stop();
                  }
                  break;
              default:
                  log.error("NO INCOMING INTERACT MESSAGE");
                  break;
          }
      }
  }

}
