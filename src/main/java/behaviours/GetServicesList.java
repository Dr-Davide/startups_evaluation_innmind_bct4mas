package behaviours;

import agents.BCAgent;
import controllers.RangeQueriesController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import logic.Heuristic;
import model.RangeQueries;
import model.pojo.Agent;
import model.pojo.Reputation;
import model.pojo.Service;
import model.pojo.ServiceRelationAgent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.swing.*;
import java.util.ArrayList;

public class GetServicesList extends OneShotBehaviour {
  private static final Logger log = Logger.getLogger(GetServicesList.class);

    private static final long serialVersionUID = 9019060558759405131L;
    BCAgent bcAgent;
    private String serviceNeededName;
    private String selectedHeuristic;

  public GetServicesList(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public GetServicesList(BCAgent agent, String serviceNameInput, String selectedHeuristicInput) {
    super(agent);
    bcAgent = agent;
    serviceNeededName = serviceNameInput;
    selectedHeuristic = selectedHeuristicInput;
  }

  @Override public void action() {

    try {
      ArrayList<ServiceRelationAgent> agentsMappedWithService = new ArrayList<>();

      // TODO: serviceId in realtà deve venire dalla range query fatta sul nome del servizio che
      ArrayList<Service> servicesQueryResultList =
          RangeQueriesController.getServicesByServiceName(bcAgent, serviceNeededName);
      log.info("SERVICES QUERY LIST: " + servicesQueryResultList);
      log.info("SERVICES QUERY LIST SIZE: " + servicesQueryResultList.size());

      if (servicesQueryResultList.size() == 0) {
        log.info(
            bcAgent.getMyName() + ": There is no agent offering the searched service: "
                + serviceNeededName);
        JOptionPane.showMessageDialog(bcAgent.bcAgentGui,
            bcAgent.getMyName() + ": There is no agent offering the searched service: "
                + serviceNeededName, "No Service Found", JOptionPane.ERROR_MESSAGE);
      } else {

        for (Service singleService : servicesQueryResultList) {
          String singleServiceNeededId = singleService.getServiceId().toString();
          ArrayList<ServiceRelationAgent> agentsMappedWithSingleService = RangeQueries
              .getAgentsByService(bcAgent.getHfClient(), bcAgent.getHfServiceChannel(),
                  singleServiceNeededId);

          agentsMappedWithService.addAll(agentsMappedWithSingleService);
        }

        ArrayList<model.pojo.Agent> agentList = new ArrayList<>();
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Reputation> reputationList = new ArrayList<>();

        // OLD WITHOUT ORDERING FEATURE
        //          fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
        //              reputationList);

        switch (selectedHeuristic) {
          case Heuristic.COST:
            // ORDER BY COST the ServiceRelationAgent List
            orderByIncreasingCostServiceRelationAgentList(agentsMappedWithService);
            // Create lists from ServiceRelationAgent List
            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
                reputationList);
            break;
          case Heuristic.TIME:
            // ORDER BY TIME the ServiceRelationAgent List
            orderByIncreasingTimeServiceRelationAgentList(agentsMappedWithService);
            // Create lists from ServiceRelationAgent List
            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
                reputationList);
            break;
          case Heuristic.REPUTATION:
            // Create lists from ServiceRelationAgent List
            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
                reputationList);
            for (Reputation singleRepuation : reputationList) {
              singleRepuation.getValue();
            }
            // ORDER BY REPUTATION the Lists (ReputationList, AgentList, ServiceList)
            orderListsByDecreasingReputationValue(agentList, serviceList, reputationList);
            break;
          default:
            log.error("SOMETHING IS WRONG");
            // ORDER BY COST the ServiceRelationAgent List
            orderByIncreasingCostServiceRelationAgentList(agentsMappedWithService);
            // Create lists from ServiceRelationAgent List
            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
                reputationList);
        }


