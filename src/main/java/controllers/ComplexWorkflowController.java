package controllers;

import agents.BCAgent;
import model.RangeQueries;
import model.pojo.Review;
import model.pojo.Feature;
import model.pojo.InnMindReputation;
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
        Channel channel = bcAgent.getHfTransactionChannel();
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


    private static boolean createFeatureAndCoupleWithAgent(BCAgent bcAgent, String serviceId,
        String serviceName, String serviceDescription, String serviceComposition, String agentId,
        String cost, String time, HFClient clientHF, Channel channel, User user,
        ArrayList<String> serviceIds) throws Exception {
      serviceIds.add(serviceId);
        boolean allPeerSuccess;
        if (!CheckerController.isFeatureMappedWithAgent(serviceId, bcAgent)) {
            // MAP FEATURE WITH AGENT
            log.info("SERVICE NOT MAPPED WITH AGENT");

            if (!CheckerController.isFeatureAlreadyInLedger(serviceId, bcAgent)) {
                // CREATE SERVICE
                log.info("SERVICE DOESN'T EXIST");
                CreateController.createFeature(clientHF, user, channel, serviceId, serviceName,
                        serviceComposition);
            }
            // GET FEATURE
            Feature featurePojo;
            featurePojo = ReadController.getFeature(clientHF, channel, serviceId);

            String newlyCreatedFeatureId = featurePojo.getFeatureId().toString();

          // TODO: ADD CONTROLLO SE IL SERVIZIO COMPOSTO GIA ESISTENTE CORRISPONDE CON QUELLO CHE VOGLIO CREARE ORA
          String serviceCompositionString = featurePojo.getFeatureComposition().toString();
          String[] ledgerFeatureCompositonPartsRaw = serviceCompositionString.split(",");
          ArrayList<String> ledgerFeatureCompositionParts = new ArrayList<>();
          for (int i = 0; i < ledgerFeatureCompositonPartsRaw.length; i++) {
            String leafFeatureId = ledgerFeatureCompositonPartsRaw[i].replace("\"", "");
            ledgerFeatureCompositionParts.add(leafFeatureId);
          }

          String[] userFeatureCompositionPartsRaw = serviceComposition.split(",");
          ArrayList<String> userFeatureCompositionParts = new ArrayList<>();
          for (int i = 0; i < userFeatureCompositionPartsRaw.length; i++) {
            String leafFeatureId = userFeatureCompositionPartsRaw[i];
            userFeatureCompositionParts.add(leafFeatureId);
          }

          log.info("LEDGER SERVICE COMPOSITION: " + ledgerFeatureCompositionParts);
          log.info("USER SERVICE COMPOSITION: " + userFeatureCompositionParts);

          log.info("SIZE: " + userFeatureCompositionParts.size());

          Boolean differentValue = false;
          if (ledgerFeatureCompositionParts.size() == userFeatureCompositionParts.size()) {
            for (int i = 0; i < userFeatureCompositionParts.size() && !differentValue; i++) {
              Boolean notAlreadyFound = true;
              for (int j = 0; j < ledgerFeatureCompositionParts.size() && notAlreadyFound; j++) {
                if (userFeatureCompositionParts.get(i)
                    .equals(ledgerFeatureCompositionParts.get(j))) {
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
            String newFeatureId = bcAgent.bcAgentGui.getDifferentId(serviceId);
            // TODO: Far inserire stesso nome con diverso id per creare nuovo servizio e mapparlo all'agente
            // Ricorsione con newFeatureId
            allPeerSuccess = createFeatureAndCoupleWithAgent(bcAgent, newFeatureId, serviceName,
                serviceDescription, serviceComposition, agentId, cost, time, clientHF, channel,
                user, serviceIds);
          } else {
            log.info("SONO UGUALI COMPOSITE SERVICES");
            // OK MAP SERVICE WITH AGENT
            // MAP SERVICE WITH AGENT
            allPeerSuccess = CreateController
                .createFeatureRelationAgent(clientHF, user, channel, newlyCreatedFeatureId, agentId,
                    cost, time, serviceDescription);
          }

        } else {
            allPeerSuccess = false;
            log.info("SERVICE ALREADY MAPPED WITH AGENT");
        }
        return allPeerSuccess;
    }

    public static boolean createFeatureAndCoupleWithAgentAndCreateReputation(BCAgent bcAgent,
        String serviceId, String serviceName, String serviceDescription, String serviceComposition,
        String agentId, String cost, String time, String initialReputationValue, HFClient clientHF,
        Channel channel,
        User user) throws Exception {
        //        String initialReputationValue = "6.0";
        String agentRole = InnMindReputation.EXPERT_ROLE;
        boolean allPeerSuccess;
        boolean serviceCreationSuccess;

      ArrayList<String> serviceIds = new ArrayList<>();

        // MAP SERVICE WITH AGENT
        serviceCreationSuccess =
            createFeatureAndCoupleWithAgent(bcAgent, serviceId, serviceName, serviceDescription,
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

    public static boolean createFeatureAndCoupleWithAgentAndCreateStandarValueReputation(
        BCAgent bcAgent, String serviceId, String serviceName, String serviceDescription,
        String serviceComposition, String agentId, String cost, String time, HFClient clientHF,
        Channel channel, User user)
        throws Exception {
        String initialReputationValue = "6.0";
        String agentRole = InnMindReputation.EXPERT_ROLE;
        boolean allPeerSuccess;
        boolean serviceCreationSuccess;


        // MAP SERVICE WITH AGENT
        serviceCreationSuccess =
            createFeatureAndCoupleWithAgent(bcAgent, serviceId, serviceName, serviceDescription,
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

  public static ArrayList<Review> getLeavesActivitiesList(List<String> compositionTimestampsList,
                                                          ArrayList<Review> leavesActivitiesList, HFClient hfClient, Channel channel,
                                                          String demanderAgentId, String executerAgentId) {

    for (int i = 0; i < compositionTimestampsList.size(); i++) {
      String leafTimestamp = compositionTimestampsList.get(i);
      ArrayList<Review> leafActivitiesList = RangeQueries
          .GetReviewsByStartupExpertTimestamp(hfClient, channel, demanderAgentId,
              executerAgentId, leafTimestamp);

      leavesActivitiesList.addAll(leafActivitiesList);
      log.info("LEAF" + i + " ACTIVITES LIST: " + leafActivitiesList.size());
    }
    
    return leavesActivitiesList;
  }
}
