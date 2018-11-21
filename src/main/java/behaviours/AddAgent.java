package behaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.apache.log4j.Logger;
import start.JadeMainContainerSingleton;
import start.StartClass;

public class AddAgent extends OneShotBehaviour {

    private static final Logger log = Logger.getLogger(AddAgent.class);


    private static final long serialVersionUID = 7329104123538608247L;
    private String agentName;
  private String agentType;
  private String expirationCertificate;

  /**
   * Add Agent in the main container
   * 
   * @param agentNameInput
   * @param agentTypeInput
   * @param expirationCertificateInput
   */
  public AddAgent(String agentNameInput, String agentTypeInput, String expirationCertificateInput) {
    agentName = agentNameInput;
    agentType = agentTypeInput;
    expirationCertificate = expirationCertificateInput;
  }

  @Override
  public void action() {

    JadeMainContainerSingleton jadeSingleton = null;

    try {
      jadeSingleton = JadeMainContainerSingleton.getInstance();
    } catch (Exception e2) {
      // TODO Auto-generated catch block
      e2.printStackTrace();
    }

    ContainerController mainContainer = jadeSingleton.getMainContainer();
    Object[] noArgs = StartClass.getEmptyAgentArguments();

    AgentController agentController;
    // create & fire the agent
    try {
      agentController = mainContainer.createNewAgent(agentName, "agents." + agentType, noArgs);
      agentController.start();
        log.info("Started agent: " + agentName + " of type: " + agentType);

    } catch (StaleProxyException e) {
      // TODO Auto-generated catch block
        log.error(e);
      e.printStackTrace();
    }
  }

}