        bcAgent.updateSearchServicesResultTableModelData(agentsMappedWithService, agentList,
            serviceList, reputationList);


      }

      // ritorna l'ID
      //      String serviceNeededId = serviceNeededName;
      //
      //
      //      agentsMappedWithService = RangeQueries
      //          .getAgentsByService(bcAgent.getHfClient(), bcAgent.getHfServiceChannel(),
      //              serviceNeededId);
      //      if (CheckerController.isAgentListEmpty(agentsMappedWithService)) {
      //        log.info(
      //            bcAgent.getMyName() + ": There is no agent offering the searched service: "
      //                + serviceNeededName);
      //        JOptionPane.showMessageDialog(bcAgent.bcAgentGui,
      //            bcAgent.getMyName() + ": There is no agent offering the searched service: "
      //                + serviceNeededName, "No Service Found", JOptionPane.ERROR_MESSAGE);
      //      } else {
      //
      //        ArrayList<model.pojo.Agent> agentList = new ArrayList<>();
      //        ArrayList<Service> serviceList = new ArrayList<>();
      //        ArrayList<Reputation> reputationList = new ArrayList<>();
      //
      //        // OLD WITHOUT ORDERING FEATURE
      //        //          fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
      //        //              reputationList);
      //
      //        switch (selectedHeuristic) {
      //          case Heuristic.COST:
      //            // ORDER BY COST the ServiceRelationAgent List
      //            orderByIncreasingCostServiceRelationAgentList(agentsMappedWithService);
      //            // Create lists from ServiceRelationAgent List
      //            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
      //                reputationList);
      //            break;
      //          case Heuristic.TIME:
      //            // ORDER BY TIME the ServiceRelationAgent List
      //            orderByIncreasingTimeServiceRelationAgentList(agentsMappedWithService);
      //            // Create lists from ServiceRelationAgent List
      //            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
      //                reputationList);
      //            break;
      //          case Heuristic.REPUTATION:
      //            // Create lists from ServiceRelationAgent List
      //            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
      //                reputationList);
      //            for (Reputation singleRepuation : reputationList) {
      //              singleRepuation.getValue();
      //            }
      //            // ORDER BY REPUTATION the Lists (ReputationList, AgentList, ServiceList)
      //            orderListsByDecreasingReputationValue(agentList, serviceList, reputationList);
      //            break;
      //          default:
      //            log.error("SOMETHING IS WRONG");
      //            // ORDER BY COST the ServiceRelationAgent List
      //            orderByIncreasingCostServiceRelationAgentList(agentsMappedWithService);
      //            // Create lists from ServiceRelationAgent List
      //            fillAgentServiceReputationLists(agentsMappedWithService, agentList, serviceList,
      //                reputationList);
      //        }
      //
      //
      //        bcAgent.updateSearchServicesResultTableModelData(agentsMappedWithService, agentList,
      //            serviceList, reputationList);
      //
      //
      //      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void orderByIncreasingTimeServiceRelationAgentList(
      ArrayList<ServiceRelationAgent> agentsMappedWithService) {
    for (int i = 1; i < agentsMappedWithService.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double timeAfter = Double.parseDouble(agentsMappedWithService.get(j).getTime().toString());
        Double timeBefore =
            Double.parseDouble(agentsMappedWithService.get(j - 1).getTime().toString());
        if (timeAfter < timeBefore) {
          ServiceRelationAgent temp = agentsMappedWithService.get(j);
          agentsMappedWithService.set(j, agentsMappedWithService.get(j - 1));
          agentsMappedWithService.set(j - 1, temp);
        }
      }
    }
  }

  private void orderListsByDecreasingReputationValue(ArrayList<Agent> agentList,
      ArrayList<Service> serviceList, ArrayList<Reputation> reputationList) {
    for (int i = 1; i < reputationList.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double reputationValueAfter =
            Double.parseDouble(reputationList.get(j).getValue().toString());
        Double reputationValueBefore =
            Double.parseDouble(reputationList.get(j - 1).getValue().toString());
        if (reputationValueAfter > reputationValueBefore) {
          Reputation reputationTemp = reputationList.get(j);
          reputationList.set(j, reputationList.get(j - 1));
          reputationList.set(j - 1, reputationTemp);

          Agent agentTemp = agentList.get(j);
          agentList.set(j, agentList.get(j - 1));
          agentList.set(j - 1, agentTemp);

          Service serviceTemp = serviceList.get(j);
          serviceList.set(j, serviceList.get(j - 1));
          serviceList.set(j - 1, serviceTemp);
        }
      }
    }
  }

  private void orderByIncreasingCostServiceRelationAgentList(
      ArrayList<ServiceRelationAgent> agentsMappedWithService) {
    for (int i = 1; i < agentsMappedWithService.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double costAfter = Double.parseDouble(agentsMappedWithService.get(j).getCost().toString());
        Double costBefore =
            Double.parseDouble(agentsMappedWithService.get(j - 1).getCost().toString());
        if (costAfter < costBefore) {
          ServiceRelationAgent temp = agentsMappedWithService.get(j);
          agentsMappedWithService.set(j, agentsMappedWithService.get(j - 1));
          agentsMappedWithService.set(j - 1, temp);
        }
      }
    }
  }

  /**
   * Does the 3 query to fill the 3 ArrayList: - Agent ArrayList - Service ArrayList- Reputation
   * ArrayList order matters, same index, same ServiceRelationAgent
   *
   * @param serviceRelationAgents
   * @param agentList
   * @param serviceList
   * @param reputationList
   * @throws ProposalException
   * @throws InvalidArgumentException
   */
  private void fillAgentServiceReputationLists(
      ArrayList<ServiceRelationAgent> serviceRelationAgents,
      ArrayList<model.pojo.Agent> agentList, ArrayList<Service> serviceList,
      ArrayList<Reputation> reputationList) throws ProposalException, InvalidArgumentException {

    for (ServiceRelationAgent singleRelation : serviceRelationAgents) {

      String agentId = singleRelation.getAgentId().toString();
      String serviceId = singleRelation.getServiceId().toString();
      String reputationId = agentId + serviceId + Reputation.EXECUTER_ROLE;
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();

        agentList.add(ReadController.getAgent(clientHF, channel, agentId));

        serviceList.add(ReadController.getService(clientHF, channel, serviceId));

        reputationList.add(ReadController.getReputation(clientHF, channel, reputationId));

    }


  }



}
