package start;

import com.google.gson.JsonIOException;
import jade.wrapper.StaleProxyException;

import java.io.IOException;

/**
 * Class "main" of the project, starts HF e JADE (substitute of cli jade.Boot)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class StartNetwork {

  public static void main(String[] args) throws JsonIOException, IOException, StaleProxyException {

    // TODO: Fare verifica della rete fabric e in base a quella eseguire poi il
    // comando "./startFabric.sh"

    Object lockWaitingHFNetwork = new Object();
    JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();

    // TODO:Aggiungere controlli se rete up down e agire di conseguenza
    if (!StartClass.isUpBlockchain()) {
      synchronized (lockWaitingHFNetwork) {
        //        HFClient client = HFClient.createNewInstance();
        //        try {
        //          client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
        //        } catch (CryptoException e) {
        //          e.printStackTrace();
        //        } catch (InvalidArgumentException e) {
        //          e.printStackTrace();
        //        } catch (IllegalAccessException e) {
        //          e.printStackTrace();
        //        } catch (InstantiationException e) {
        //          e.printStackTrace();
        //        } catch (ClassNotFoundException e) {
        //          e.printStackTrace();
        //        } catch (NoSuchMethodException e) {
        //          e.printStackTrace();
        //        } catch (InvocationTargetException e) {
        //          e.printStackTrace();
        //        }
        //        Channel channel = null;
        //        try {
        //          channel = client.newChannel("service-cc");
        //        } catch (InvalidArgumentException e) {
        //          e.printStackTrace();
        //        }
        //
        //        try {
        //          SdkIntegration.installChaincodeProposalRequest(client, channel);
        //        } catch (InvalidArgumentException e) {
        //          e.printStackTrace();
        //        } catch (ProposalException e) {
        //          e.printStackTrace();
        //        }
        // TODO: Non funziona più, indagare (penso problema linux permission)
        //        ExecuteShellCommand.executeBashCommand("./bootFromJava.sh");
        // ExecuteShellCommand.executeBashCommand("ls");
        // ExecuteShellCommand.executeBashCommand("./startFabric.sh");
        // start the HF
      } // blockchain
    }

    jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
    // START RETAGGI SCENARIO
    // StartClass.startJadeRetaggiScenario(jadeJson2Pojo.getAgentNames(),
    // jadeJson2Pojo.getAgentTypes(), jadeJson2Pojo.getPort());

    // START AGENT HANDLER SCENARIO
    StartClass.startJadeHandler(jadeJson2Pojo.getPort());

  }
}
