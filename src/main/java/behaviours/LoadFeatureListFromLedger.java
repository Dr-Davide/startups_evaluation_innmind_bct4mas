package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.CheckerController;
import controllers.RangeQueriesController;
import jade.core.behaviours.OneShotBehaviour;
import model.FeatureView;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Synchronize the bcAgent.serviceList (StructFeature) with the results of the service-agents
 * mappings in the ledger (persistence) For sure behaviour to do in the bootstrap of the application
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class LoadFeatureListFromLedger extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(LoadFeatureListFromLedger.class);


  private static final long serialVersionUID = 2667054425771882233L;
    BCAgent bcAgent = null;

  public LoadFeatureListFromLedger(BCAgent a) {
    super(a);
    bcAgent = a;
  }

  @Override
  public void action() {

    try {
      log.info("Agent " + bcAgent.getLocalName()
          + " getting his services from the ledger (persistence)");
        ArrayList<FeatureView> agentFeaturesList;


      /**
       * needs as createFeature query as how much service the agent has (e.g. look on Agent3)
       */

      // TODO: Tutti questi controlli saranno ridondanti(a parte createFeature e
        // isAgentAlreadyInLedger per non creare servizi inutili (che poi non verranno mappati
      // all'agente)
      // perché coupleFeatureWithAgent chiamerà la
      // rispettiva funzione del chaincode che fa i check di integrità internamente
      log.info("Before adding the services I verify if the agent is in the ledger");
        if (CheckerController.isAgentAlreadyInLedger(bcAgent)) {
          log.info("The agent is in the ledger");

        // TODO: Usare ID al posto del nome
        String agentId = bcAgent.getMyName();


            agentFeaturesList = RangeQueriesController.getStructFeatureListProvidedByAgent(
            bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), agentId);

        bcAgent = BCAgentController.refreshStructFeatureListInAgent(agentFeaturesList, bcAgent);


      } else {
          log.info("The agent is not present in the Ledger");
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }


  }

}
