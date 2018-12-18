package behaviours;

import agents.BCAgent;
import controllers.RangeQueriesController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import logic.Heuristic;
import model.RangeQueries;
import model.pojo.Agent;
import model.pojo.InnMindReputation;
import model.pojo.Feature;
import model.pojo.FeatureRelationAgent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.swing.*;
import java.util.ArrayList;

public class GetFeaturesList extends OneShotBehaviour {
  private static final Logger log = Logger.getLogger(GetFeaturesList.class);

    private static final long serialVersionUID = 9019060558759405131L;
    BCAgent bcAgent;
    private String serviceNeededName;
    private String selectedHeuristic;

  public GetFeaturesList(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public GetFeaturesList(BCAgent agent, String serviceNameInput, String selectedHeuristicInput) {
    super(agent);
    bcAgent = agent;
    serviceNeededName = serviceNameInput;
    selectedHeuristic = selectedHeuristicInput;
  }

  @Override public void action() {

    try {
      ArrayList<FeatureRelationAgent> agentsMappedWithFeature = new ArrayList<>();

      // TODO: serviceId in realtà deve venire dalla range query fatta sul nome del servizio che
      ArrayList<Feature> servicesQueryResultList =
          RangeQueriesController.getFeaturesByFeatureName(bcAgent, serviceNeededName);
      log.info("SERVICES QUERY LIST: " + servicesQueryResultList);
      log.info("SERVICES QUERY LIST SIZE: " + servicesQueryResultList.size());

      if (servicesQueryResultList.size() == 0) {
        log.info(
            bcAgent.getMyName() + ": There is no agent offering the searched service: "
                + serviceNeededName);
        JOptionPane.showMessageDialog(bcAgent.bcAgentGui,
            bcAgent.getMyName() + ": There is no agent offering the searched service: "
                + serviceNeededName, "No Feature Found", JOptionPane.ERROR_MESSAGE);
      } else {

        for (Feature singleFeature : servicesQueryResultList) {
          String singleFeatureNeededId = singleFeature.getFeatureId().toString();
          ArrayList<FeatureRelationAgent> agentsMappedWithSingleFeature = RangeQueries
              .getAgentsByFeature(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(),
                  singleFeatureNeededId);

          agentsMappedWithFeature.addAll(agentsMappedWithSingleFeature);
        }

        ArrayList<model.pojo.Agent> agentList = new ArrayList<>();
        ArrayList<Feature> serviceList = new ArrayList<>();
        ArrayList<InnMindReputation> innMindReputationList = new ArrayList<>();

        // OLD WITHOUT ORDERING FEATURE
        //          fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
        //              innMindReputationList);

        switch (selectedHeuristic) {
          case Heuristic.COST:
            // ORDER BY COST the FeatureRelationAgent List
            orderByIncreasingCostFeatureRelationAgentList(agentsMappedWithFeature);
            // Create lists from FeatureRelationAgent List
            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
                    innMindReputationList);
            break;
          case Heuristic.TIME:
            // ORDER BY TIME the FeatureRelationAgent List
            orderByIncreasingTimeFeatureRelationAgentList(agentsMappedWithFeature);
            // Create lists from FeatureRelationAgent List
            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
                    innMindReputationList);
            break;
          case Heuristic.REPUTATION:
            // Create lists from FeatureRelationAgent List
            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
                    innMindReputationList);
            for (InnMindReputation singleRepuation : innMindReputationList) {
              singleRepuation.getValue();
            }
            // ORDER BY REPUTATION the Lists (ReputationList, AgentList, FeatureList)
            orderListsByDecreasingReputationValue(agentList, serviceList, innMindReputationList);
            break;
          default:
            log.error("SOMETHING IS WRONG");
            // ORDER BY COST the FeatureRelationAgent List
            orderByIncreasingCostFeatureRelationAgentList(agentsMappedWithFeature);
            // Create lists from FeatureRelationAgent List
            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
                    innMindReputationList);
        }


