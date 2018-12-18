package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;
import logic.ReputationLogic;
import model.pojo.Review;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class ComputeReputation extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(ComputeReputation.class);

  private static final long serialVersionUID = 1250309370464394239L;
  private BCAgent bcAgent;
  private ArrayList<Review> serviceActivitiesList;
  private ArrayList<Review> leavesActivitiesList;

  ComputeReputation(BCAgent bcAgent, ArrayList<Review> serviceActivitiesList,
      ArrayList<Review> leavesActivitiesList) {
    super(bcAgent);
    this.bcAgent = bcAgent;
    this.serviceActivitiesList = serviceActivitiesList;
    this.leavesActivitiesList = leavesActivitiesList;
    log.info(
        "COMPUTE REPUTATION SERVICE ACTIVITIES LIST SIZE: " + this.serviceActivitiesList.size());
    log.info("COMPUTE REPUTATION LEAVES ACTIVITIES LIST SIZE: " + this.leavesActivitiesList.size());
  }

  @Override public void action() {


    // Update InnMindReputation of Composite Feature
    if (serviceActivitiesList.size() == 2) {
      ReputationLogic compositeFeatureReputationLogic = new ReputationLogic(bcAgent);

      Review serviceFirstReview = serviceActivitiesList.get(0);
      Review serviceSecondReview = serviceActivitiesList.get(1);

      compositeFeatureReputationLogic
          .setDemanderAndExecuter(serviceFirstReview, serviceSecondReview);

      updateFeatureDemanderAndExecuterReputation(compositeFeatureReputationLogic);
    }


    // EXTEND TO THE LEAF SERVICES (si potrebbe integrare tutte le due liste (composite, leaves) in un'unica lista, tengo separato per eventuali cambianmenti)

    // Update InnMindReputation of Leaves Features (if exists)
    for (int i = 0; i < leavesActivitiesList.size(); i = i + 2) {
      ReputationLogic leafFeatureReputationLogic = new ReputationLogic(bcAgent);
      // si da per scontato che le attività siano inserite correttamente (bisognerebbe fare il controllo che abbiano lo stesso timestamp e stessi attori)
      Review serviceFirstReview = leavesActivitiesList.get(i);
      Review serviceSecondReview = leavesActivitiesList.get(i + 1);


      leafFeatureReputationLogic
          .setDemanderAndExecuter(serviceFirstReview, serviceSecondReview);

      updateFeatureDemanderAndExecuterReputation(leafFeatureReputationLogic);
    }



  }

  /**
   * Wrapper that call the Update InnMindReputation for Demander and Executer of the Feature
   *
   * @param reputationLogic
   */
  private void updateFeatureDemanderAndExecuterReputation(ReputationLogic reputationLogic) {
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
