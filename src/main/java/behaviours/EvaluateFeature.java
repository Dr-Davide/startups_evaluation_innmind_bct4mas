package behaviours;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.CreateController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import model.RangeQueries;
import model.pojo.Review;
import model.pojo.Feature;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Per ora usato solo dal demander, l'executer valuta il servizio quando lo esegue (addbehaviour mette in coda il behaviour, non è preemptive)
public class EvaluateFeature extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(EvaluateFeature.class);


  private static final long serialVersionUID = 442251848309328026L;

  private BCAgent bcAgent;
  private ACLMessage aclMessage;

  EvaluateFeature(BCAgent agent, ACLMessage aclMessage) {
    this.aclMessage = aclMessage;
    bcAgent = agent;
  }

  @Override public void action() {

    serviceCompletedDemanderEvaluation(aclMessage);
  }

  private void serviceCompletedDemanderEvaluation(ACLMessage aclMessage) {
    String expertAgentId = bcAgent.getMyName();


    try {

      String[] split = aclMessage.getContent().split("%");
      String executedFeatureId = split[2];
      String serviceTimestamp = split[3]; // Get the time when service was executed
      String compositionTimestampsAsString = split[4];
      List<String> compositionTimestampsList = new ArrayList<>();

      compositionTimestampsList =
          getCompositionTimestampsList(compositionTimestampsAsString, compositionTimestampsList);

      ArrayList<String> compositionTimestampsArrayList = new ArrayList<>(compositionTimestampsList);


      log.info("SERVICE TIMESTAMP: " + serviceTimestamp);
      log.info("COMPOSITION TIMESTAMPS: " + compositionTimestampsAsString);
      log.info("COMPOSITION SIZE: " + compositionTimestampsList.size());



      log.info(
          bcAgent.getLocalName() + ": " + executedFeatureId + " of " + aclMessage.getSender()
              .getLocalName() + " completed!");
      // get the txid from the message
      String executedFeatureTxId = split[1];
      // get the actual timestamp
      // String timestamp = new Timestamp(System.currentTimeMillis()).toString();

      // TODO: IF INUTILE
      if (split[0].equals("serviceDone")) {

        String expertReview = null;

        Double compositeFeatureEvaluationDouble = 0.0;

        String startupAgentId = aclMessage.getSender().getLocalName();

//        bcAgent.bcAgentGui.getFeatureCompletedMessage(startupAgentId, executedFeatureId);

        // TODO: Integrare esecuzione di servizi compositi come successione di esecuzione di servizi foglia e di conseguenza triggerare le rispettive valutazioni

        try {
          // Simulate Feature Execution
          log.info(
              "SERVICE EVALUATION: serviceId: " + executedFeatureId + ", executer Agent: "
                  + startupAgentId + ", demander agentId: " + bcAgent.getLocalName());
          Feature featurePojo = ReadController
              .getFeature(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), executedFeatureId);
          String serviceCompositionString = featurePojo.getFeatureComposition().toString();
          //      log.info("SERVICE COMPOSITION STRING: " + serviceCompositionString);
          String[] serviceCompositonParts = serviceCompositionString.split(",");

          // UNSAFE CONTROL, MEANS: "IF IS A LEAF SERVICE" (We don't permit to create a composite service as a composition of only one service)
          // not control on == 0 because Strings .split is returning length == 1 with empty string
          if (serviceCompositonParts.length == 1) {
            // LEAF SERVICE
            log.info("EXECUTION LEAF SERVICE");
            TimeUnit.SECONDS.sleep(1);
            // TODO: behaviour Evaluate Feature
            //        bcAgent.addBehaviour(new EvaluateFeature(bcAgent, 0));
            expertReview = bcAgent.bcAgentGui.getExpertReview(executedFeatureId,bcAgent.getLocalName() );
          } else {
            // COMPOSITE SERVICE
            log.info("EXECUTION COMPOSITE SERVICE");
            Double sum = 0.0;
            int numberLeaves = 0;
            for (int i = 0; i < serviceCompositonParts.length; i++) {
              // EVALUATE ALL LEAVES THAT COMPOSE THE SERVICE
              String leafFeatureId = serviceCompositonParts[i].replace("\"", "");

              String demanderLeafEvaluation =
                  bcAgent.bcAgentGui.getExpertReview(leafFeatureId, bcAgent.getLocalName());
              double demanderLeafEvaluationDouble = Double.parseDouble(demanderLeafEvaluation);
              log.info("DOUBLE VALUE: " + demanderLeafEvaluationDouble);


              log.info("Demander Evaluation: " + demanderLeafEvaluation);
              //              Timestamp leafExecutionTimestamp = new Timestamp(System.currentTimeMillis());
              //              String stringLeafTimestamp = leafExecutionTimestamp.toString();
              String stringLeafTimestamp = compositionTimestampsList.get(i);
              log.info("SERVICE ID: " + executedFeatureId);
              log.info("LEAF SERVICE ID: " + leafFeatureId);
              // BUG:  Failed to find executedFeature by id Feature non found, FeatureId: "asdfff"
              // TODO: Provo semplicemente ad invertire i create
//              boolean isCreatedActivity = CreateController
//                  .createDemanderWriterActivity(bcAgent, executerAgentId, leafFeatureId,
//                      stringLeafTimestamp, demanderLeafEvaluation);
              boolean isCreatedActivity = CreateController
                      .createExecuterWriterActivity(bcAgent, startupAgentId,
                              leafFeatureId, stringLeafTimestamp, demanderLeafEvaluation);

              if (isCreatedActivity) {
                sum = sum + demanderLeafEvaluationDouble;
                numberLeaves = i + 1;
                log.info("CREATED LEAF ACTIVITY OK: " + numberLeaves);
              } else {
                log.error("Not Created Leaf Review");
              }

            }
            log.info("NUMBER LEAVES: " + numberLeaves);
            compositeFeatureEvaluationDouble = sum / numberLeaves;

            log.info("MEAN VALUE: " + compositeFeatureEvaluationDouble);

            expertReview = String.valueOf(compositeFeatureEvaluationDouble);

            // TODO: Calcolare media leaf evaluations per creare composite evaluation

          }

          //      TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        log.info("Demander Evaluation: " + expertReview);
        //          String executerAgentId = aclMessage.getSender().getLocalName();
        boolean isCreatedActivity = CreateController
            .createExecuterWriterActivity(bcAgent, startupAgentId, executedFeatureId,
                serviceTimestamp, expertReview);

        log.info("DEMANDER ACTIVITY CREATE LOG: " + isCreatedActivity);



        //        TimeUnit.SECONDS.sleep(4);

        boolean isSecondWriterAgent;
        // TODO: Do the check and the get in only one function (one query to the ledger)
        isSecondWriterAgent = CheckerController
            .isTimestampTwoTimesActivities(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(),
                startupAgentId, expertAgentId, serviceTimestamp);


        ArrayList<Review> serviceActivitiesList = new ArrayList<>();

        ArrayList<Review> leavesActivitiesList = new ArrayList<>();


//        String startupAgentId = bcAgent.getMyName();
//        String expertAgentId = aclMessage.getSender().getLocalName();

        serviceActivitiesList = RangeQueries
            .GetReviewsByStartupExpertTimestamp(bcAgent.getHfClient(),
                bcAgent.getHfTransactionChannel(), startupAgentId,
                expertAgentId, serviceTimestamp);

        leavesActivitiesList = ComplexWorkflowController
            .getLeavesActivitiesList(compositionTimestampsList, leavesActivitiesList,
                bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), startupAgentId,
                expertAgentId);
        log.info("LEAVES ACTIVITIES LIST: " + leavesActivitiesList.size());
        log.info("SERVICE ACTIVITIES LIST: " + serviceActivitiesList.size());

        if (isSecondWriterAgent) {
          bcAgent.addBehaviour(
              new ComputeReputation(bcAgent, serviceActivitiesList, leavesActivitiesList));
        } else {
          log.info("I'm not the second Writer");
        }
      }

    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  private List<String> getCompositionTimestampsList(String compositionTimestampsAsString,
      List<String> compositionTimestampsList) {
    log.info("COMPOSTION TIMESTAMPS: " + compositionTimestampsAsString);
    if (!compositionTimestampsAsString.equals("[]")) {
      String compositionTimestampsWithoutBrackets =
          compositionTimestampsAsString.replace("[", "").replace("]", "");
      compositionTimestampsList = Arrays.asList(compositionTimestampsWithoutBrackets.split(", "));
      for (int i = 0; i < compositionTimestampsList.size(); i++) {
        log.info("TIMESTAMP ELEMENT: " + compositionTimestampsList.get(i));
      }
    }
    return compositionTimestampsList;
  }
}
