package model.dao;

import com.google.protobuf.ByteString;
import fabric.ChaincodeEventCapture;
import fabric.SdkIntegration;
import model.GeneralLedgerInteraction;
import model.pojo.Agent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;



public class AgentDAO extends GeneralLedgerInteraction implements Dao<Agent> {

    private static final Logger log = Logger.getLogger(AgentDAO.class);

    @Override public Optional<Agent> get(HFClient hfClient, Channel channel, String agentId) {
        String chaincodeFunction = "GetAgent";

        String[] chaincodeArguments = new String[] {agentId};

        model.pojo.Agent agentPojo = new model.pojo.Agent();

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

                    // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
                    if (firstPeerAnswer) {
                        agentPojo.setAgentId(jsonObject.getString("AgentId"));
                        agentPojo.setName(jsonObject.getString("Name"));
                        agentPojo.setAddress(jsonObject.getString("Address"));
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

        Optional<Agent> optionalAgent = Optional.of(agentPojo);

        return optionalAgent;
    }

    @Override public List<Agent> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean create(HFClient clientHF, User userHF, Channel channel, Agent newAgent)
        throws ProposalException, InvalidArgumentException {

      String chaincodeFunctionName = "CreateAgent";
      String expectedEventName = "AgentCreatedEvent";
      Integer eventTimeout = 150;

      String agentId = newAgent.getAgentId().toString();
      String agentName = newAgent.getName().toString();
      String agentAddress = newAgent.getAddress().toString();

      String[] chaincodeArguments = new String[] {agentId, agentName, agentAddress};

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

        // itero ogni risposta dei peer
      boolean allPeerSucces =
          printWriteProposalResponse(successful, failed, invokePropResp, channel);

      log.info("successfully received transaction proposal responses.");



        /**
         * Send transaction to orderer only if all peer success
         */

        sendTxToOrderer(userHF, channel, successful, allPeerSucces);

        // Wait for the event OLD
        //        while (chaincodeEvents.isEmpty()) {
        //            // do nothing
        //        }

        // Wait for the event
        boolean eventDone = false;
      eventDone = SdkIntegration.waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents,
          chaincodeEventListenerHandle,
                expectedEventName);
        log.info("eventDone: " + eventDone);

        return allPeerSucces;

    }

    @Override
    public boolean update(HFClient clientHF, User userHF, Channel channel, Agent t, String[] params)
        throws ProposalException, InvalidArgumentException {
        // TODO Auto-generated method stub
        return false;
    }

  @Override public boolean delete(HFClient clientHF, User user, Channel channel, Agent t) {
        // TODO Auto-generated method stub
    return false;
    }


}
