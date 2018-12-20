package behaviours;

import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import messages.BCMessage;
import org.apache.log4j.Logger;

import java.sql.Timestamp;

public class AcceptReviewFeature extends OneShotBehaviour {
    private static final Logger log = Logger.getLogger(AcceptReviewFeature.class);



    private static final long serialVersionUID = -4389566785150237984L;
    private BCAgent bcAgent;
    private String featureId;
    private String demanderAgentId;

    public AcceptReviewFeature(BCAgent agent, String featureId, String startupAgentId) {
        super(agent);
        bcAgent = agent;
        // TODO: Andare a prendere veramente id service (per ora nome==id)
        this.featureId = featureId;
        this.demanderAgentId = startupAgentId;
    }

    public AcceptReviewFeature(BCAgent agent) {
        super(agent);
        bcAgent = agent;
    }

    @Override
    public void action() {
        // TODO: NB uso name come id
        String startupAgentId = demanderAgentId;
        sendAcceptReviewFeatureMessage(featureId, startupAgentId);
    }

    private void sendAcceptReviewFeatureMessage(String featureId, String startupAgentId) {
        // TODO: AGGIUNGERE TIMESTAMP LEAVES SERVICES
        ACLMessage acceptReviewFeatureMessage = new BCMessage(ACLMessage.ACCEPT_PROPOSAL);

        String messageType = BCMessage.REQUEST_FEATURE_REVIEW_ACCEPT;
        // TODO: For now in a REQUEST_FEATURE_REVIEW the Object is the same of the body of the message
        // (service name)
        //    String messageObject = selectedFeatureName;
        //    String messageBody = selectedFeatureName;
        String messageObject = featureId;
        String messageBody = featureId;
        Timestamp requestTimestamp = new Timestamp(System.currentTimeMillis());


        // TODO: Add Message Type (REQ,MEX) in this case "REQ" (REQUEST)
        // TODO: Add Message Object

        acceptReviewFeatureMessage
                .setContent(messageType + "%" + messageObject + "%" + messageBody + "%" + requestTimestamp);
        // requestFeatureExecution.setContent(selectedFeatureName);
        acceptReviewFeatureMessage.addReceiver(new AID(startupAgentId, AID.ISLOCALNAME));

        bcAgent.send(acceptReviewFeatureMessage);

    }


}
