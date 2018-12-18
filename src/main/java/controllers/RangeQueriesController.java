package controllers;

import agents.BCAgent;
import model.RangeQueries;
import model.FeatureView;
import model.dao.FeatureDAO;
import model.pojo.Feature;
import model.pojo.FeatureRelationAgent;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che fa da tramite tra gli agenti (JADE) e FeatureLedgerInteractor che è solo HF Class for
 * doing more complex interactions with the SL
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class RangeQueriesController {
    private static final Logger log = Logger.getLogger(RangeQueriesController.class);


    private FeatureDAO featureDAO;

  // FIRST PART: JAVA DATA LOAD IN LEDGER (WRAPPER OF FeatureLedgerInteraction CREATION FUNCTIONS)

  /**
   * Wrapper of the thigly coupling of: 1) service creation (creation of service record) (if not
   * already exist) 2) mapping with agent (creation of serviceRelationAgent record)
   * 
   * @throws Exception
   */
  public static void loadStructFeatureInLedger(FeatureView featureViewToLoad, BCAgent bcAgent)
      throws Exception {

    String serviceName = featureViewToLoad.getName();
    String serviceId = serviceName;
    String serviceDescription = featureViewToLoad.getDescription();
    String serviceComposition = featureViewToLoad.getFeatureComposition();
    String serviceCost = featureViewToLoad.getCost();
    String serviceTime = featureViewToLoad.getTime();
    String agentId = bcAgent.getMyName();
    String initReputationValue = featureViewToLoad.getReputation();

      HFClient clientHF = bcAgent.getHfClient();
      Channel channel = bcAgent.getHfTransactionChannel();
      User user = bcAgent.getUser();

      // TODO: Ora che abbiamo l'event handling possiamo scomporre i due task in modo da integrare FeatureDAO.create e FeatureRelationAgentDAO.create
      //      boolean printBool = RangeQueries.createFeatureAndCoupleWithAgent(
      //        bcAgent.getHfClient(), bcAgent.getUser(), bcAgent.getHfTransactionChannel(), serviceId,
      //        serviceName, serviceDescription, agentId, serviceCost, serviceTime, initReputationValue);

      boolean printBool = ComplexWorkflowController
          .createFeatureAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
              serviceDescription, serviceComposition, agentId, serviceCost, serviceTime,
              initReputationValue, clientHF, channel, user);

      log.info("My version gives: " + printBool);
  }

  // SECOND PART: CHECKERS ON THE LEDGER


    // THIRD PART: WRAPPER OF THE RANGE QUERY FUNCTIONS ON THE FeatureLedgerInteraction TO JAVA DATA
  // FORMAT

  /**
   * Get the List<String> of agentIds from a list of serviceRelationAgent
   * 
   * @param agentsList
   * @return
   */
  public static List<String> getAgentsIdsList(ArrayList<FeatureRelationAgent> agentsList) {
      List<String> agentsIds = new ArrayList<>();
    int i = 0;
    for (FeatureRelationAgent singleAgent : agentsList) {
      log.info("Agente NUMERO " + i + ": ");
      log.info("RelationId: " + singleAgent.getRelationId().toString());
      log.info("FeatureId: " + singleAgent.getFeatureId().toString());
      log.info("AgentId: " + singleAgent.getAgentId().toString());
      log.info("Cost: " + singleAgent.getCost().toString());
      log.info("Time: " + singleAgent.getTime().toString());
      agentsIds.add(singleAgent.getAgentId().toString());
      i++;
    }
    return agentsIds;
  }

  /**
   * Get the List<String> of serviceIds provided by the agent
   * 
   * @param hfClient
   * @param channel
   * @param agentId
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  public static ArrayList<FeatureView> getStructFeatureListProvidedByAgent(HFClient hfClient,
                                                                           Channel channel, String agentId) throws ProposalException, InvalidArgumentException {
    ArrayList<FeatureView> servicesProvidedByAgentList = new ArrayList<>();

    ArrayList<FeatureRelationAgent> servicesMappedWithAgent =
        RangeQueries.getFeaturesByAgent(hfClient, channel, agentId);
      if (CheckerController.isRangeQueryResultEmpty(servicesMappedWithAgent)) {
        log.info("Features offered by agent " + agentId + ": not present in the ledger");

    } else {
        servicesProvidedByAgentList =
            RangeQueriesController.getStructFeatureListFromFeatureRelationAgentList(hfClient,
              channel, servicesMappedWithAgent);
    }

    return servicesProvidedByAgentList;

  }

  /**
   * Get the ArrayList<StructFeature> of services provided by the agent
   * 
   * @param hfClient
   * @param channel
   * @param featureRelationAgentList
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  private static ArrayList<FeatureView> getStructFeatureListFromFeatureRelationAgentList(
      HFClient hfClient, Channel channel, ArrayList<FeatureRelationAgent> featureRelationAgentList)
      throws ProposalException, InvalidArgumentException {
    ArrayList<FeatureView> agentFeaturesList = new ArrayList<>();
    //    String serviceId;
    String name;
    String cost;
    String time;
    String description;
    String serviceComposition;
    String reputation;

    int i = 0;
    // CYCLE ALL THE SERVICERELATIONAGENT RECORDS
    for (FeatureRelationAgent singleFeatureRelationAgent : featureRelationAgentList) {
      String agentId = singleFeatureRelationAgent.getAgentId().toString();
      String serviceId = singleFeatureRelationAgent.getFeatureId().toString();
      String agentRole = InnMindReputation.EXPERT_ROLE;
      FeatureView agentFeature = new FeatureView();
        Feature featurePojo;
        InnMindReputation innMindReputationPojo;
      // GET THE SERVICE RECORD
        featurePojo = ReadController.getFeature(hfClient, channel, serviceId);
      // TODO: GET THE REPUTATION RECORD
      String reputationId = agentId + serviceId + agentRole;
        innMindReputationPojo = ReadController.getReputation(hfClient, channel, reputationId);

      //      serviceId = featurePojo.getFeatureId().toString();
      name = featurePojo.getName().toString();
      cost = singleFeatureRelationAgent.getCost().toString();
      time = singleFeatureRelationAgent.getTime().toString();
//      description = featurePojo.getDescription().toString();
      serviceComposition = featurePojo.getFeatureComposition().toString();

      reputation = innMindReputationPojo.getValue().toString();

      agentFeature.setFeatureId(serviceId);
      agentFeature.setName(name);
      agentFeature.setCost(cost);
      agentFeature.setTime(time);
//      agentFeature.setDescription(description);
      agentFeature.setFeatureComposition(serviceComposition);
      agentFeature.setReputation(reputation);

      agentFeaturesList.add(agentFeature);
      i++;
    }
    return agentFeaturesList;
  }

  public static ArrayList<Feature> getFeaturesByFeatureName(BCAgent bcAgent, String serviceName)
      throws ParseException {

    HFClient clientHF = bcAgent.getHfClient();
    Channel channel = bcAgent.getHfTransactionChannel();


    ArrayList<Feature> servicesQueryResultList;

    servicesQueryResultList = RangeQueries.getFeaturesByFeatureName(clientHF, channel, serviceName);

    return servicesQueryResultList;


  }

    public static ArrayList<InnMindReputation> getReputationHistory(BCAgent bcAgent, String reputationId)
        throws ParseException {

        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfTransactionChannel();


      ArrayList<InnMindReputation> innMindReputationHistoryList;

        innMindReputationHistoryList = RangeQueries.getReputationHistory(clientHF, channel, reputationId);

        return innMindReputationHistoryList;


    }
}
