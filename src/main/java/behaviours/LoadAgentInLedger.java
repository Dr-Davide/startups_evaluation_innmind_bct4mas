package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.CreateController;
import jade.core.behaviours.OneShotBehaviour;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class LoadAgentInLedger extends OneShotBehaviour {

    private static final Logger log = Logger.getLogger(LoadAgentInLedger.class);
    private static final long serialVersionUID = -1367559804806283124L;

    private BCAgent bcAgent;

    public LoadAgentInLedger(BCAgent a) {
    super(a);
    bcAgent = a;
  }

  @Override
  public void action() {
    log.info("Verifying if the Agent already exist in the Ledger");

    try {
        if (CheckerController.isAgentAlreadyInLedger(bcAgent)) {
        log.info("The agent already exist in the Ledger");
      } else {
        log.info("The agent doesn't exist in the Ledger, I'll create it");

            boolean agentCreated;
            agentCreated = CreateController
              .createAgent(bcAgent.getHfClient(), bcAgent.getUser(), bcAgent.getHfTransactionChannel(),
                  bcAgent.getMyName(), bcAgent.getMyAddress());

            log.info("Agent " + bcAgent.getMyName() + " created: " + agentCreated);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
