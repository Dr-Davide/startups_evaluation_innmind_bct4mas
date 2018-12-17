package model.dao;

import com.google.protobuf.ByteString;
import fabric.ChaincodeEventCapture;
import fabric.SdkIntegration;
import model.GeneralLedgerInteraction;
import model.pojo.Reputation;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;

// TODO: extend GeneralLedgerInteraction

public class ReputationDAO extends GeneralLedgerInteraction implements Dao<Reputation> {

    private static final Logger log = Logger.getLogger(ReputationDAO.class);


    @Override
    public Optional<Reputation> get(HFClient hfClient, Channel channel, String reputationId) {

        String chaincodeFunction = "GetReputation";

        String[] chaincodeArguments = new String[] {reputationId};

        Reputation reputationPojo = new Reputation();

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
                        reputationPojo.setReputationId(jsonObject.getString("ReputationId"));
                        reputationPojo.setAgentId(jsonObject.getString("AgentId"));
                        reputationPojo.setServiceId(jsonObject.getString("ServiceId"));
                        reputationPojo.setAgentRole(jsonObject.getString("AgentRole"));
                        reputationPojo.setValue(jsonObject.getString("Value"));

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

        Optional<Reputation> optionalReputation = Optional.of(reputationPojo);

        return optionalReputation;
    }

  @Override
  /**
   *
   */
  public List<Reputation> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean create(HFClient clientHF, User userHF, Channel channel, Reputation newReputation)
      throws ProposalException, InvalidArgumentException {

    String chaincodeFunctionName = "CreateReputation";
    String expectedEventName = "ReputationCreatedEvent";
    Integer eventTimeout = 150;

    String agentId = newReputation.getAgentId().toString();
    String serviceId = newReputation.getServiceId().toString();
    String agentRole = newReputation.getAgentRole().toString();
    String value = newReputation.getValue().toString();

    String[] chaincodeArguments = new String[] {agentId, serviceId, agentRole, value};

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


    /**
     * You can only modify the {@link Reputation.value}
     */
    @Override public boolean update(HFClient clientHF, User userHF, Channel channel,
        Reputation reputationToUpdate, String[] params)
        throws ProposalException, InvalidArgumentException {

        if (params.length != 1) {
            throw new InvalidArgumentException(
                "Insert only one Parameter, the Value of reputation that you want to change, now params length is: "
                    + params.length);
        }

      String chaincodeFunctionName = "ModifyReputationValue";
      String expectedEventName = "ReputationModifiedEvent";
      Integer eventTimeout = 150;

      String reputationId = reputationToUpdate.getReputationId().toString();
      String newValue = params[0];

      String[] chaincodeArguments = new String[] {reputationId, newValue};

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
        boolean eventDone = SdkIntegration
            .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents,
                chaincodeEventListenerHandle,
                expectedEventName);
        log.info("eventDone: " + eventDone);

        return allPeerSuccess;

  }

  @Override public boolean delete(HFClient clientHF, User user, Channel channel, Reputation t) {
    // TODO Auto-generated method stub
    return false;

  }

}
