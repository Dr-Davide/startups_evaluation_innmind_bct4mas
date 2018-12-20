package behaviours;

import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import messages.BCMessage;
import model.StructFeatureRequest;

import java.sql.Timestamp;

/**
 * Ask the SelectedFeature to the Agent
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class AskSelectedFeature extends OneShotBehaviour {
  private static final long serialVersionUID = -2504173728777343765L;
  BCAgent bcAgent;
  private String selectedFeatureId;
  private String selectedFeatureName;
  private StructFeatureRequest selectedFeatureRequest;

  public AskSelectedFeature(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public AskSelectedFeature(BCAgent agent, String serviceIdInput, String serviceNameInput,
      StructFeatureRequest selectedFeatureInput) {
    super(agent);
    bcAgent = agent;
    selectedFeatureId = serviceIdInput;
    selectedFeatureName = serviceNameInput;
    selectedFeatureRequest = selectedFeatureInput;
  }

  @Override
  public void action() {
    // Richiesta del servizio vera e propria
    // TODO: La richiesta non fa altro che popolare la tabella delle richieste dell'agente executer,
    // poi lui sceglierà manualmente se eseguire o no il servizio
    // TODO: Gestire BCAgent DOWN
    // ACCEPT_PROPOSAL -> OLD WORKFLOW
    // INFORM -> NEW WORKFLOW
    // ACLMessage requestFeatureExecution = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    ACLMessage requestFeatureExecution = new BCMessage(ACLMessage.INFORM);

    String messageType = BCMessage.REQUEST_FEATURE_REVIEW;
    // TODO: For now in a REQUEST_FEATURE_REVIEW the Object is the same of the body of the message
    // (service name)
    //    String messageObject = selectedFeatureName;
    //    String messageBody = selectedFeatureName;
    String messageObject = selectedFeatureId;
    String messageBody = selectedFeatureId;
    Timestamp requestTimestamp = new Timestamp(System.currentTimeMillis());


    // TODO: Add Message Type (REQ,MEX) in this case "REQ" (REQUEST)
    // TODO: Add Message Object

    String agentToRequestFeature = selectedFeatureRequest.getAgentName();
    requestFeatureExecution
        .setContent(messageType + "%" + messageObject + "%" + messageBody + "%" + requestTimestamp);
    // requestFeatureExecution.setContent(selectedFeatureName);
    requestFeatureExecution.addReceiver(new AID(agentToRequestFeature, AID.ISLOCALNAME));
    bcAgent.send(requestFeatureExecution);

    // TODO: Aggiungere controllo risposta (se l'executer ha accettato o meno di eseguire il
    // servizio)


  }
}