        bcAgent.updateSearchFeaturesResultTableModelData(agentsMappedWithFeature, agentList,
            serviceList, innMindReputationList);


      }

      // ritorna l'ID
      //      String serviceNeededId = serviceNeededName;
      //
      //
      //      agentsMappedWithFeature = RangeQueries
      //          .getAgentsByFeature(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(),
      //              serviceNeededId);
      //      if (CheckerController.isAgentListEmpty(agentsMappedWithFeature)) {
      //        log.info(
      //            bcAgent.getMyName() + ": There is no agent offering the searched service: "
      //                + serviceNeededName);
      //        JOptionPane.showMessageDialog(bcAgent.startupAgentGui,
      //            bcAgent.getMyName() + ": There is no agent offering the searched service: "
      //                + serviceNeededName, "No Feature Found", JOptionPane.ERROR_MESSAGE);
      //      } else {
      //
      //        ArrayList<model.pojo.Agent> agentList = new ArrayList<>();
      //        ArrayList<Feature> serviceList = new ArrayList<>();
      //        ArrayList<InnMindReputation> reputationList = new ArrayList<>();
      //
      //        // OLD WITHOUT ORDERING FEATURE
      //        //          fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
      //        //              reputationList);
      //
      //        switch (selectedHeuristic) {
      //          case Heuristic.COST:
      //            // ORDER BY COST the FeatureRelationAgent List
      //            orderByIncreasingCostFeatureRelationAgentList(agentsMappedWithFeature);
      //            // Create lists from FeatureRelationAgent List
      //            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
      //                reputationList);
      //            break;
      //          case Heuristic.TIME:
      //            // ORDER BY TIME the FeatureRelationAgent List
      //            orderByIncreasingTimeFeatureRelationAgentList(agentsMappedWithFeature);
      //            // Create lists from FeatureRelationAgent List
      //            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
      //                reputationList);
      //            break;
      //          case Heuristic.REPUTATION:
      //            // Create lists from FeatureRelationAgent List
      //            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
      //                reputationList);
      //            for (InnMindReputation singleRepuation : reputationList) {
      //              singleRepuation.getValue();
      //            }
      //            // ORDER BY REPUTATION the Lists (ReputationList, AgentList, FeatureList)
      //            orderListsByDecreasingReputationValue(agentList, serviceList, reputationList);
      //            break;
      //          default:
      //            log.error("SOMETHING IS WRONG");
      //            // ORDER BY COST the FeatureRelationAgent List
      //            orderByIncreasingCostFeatureRelationAgentList(agentsMappedWithFeature);
      //            // Create lists from FeatureRelationAgent List
      //            fillAgentFeatureReputationLists(agentsMappedWithFeature, agentList, serviceList,
      //                reputationList);
      //        }
      //
      //
      //        bcAgent.updateSearchFeaturesResultTableModelData(agentsMappedWithFeature, agentList,
      //            serviceList, reputationList);
      //
      //
      //      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void orderByIncreasingTimeFeatureRelationAgentList(
      ArrayList<FeatureRelationAgent> agentsMappedWithFeature) {
    for (int i = 1; i < agentsMappedWithFeature.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double timeAfter = Double.parseDouble(agentsMappedWithFeature.get(j).getTime().toString());
        Double timeBefore =
            Double.parseDouble(agentsMappedWithFeature.get(j - 1).getTime().toString());
        if (timeAfter < timeBefore) {
          FeatureRelationAgent temp = agentsMappedWithFeature.get(j);
          agentsMappedWithFeature.set(j, agentsMappedWithFeature.get(j - 1));
          agentsMappedWithFeature.set(j - 1, temp);
        }
      }
    }
  }

  private void orderListsByDecreasingReputationValue(ArrayList<Agent> agentList,
      ArrayList<Feature> serviceList, ArrayList<InnMindReputation> innMindReputationList) {
    for (int i = 1; i < innMindReputationList.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double reputationValueAfter =
            Double.parseDouble(innMindReputationList.get(j).getValue().toString());
        Double reputationValueBefore =
            Double.parseDouble(innMindReputationList.get(j - 1).getValue().toString());
        if (reputationValueAfter > reputationValueBefore) {
          InnMindReputation innMindReputationTemp = innMindReputationList.get(j);
          innMindReputationList.set(j, innMindReputationList.get(j - 1));
          innMindReputationList.set(j - 1, innMindReputationTemp);

          Agent agentTemp = agentList.get(j);
          agentList.set(j, agentList.get(j - 1));
          agentList.set(j - 1, agentTemp);

          Feature serviceTemp = serviceList.get(j);
          serviceList.set(j, serviceList.get(j - 1));
          serviceList.set(j - 1, serviceTemp);
        }
      }
    }
  }

  private void orderByIncreasingCostFeatureRelationAgentList(
      ArrayList<FeatureRelationAgent> agentsMappedWithFeature) {
    for (int i = 1; i < agentsMappedWithFeature.size(); i++) {
      for (int j = i; j > 0; j--) {
        Double costAfter = Double.parseDouble(agentsMappedWithFeature.get(j).getCost().toString());
        Double costBefore =
            Double.parseDouble(agentsMappedWithFeature.get(j - 1).getCost().toString());
        if (costAfter < costBefore) {
          FeatureRelationAgent temp = agentsMappedWithFeature.get(j);
          agentsMappedWithFeature.set(j, agentsMappedWithFeature.get(j - 1));
          agentsMappedWithFeature.set(j - 1, temp);
        }
      }
    }
  }

  /**
   * Does the 3 query to fill the 3 ArrayList: - Agent ArrayList - Feature ArrayList- InnMindReputation
   * ArrayList order matters, same index, same FeatureRelationAgent
   *
   * @param serviceRelationAgents
   * @param agentList
   * @param serviceList
   * @param innMindReputationList
   * @throws ProposalException
   * @throws InvalidArgumentException
   */
  private void fillAgentFeatureReputationLists(
      ArrayList<FeatureRelationAgent> serviceRelationAgents,
      ArrayList<model.pojo.Agent> agentList, ArrayList<Feature> serviceList,
      ArrayList<InnMindReputation> innMindReputationList) throws ProposalException, InvalidArgumentException {

    for (FeatureRelationAgent singleRelation : serviceRelationAgents) {

      String agentId = singleRelation.getAgentId().toString();
      String serviceId = singleRelation.getFeatureId().toString();
      String reputationId = agentId + serviceId + InnMindReputation.EXPERT_ROLE;
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfTransactionChannel();

        agentList.add(ReadController.getAgent(clientHF, channel, agentId));

        serviceList.add(ReadController.getFeature(clientHF, channel, serviceId));

        innMindReputationList.add(ReadController.getReputation(clientHF, channel, reputationId));

    }


  }



}
