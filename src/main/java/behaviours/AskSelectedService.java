package behaviours;

import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import messages.BCMessage;
import model.StructServiceRequest;

import java.sql.Timestamp;

/**
 * Ask the SelectedService to the Agent
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class AskSelectedService extends OneShotBehaviour {
  private static final long serialVersionUID = -2504173728777343765L;
  BCAgent bcAgent;
  private String selectedServiceId;
  private String selectedServiceName;
  private StructServiceRequest selectedServiceRequest;

  public AskSelectedService(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public AskSelectedService(BCAgent agent, String serviceIdInput, String serviceNameInput,
      StructServiceRequest selectedServiceInput) {
    super(agent);
    bcAgent = agent;
    selectedServiceId = serviceIdInput;
    selectedServiceName = serviceNameInput;
    selectedServiceRequest = selectedServiceInput;
  }

  @Override
  public void action() {
    // Richiesta del servizio vera e propria
    // TODO: La richiesta non fa altro che popolare la tabella delle richieste dell'agente executer,
    // poi lui sceglierà manualmente se eseguire o no il servizio
    // TODO: Gestire BCAgent DOWN
    // ACCEPT_PROPOSAL -> OLD WORKFLOW
    // INFORM -> NEW WORKFLOW
    // ACLMessage requestServiceExecution = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    ACLMessage requestServiceExecution = new BCMessage(ACLMessage.INFORM);

    String messageType = BCMessage.SERVICE_EXECUTION;
    // TODO: For now in a SERVICE_EXECUTION the Object is the same of the body of the message
    // (service name)
    //    String messageObject = selectedServiceName;
    //    String messageBody = selectedServiceName;
    String messageObject = selectedServiceId;
    String messageBody = selectedServiceId;
    Timestamp requestTimestamp = new Timestamp(System.currentTimeMillis());


    // TODO: Add Message Type (REQ,MEX) in this case "REQ" (REQUEST)
    // TODO: Add Message Object

    String agentToRequestService = selectedServiceRequest.getAgentName();
    requestServiceExecution
        .setContent(messageType + "%" + messageObject + "%" + messageBody + "%" + requestTimestamp);
    // requestServiceExecution.setContent(selectedServiceName);
    requestServiceExecution.addReceiver(new AID(agentToRequestService, AID.ISLOCALNAME));
    bcAgent.send(requestServiceExecution);

    // TODO: Aggiungere controllo risposta (se l'executer ha accettato o meno di eseguire il
    // servizio)


  }
}
