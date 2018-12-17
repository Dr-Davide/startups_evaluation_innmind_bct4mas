package controllers;

import agents.BCAgent;
import model.RangeQueries;
import model.pojo.*;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.ArrayList;

/**
 * Class for doing more complex interactions with the TL
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class CheckerController {
    private static final Logger log = Logger.getLogger(CheckerController.class);


    public static boolean isTimestampTwoTimesActivities(HFClient hfClient, Channel channel,
      String demanderAgentId, String executerAgentId, String timestamp) {
      ArrayList<Activity> activitiesList = new ArrayList<>();
        //    activitiesList = transactionLedgerInteraction.getActivitiesByDemanderExecuterTimestamp(hfClient,
        //        channel, demanderAgentId, executerAgentId, timestamp);
        activitiesList = RangeQueries.getActivitiesByDemanderExecuterTimestamp(hfClient,
        channel, demanderAgentId, executerAgentId, timestamp);
    if (activitiesList.size() != 2) {
      return false;
    } else {
      return true;
    }

  }

    /**
     * Verify if the activity already exist in the ledger using DAO
     *
     * @param bcAgent
     * @return
     */
    public static boolean isActivityAlreadyInLedger(BCAgent bcAgent, String evaluationId) {
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();

        Activity agentPojo = ReadController.getActivity(clientHF, channel, evaluationId);

        boolean isAgentIntheledger = agentPojo.getEvaluationId().toString().equals(evaluationId);

        return isAgentIntheledger;
    }

    /**
     * Verify if the agent already exist in the ledger using DAO
     *
     * @param bcAgent
     * @return
     */
    public static boolean isAgentAlreadyInLedger(BCAgent bcAgent) {
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();
        // TODO: Usare ID veramente
        String agentId = bcAgent.getMyName();

        Agent agentPojo = ReadController.getAgent(clientHF, channel, agentId);

        boolean isAgentIntheledger = agentPojo.getAgentId().toString().equals(agentId);

        return isAgentIntheledger;
    }

    /**
     * Verify if the reputation record already exist in the ledger
     * @param bcAgent
     * @param agentId
     * @param serviceId
     * @param agentRole
     * @return
     */
    static boolean isReputationAlreadyInLedger(BCAgent bcAgent, String agentId, String serviceId,
        String agentRole) {
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();

        String reputationId = agentId + serviceId + agentRole;

        Reputation reputationPojo = ReadController.getReputation(clientHF, channel, reputationId);

        boolean isReputationInTheLedger =
            reputationPojo.getReputationId().toString().equals(reputationId);

        return isReputationInTheLedger;
    }

    /**
     * Verify if the service already exist in the ledger without using DAO
     *
     *
     * @param bcAgent
     * @return
     * @throws ProposalException
     * @throws InvalidArgumentException
     */
    public static boolean isServiceAlreadyInLedger(String serviceId, BCAgent bcAgent) {
        HFClient clientHF = bcAgent.getHfClient();
        Channel channel = bcAgent.getHfServiceChannel();

        Service servicePojo = ReadController.getService(clientHF, channel, serviceId);

        boolean isServiceAlreadyInLedger = servicePojo.getServiceId().toString().equals(serviceId);

        return isServiceAlreadyInLedger;

    }



    /**
     * Generic empty ArrayList checker
     *
     * @param rangeQueryList
     * @return
     */
    static <T> boolean isRangeQueryResultEmpty(ArrayList<T> rangeQueryList) {
        boolean emptyRangeQueryList = false;

        if (rangeQueryList.size() == 0) {
            // vuol dire che il servizio non è mappato con nessun agente
            // it means that the service is not mapped with any agent
            emptyRangeQueryList = true;
        }
        return emptyRangeQueryList;
    }

    /**
     * Only a Wrapper of the generic of isRangeQueryResultEmpty
     *
     * @param agentIdsList
     * @return
     */
    public static boolean isAgentListEmpty(ArrayList<ServiceRelationAgent> agentIdsList) {
        boolean emptyAgentList;
        emptyAgentList = isRangeQueryResultEmpty(agentIdsList);
        return emptyAgentList;
    }

    private static boolean isAgentInAgentList(ArrayList<ServiceRelationAgent> agentsList,
        String agentId) {
        boolean agentInAgentList = false;
        // per segnalare "non trovato" setto il campo agentId del primo agente della lista a ""
        if (isAgentListEmpty(agentsList)) {
            // vuol dire che il servizio non è mappato con nessun agente
            agentInAgentList = false;
        } else {
            // cerco nella lista (index 2 equivale a agentId)
            for (ServiceRelationAgent agentValuesList : agentsList) {
                if (agentValuesList.getAgentId().toString().equals(agentId)) {
                    agentInAgentList = true;
                }
            }
        }
        return agentInAgentList;
    }

    public static boolean isServiceMappedWithAgent(String serviceId, BCAgent bcAgent) {
        ArrayList<ServiceRelationAgent> agentsMappedWithService = RangeQueries
            .getAgentsByService(bcAgent.getHfClient(), bcAgent.getHfServiceChannel(), serviceId);
        String agentId = bcAgent.getMyName();
        boolean isServiceMappedWithAgent = isAgentInAgentList(agentsMappedWithService, agentId);
        return isServiceMappedWithAgent;
    }


}
