package behaviours;

import java.util.ArrayList;
import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.StructServiceRequest;

public class AskService extends OneShotBehaviour {
  BCAgent bcAgent;
  String serviceNeededName;
  ArrayList<StructServiceRequest> orderedList;

  public AskService(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public AskService(BCAgent agent, String serviceNameInput,
      ArrayList<StructServiceRequest> orderedListInput) {
    super(agent);
    bcAgent = agent;
    serviceNeededName = serviceNameInput;
    orderedList = orderedListInput;
  }

  @Override
  public void action() {
    // Richiesta del servizio vera e propria (alla testa della lista ordinata)
    // TODO: La richiesta non fa altro che popolare la tabella delle richieste dell'agente executer,
    // poi lui sceglierà manualmente se eseguire o no il servizio
    // TODO: Gestire BCAgent DOWN
    ACLMessage requestServiceExecution = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    requestServiceExecution.setContent(serviceNeededName);
    requestServiceExecution
        .addReceiver(new AID(orderedList.get(0).getAgentName(), AID.ISLOCALNAME));
    myAgent.send(requestServiceExecution);

    // TODO: Aggiungere controllo risposta (se l'executer ha accettato di eseguire il servizio)
    // Rifiuto tutti gli altri
    ACLMessage refuseServiceExecution = new ACLMessage(ACLMessage.REFUSE);
    for (int i = 1; i < orderedList.size(); i++) {
      refuseServiceExecution
          .addReceiver(new AID(orderedList.get(i).getAgentName(), AID.ISLOCALNAME));
      myAgent.send(refuseServiceExecution);
    }

  }
}
