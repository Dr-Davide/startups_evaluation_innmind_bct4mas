package controllers;

import agents.BCAgent;
import model.RangeQueries;
import model.pojo.Activity;
import model.pojo.Reputation;
import model.pojo.Service;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

import java.util.ArrayList;
import java.util.List;

public class ComplexWorkflowController {

    private static final Logger log = Logger.getLogger(ComplexWorkflowController.class);

    /**
     * Check Existing and create or update in case using the DAO class CRUD
     *
     * @param bcAgent
     * @param agentId
     * @param serviceId
     * @param agentRole
     * @param newReputationValue
     * @return
     * @throws Exception
     */
    public static boolean updateOrCreateReputation(BCAgent bcAgent, String agentId,
        String serviceId, String agentRole, Double newReputationValue) throws Exception {
        boolean allPeerSuccess;
        String reputationId = agentId + serviceId + agentRole;
        HFClient clientHF = bcAgent.getHfClient();
        User userHF = bcAgent.getUser();
        Channel channel = bcAgent.getHfServiceChannel();
        String stringNewReputationValue = newReputationValue.toString();

        if (CheckerController.isReputationAlreadyInLedger(bcAgent, agentId, serviceId, agentRole)) {
            // UPDATE REPUTATION

            allPeerSuccess = UpdateController
                .updateReputation(reputationId, clientHF, userHF, channel,
                    stringNewReputationValue);
        } else {
            // CREATE REPUTATION

            allPeerSuccess = CreateController
                .createReputation(agentId, serviceId, agentRole, reputationId, clientHF, userHF,
                    channel, stringNewReputationValue);
        }

        return allPeerSuccess;

    }


    private static boolean createServiceAndCoupleWithAgent(BCAgent bcAgent, String serviceId,
        String serviceName, String serviceDescription, String serviceComposition, String agentId,
        String cost, String time, HFClient clientHF, Channel channel, User user,
        ArrayList<String> serviceIds) throws Exception {
      serviceIds.add(serviceId);
        boolean allPeerSuccess;
        if (!CheckerController.isServiceMappedWithAgent(serviceId, bcAgent)) {
            // MAP SERVICE WITH AGENT
            log.info("SERVICE NOT MAPPED WITH AGENT");

            if (!CheckerController.isServiceAlreadyInLedger(serviceId, bcAgent)) {
                // CREATE SERVICE
                log.info("SERVICE DOESN'T EXIST");
                CreateController.createService(clientHF, user, channel, serviceId, serviceName,
                    serviceDescription, serviceComposition);
            }
            // GET SERVICE
            Service servicePojo;
            servicePojo = ReadController.getService(clientHF, channel, serviceId);

            String newlyCreatedServiceId = servicePojo.getServiceId().toString();

          // TODO: ADD CONTROLLO SE IL SERVIZIO COMPOSTO GIA ESISTENTE CORRISPONDE CON QUELLO CHE VOGLIO CREARE ORA
          String serviceCompositionString = servicePojo.getServiceComposition().toString();
          String[] ledgerServiceCompositonPartsRaw = serviceCompositionString.split(",");
          ArrayList<String> ledgerServiceCompositionParts = new ArrayList<>();
          for (int i = 0; i < ledgerServiceCompositonPartsRaw.length; i++) {
            String leafServiceId = ledgerServiceCompositonPartsRaw[i].replace("\"", "");
            ledgerServiceCompositionParts.add(leafServiceId);
          }

          String[] userServiceCompositionPartsRaw = serviceComposition.split(",");
          ArrayList<String> userServiceCompositionParts = new ArrayList<>();
          for (int i = 0; i < userServiceCompositionPartsRaw.length; i++) {
            String leafServiceId = userServiceCompositionPartsRaw[i];
            userServiceCompositionParts.add(leafServiceId);
          }

          log.info("LEDGER SERVICE COMPOSITION: " + ledgerServiceCompositionParts);
          log.info("USER SERVICE COMPOSITION: " + userServiceCompositionParts);

          log.info("SIZE: " + userServiceCompositionParts.size());

          Boolean differentValue = false;
          if (ledgerServiceCompositionParts.size() == userServiceCompositionParts.size()) {
            for (int i = 0; i < userServiceCompositionParts.size() && !differentValue; i++) {
              Boolean notAlreadyFound = true;
              for (int j = 0; j < ledgerServiceCompositionParts.size() && notAlreadyFound; j++) {
                if (userServiceCompositionParts.get(i)
                    .equals(ledgerServiceCompositionParts.get(j))) {
                  notAlreadyFound = false;
                }
              }
              if (notAlreadyFound) {
                differentValue = true;
              }
            }
          } else {
            // NAMING CONFLICT BETWEEN COMPOSITE SERVICE AND LEAF SERVICE
            differentValue = true;
          }

          if (differentValue) {
            log.info("SONO DIFFERENTI COMPOSITE SERVICES CON STESSO ID");
            String newServiceId = bcAgent.bcAgentGui.getDifferentId(serviceId);
            // TODO: Far inserire stesso nome con diverso id per creare nuovo servizio e mapparlo all'agente
            // Ricorsione con newServiceId
            allPeerSuccess = createServiceAndCoupleWithAgent(bcAgent, newServiceId, serviceName,
                serviceDescription, serviceComposition, agentId, cost, time, clientHF, channel,
                user, serviceIds);
          } else {
            log.info("SONO UGUALI COMPOSITE SERVICES");
            // OK MAP SERVICE WITH AGENT
            // MAP SERVICE WITH AGENT
            allPeerSuccess = CreateController
                .createServiceRelationAgent(clientHF, user, channel, newlyCreatedServiceId, agentId,
                    cost, time, serviceDescription);
          }

        } else {
            allPeerSuccess = false;
            log.info("SERVICE ALREADY MAPPED WITH AGENT");
        }
        return allPeerSuccess;
    }

