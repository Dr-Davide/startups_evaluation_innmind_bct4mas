package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;
import logic.ReputationLogic;
import model.pojo.Activity;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class ComputeReputation extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(ComputeReputation.class);

  private static final long serialVersionUID = 1250309370464394239L;
  private BCAgent bcAgent;
  private ArrayList<Activity> serviceActivitiesList;
  private ArrayList<Activity> leavesActivitiesList;

  ComputeReputation(BCAgent bcAgent, ArrayList<Activity> serviceActivitiesList,
      ArrayList<Activity> leavesActivitiesList) {
    super(bcAgent);
    this.bcAgent = bcAgent;
    this.serviceActivitiesList = serviceActivitiesList;
    this.leavesActivitiesList = leavesActivitiesList;
    log.info(
        "COMPUTE REPUTATION SERVICE ACTIVITIES LIST SIZE: " + this.serviceActivitiesList.size());
    log.info("COMPUTE REPUTATION LEAVES ACTIVITIES LIST SIZE: " + this.leavesActivitiesList.size());
  }

  @Override public void action() {


    // Update Reputation of Composite Service
    if (serviceActivitiesList.size() == 2) {
      ReputationLogic compositeServiceReputationLogic = new ReputationLogic(bcAgent);

      Activity serviceFirstActivity = serviceActivitiesList.get(0);
      Activity serviceSecondActivity = serviceActivitiesList.get(1);

      compositeServiceReputationLogic
          .setDemanderAndExecuter(serviceFirstActivity, serviceSecondActivity);

      updateServiceDemanderAndExecuterReputation(compositeServiceReputationLogic);
    }


    // EXTEND TO THE LEAF SERVICES (si potrebbe integrare tutte le due liste (composite, leaves) in un'unica lista, tengo separato per eventuali cambianmenti)

    // Update Reputation of Leaves Services (if exists)
    for (int i = 0; i < leavesActivitiesList.size(); i = i + 2) {
      ReputationLogic leafServiceReputationLogic = new ReputationLogic(bcAgent);
      // si da per scontato che le attività siano inserite correttamente (bisognerebbe fare il controllo che abbiano lo stesso timestamp e stessi attori)
      Activity serviceFirstActivity = leavesActivitiesList.get(i);
      Activity serviceSecondActivity = leavesActivitiesList.get(i + 1);


      leafServiceReputationLogic
          .setDemanderAndExecuter(serviceFirstActivity, serviceSecondActivity);

      updateServiceDemanderAndExecuterReputation(leafServiceReputationLogic);
    }



  }

  /**
   * Wrapper that call the Update Reputation for Demander and Executer of the Service
   *
   * @param reputationLogic
   */
  private void updateServiceDemanderAndExecuterReputation(ReputationLogic reputationLogic) {
    if (reputationLogic.isDemanderSet() && reputationLogic.isExecuterSet()) {
      try {
        Boolean updatedDemanderReputation = reputationLogic.updateDemanderReputation();
        Boolean updatedExecuterReputation = reputationLogic.updateExecuterReputation();
        log.info("Updated Demander: " + updatedDemanderReputation);
        log.info("Updated Executer: " + updatedExecuterReputation);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
