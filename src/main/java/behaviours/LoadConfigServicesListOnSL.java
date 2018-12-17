package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.CreateController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import model.pojo.Service;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

/**
 * Carica i servizi presenti nel file di configurazione (DA SOSTITUIRE CON INTERAZIONI GUI) NB: Crea
 * problemi in riaccensione una volta inseriti dei nuovi servizi perché non va a metterli nel
 * bcAgent.servicesList che anzi, viene caricato solo con i servizi presenti nel file di
 * configurazione (Da sistemare tramite UpdateAgentServicesList)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class LoadConfigServicesListOnSL extends OneShotBehaviour {

    private static final Logger log = Logger.getLogger(LoadConfigServicesListOnSL.class);


    private static final long serialVersionUID = -6508594503777326408L;
    BCAgent bcAgent = null;

  public LoadConfigServicesListOnSL(BCAgent a) {
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
       * needs as createService query as how much service the agent has (e.g. look on Agent3)
       */

      // TODO: Tutti questi controlli saranno ridondanti(a parte createService e
        // isAgentAlreadyInLedger per non creare servizi inutili (che poi non verranno mappati
      // all'agente)
      // perché coupleServiceWithAgent chiamerà la
      // rispettiva funzione del chaincode che fa i check di integrità internamente

      log.info("Prima di aggiungere i servizi verifico se l'agente è nel ledger");
        if (CheckerController.isAgentAlreadyInLedger(bcAgent)) {
        // PROBLEMA: All'avvio l'agente appena creato non è nel ledger for con sleep è "sistemato"
          log.info("Agente è nel ledger");

        for (int i = 0; i < bcAgent.servicesList.size(); i++) {
          // NB TODO: Usare veramente serviceId e agentId
          String serviceName = bcAgent.servicesList.get(i).getName();
          String serviceId = serviceName;
          String serviceDescription = bcAgent.servicesList.get(i).getDescription();
          String serviceComposition = bcAgent.servicesList.get(i).getServiceComposition();
          String agentId = bcAgent.getMyName();

            HFClient clientHF = bcAgent.getHfClient();
            Channel channel = bcAgent.getHfServiceChannel();
            User user = bcAgent.getUser();

            String cost = bcAgent.servicesList.get(i).getCost();
            String time = bcAgent.servicesList.get(i).getTime();

            /////////////////////////////

            boolean success = ComplexWorkflowController
                .createServiceAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
                    serviceDescription, serviceComposition, agentId, cost, time,
                    initReputationValue, clientHF, channel, user);

            //                createServiceAndCoupleWithAgent(serviceName, serviceId, serviceDescription, agentId,
            //                    clientHF, channel, user, cost, time);

            /////////////////////////////
            //            boolean success = RangeQueries.createServiceAndCoupleWithAgent(
            //              bcAgent.getHfClient(), bcAgent.getUser(), bcAgent.getHfServiceChannel(), serviceId,
            //              serviceName, serviceDescription, agentId, bcAgent.servicesList.get(i).getCost(),
            //              bcAgent.servicesList.get(i).getTime(), initReputationValue);

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

    private boolean createServiceAndCoupleWithAgent(String serviceName, String serviceId,
        String serviceDescription, String serviceComposition, String agentId, HFClient clientHF,
        Channel channel, User user,
        String cost, String time) throws Exception {
        boolean allPeerSuccess = false;
        if (!CheckerController.isServiceMappedWithAgent(serviceId, bcAgent)) {
            log.info("SERVICE NOT MAPPED WITH AGENT");
            if (!CheckerController.isServiceAlreadyInLedger(serviceId, bcAgent)) {
                log.info("SERVICE DOESN'T EXIST");
                CreateController.createService(clientHF, user, channel, serviceId, serviceName,
                    serviceDescription, serviceComposition);
            }
            Service servicePojo;
            servicePojo = ReadController.getService(clientHF, channel, serviceId);

            String newlyCreatedServiceId = servicePojo.getServiceId().toString();

            allPeerSuccess = CreateController
                .createServiceRelationAgent(clientHF, user, channel, newlyCreatedServiceId, agentId,
                    cost, time, serviceDescription);
        }
        return allPeerSuccess;
    }
}
