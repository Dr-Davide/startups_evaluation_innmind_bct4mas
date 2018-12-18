package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.CreateController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import model.pojo.Feature;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

/**
 * Carica i servizi presenti nel file di configurazione (DA SOSTITUIRE CON INTERAZIONI GUI) NB: Crea
 * problemi in riaccensione una volta inseriti dei nuovi servizi perché non va a metterli nel
 * bcAgent.featuresList che anzi, viene caricato solo con i servizi presenti nel file di
 * configurazione (Da sistemare tramite UpdateAgentFeaturesList)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class LoadConfigFeaturesListOnSL extends OneShotBehaviour {

    private static final Logger log = Logger.getLogger(LoadConfigFeaturesListOnSL.class);


    private static final long serialVersionUID = -6508594503777326408L;
    BCAgent bcAgent = null;

  public LoadConfigFeaturesListOnSL(BCAgent a) {
    super(a);
    bcAgent = a;
  }

  @Override
  public void action() {

    // EVERYTHING STARTS WITH 6.0,
    // TODO put in the JSON
    String initReputationValue = "6.0";


    try {
      log.info(bcAgent.getLocalName() + " insert his own services");

      /**
       * needs as createFeature query as how much service the agent has (e.g. look on Agent3)
       */

      // TODO: Tutti questi controlli saranno ridondanti(a parte createFeature e
        // isAgentAlreadyInLedger per non creare servizi inutili (che poi non verranno mappati
      // all'agente)
      // perché coupleFeatureWithAgent chiamerà la
      // rispettiva funzione del chaincode che fa i check di integrità internamente

      log.info("Prima di aggiungere i servizi verifico se l'agente è nel ledger");
        if (CheckerController.isAgentAlreadyInLedger(bcAgent)) {
        // PROBLEMA: All'avvio l'agente appena creato non è nel ledger for con sleep è "sistemato"
          log.info("Agente è nel ledger");

        for (int i = 0; i < bcAgent.featuresList.size(); i++) {
          // NB TODO: Usare veramente serviceId e agentId
          String serviceName = bcAgent.featuresList.get(i).getName();
          String serviceId = serviceName;
          String serviceDescription = bcAgent.featuresList.get(i).getDescription();
          String serviceComposition = bcAgent.featuresList.get(i).getFeatureComposition();
          String agentId = bcAgent.getMyName();

            HFClient clientHF = bcAgent.getHfClient();
            Channel channel = bcAgent.getHfTransactionChannel();
            User user = bcAgent.getUser();

            String cost = bcAgent.featuresList.get(i).getCost();
            String time = bcAgent.featuresList.get(i).getTime();

            /////////////////////////////

            boolean success = ComplexWorkflowController
                .createFeatureAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
                    serviceDescription, serviceComposition, agentId, cost, time,
                    initReputationValue, clientHF, channel, user);

            //                createFeatureAndCoupleWithAgent(serviceName, serviceId, serviceDescription, agentId,
            //                    clientHF, channel, user, cost, time);

            /////////////////////////////
            //            boolean success = RangeQueries.createFeatureAndCoupleWithAgent(
            //              bcAgent.getHfClient(), bcAgent.getUser(), bcAgent.getHfTransactionChannel(), serviceId,
            //              serviceName, serviceDescription, agentId, bcAgent.featuresList.get(i).getCost(),
            //              bcAgent.featuresList.get(i).getTime(), initReputationValue);

          //          log.info("Servizio salvato, vado a dormire 3 secondi");
            //          try {
            //            Thread.sleep(3000);
            //          } catch (InterruptedException e) { // TODO Auto-generated
            //            e.printStackTrace();
            //          }
          if (!success) {
            log.info("Servizio già esistente");
          }
          /////////////////////////////

        }
      }
    } catch (

    Exception e) {
      e.printStackTrace();
    }
  }

    private boolean createFeatureAndCoupleWithAgent(String serviceName, String serviceId,
        String serviceDescription, String serviceComposition, String agentId, HFClient clientHF,
        Channel channel, User user,
        String cost, String time) throws Exception {
        boolean allPeerSuccess = false;
        if (!CheckerController.isFeatureMappedWithAgent(serviceId, bcAgent)) {
            log.info("SERVICE NOT MAPPED WITH AGENT");
            if (!CheckerController.isFeatureAlreadyInLedger(serviceId, bcAgent)) {
                log.info("SERVICE DOESN'T EXIST");
                CreateController.createFeature(clientHF, user, channel, serviceId, serviceName,
                    serviceDescription, serviceComposition);
            }
            Feature featurePojo;
            featurePojo = ReadController.getFeature(clientHF, channel, serviceId);

            String newlyCreatedFeatureId = featurePojo.getFeatureId().toString();

            allPeerSuccess = CreateController
                .createFeatureRelationAgent(clientHF, user, channel, newlyCreatedFeatureId, agentId,
                    cost, time, serviceDescription);
        }
        return allPeerSuccess;
    }
}
