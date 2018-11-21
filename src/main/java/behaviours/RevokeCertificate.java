package behaviours;

import agents.BCAgent;
import jade.core.behaviours.OneShotBehaviour;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TODO: richiama bash, per ora il file bash elimina solo a1
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class RevokeCertificate extends OneShotBehaviour {

  private static final Logger log = Logger.getLogger(RevokeCertificate.class);
  private static final long serialVersionUID = 6532009391353162691L;


  BCAgent bcAgent;

  public RevokeCertificate(BCAgent agent) {
	super(agent);
	bcAgent = agent;
  }

  @Override
  public void action() {
	try {
	  revokeBcAgentCertificate();
	} catch (IOException e) {
	  e.printStackTrace();
	} catch (InterruptedException e) {
	  e.printStackTrace();
	}

  }

  private void revokeBcAgentCertificate() throws IOException, InterruptedException {

	if (bcAgent.getConfigurationType().equals("config4")) {

	  // TODO: generalizzare revoke-a1.sh prendere il nome agente come parametro del
	  // bash
	  ProcessBuilder processBuilder = new ProcessBuilder("bash", "fabric-tools/revoke-a1.sh"); // TODO:
																							   // capire
																							   // cosa
																							   // fa

	  processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	  processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    log.info("Revoking certificate for a1....");
	  processBuilder.start();
	  TimeUnit.SECONDS.sleep(15); // TODO: togliere sleep
	}
  }

}
