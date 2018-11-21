package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.CreateController;
import controllers.ReadController;
import fabric.Utils;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.RangeQueries;
import model.pojo.Activity;
import model.pojo.Service;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExecuteService extends OneShotBehaviour {
  private static final Logger log = Logger.getLogger(ExecuteService.class);



  private static final long serialVersionUID = -4389566785150237984L;
  private BCAgent bcAgent;
  private String serviceId;
  private String demanderAgentId;

  public ExecuteService(BCAgent agent, String serviceId, String demanderAgentId) {
    super(agent);
    bcAgent = agent;
    // TODO: Andare a prendere veramente id service (per ora nome==id)
    this.serviceId = serviceId;
    this.demanderAgentId = demanderAgentId;
  }

  public ExecuteService(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  @Override
  public void action() {
    String executerEvaluation = null;

    Double serviceExecutionDouble = 0.0;

    Timestamp serviceTimestamp = new Timestamp(System.currentTimeMillis());
    ArrayList<Timestamp> compositionTimestamps = new ArrayList<>();
    List<String> compositionTimestampsList = new ArrayList<>();

    // TODO: Integrare esecuzione di servizi compositi come successione di esecuzione di servizi foglia e di conseguenza triggerare le rispettive valutazioni

    try {
      // Simulate Service Execution
      log.info("SERVICE EXECUTION: serviceId: " + serviceId + ", executer Agent: "
          + bcAgent.getLocalName() + ", demander agentId: " + demanderAgentId);
      Service servicePojo = ReadController
          .getService(bcAgent.getHfClient(), bcAgent.getHfServiceChannel(), serviceId);
      String serviceCompositionString = servicePojo.getServiceComposition().toString();
      //      log.info("SERVICE COMPOSITION STRING: " + serviceCompositionString);
      String[] serviceCompositonParts = serviceCompositionString.split(",");

      // UNSAFE CONTROL, MEANS: "IF IS A LEAF SERVICE" (We don't permit to create a composite service as a composition of only one service)
      // not control on == 0 because Strings .split is returning length == 1 with empty string
      if (serviceCompositonParts.length == 1) {
        // LEAF SERVICE
        log.info("EXECUTION LEAF SERVICE");
        TimeUnit.SECONDS.sleep(1);
        // TODO: behaviour Evaluate Service
        //        bcAgent.addBehaviour(new EvaluateService(bcAgent, 0));
        bcAgent.bcAgentGui.getServiceCompletedMessage(bcAgent.getMyName(), serviceId);
        executerEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(serviceId);
      } else {
        // COMPOSITE SERVICE
        log.info("EXECUTION COMPOSITE SERVICE");
        Double sum = 0.0;
        int numberLeaves = 0;
        bcAgent.bcAgentGui.getServiceCompletedMessage(bcAgent.getMyName(), serviceId);
        for (int i = 0; i < serviceCompositonParts.length; i++) {
          String leafServiceId = serviceCompositonParts[i].replace("\"", "");
          log.info("SIMULATION " + i + " LEAF SERVICE  : " + leafServiceId);
          TimeUnit.SECONDS.sleep(1);

          // TODO: behaviour Evaluate Service
          //          bcAgent.addBehaviour(new EvaluateService(bcAgent, i));
          String executerLeafEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(leafServiceId);
          double executerLeafEvaluationDouble = Double.parseDouble(executerLeafEvaluation);
          log.info("DOUBLE VALUE: " + executerLeafEvaluationDouble);


          log.info("Executer Evaluation: " + executerLeafEvaluation);
          Timestamp leafExecutionTimestamp = new Timestamp(System.currentTimeMillis());
          compositionTimestamps.add(leafExecutionTimestamp);
          String stringLeafTimestamp = leafExecutionTimestamp.toString();
          compositionTimestampsList.add(stringLeafTimestamp);

          log.info("SERVICE ID: " + serviceId);
          log.info("LEAF SERVICE ID: " + leafServiceId);

          // BUG:  Failed to find executedService by id Service non found, ServiceId: "asdfff"
          boolean isCreatedActivity = CreateController
              .createExecuterWriterActivity(bcAgent, demanderAgentId, leafServiceId,
                  stringLeafTimestamp, executerLeafEvaluation);
          if (isCreatedActivity) {
            sum = sum + executerLeafEvaluationDouble;
            numberLeaves = i + 1;
          } else {
            log.error("Not Created Leaf Activity");
          }

        }
        log.info("NUMBER LEAVES: " + numberLeaves);
        serviceExecutionDouble = sum / numberLeaves;

        log.info("MEAN VALUE: " + serviceExecutionDouble);

        executerEvaluation = String.valueOf(serviceExecutionDouble);

        // TODO: Calcolare media leaf evaluations per creare composite evaluation

      }

      //      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // -------------------------------------------------------------------------

    // TODO: Get executer Evaluation of leaf service that compose the composite service in case is a composite service


    log.info("STRING EVALUATION TOTAL: " + executerEvaluation);
    //    executerEvaluation = getExecuterEvaluation();



    // ---------------------------------------------------------------

    log.info("Executer Evaluation: " + executerEvaluation);
    String stringTimestamp = serviceTimestamp.toString();
    boolean isCreatedActivity = CreateController.createExecuterWriterActivity(bcAgent,
        demanderAgentId, serviceId, stringTimestamp, executerEvaluation);
    if (isCreatedActivity) {
      sendCreatedActivityMessage(serviceId, demanderAgentId, bcAgent.getMyName(), serviceTimestamp,
          compositionTimestamps);
    } else {
      log.info("Not Created Activity by the executer");
    }

    // TODO: Aggiungere verifica se è secondo (EXECUTER)
    boolean isSecondWriterAgent;
    isSecondWriterAgent = CheckerController.isTimestampTwoTimesActivities(bcAgent.getHfClient(),
        bcAgent.getHfServiceChannel(), demanderAgentId, bcAgent.getMyName(), stringTimestamp);

    // // TODO: Trigger Algoritmo Calcolo Reputazione
    if (isSecondWriterAgent) {

      ArrayList<Activity> activitiesList;
      // activitiesList = transactionLedgerInteraction.getActivitiesByDemanderExecuterTimestamp(
      // bcAgent.getHfClient(), bcAgent.getHfServiceChannel(), demanderAgentId,
      // bcAgent.getMyName(), executionTimestamp.toString());
      activitiesList = RangeQueries.getActivitiesByDemanderExecuterTimestamp(bcAgent.getHfClient(),
          bcAgent.getHfServiceChannel(), demanderAgentId, bcAgent.getMyName(), stringTimestamp);
      Utils.printActivitiesList(activitiesList);
      ArrayList<Activity> leavesActivitiesList = new ArrayList<>();
      leavesActivitiesList = ComplexWorkflowController
          .getLeavesActivitiesList(compositionTimestampsList, leavesActivitiesList,
              bcAgent.getHfClient(), bcAgent.getHfServiceChannel(), demanderAgentId,
              bcAgent.getMyName());
      bcAgent.addBehaviour(new ComputeReputation(bcAgent, activitiesList, leavesActivitiesList));
    } else {
      log.info("I'm not the second Writer");
    }

  }

  private String getExecuterEvaluation() {
    String executerEvaluation;
    String showInputDialogMessage = "Agent " + bcAgent.getLocalName()
        + ", please evaluate the QoS as the Service Executer Role in the transaction";
    String showInputDialogTitle = "Executer Service Evaluation";

    executerEvaluation =
        bcAgent.bcAgentGui.getEvaluation(showInputDialogMessage, showInputDialogTitle);

    return executerEvaluation;
  }


  /**
   * sendCreatedActivityMessage - Run from the service executer agent after creating the activity in
   * the ledger to inform the demander of the service being done
   *  @param serviceId TODO
   * @param demanderAgentId TODO
   * @param myName
   * @param ServiceTimestamp
   * @param CompositionTimestamps
   */
  private void sendCreatedActivityMessage(String serviceId, String demanderAgentId, String myName,
      Timestamp ServiceTimestamp, ArrayList<Timestamp> CompositionTimestamps) {
    // TODO: AGGIUNGERE TIMESTAMP LEAVES SERVICES
    String executedServiceTxId = UUID.randomUUID().toString();
    ACLMessage replyAclMessage = new ACLMessage(ACLMessage.INFORM_REF);
    replyAclMessage.setContent(
        "serviceDone%" + executedServiceTxId + "%" + serviceId + "%" + ServiceTimestamp + "%"
            + CompositionTimestamps);
    replyAclMessage.addReceiver(new AID(demanderAgentId, AID.ISLOCALNAME));

    log.info(myName + " :inserting transaction at timestamp " + ServiceTimestamp + " and txid "
        + executedServiceTxId + "ACL MESSAGE: " + serviceId);

    bcAgent.send(replyAclMessage);

  }

}
