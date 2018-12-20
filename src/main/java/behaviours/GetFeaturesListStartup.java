package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.RangeQueriesController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import logic.Heuristic;
import model.RangeQueries;
import model.pojo.Agent;
import model.pojo.Feature;
import model.pojo.FeatureRelationAgent;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import view.StartupAgentGui;

import javax.swing.*;
import java.util.ArrayList;

public class GetFeaturesListStartup extends OneShotBehaviour {
    private static final Logger log = Logger.getLogger(GetFeaturesListStartup.class);

    private static final long serialVersionUID = 9019060558759405131L;
    BCAgent bcAgent;
    private String serviceNeededName;
    private String selectedHeuristic;

    public GetFeaturesListStartup(BCAgent agent) {
        super(agent);
        bcAgent = agent;
    }

    public GetFeaturesListStartup(BCAgent agent, String serviceNameInput, String selectedHeuristicInput) {
        super(agent);
        bcAgent = agent;
        serviceNeededName = serviceNameInput;
        selectedHeuristic = selectedHeuristicInput;
    }

    @Override public void action() {

        // TODO: Uso name as ID
        String agentId = bcAgent.getMyName();
        String featureId = serviceNeededName;

        if (CheckerController.isReputationAlreadyInLedger(bcAgent,agentId, featureId, InnMindReputation.STARTUP_ROLE)) {
            bcAgent.getFeaturesListTrigger(featureId, selectedHeuristic);
        } else {

            bcAgent.bcAgentGui.getDeniedAccessMessage(bcAgent.getMyName(),featureId,InnMindReputation.STARTUP_ROLE);
        }
    }


}