    public static boolean createServiceAndCoupleWithAgentAndCreateReputation(BCAgent bcAgent,
        String serviceId, String serviceName, String serviceDescription, String serviceComposition,
        String agentId, String cost, String time, String initialReputationValue, HFClient clientHF,
        Channel channel,
        User user) throws Exception {
        //        String initialReputationValue = "6.0";
        String agentRole = Reputation.EXECUTER_ROLE;
        boolean allPeerSuccess;
        boolean serviceCreationSuccess;

      ArrayList<String> serviceIds = new ArrayList<>();

        // MAP SERVICE WITH AGENT
        serviceCreationSuccess =
            createServiceAndCoupleWithAgent(bcAgent, serviceId, serviceName, serviceDescription,
                serviceComposition, agentId, cost, time, clientHF, channel, user, serviceIds);
      log.info("SERVICE ID TRIED LIST SIZE: " + serviceIds.size());
      for (String singleId : serviceIds) {
        log.info("SINGLE SERVICE ID TRIED: " + singleId);
      }
      // REFRESH ID
      serviceId = serviceIds.get(serviceIds.size() - 1);

      // TODO: Check if reputation already exist

        if (serviceCreationSuccess) {
          String reputationId = agentId + serviceId + agentRole;


          if (!CheckerController
              .isReputationAlreadyInLedger(bcAgent, agentId, serviceId, agentRole)) {
            // CREATE REPUTATION

            log.info("CREATION OF REPUTATION");
            // CREATE REPUTATION AS EXECUTER of VALUE 6

            allPeerSuccess = CreateController
                .createReputation(agentId, serviceId, agentRole, reputationId, clientHF, user,
                    channel, initialReputationValue);
          } else {
            // REPUTATION ALREADY IN LEDGER
            allPeerSuccess = true;
          }


            log.info("CREATION REPUTATION LOG: " + allPeerSuccess);
        } else {
            log.error("PROBLEM IN SERVICE CREATION AND MAPPING WITH AGENT");
            allPeerSuccess = false;
        }

        return allPeerSuccess;
    }

    public static boolean createServiceAndCoupleWithAgentAndCreateStandarValueReputation(
        BCAgent bcAgent, String serviceId, String serviceName, String serviceDescription,
        String serviceComposition, String agentId, String cost, String time, HFClient clientHF,
        Channel channel, User user)
        throws Exception {
        String initialReputationValue = "6.0";
        String agentRole = Reputation.EXECUTER_ROLE;
        boolean allPeerSuccess;
        boolean serviceCreationSuccess;


        // MAP SERVICE WITH AGENT
        serviceCreationSuccess =
            createServiceAndCoupleWithAgent(bcAgent, serviceId, serviceName, serviceDescription,
                serviceComposition, agentId, cost, time, clientHF, channel, user, null);
        if (serviceCreationSuccess) {
            log.info("CREATION OF REPUTATION");
            // CREATE REPUTATION AS EXECUTER of VALUE 6
            String reputationId = agentId + serviceId + agentRole;
            allPeerSuccess = CreateController
                .createReputation(agentId, serviceId, agentRole, reputationId, clientHF, user,
                    channel, initialReputationValue);
            log.info("CREATION REPUTATION LOG: " + allPeerSuccess);
        } else {
            log.error("PROBLEM IN SERVICE CREATION AND MAPPING WITH AGENT");
            allPeerSuccess = false;
        }

        return allPeerSuccess;
    }

  public static ArrayList<Activity> getLeavesActivitiesList(List<String> compositionTimestampsList,
      ArrayList<Activity> leavesActivitiesList, HFClient hfClient, Channel channel,
      String demanderAgentId, String executerAgentId) {

    for (int i = 0; i < compositionTimestampsList.size(); i++) {
      String leafTimestamp = compositionTimestampsList.get(i);
      ArrayList<Activity> leafActivitiesList = RangeQueries
          .getActivitiesByDemanderExecuterTimestamp(hfClient, channel, demanderAgentId,
              executerAgentId, leafTimestamp);

      leavesActivitiesList.addAll(leafActivitiesList);
      log.info("LEAF" + i + " ACTIVITES LIST: " + leafActivitiesList.size());
    }
    
    return leavesActivitiesList;
  }
}
