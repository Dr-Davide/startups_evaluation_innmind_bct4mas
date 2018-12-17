package model.dao;

import com.google.protobuf.ByteString;
import fabric.ChaincodeEventCapture;
import fabric.SdkIntegration;
import model.GeneralLedgerInteraction;
import model.pojo.ServiceRelationAgent;
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
public class ServiceRelationAgentDAO extends GeneralLedgerInteraction
    implements Dao<ServiceRelationAgent> {

    private static final Logger log = Logger.getLogger(ServiceRelationAgentDAO.class);

    @Override public Optional<ServiceRelationAgent> get(HFClient hfClient, Channel channel,
        String relationId) {

        String chaincodeFunction = "GetServiceRelationAgent";

        String[] chaincodeArguments = new String[] {relationId};

        ServiceRelationAgent serviceRelationAgentPojo = new ServiceRelationAgent();

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
                        serviceRelationAgentPojo.setRelationId(jsonObject.getString("RelationId"));
                        serviceRelationAgentPojo.setServiceId(jsonObject.getString("ServiceId"));
                        serviceRelationAgentPojo.setAgentId(jsonObject.getString("AgentId"));
                        serviceRelationAgentPojo.setCost(jsonObject.getString("Cost"));
                        serviceRelationAgentPojo.setTime(jsonObject.getString("Time"));

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

        Optional<ServiceRelationAgent> optionalServiceRelationAgent =
            Optional.of(serviceRelationAgentPojo);

        return optionalServiceRelationAgent;
    }

  @Override
  public List<ServiceRelationAgent> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean create(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgent)
      throws ProposalException, InvalidArgumentException {

    String chaincodeFunctionName = "CreateServiceRelationAgent";
    String expectedEventName = "ServiceRelationAgentCreatedEvent";
    Integer eventTimeout = 100;

    // TODO: Gestire creazione ID(incrementale)

    String serviceId = serviceRelationAgent.getServiceId().toString();
    String agentId = serviceRelationAgent.getAgentId().toString();
    String cost = serviceRelationAgent.getCost().toString();
    String time = serviceRelationAgent.getTime().toString();
    String description = serviceRelationAgent.getDescription().toString();

    String[] chaincodeArguments = new String[] {serviceId, agentId, cost, time, description};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
      Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture
      String chaincodeEventListenerHandle =
          SdkIntegration.setChaincodeEventListener(channel, expectedEventName, chaincodeEvents);
    log.info("Chaincode Event Listener Handle: " + chaincodeEventListenerHandle);
      // END CHAINCODE EVENT LISTENER HANDLER

    Collection<ProposalResponse> invokePropResp = GeneralLedgerInteraction.writeBlockchain(clientHF,
        userHF, channel, chaincodeName, chaincodeFunctionName, chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSuccess =
        printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info(agentId + ": successfully received transaction proposal responses.");


      sendTxToOrderer(userHF, channel, successful, allPeerSuccess);

      // Wait for the event
      boolean eventDone = SdkIntegration
          .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents,
              chaincodeEventListenerHandle,
              expectedEventName);
      log.info("eventDone: " + eventDone);

      return allPeerSuccess;

  }

  @Override public boolean update(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgentToUpdate,
      String[] params) throws ProposalException, InvalidArgumentException {

    if (params.length != 2) {
      throw new InvalidArgumentException(
          "Insert only two Parameters, the Value of ServiceRelationAgent that you want to change, and the Field Specification, now params length is: "
              + params.length);
    }

    String fieldToUpdate = params[1];

    boolean allPeerSuccess;

    switch (fieldToUpdate) {
      case ServiceRelationAgent.COST:
        String newCost = params[0];
        allPeerSuccess =
            updateCostField(clientHF, userHF, channel, serviceRelationAgentToUpdate, newCost);
        break;
      case ServiceRelationAgent.TIME:
        String newTime = params[0];
        allPeerSuccess =
            updateTimeField(clientHF, userHF, channel, serviceRelationAgentToUpdate, newTime);
        break;
      case ServiceRelationAgent.DESCRIPTION:
        String newDescription = params[0];
        allPeerSuccess =
            updateDescriptionField(clientHF, userHF, channel, serviceRelationAgentToUpdate,
                newDescription);
        break;
      default: // should be unreachable
        IllegalStateException illegalStateException =
            new IllegalStateException("Wrong field to update, it's not in the expected ones");
        log.error(illegalStateException);
        throw illegalStateException;
    }

    return allPeerSuccess;

  }

  @Override public boolean delete(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgent)
      throws InvalidArgumentException, ProposalException {

    String chaincodeFunctionName = "DeleteServiceRelationAgent";
    String expectedEventName = "ServiceRelationAgentDeletedEvent";
    Integer eventTimeout = 100;

    // TODO: Gestire creazione ID(incrementale)

    String relationId = serviceRelationAgent.getRelationId().toString();

    String[] chaincodeArguments = new String[] {relationId};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
    Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture
    String chaincodeEventListenerHandle =
        SdkIntegration.setChaincodeEventListener(channel, expectedEventName, chaincodeEvents);
    log.info("Chaincode Event Listener Handle: " + chaincodeEventListenerHandle);
    // END CHAINCODE EVENT LISTENER HANDLER

    Collection<ProposalResponse> invokePropResp = GeneralLedgerInteraction
        .writeBlockchain(clientHF, userHF, channel, chaincodeName, chaincodeFunctionName,
            chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSuccess =
        printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info(serviceRelationAgent.getAgentId().toString()
        + ": successfully received transaction proposal responses.");

    sendTxToOrderer(userHF, channel, successful, allPeerSuccess);

    // Wait for the event
    boolean eventDone = SdkIntegration
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
            expectedEventName);

    log.info("eventDone: " + eventDone);

    return allPeerSuccess;

  }

  private boolean updateCostField(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgentToUpdate, String newCost)
      throws InvalidArgumentException, ProposalException {

    String chaincodeFunctionName = "UpdateServiceRelationAgentCost";
    String expectedEventName = "ServiceRelationAgentCostUpdatedEvent";
    Integer eventTimeout = 100;

    String relationId = serviceRelationAgentToUpdate.getRelationId().toString();
    String[] chaincodeArguments = new String[] {relationId, newCost};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
    //      String expectedEventName = "ReputationModifiedEvent";
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
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
            expectedEventName);
    log.info("eventDone: " + eventDone);

    return allPeerSuccess;

  }

  private boolean updateTimeField(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgentToUpdate, String newTime)
      throws InvalidArgumentException, ProposalException {

    String chaincodeFunctionName = "UpdateServiceRelationAgentTime";
    String expectedEventName = "ServiceRelationAgentTimeUpdatedEvent";
    Integer eventTimeout = 100;

    String relationId = serviceRelationAgentToUpdate.getRelationId().toString();
    String[] chaincodeArguments = new String[] {relationId, newTime};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
    //      String expectedEventName = "ReputationModifiedEvent";
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
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
            expectedEventName);
    log.info("eventDone: " + eventDone);

    return allPeerSuccess;

  }

  private boolean updateDescriptionField(HFClient clientHF, User userHF, Channel channel,
      ServiceRelationAgent serviceRelationAgentToUpdate, String newTime)
      throws InvalidArgumentException, ProposalException {

    String chaincodeFunctionName = "UpdateServiceRelationAgentDescription";
    String expectedEventName = "ServiceRelationAgentDescriptionUpdatedEvent";
    Integer eventTimeout = 100;

    String relationId = serviceRelationAgentToUpdate.getRelationId().toString();
    String[] chaincodeArguments = new String[] {relationId, newTime};

    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // START CHAINCODE EVENT LISTENER HANDLER WORKING:
    //      String expectedEventName = "ReputationModifiedEvent";
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
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
            expectedEventName);
    log.info("eventDone: " + eventDone);

    return allPeerSuccess;

  }

}
