package start;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

public class StartClass {

  public static final String JADE_CONFIG_FILE = "configJade.json";
  public static final String HF_CONFIG_FILE = "configHF.json";

  /**
   * Verify HF Blockchain State
   *
   * @return
   */
  static boolean isUpBlockchain() {
    // TODO: Verifica lo stato della blockchain, se già creata o da creare

    //    return true;
    return false;
  }

  /**
   * Start the AgentHandler
   *
   * @param agentNames
   * @param agentTypes
   * @param jadePort
   * @throws StaleProxyException
   */
  static void startJadeHandler(String jadePort) throws StaleProxyException {
    JadeMainContainerSingleton jadeSingleton = JadeMainContainerSingleton.getInstance(jadePort);

    Object[] noArgs = getEmptyAgentArguments();

    AgentController agentHandlerController = null, snifferController = null, rmaController = null;

    agentHandlerController =
        jadeSingleton.getMainContainer().createNewAgent("handler", "agents.AgentHandler", noArgs);

    // rmaController =
    // jadeSingleton.getMainContainer().createNewAgent("rma", "jade.tools.rma.rma", noArgs);

    snifferController = jadeSingleton.getMainContainer().createNewAgent("sniffer",
        "jade.tools.sniffer.Sniffer", new Object[] {"a2"}); // TODO: prende solo un parametro (da
                                                            // API) trovare workaround

    // FIRE UP THE AGENTS
    agentHandlerController.start();
    // Fire up the RMA & SNIFFER
    // rmaController.start();
    // snifferController.start();
    // FOR the jade v 4.5.0 to not show the rma
    try {
      rmaController = jadeSingleton.getMainContainer().getAgent("rma");
    } catch (jade.wrapper.ControllerException e) {
      e.printStackTrace();
    }
    rmaController.kill();

  }

  /**
   *
   * @return an Object[1]
   */
  public static Object[] getEmptyAgentArguments() {
    Object reference = new Object();
    Object noArgs[] = new Object[1];
    noArgs[0] = reference;
    return noArgs;
  }

  /**
   * Start the Jade agents (Retaggi Version)
   *
   * @param agentNames
   * @param agentTypes
   * @param jadePort
   * @throws StaleProxyException
   */
  public static void startJadeRetaggiScenario(List<String> agentNames, List<String> agentTypes,
      String jadePort) throws StaleProxyException {

    JadeMainContainerSingleton jadeSingleton = JadeMainContainerSingleton.getInstance(jadePort);

    Object[] noArgs = getEmptyAgentArguments();

    AgentController agentHandlerController = null, snifferController = null, rmaController = null,
        generalAgentController = null;

    agentHandlerController =
        jadeSingleton.getMainContainer().createNewAgent("handler", "agents.AgentHandler", noArgs);

    rmaController =
        jadeSingleton.getMainContainer().createNewAgent("rma", "jade.tools.rma.rma", noArgs);

    snifferController = jadeSingleton.getMainContainer().createNewAgent("sniffer",
        "jade.tools.sniffer.Sniffer", new Object[] {"a2"}); // TODO: prende solo un parametro (da
                                                            // API) trovare workaround

    // Fire up the Jade agents (CAAgent, BCAgent)
    for (int i = 0; i < agentTypes.size(); i++) {
      generalAgentController = jadeSingleton.getMainContainer().createNewAgent(agentNames.get(i),
          "agents." + agentTypes.get(i), noArgs);
      generalAgentController.start();
    }

    agentHandlerController.start();

    // Fire up the RMA & SNIFFER
    rmaController.start();
    snifferController.start();

  }

  /**
   * Return config Jade POJO class
   *
   * @param configFile
   * @return
   * @throws JsonSyntaxException
   * @throws JsonIOException
   * @throws FileNotFoundException
   */
  public static JadeJson2Pojo getJadeJsonConfig(String configFile)
      throws JsonSyntaxException, JsonIOException, FileNotFoundException {
    JadeJson2Pojo jadeJson2Pojo;
    Reader inputStreamReader = getReaderFromResourceFilePath(configFile);

    jadeJson2Pojo = new Gson().fromJson(inputStreamReader, JadeJson2Pojo.class);
    return jadeJson2Pojo;

  }

  // TODO: SMELL CODE REFACTOR ASAP
  private static Reader getReaderFromResourceFilePath(String configFile) {
    //Get file from resources folder
    InputStream inputStream = getInputStream(configFile);
    return new InputStreamReader(inputStream);
  }

  // TODO: SMELL CODE REFACTOR ASAP
  public static InputStream getInputStreamPublic(String configFile) {
    return getInputStream(configFile);
  }

  // TODO: SMELL CODE REFACTOR ASAP
  private static InputStream getInputStream(String configFile) {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    return classLoader.getResourceAsStream(configFile);
  }

  // TODO: SMELL CODE REFACTOR ASAP
  public static URL getURLPublic(String configFile) {
    return getURL(configFile);
  }

  // TODO: SMELL CODE REFACTOR ASAP
  private static URL getURL(String configFile) {
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    return classLoader.getResource(configFile);
  }

  // TODO: SMELL CODE REFACTOR ASAP
  public static Reader getReaderFromResourceFilePathPublic(String configFile) {
    return getReaderFromResourceFilePath(configFile);
  }

  /**
   * Return config HF POJO class
   *
   * @param configFile
   * @return
   * @throws JsonSyntaxException
   * @throws JsonIOException
   * @throws FileNotFoundException
   */
  public static HFJson2Pojo getHfJsonConfig(String configFile)
      throws JsonSyntaxException, JsonIOException, FileNotFoundException {
    HFJson2Pojo hfJson2Pojo;
    Reader inputStreamReader = getReaderFromResourceFilePath(configFile);

    hfJson2Pojo = new Gson().fromJson(inputStreamReader, HFJson2Pojo.class);
    return hfJson2Pojo;
  }
}
