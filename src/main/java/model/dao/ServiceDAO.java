package model.dao;

import com.google.protobuf.ByteString;
import fabric.ChaincodeEventCapture;
import fabric.SdkIntegration;
import model.GeneralLedgerInteraction;
import model.Utils;
import model.pojo.Service;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;


public class ServiceDAO extends GeneralLedgerInteraction implements Dao<Service> {

    private static final Logger log = Logger.getLogger(ServiceDAO.class);

    @Override public Optional<Service> get(HFClient hfClient, Channel channel, String serviceId) {

        String chaincodeFunction = "GetService";

        String[] chaincodeArguments = new String[] {serviceId};

        Service servicePojo = new Service();

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
                  log.info("JSON OBJECT: " + jsonObject);

                    // add at the answer only the response of the first peer
                    // (if all success we infer that the data returned are all the same)
                    if (firstPeerAnswer) {
                        servicePojo.setServiceId(jsonObject.getString("ServiceId"));
                        servicePojo.setName(jsonObject.getString("Name"));
                        servicePojo.setDescription(jsonObject.getString("Description"));
                      servicePojo.setServiceComposition(Utils.parseServiceComposition(jsonObject));

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

        Optional<Service> optionalService = Optional.of(servicePojo);

        return optionalService;
    }

  @Override
  public List<Service> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean create(HFClient clientHF, User userHF, Channel channel, Service newService)
      throws ProposalException, InvalidArgumentException {

    String chaincodeFunction = "CreateService";
    String expectedEventName = "ServiceCreatedEvent";
    Integer eventTimeout = 150;

    String serviceId = newService.getServiceId().toString();
    String serviceName = newService.getName().toString();
    String serviceDesc = newService.getDescription().toString();
    String serviceComposition;

    //    if (newService.getServiceComposition().equals(null)) {
    //      log.info("NULLO");
    //      serviceComposition = "";
    //    } else {
    serviceComposition = newService.getServiceComposition().toString();
    //    }

    String[] chaincodeArguments =
        new String[] {serviceId, serviceName, serviceDesc, serviceComposition};


    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // INIZIO CHAINCODE EVENT LISTENER HANDLER WORKING:
      Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture
      String chaincodeEventListenerHandle =
          SdkIntegration.setChaincodeEventListener(channel, expectedEventName, chaincodeEvents);
    log.info("Chaincode Event Listener Handle: " + chaincodeEventListenerHandle);
      // FINE CHAINCODE EVENT LISTENER HANDLER

    Collection<ProposalResponse> invokePropResp = writeBlockchain(clientHF, userHF, channel,
        chaincodeName, chaincodeFunction, chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSucces = printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info("successfully received transaction proposal responses.");

    sendTxToOrderer(userHF, channel, successful, allPeerSucces);

      // Wait for the event
    boolean eventDone;
    eventDone = SdkIntegration
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
              expectedEventName);
      log.info("eventDone: " + eventDone);

    return allPeerSucces;

  }

  public boolean createLeafService(HFClient clientHF, User userHF, Channel channel,
      Service newService) throws ProposalException, InvalidArgumentException {

    String chaincodeFunction = "CreateLeafService";
    String expectedEventName = "ServiceCreatedEvent";
    Integer eventTimeout = 150;

    String serviceId = newService.getServiceId().toString();
    String serviceName = newService.getName().toString();
    String serviceDesc = newService.getDescription().toString();

    String[] chaincodeArguments = new String[] {serviceId, serviceName, serviceDesc};


    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    // INIZIO CHAINCODE EVENT LISTENER HANDLER WORKING:
    Vector<ChaincodeEventCapture> chaincodeEvents = new Vector<>(); // Test list to capture
    String chaincodeEventListenerHandle =
        SdkIntegration.setChaincodeEventListener(channel, expectedEventName, chaincodeEvents);
    log.info("Chaincode Event Listener Handle: " + chaincodeEventListenerHandle);
    // FINE CHAINCODE EVENT LISTENER HANDLER

    Collection<ProposalResponse> invokePropResp =

        writeBlockchain(clientHF, userHF, channel, chaincodeName, chaincodeFunction,
            chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSucces = printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info("successfully received transaction proposal responses.");

    sendTxToOrderer(userHF, channel, successful, allPeerSucces);

    // Wait for the event
    boolean eventDone = false;
    eventDone = SdkIntegration
        .waitForChaincodeEvent(eventTimeout, channel, chaincodeEvents, chaincodeEventListenerHandle,
            expectedEventName);
    log.info("eventDone: " + eventDone);

    return allPeerSucces;

  }

    @Override public boolean update(HFClient clientHF, User userHF, Channel channel, Service t,
        String[] params) throws ProposalException, InvalidArgumentException {
    // TODO Auto-generated method stub
        return false;

  }

  @Override public boolean delete(HFClient clientHF, User user, Channel channel, Service t) {
    // TODO Auto-generated method stub

    return false;
  }



}
