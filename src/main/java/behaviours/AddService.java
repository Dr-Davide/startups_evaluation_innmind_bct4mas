package behaviours;

import agents.BCAgent;
import controllers.BCAgentController;
import controllers.CheckerController;
import controllers.ComplexWorkflowController;
import controllers.ReadController;
import jade.core.behaviours.OneShotBehaviour;
import model.ServiceView;
import model.pojo.Reputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;

import javax.swing.*;

public class AddService extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(AddService.class);

  private static final long serialVersionUID = 7570072986881441223L;
  private BCAgent bcAgent;
  private String serviceName;
  private String serviceDescription;
  private String serviceComposition;
  private String serviceWeight;
  private String serviceTime;
  private String initReputationValue = "6.0";

  public AddService(BCAgent agent) {
    super(agent);
    bcAgent = agent;
  }

  /**
   * Constructor that initalize with the service values
   *
   * AddService @param agent AddService @param serviceNameInput AddService @param
   * serviceDescriptionInput AddService @param serviceWeightInput AddService @param serviceTimeInput
   */
  public AddService(BCAgent agent, String serviceNameInput, String serviceDescriptionInput,
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

      ServiceView newServiceView;


      // TODO: Use the REAL ID
      String serviceId = serviceName;

      if (!CheckerController.isServiceMappedWithAgent(serviceId, bcAgent)) {
        synchronized (lockWaitingHFNetworkUpdatePropagation) {

          newServiceView =
              new ServiceView(serviceId, serviceName, serviceDescription, serviceComposition,
                  serviceWeight,
                  serviceTime, initReputationValue);


          String serviceName = newServiceView.getName();
          String serviceDescription = newServiceView.getDescription();
          String serviceComposition = newServiceView.getServiceComposition();
          String serviceCost = newServiceView.getCost();
          String serviceTime = newServiceView.getTime();
          String agentId = bcAgent.getMyName();
          String initReputationValue = newServiceView.getReputation();
          HFClient clientHF = bcAgent.getHfClient();
          Channel channel = bcAgent.getHfServiceChannel();
          User user = bcAgent.getUser();

          ComplexWorkflowController
              .createServiceAndCoupleWithAgentAndCreateReputation(bcAgent, serviceId, serviceName,
                  serviceDescription, serviceComposition, agentId, serviceCost, serviceTime,
                  initReputationValue,
                  clientHF, channel, user);
          String reputationId = agentId + serviceId + Reputation.EXECUTER_ROLE;
          String realReputationValue =
              ReadController.getReputation(clientHF, channel, reputationId).getValue().toString();
          newServiceView.setReputation(realReputationValue);

          // TODO: Refresh the serviceId

          //            RangeQueriesController.loadStructServiceInLedger(newStructService, bcAgent);

          bcAgent = BCAgentController.loadStructServiceInAgent(newServiceView, bcAgent);
        }

        bcAgent.addBehaviour(new RefreshBcAgentGui(bcAgent));
      } else {
        //          log.info("The service " + serviceId + " of the agent " + bcAgent
        //                    .getLocalName() + " already existing in the ledger");
        JOptionPane.showMessageDialog(bcAgent.bcAgentGui,
            "The service " + serviceId + " of the agent " + bcAgent.getLocalName()
                + " already existing in the ledger", "Service Already Mapped to Agent ",
            JOptionPane.ERROR_MESSAGE);
      }


    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
