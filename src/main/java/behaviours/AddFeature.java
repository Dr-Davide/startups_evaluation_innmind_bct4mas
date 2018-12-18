package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import model.FeatureView;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

import javax.swing.*;

public class AddFeature extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(AddFeature.class);

  private static final long serialVersionUID = 7570072986881441223L;
  private BCAgent bcAgent;
  private String serviceName;
  private String serviceDescription;
  private String serviceComposition;
  private String serviceWeight;
  private String serviceTime;
  private String initReputationValue = "6.0";

  public AddFeature(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  /**
   * Constructor that initalize with the service values
   *
   * AddFeature @param agent AddFeature @param serviceNameInput AddFeature @param
   * serviceDescriptionInput AddFeature @param serviceWeightInput AddFeature @param serviceTimeInput
   */
  public AddFeature(BCAgent agent, String serviceNameInput, String serviceDescriptionInput,
      String serviceCompositionInput, String serviceWeightInput, String serviceTimeInput) {
    super(agent);
    bcAgent = agent;
    serviceName = serviceNameInput;
    serviceDescription = serviceDescriptionInput;
    serviceComposition = serviceCompositionInput;
    serviceWeight = serviceWeightInput;
    serviceTime = serviceTimeInput;
  }

  @Override
  public void action() {
    Object lockWaitingHFNetworkUpdatePropagation = new Object();


    try {
      log.info(bcAgent.getLocalName() + " add composite service " + serviceName + " of weight "
          + serviceWeight.toString());

      FeatureView newFeatureView;


      // TODO: Use the REAL ID
      String serviceId = serviceName;

      if (!CheckerController.isFeatureMappedWithAgent(serviceId, bcAgent)) {
        synchronized (lockWaitingHFNetworkUpdatePropagation) {

          newFeatureView =
              new FeatureView(serviceId, serviceName, serviceDescription, serviceComposition,
                  serviceWeight,
                  serviceTime, initReputationValue);


          String serviceName = newFeatureView.getName();
          String serviceDescription = newFeatureView.getDescription();
          String serviceComposition = newFeatureView.getFeatureComposition();
          String serviceCost = newFeatureView.getCost();
          String serviceTime = newFeatureView.getTime();
          String agentId = bcAgent.getMyName();
          String initReputationValue = newFeatureView.getReputation();
          HFClient clientHF = bcAgent.getHfClient();
          Channel channel = bcAgent.getHfTransactionChannel();
          User user = bcAgent.getUser();

          ComplexWorkflowController
              .createFeatureAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
                  serviceDescription, serviceComposition, agentId, serviceCost, serviceTime,
                  initReputationValue,
                  clientHF, channel, user);
          String reputationId = agentId + serviceId + InnMindReputation.EXPERT_ROLE;
          String realReputationValue =
              ReadController.getReputation(clientHF, channel, reputationId).getValue().toString();
          newFeatureView.setReputation(realReputationValue);

          // TODO: Refresh the serviceId

          //            RangeQueriesController.loadStructFeatureInLedger(newStructFeature, bcAgent);

//          bcAgent = BCAgentController.loadStructFeatureInAgent(newFeatureView, bcAgent);
          bcAgent.featuresList.add(newFeatureView);
        }

        bcAgent.addBehaviour(new RefreshBcAgentGui(bcAgent));
      } else {
        //          log.info("The service " + serviceId + " of the agent " + bcAgent
        //                    .getLocalName() + " already existing in the ledger");
        JOptionPane.showMessageDialog(bcAgent.bcAgentGui,
            "The service " + serviceId + " of the agent " + bcAgent.getLocalName()
                + " already existing in the ledger", "Feature Already Mapped to Agent ",
            JOptionPane.ERROR_MESSAGE);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
