package behaviours;

import java.util.ArrayList;
import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.StructFeatureRequest;

public class AskFeature extends OneShotBehaviour {
  BCAgent bcAgent;
  String serviceNeededName;
  ArrayList<StructFeatureRequest> orderedList;

  public AskFeature(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  public AskFeature(BCAgent agent, String serviceNameInput,
      ArrayList<StructFeatureRequest> orderedListInput) {
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
    ACLMessage requestFeatureExecution = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
    requestFeatureExecution.setContent(serviceNeededName);
    requestFeatureExecution
        .addReceiver(new AID(orderedList.get(0).getAgentName(), AID.ISLOCALNAME));
    myAgent.send(requestFeatureExecution);

    // TODO: Aggiungere controllo risposta (se l'executer ha accettato di eseguire il servizio)
    // Rifiuto tutti gli altri
    ACLMessage refuseFeatureExecution = new ACLMessage(ACLMessage.REFUSE);
    for (int i = 1; i < orderedList.size(); i++) {
      refuseFeatureExecution
          .addReceiver(new AID(orderedList.get(i).getAgentName(), AID.ISLOCALNAME));
      myAgent.send(refuseFeatureExecution);
    }

  }
}
