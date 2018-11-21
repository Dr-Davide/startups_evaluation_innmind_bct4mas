package start;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

/**
 * Singleton for containing the mainContainer instance of Jade to enable
 * creation of agents programmatically in the main container (reference, first
 * option of: http://jade.tilab.com/pipermail/jade-develop/2008q3/012842.html)
 *
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class JadeMainContainerSingleton {

  private static JadeMainContainerSingleton jadeInstance;
  private ContainerController mainContainer;

  /**
   * Construct the jade instance with the main container
   * JadeMainContainerSingleton @param jadePort
   */
  private JadeMainContainerSingleton(String jadePort) {
	mainContainer = bootMainContainer(jadePort);
  }

  /**
   * Use to create a jadeInstance in the desired jadePort
   *
   * @param jadePort
   * @return
   */
  public static JadeMainContainerSingleton getInstance(String jadePort) {
	if (jadeInstance == null) {
	  jadeInstance = new JadeMainContainerSingleton(jadePort);
	}
	return jadeInstance;
  }

  /**
   * Use only to call an already existing jadeInstance
   *
   * @return
   * @throws Exception
   */
  public static JadeMainContainerSingleton getInstance() throws Exception {
	if (jadeInstance == null) {
	  Exception exception = new Exception(
		  "Jade istance doesn't already exist, put the jadePort as a parameter to create a fresh one jade istance");
	  throw exception;
	}
	return jadeInstance;
  }

  /**
   * Set Up the Jade's main container
   *
   * @param jadePort
   * @return
   */
  private ContainerController bootMainContainer(String jadePort) {
	// Get a hold on JADE runtime (SINGLETON)
	Runtime runtime = Runtime.instance();
	// Create a default profile
	ProfileImpl profile = new ProfileImpl();
	profile.setParameter(Profile.GUI, "true");
	profile.setParameter(Profile.MAIN_PORT, jadePort);
	profile.setParameter(Profile.LOCAL_PORT, jadePort);
	ContainerController mainContainer = runtime.createMainContainer(profile);
	return mainContainer;
  }

  /**
   * @return the mainContainer
   */
  public ContainerController getMainContainer() {
	return mainContainer;
  }
}
