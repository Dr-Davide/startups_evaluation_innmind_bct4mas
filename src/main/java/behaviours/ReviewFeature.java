//package behaviours;
//
//import agents.BCAgent;
//import controllers.CheckerController;
//import controllers.ComplexWorkflowController;
//import controllers.CreateController;
//import controllers.ReadController;
//import fabric.Utils;
//import jade.core.AID;
//import jade.core.behaviours.OneShotBehaviour;
//import jade.lang.acl.ACLMessage;
//import model.RangeQueries;
//import model.pojo.Feature;
//import model.pojo.InnMindReputation;
//import model.pojo.Review;
//import org.apache.log4j.Logger;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//public class ReviewFeature extends OneShotBehaviour {
//    private static final Logger log = Logger.getLogger(ReviewFeature.class);
//
//
//
//    private static final long serialVersionUID = -4389566785150237984L;
//    private BCAgent bcAgent;
//    private String serviceId;
//    private String demanderAgentId;
//    private String agentRole;
//
//    public ReviewFeature(BCAgent agent, String serviceId, String demanderAgentId, String agentRole) {
//        super(agent);
//        bcAgent = agent;
//        // TODO: Andare a prendere veramente id service (per ora nome==id)
//        this.serviceId = serviceId;
//        this.demanderAgentId = demanderAgentId;
//        this.agentRole = agentRole;
//    }
//
//    public ReviewFeature(BCAgent agent) {
//        super(agent);
//        bcAgent = agent;
//    }
//
//    @Override
//    public void action() {
//        String executerEvaluation = null;
//        String demanderEvaluation = null;
//
//        Double serviceExecutionDouble = 0.0;
//
//        Timestamp serviceTimestamp = new Timestamp(System.currentTimeMillis());
//        ArrayList<Timestamp> compositionTimestamps = new ArrayList<>();
//        List<String> compositionTimestampsList = new ArrayList<>();
//
//        // TODO: Integrare esecuzione di servizi compositi come successione di esecuzione di servizi foglia e di conseguenza triggerare le rispettive valutazioni
//
//        try {
//            // Simulate Feature Execution
//            log.info("SERVICE EXECUTION: serviceId: " + serviceId + ", executer Agent: "
//                    + bcAgent.getLocalName() + ", demander agentId: " + demanderAgentId);
//            Feature featurePojo = ReadController
//                    .getFeature(bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), serviceId);
//            String serviceCompositionString = featurePojo.getFeatureComposition().toString();
//            //      log.info("SERVICE COMPOSITION STRING: " + serviceCompositionString);
//            String[] serviceCompositonParts = serviceCompositionString.split(",");
//
//            // UNSAFE CONTROL, MEANS: "IF IS A LEAF SERVICE" (We don't permit to create a composite service as a composition of only one service)
//            // not control on == 0 because Strings .split is returning length == 1 with empty string
//            if (serviceCompositonParts.length == 1) {
//                // LEAF SERVICE
//                log.info("EXECUTION LEAF SERVICE");
//                TimeUnit.SECONDS.sleep(1);
//                // TODO: behaviour Evaluate Feature
//                //        bcAgent.addBehaviour(new ExpertEvaluateFeature(bcAgent, 0));
//                bcAgent.bcAgentGui.getFeatureCompletedMessage(bcAgent.getMyName(), serviceId);
//                switch (agentRole) {
//                    case InnMindReputation.EXPERT_ROLE:
//                        executerEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(serviceId);
//                        break;
//                    case InnMindReputation.STARTUP_ROLE:
//                        demanderEvaluation = bcAgent.bcAgentGui.getDemanderEvaluation(serviceId);
//                        break;
//                    default: // should be unreachable
//                        IllegalStateException illegalStateException =
//                                new IllegalStateException("Wrong field to update, it's not in the expected ones");
//                        log.error(illegalStateException);
//                        throw illegalStateException;
//
//                }
////                executerEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(serviceId);
//            } else {
//                // COMPOSITE SERVICE
//                log.info("EXECUTION COMPOSITE SERVICE");
//                Double sum = 0.0;
//                int numberLeaves = 0;
//                // TODO: Levare completed message
//                bcAgent.bcAgentGui.getFeatureCompletedMessage(bcAgent.getMyName(), serviceId);
//                for (int i = 0; i < serviceCompositonParts.length; i++) {
//                    String leafFeatureId = serviceCompositonParts[i].replace("\"", "");
//                    log.info("SIMULATION " + i + " LEAF SERVICE  : " + leafFeatureId);
//                    TimeUnit.SECONDS.sleep(1);
//
//                    // TODO: behaviour Evaluate Feature
//                    //          bcAgent.addBehaviour(new ExpertEvaluateFeature(bcAgent, i));
//
////                    String executerLeafEvaluation;
////                    String demanderLeafEvaluation;
//                    Timestamp leafExecutionTimestamp;
//                    String stringLeafTimestamp;
//                    boolean isCreatedActivity;
//
//                    switch (agentRole) {
//                        case InnMindReputation.EXPERT_ROLE:
//                            String executerLeafEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(leafFeatureId);
//                            double executerLeafEvaluationDouble = Double.parseDouble(executerLeafEvaluation);
//                            log.info("DOUBLE VALUE: " + executerLeafEvaluationDouble);
//
//
//                            log.info("Executer Evaluation: " + executerLeafEvaluation);
//                            leafExecutionTimestamp = new Timestamp(System.currentTimeMillis());
//                            compositionTimestamps.add(leafExecutionTimestamp);
//                            stringLeafTimestamp = leafExecutionTimestamp.toString();
//                            compositionTimestampsList.add(stringLeafTimestamp);
//
//                            log.info("SERVICE ID: " + serviceId);
//                            log.info("LEAF SERVICE ID: " + leafFeatureId);
//
//                            // BUG:  Failed to find executedFeature by id Feature non found, FeatureId: "asdfff"
//                            isCreatedActivity = CreateController
//                                    .createExecuterWriterActivity(bcAgent, demanderAgentId, leafFeatureId,
//                                            stringLeafTimestamp, executerLeafEvaluation);
//                            break;
//                        case InnMindReputation.STARTUP_ROLE:
//                            String demanderLeafEvaluation = bcAgent.bcAgentGui.getDemanderEvaluation(leafFeatureId);
//                            double demanderLeafEvaluationDouble = Double.parseDouble(demanderLeafEvaluation);
//                            log.info("DOUBLE VALUE: " + demanderLeafEvaluationDouble);
//
//
//                            log.info("Executer Evaluation: " + demanderLeafEvaluation);
//                            leafExecutionTimestamp = new Timestamp(System.currentTimeMillis());
//                            compositionTimestamps.add(leafExecutionTimestamp);
//                            stringLeafTimestamp = leafExecutionTimestamp.toString();
//                            compositionTimestampsList.add(stringLeafTimestamp);
//
//                            log.info("SERVICE ID: " + serviceId);
//                            log.info("LEAF SERVICE ID: " + leafFeatureId);
//
//                            // BUG:  Failed to find executedFeature by id Feature non found, FeatureId: "asdfff"
//                            isCreatedActivity = CreateController
//                                    .createDemanderWriterActivity(bcAgent, demanderAgentId, leafFeatureId,
//                                            stringLeafTimestamp, demanderLeafEvaluation);
//                            break;
//                        default: // should be unreachable
//                            IllegalStateException illegalStateException =
//                                    new IllegalStateException("Wrong field to update, it's not in the expected ones");
//                            log.error(illegalStateException);
//                            throw illegalStateException;
////                    String executerLeafEvaluation = bcAgent.bcAgentGui.getExecuterEvaluation(leafFeatureId);
////                    double executerLeafEvaluationDouble = Double.parseDouble(executerLeafEvaluation);
////                    log.info("DOUBLE VALUE: " + executerLeafEvaluationDouble);
////                    log.info("Executer Evaluation: " + executerLeafEvaluation);
////                    Timestamp leafExecutionTimestamp = new Timestamp(System.currentTimeMillis());
////                    compositionTimestamps.add(leafExecutionTimestamp);
////                    String stringLeafTimestamp = leafExecutionTimestamp.toString();
////                    compositionTimestampsList.add(stringLeafTimestamp);
////
////                    log.info("SERVICE ID: " + serviceId);
////                    log.info("LEAF SERVICE ID: " + leafFeatureId);
////
////                    // BUG:  Failed to find executedFeature by id Feature non found, FeatureId: "asdfff"
////                    boolean isCreatedActivity = CreateController
////                            .createExecuterWriterActivity(bcAgent, demanderAgentId, leafFeatureId,
////                                    stringLeafTimestamp, executerLeafEvaluation);
//                            if (isCreatedActivity) {
//                                sum = sum + executerLeafEvaluationDouble;
//                                numberLeaves = i + 1;
//                            } else {
//                                log.error("Not Created Leaf Review");
//                            }
//
//                    }
//                    log.info("NUMBER LEAVES: " + numberLeaves);
//                    serviceExecutionDouble = sum / numberLeaves;
//
//                    log.info("MEAN VALUE: " + serviceExecutionDouble);
//
//                    switch (agentRole) {
//                        case InnMindReputation.EXPERT_ROLE:
//                            executerEvaluation = String.valueOf(serviceExecutionDouble);
//                            break;
//                        case InnMindReputation.STARTUP_ROLE:
//                            demanderEvaluation = String.valueOf(serviceExecutionDouble);
//                            break;
//                        default: // should be unreachable
//                            IllegalStateException illegalStateException =
//                                    new IllegalStateException("Wrong field to update, it's not in the expected ones");
//                            log.error(illegalStateException);
//                            throw illegalStateException;
//
//                    }
////                executerEvaluation = String.valueOf(serviceExecutionDouble);
//
//                    // TODO: Calcolare media leaf evaluations per creare composite evaluation
//
//                }
//            }
//            //      TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // -------------------------------------------------------------------------
//
//        // TODO: Get executer Evaluation of leaf service that compose the composite service in case is a composite service
//
//
//        log.info("STRING EVALUATION TOTAL: " + executerEvaluation);
//        //    executerEvaluation = getExecuterEvaluation();
//
//
//
//        // ---------------------------------------------------------------
//
//        log.info("Executer Evaluation: " + executerEvaluation);
//        String stringTimestamp = serviceTimestamp.toString();
//        boolean isCreatedActivity = CreateController.createExecuterWriterActivity(bcAgent,
//                demanderAgentId, serviceId, stringTimestamp, executerEvaluation);
//        if (isCreatedActivity) {
//            sendCreatedActivityMessage(serviceId, demanderAgentId, bcAgent.getMyName(), serviceTimestamp,
//                    compositionTimestamps);
//        } else {
//            log.info("Not Created Review by the executer");
//        }
//
//        // TODO: Aggiungere verifica se è secondo (EXECUTER)
//        boolean isSecondWriterAgent;
//        isSecondWriterAgent = CheckerController.isTimestampTwoTimesActivities(bcAgent.getHfClient(),
//                bcAgent.getHfTransactionChannel(), demanderAgentId, bcAgent.getMyName(), stringTimestamp);
//
//        // // TODO: Trigger Algoritmo Calcolo Reputazione
//        if (isSecondWriterAgent) {
//
//            ArrayList<Review> activitiesList;
//            // activitiesList = transactionLedgerInteraction.GetReviewsByStartupExpertTimestamp(
//            // bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), demanderAgentId,
//            // bcAgent.getMyName(), executionTimestamp.toString());
//            activitiesList = RangeQueries.GetReviewsByStartupExpertTimestamp(bcAgent.getHfClient(),
//                    bcAgent.getHfTransactionChannel(), demanderAgentId, bcAgent.getMyName(), stringTimestamp);
//            Utils.printActivitiesList(activitiesList);
//            ArrayList<Review> leavesActivitiesList = new ArrayList<>();
//            leavesActivitiesList = ComplexWorkflowController
//                    .getLeavesActivitiesList(compositionTimestampsList, leavesActivitiesList,
//                            bcAgent.getHfClient(), bcAgent.getHfTransactionChannel(), demanderAgentId,
//                            bcAgent.getMyName());
//            bcAgent.addBehaviour(new ComputeReputation(bcAgent, activitiesList, leavesActivitiesList));
//        } else {
//            log.info("I'm not the second Writer");
//        }
//
//    }
//
//    private String getExecuterEvaluation() {
//        String executerEvaluation;
//        String showInputDialogMessage = "Agent " + bcAgent.getLocalName()
//                + ", please evaluate the QoS as the Feature Executer Role in the transaction";
//        String showInputDialogTitle = "Executer Feature Evaluation";
//
//        executerEvaluation =
//                bcAgent.bcAgentGui.getEvaluation(showInputDialogMessage, showInputDialogTitle);
//
//        return executerEvaluation;
//    }
//
//
//    /**
//     * sendCreatedActivityMessage - Run from the service executer agent after creating the activity in
//     * the ledger to inform the demander of the service being done
//     *  @param serviceId TODO
//     * @param demanderAgentId TODO
//     * @param myName
//     * @param FeatureTimestamp
//     * @param CompositionTimestamps
//     */
//    private void sendCreatedActivityMessage(String serviceId, String demanderAgentId, String myName,
//                                            Timestamp FeatureTimestamp, ArrayList<Timestamp> CompositionTimestamps) {
//        // TODO: AGGIUNGERE TIMESTAMP LEAVES SERVICES
//        String executedFeatureTxId = UUID.randomUUID().toString();
//        ACLMessage replyAclMessage = new ACLMessage(ACLMessage.INFORM_REF);
//        replyAclMessage.setContent(
//                "serviceDone%" + executedFeatureTxId + "%" + serviceId + "%" + FeatureTimestamp + "%"
//                        + CompositionTimestamps);
//        replyAclMessage.addReceiver(new AID(demanderAgentId, AID.ISLOCALNAME));
//
//        log.info(myName + " :inserting transaction at timestamp " + FeatureTimestamp + " and txid "
//                + executedFeatureTxId + "ACL MESSAGE: " + serviceId);
//
//        bcAgent.send(replyAclMessage);
//
//    }
//
//}
