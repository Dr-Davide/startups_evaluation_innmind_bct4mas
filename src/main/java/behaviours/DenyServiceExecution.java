package behaviours;

import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;

public class DenyServiceExecution extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(DenyServiceExecution.class);
  private static final long serialVersionUID = 5450656987948863989L;

  private BCAgent bcAgent;
  private String serviceId;
  private String demanderAgentId;

  public DenyServiceExecution(BCAgent agent, String serviceId, String demanderAgentId) {
    super(agent);
    bcAgent = agent;
    // TODO: Andare a prendere veramente id service (per ora nome==id)
    this.serviceId = serviceId;
    this.demanderAgentId = demanderAgentId;
  }

  public DenyServiceExecution(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {



    log.info("SERVICE DENIAL: serviceId: " + serviceId + ", denial Agent: "
        + bcAgent.getLocalName() + ", demander agentId: " + demanderAgentId);

    // TODO: Mettere tutto ciò che viene dopo in un behaviour a parte (tipo: EvaluateActivity)

    sendDenyServiceExecutionMessage(serviceId, demanderAgentId, bcAgent.getMyName());

  }

  /**
   * sendCreatedActivityMessage - Run from the service executer agent after creating the activity in
   * the ledger to inform the demander of the service being done
   * 
   * @param serviceId TODO
   * @param demanderAgentId TODO
   * @param myName
   */
  private void sendDenyServiceExecutionMessage(String serviceId, String demanderAgentId,
      String myName) {
    ACLMessage replyAclMessage = new ACLMessage(ACLMessage.REFUSE);
    replyAclMessage.setContent(serviceId + "%" + myName);
    replyAclMessage.addReceiver(new AID(demanderAgentId, AID.ISLOCALNAME));
    bcAgent.send(replyAclMessage);

  }

}
