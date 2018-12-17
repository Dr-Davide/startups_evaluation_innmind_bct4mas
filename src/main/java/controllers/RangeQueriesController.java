package controllers;

import agents.BCAgent;
import model.RangeQueries;
import model.ServiceView;
import model.dao.ServiceDAO;
import model.pojo.Reputation;
import model.pojo.Service;
import model.pojo.ServiceRelationAgent;
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
 * Classe che fa da tramite tra gli agenti (JADE) e ServiceLedgerInteractor che è solo HF Class for
 * doing more complex interactions with the SL
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class RangeQueriesController {
    private static final Logger log = Logger.getLogger(RangeQueriesController.class);


    private ServiceDAO serviceDAO;

  // FIRST PART: JAVA DATA LOAD IN LEDGER (WRAPPER OF ServiceLedgerInteraction CREATION FUNCTIONS)

  /**
   * Wrapper of the thigly coupling of: 1) service creation (creation of service record) (if not
   * already exist) 2) mapping with agent (creation of serviceRelationAgent record)
   * 
   * @throws Exception
   */
  public static void loadStructServiceInLedger(ServiceView serviceViewToLoad, BCAgent bcAgent)
      throws Exception {

    String serviceName = serviceViewToLoad.getName();
    String serviceId = serviceName;
    String serviceDescription = serviceViewToLoad.getDescription();
    String serviceComposition = serviceViewToLoad.getServiceComposition();
    String serviceCost = serviceViewToLoad.getCost();
    String serviceTime = serviceViewToLoad.getTime();
    String agentId = bcAgent.getMyName();
    String initReputationValue = serviceViewToLoad.getReputation();

      HFClient clientHF = bcAgent.getHfClient();
      Channel channel = bcAgent.getHfServiceChannel();
      User user = bcAgent.getUser();

      // TODO: Ora che abbiamo l'event handling possiamo scomporre i due task in modo da integrare ServiceDAO.create e ServiceRelationAgentDAO.create
      //      boolean printBool = RangeQueries.createServiceAndCoupleWithAgent(
      //        bcAgent.getHfClient(), bcAgent.getUser(), bcAgent.getHfServiceChannel(), serviceId,
      //        serviceName, serviceDescription, agentId, serviceCost, serviceTime, initReputationValue);

      boolean printBool = ComplexWorkflowController
          .createServiceAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
              serviceDescription, serviceComposition, agentId, serviceCost, serviceTime,
              initReputationValue, clientHF, channel, user);

      log.info("My version gives: " + printBool);
  }

  // SECOND PART: CHECKERS ON THE LEDGER


    // THIRD PART: WRAPPER OF THE RANGE QUERY FUNCTIONS ON THE ServiceLedgerInteraction TO JAVA DATA
  // FORMAT

  /**
   * Get the List<String> of agentIds from a list of serviceRelationAgent
   * 
   * @param agentsList
   * @return
   */
  public static List<String> getAgentsIdsList(ArrayList<ServiceRelationAgent> agentsList) {
      List<String> agentsIds = new ArrayList<>();
    int i = 0;
    for (ServiceRelationAgent singleAgent : agentsList) {
      log.info("Agente NUMERO " + i + ": ");
      log.info("RelationId: " + singleAgent.getRelationId().toString());
      log.info("ServiceId: " + singleAgent.getServiceId().toString());
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
  public static ArrayList<ServiceView> getStructServiceListProvidedByAgent(HFClient hfClient,
      Channel channel, String agentId) throws ProposalException, InvalidArgumentException {
    ArrayList<ServiceView> servicesProvidedByAgentList = new ArrayList<>();

    ArrayList<ServiceRelationAgent> servicesMappedWithAgent =
        RangeQueries.getServicesByAgent(hfClient, channel, agentId);
      if (CheckerController.isRangeQueryResultEmpty(servicesMappedWithAgent)) {
        log.info("Services offered by agent " + agentId + ": not present in the ledger");

    } else {
        servicesProvidedByAgentList =
            RangeQueriesController.getStructServiceListFromServiceRelationAgentList(hfClient,
              channel, servicesMappedWithAgent);
    }

    return servicesProvidedByAgentList;

  }

  /**
   * Get the ArrayList<StructService> of services provided by the agent
   * 
   * @param hfClient
   * @param channel
   * @param serviceRelationAgentList
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  private static ArrayList<ServiceView> getStructServiceListFromServiceRelationAgentList(
      HFClient hfClient, Channel channel, ArrayList<ServiceRelationAgent> serviceRelationAgentList)
      throws ProposalException, InvalidArgumentException {
    ArrayList<ServiceView> agentServicesList = new ArrayList<>();
    //    String serviceId;
    String name;
    String cost;
    String time;
    String description;
    String serviceComposition;
    String reputation;

    int i = 0;
    // CYCLE ALL THE SERVICERELATIONAGENT RECORDS
    for (ServiceRelationAgent singleServiceRelationAgent : serviceRelationAgentList) {
      String agentId = singleServiceRelationAgent.getAgentId().toString();
      String serviceId = singleServiceRelationAgent.getServiceId().toString();
      String agentRole = Reputation.EXECUTER_ROLE;
      ServiceView agentService = new ServiceView();
        Service servicePojo;
        Reputation reputationPojo;
      // GET THE SERVICE RECORD
        servicePojo = ReadController.getService(hfClient, channel, serviceId);
      // TODO: GET THE REPUTATION RECORD
      String reputationId = agentId + serviceId + agentRole;
        reputationPojo = ReadController.getReputation(hfClient, channel, reputationId);

      //      serviceId = servicePojo.getServiceId().toString();
      name = servicePojo.getName().toString();
      cost = singleServiceRelationAgent.getCost().toString();
      time = singleServiceRelationAgent.getTime().toString();
      description = servicePojo.getDescription().toString();
      serviceComposition = servicePojo.getServiceComposition().toString();

      reputation = reputationPojo.getValue().toString();

      agentService.setServiceId(serviceId);
      agentService.setName(name);
      agentService.setCost(cost);
      agentService.setTime(time);
      agentService.setDescription(description);
      agentService.setServiceComposition(serviceComposition);
      agentService.setReputation(reputation);

      agentServicesList.add(agentService);
      i++;
    }
    return agentServicesList;
  }

  public static ArrayList<Service> getServicesByServiceName(BCAgent bcAgent, String serviceName)
      throws ParseException {

    HFClient clientHF = bcAgent.getHfClient();
    Channel channel = bcAgent.getHfServiceChannel();


    ArrayList<Service> servicesQueryResultList;

    servicesQueryResultList = RangeQueries.getServicesByServiceName(clientHF, channel, serviceName);

    return servicesQueryResultList;


  }

    public static ArrayList<Reputation> getReputationHistory(BCAgent bcAgent, String reputationId)
        throws ParseException {

        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();


      ArrayList<Reputation> reputationHistoryList;

        reputationHistoryList = RangeQueries.getReputationHistory(clientHF, channel, reputationId);

        return reputationHistoryList;


    }
}
