package behaviours;

import agents.BCAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import model.FeatureView;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class ReceiveMessage extends TickerBehaviour {

    private static final Logger log = Logger.getLogger(ReceiveMessage.class);


    private static final long serialVersionUID = -6252545494077833820L;
    BCAgent bcAgent;
  private ACLMessage replyMessage;

  public ReceiveMessage(BCAgent agent) {
    super(agent, 1000);

    bcAgent = agent;
  }

  @Override
  public void onStart() {

  }

  @Override
  public void onTick() {
    ACLMessage message = bcAgent.receive();
    if (message != null) {
      createMessageResponse(message);
    }
  }

  private void createMessageResponse(ACLMessage message) {

    switch (message.getPerformative()) {

      // ACLMessage.REQUEST
      // request to perform a service
      // NB: Nel nuovo workflow questa richiesta non viene più fatta (prende i dati(lista agenti che
      // offrono il servizio cercato con costo,tempo,reputatizone) direttamente
      // dalla BCT)
      case ACLMessage.REQUEST:
        // case 16:
        /**
         * scorro il vettore dei servizi e vedo se ho il servizio poi invio costo e tempo
         */
        replyWithPing(message);

        break;

      case ACLMessage.ACCEPT_PROPOSAL:
        // triggero evaluation feature della startup
        bcAgent.bcAgentGui.getNewInBoxMessageAlert(bcAgent.getMyName());

        bcAgent.addMessageTrigger(message);

        break;

      case ACLMessage.REFUSE:
        bcAgent.showDenyExecutionTrigger(message);

        break;


      // TODO: use REQUEST
      // richiesta del demander al (possibile) executer, il quale (executer) mostra la richiesta
      // nell'InBox Messages
      case ACLMessage.INFORM:

        // USO bcAgent perchè devo andare a modificare la GUI (expertAgentGui)
        bcAgent.bcAgentGui.getNewInBoxMessageAlert(bcAgent.getMyName());
        bcAgent.addMessageTrigger(message);
        break;

      // ACLMessage.INFORM_REF
      case ACLMessage.INFORM_REF:
        // case 9:

        // agent that insert, whoffer, whorequest, whichservice, txid (random code),
        // timestamp, outcome (ok or no), rating
        bcAgent.addBehaviour(new EvaluateFeature(bcAgent, message));

        //        serviceCompleted(message);

        log.info(
            "Finish! Restart the network with stopFabric.sh/startFabric.sh and edit the config.xml to simulate another scenario.");
        break;

      default:

    }
  }

  /**
   *
   * @param msg
   */
  private void replyWithPing(ACLMessage msg) {
    setReplyMessage(msg.createReply());
    getReplyMessage().setPerformative(ACLMessage.PROPOSE);

    String serviceRequested = msg.getContent();
    ArrayList<FeatureView> agentFeatures = bcAgent.featuresList;

    setFeatureInReplyMessage(serviceRequested, agentFeatures);
    log.info("REPLY CONTENT: " + getReplyMessage().getContent());

    myAgent.send(getReplyMessage());
  }

  /**
   * Put the service agent's cost and time in the service needed response
   * 
   * @param serviceRequested
   * @param agentFeatures
   */
  private void setFeatureInReplyMessage(String serviceRequested,
      ArrayList<FeatureView> agentFeatures) {

    for (int i = 0; i < agentFeatures.size(); i++) {
      if (serviceRequested.equals(agentFeatures.get(i).getName())) {

        getReplyMessage().setContent("msgArrived/" + bcAgent.featuresList.get(i).getCost() + "/"
            + bcAgent.featuresList.get(i).getTime() + "/"
            + bcAgent.featuresList.get(i).getReputation());

        log.info(bcAgent.getLocalName() + ": i can offer "
            + agentFeatures.get(i).getName() + " with weight=" + agentFeatures.get(i).getCost()
            + " and time=" + agentFeatures.get(i).getTime() + ", with reputation="
            + agentFeatures.get(i).getReputation());
        break;
      }
    }
  }

  //  @Deprecated
  //  private void executeFeature(ACLMessage aclMessage) {
  //    // TODO: Aggiungere nel messaggio la description del servizio
  //    Timestamp timestamp = new Timestamp(System.currentTimeMillis()); // Create the timestamp to
  //                                                                     // memorize the time when the
  //                                                                     // service is executed
  //
  //    String demanderAgentId = aclMessage.getSender().getLocalName();
  //
  //      bcAgent.addBehaviour(new OneShotBehaviour() {
  //
  //          private static final long serialVersionUID = 2907854339604824231L;
  //
  //          @Override public void action() {
  //              log.info(
  //                  "Agent: " + bcAgent.getLocalName() + ": executing " + aclMessage.getContent()
  //                      + " for agent:  " + aclMessage.getSender().getLocalName());
  //              try {
  //                  // Simulate Feature Execution
  //                  log.info(aclMessage.getContent());
  //                  TimeUnit.SECONDS.sleep(2);
  //              } catch (InterruptedException e) {
  //                  e.printStackTrace();
  //              }
  //
  //              // TODO: Andare a prendere veramente id service (per ora nome==id)
  //              String executedFeatureId = aclMessage.getContent();
  //              String messageContent = aclMessage.getContent();
  //              String senderName = aclMessage.getSender().getLocalName();
  //
  //              // GUI INTERACTION FOR EXECUTER SERVICE EVALUATION
  //              // TODO: Accomplish in jade MVC architecture
  //              String executerEvaluation = JOptionPane.showInputDialog(
  //                  "Agent " + bcAgent.getLocalName().toString()
  //                      + " please evaluate the QoS as the Feature Executer Role in the transaction",
  //                  "6.0");
  //              // JOptionPane.showMessageDialog(null, "Select the evaluation of the service",
  //              // "InfoBox: " + "Heuristic Needed", JOptionPane.INFORMATION_MESSAGE);
  //
  //
  //
  //              log.info("Executer Evaluation: " + executerEvaluation);
  //              String stringTimestamp = timestamp.toString();
  //              boolean isCreatedActivity = CreateController
  //                  .createExecuterWriterActivity(bcAgent, demanderAgentId, executedFeatureId,
  //                      stringTimestamp,
  //                      executerEvaluation);
  //              if (isCreatedActivity) {
  //                  // String messageContent = aclMessage.getContent();
  //                  // String senderName = aclMessage.getSender().getLocalName();
  //                  sendCreatedActivityMessage(messageContent, senderName, bcAgent.getMyName(),
  //                      timestamp);
  //              } else {
  //                  log.info("Not Created Review by the executer");
  //              }
  //
  //              // TODO: Aggiungere verifica se è secondo (EXECUTER)
  //              boolean isSecondWriterAgent;
  //              isSecondWriterAgent = CheckerController
  //                  .isTimestampTwoTimesActivities(bcAgent.getHfClient(),
  //                      bcAgent.getHfTransactionChannel(), aclMessage.getSender().getLocalName(),
  //                      bcAgent.getMyName(), timestamp.toString());
  //
  //
  //              // // TODO: Trigger Algoritmo Calcolo Reputazione
  //              if (isSecondWriterAgent) {
  //
  //                  ArrayList<Review> activitiesList;
  //
  //                  activitiesList = RangeQueries
  //                      .GetReviewsByStartupExpertTimestamp(bcAgent.getHfClient(),
  //                          bcAgent.getHfTransactionChannel(), aclMessage.getSender().getLocalName(),
  //                          bcAgent.getMyName(), timestamp.toString());
  //
  //                  Utils.printActivitiesList(activitiesList);
  //                  bcAgent.addBehaviour(new ComputeReputation(bcAgent, activitiesList, ));
  //              } else {
  //                  log.info("I'm not the second Writer");
  //              }
  //
  //
  //          }
  //      });
  //  }

  /**
   * sendCreatedActivityMessage - Run from the service executer agent after creating the activity in
   * the ledger to inform the requester of the service being done
   * 
   * @param messageContent TODO
   * @param senderName TODO
   * @param myName
   * @param timestamp
   */
  private void sendCreatedActivityMessage(String messageContent, String senderName, String myName,
      Timestamp timestamp) {
    String executedFeatureTxId = UUID.randomUUID().toString();
    ACLMessage replyAclMessage = new ACLMessage(ACLMessage.INFORM_REF);
    replyAclMessage
        .setContent("serviceDone%" + executedFeatureTxId + "%" + messageContent + "%" + timestamp);
    replyAclMessage.addReceiver(new AID(senderName, AID.ISLOCALNAME));
    log.info(myName + " :inserting transaction at timestamp " + timestamp + " and txid "
        + executedFeatureTxId + "ACL MESSAGE: " + messageContent);
    bcAgent.send(replyAclMessage);

  }

  /**
   * @return the replyMessage
   */
  private ACLMessage getReplyMessage() {
    return replyMessage;
  }

  /**
   * @param replyMessage the replyMessage to set
   */
  private void setReplyMessage(ACLMessage replyMessage) {
    this.replyMessage = replyMessage;
  }
}
