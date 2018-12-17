package model.dao;

import com.google.protobuf.ByteString;
import fabric.ChaincodeEventCapture;
import fabric.SdkIntegration;
import model.GeneralLedgerInteraction;
import model.pojo.Activity;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;

public class ActivityDAO extends GeneralLedgerInteraction implements Dao<Activity> {

    private static final Logger log = Logger.getLogger(ActivityDAO.class);

    @Override public Optional<Activity> get(HFClient hfClient, Channel channel, String activityId) {

        String chaincodeFunction = "GetActivity";

        String[] chaincodeArguments = new String[] {activityId};

        Activity activityPojo = new Activity();

        Collection<ProposalResponse> proposalResponseCollection = null;
        // proposalResponseCollection contiene le risposte dei 3 peer
        try {
            proposalResponseCollection =
                queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction,
                    chaincodeArguments);
        } catch (ProposalException e) {
            e.printStackTrace();
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        boolean firstPeerAnswer = true;
        // take (iter) every response from the peers
        for (ProposalResponse proposalResponse : proposalResponseCollection) {
            if (proposalResponse.isVerified()
                && proposalResponse.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                ByteString payload =
                    proposalResponse.getProposalResponse().getResponse().getPayload();
                try (JsonReader jsonReader = Json
                    .createReader(new ByteArrayInputStream(payload.toByteArray()))) {
                    // parse response
                    JsonObject jsonObject = jsonReader.readObject();
                    log.info(jsonObject);

                    // add at the answer only the response of the first peer
                    // (if all success we infer that the data returned are all the same)
                    if (firstPeerAnswer) {
                        activityPojo.setEvaluationId(jsonObject.getString("EvaluationId"));
                        activityPojo.setWriterAgentId(jsonObject.getString("WriterAgentId"));
                        activityPojo.setDemanderAgentId(jsonObject.getString("DemanderAgentId"));
                        activityPojo.setExecuterAgentId(jsonObject.getString("ExecuterAgentId"));
                        activityPojo
                            .setExecutedServiceId(jsonObject.getString("ExecutedServiceId"));
                        activityPojo
                            .setExecutedServiceTxId(jsonObject.getString("ExecutedServiceTxid"));
                        activityPojo.setExecutedServiceTimestamp(
                            jsonObject.getString("ExecutedServiceTimestamp"));
                        activityPojo.setValue(jsonObject.getString("Value"));

                        firstPeerAnswer = false;
                    }

                    String payloadString =
                        new String(proposalResponse.getChaincodeActionResponsePayload());
                  log.info(
                        "response from peer: " + proposalResponse.getPeer().getName() + ": "
                            + payloadString);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            } else {
                log.error("response failed. status: " + proposalResponse.getStatus().getStatus());
            }
        }

        Optional<Activity> optionalActivity = Optional.of(activityPojo);

        return optionalActivity;
    }

  @Override
  public List<Activity> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean create(HFClient clientHF, User userHF, Channel channel, Activity newActivity)
      throws ProposalException, InvalidArgumentException {

    String chaincodeFunctionName = "CreateActivity";
    String expectedEventName = "ActivityCreatedEvent";
    Integer eventTimeout = 150;

    String writerAgentId = newActivity.getWriterAgentId().toString();
    String demanderAgentId = newActivity.getDemanderAgentId().toString();
    String executerAgentId = newActivity.getExecuterAgentId().toString();
    String executedServiceId = newActivity.getExecutedServiceId().toString();
    String executedServiceTxId = newActivity.getExecutedServiceTxId().toString();
    String executedServiceTimestamp = newActivity.getExecutedServiceTimestamp().toString();
    String value = newActivity.getValue().toString();

    String[] chaincodeArguments =
          new String[] {writerAgentId, demanderAgentId, executerAgentId, executedServiceId,
              executedServiceTxId, executedServiceTimestamp, value};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
      Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture
      String chaincodeEventListenerHandle =
          SdkIntegration.setChaincodeEventListener(channel, expectedEventName, chaincodeEvents);
    log.info("Chaincode Event Listener Handle: " + chaincodeEventListenerHandle);
      // END CHAINCODE EVENT LISTENER HANDLER

      Collection<ProposalResponse> invokePropResp =
          writeBlockchain(clientHF, userHF, channel, chaincodeName, chaincodeFunctionName,
              chaincodeArguments);

      // iter every answer from the peers
    boolean allPeerSuccess =
        printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info("successfully received transaction proposal responses.");



      /**
       * Send transaction to orderer only if all peer success
       */

      sendTxToOrderer(userHF, channel, successful, allPeerSuccess);

      // Wait for the event
      boolean eventDone = false;
    eventDone = SdkIntegration
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
              expectedEventName);
      log.info("eventDone: " + eventDone);

      return allPeerSuccess;

  }

    @Override public boolean update(HFClient clientHF, User userHF, Channel channel, Activity t,
        String[] params) {
    // TODO Auto-generated method stub
        // for now we don't want the client sdk to modify the activities
        return false;
  }

  @Override public boolean delete(HFClient clientHF, User user, Channel channel, Activity t) {
    // TODO Auto-generated method stub
    return false;

      // for now we don't want the client sdk to delete the activities
  }


}
