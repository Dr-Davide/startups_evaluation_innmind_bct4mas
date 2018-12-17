package model;

import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.Collection;



public class GeneralLedgerInteraction {

  public String chaincodeName = "trustreputationledger";

  /**
   * Constructor
   */
  public GeneralLedgerInteraction() {
    super();
  }

  private static final Logger log = Logger.getLogger(GeneralLedgerInteraction.class);

  /**
   * Invoke Blockchain Query
   * 
   * @param hfClient
   * @param channel
   * @param chaincodeName
   * @param chaincodeFunction
   * @param chaincodeArguments
   * @return
   * @throws ProposalException
   * @throws InvalidArgumentException
   */
  protected static Collection<ProposalResponse> queryBlockChain(HFClient hfClient, Channel channel,
      String chaincodeName, String chaincodeFunction, String[] chaincodeArguments)
      throws ProposalException, InvalidArgumentException {
    // create chaincode request
    QueryByChaincodeRequest queryByChaincodeRequest =
        setQueryByChaincodeRequest(hfClient, chaincodeName, chaincodeFunction, chaincodeArguments);

    Collection<ProposalResponse> responses = channel.queryByChaincode(queryByChaincodeRequest);
    return responses;
  }

  /**
   * Wrapper that prepare the structure (request) to pass in the call of the chaincode query
   * 
   * @param clientHf
   * @param chaincodeName
   * @param chaincodeFunction
   * @param chaincodeArguments
   * @return QueryByChaincodeRequest
   */
  private static QueryByChaincodeRequest setQueryByChaincodeRequest(HFClient clientHf,
      String chaincodeName, String chaincodeFunction, String[] chaincodeArguments) {

    QueryByChaincodeRequest queryByChaincodeRequest = clientHf.newQueryProposalRequest();

    // build cc id providing the chaincode name. Version is omitted here.
    ChaincodeID chaincodeId = ChaincodeID.newBuilder().setName(chaincodeName).build();

    queryByChaincodeRequest.setChaincodeID(chaincodeId);
    queryByChaincodeRequest.setFcn(chaincodeFunction);
    queryByChaincodeRequest.setArgs(chaincodeArguments);

    return queryByChaincodeRequest;
  }

  /**
   * Print the result of queryBlockChain
   * 
   * @param proposalResponses
   * @throws InvalidArgumentException
   */
  static void printProposalResponses(Collection<ProposalResponse> proposalResponses)
      throws InvalidArgumentException {
    // display response
    for (ProposalResponse proposalResponse : proposalResponses) {
      String stringResponse = new String(proposalResponse.getChaincodeActionResponsePayload());
      log.info(stringResponse);
    }
  }



  /**
   * Invoke Blockchain Write
   * 
   * @param clientHF
   * @param userHF
   * @param channel
   * @param chaincodeName TODO
   * @param chaincodeFunctionName
   * @param chaincodeArguments
   * @return
   * @throws ProposalException
   * @throws InvalidArgumentException
   */
  protected static Collection<ProposalResponse> writeBlockchain(HFClient clientHF, User userHF,
      Channel channel, String chaincodeName, String chaincodeFunctionName,
      String[] chaincodeArguments) throws ProposalException, InvalidArgumentException {
    TransactionProposalRequest transactionProposalRequest = setTransactionProposalRequest(clientHF,
        userHF, chaincodeName, chaincodeFunctionName, chaincodeArguments);

    // INVIA la request nel canale, tutti i peer rispondono
    Collection<ProposalResponse> invokePropResp =
        channel.sendTransactionProposal(transactionProposalRequest);
    return invokePropResp;
  }

  /**
   * Wrapper to prepare the request for the blockchain write
   * 
   * @param clientHf
   * @param userHF
   * @param chaincodeName
   * @param chaincodeFunctionName
   * @param chaincodeArguments
   * @return
   */
  private static TransactionProposalRequest setTransactionProposalRequest(HFClient clientHf,
      User userHF, String chaincodeName, String chaincodeFunctionName,
      String[] chaincodeArguments) {

    TransactionProposalRequest transactionProposalRequest =
        clientHf.newTransactionProposalRequest();

    ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).build();


    // CONSTRUCT TRANSACTION PROPOSAL REQUEST

    transactionProposalRequest.setChaincodeID(chaincodeID);
    transactionProposalRequest.setFcn(chaincodeFunctionName);
    transactionProposalRequest.setArgs(chaincodeArguments);
    transactionProposalRequest.setUserContext(userHF);
    return transactionProposalRequest;
  }

  /**
   * Print the Proposal Response of a write
   * 
   * @param successful
   * @param failed
   * @param invokePropResp
   * @param channel
   * @return
   */
  protected static boolean printWriteProposalResponse(Collection<ProposalResponse> successful,
      Collection<ProposalResponse> failed, Collection<ProposalResponse> invokePropResp,
      Channel channel) {
    boolean allPeerSuccess = true;
    log.info("Sending proposal response");

    ////////////////////////////
    // only a client from the same org as the peer can issue an install request
    int numInstallProposal = 0;
    // Set<String> orgs = orgPeers.keySet();
    // for (SampleOrg org : testSampleOrgs) {

    Collection<Peer> peers = channel.getPeers();
    numInstallProposal = numInstallProposal + peers.size();

    for (ProposalResponse response : invokePropResp) {

      String peerName = response.getPeer().getName();

      if (response.getStatus() == Status.SUCCESS) {
        log.info("uccessful transaction proposal: " + response.getStatus()
            + " response Txid: " + response.getTransactionID() + " from peer " + peerName);
        successful.add(response);
      } else {
        log.info("Failed transaction proposal: " + response.getStatus()
            + " response Txid: " + response.getTransactionID() + " from peer " + peerName
            + " with message: " + response.getMessage());
        failed.add(response);
        log.info(response.getStatus().toString());
        allPeerSuccess = false;
      }
    }

    log.info(
        "Received " + numInstallProposal + " proposal responses. Successful+verified: " + successful
            .size() + " . Failed: " + failed.size());
    if (failed.size() > 0) {
      ProposalResponse first = failed.iterator().next();
      log.error(
          "Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
    }

    return allPeerSuccess;
  }

  /**
   * Verify if the chaincode return a FAILURE
   * 
   * @param proposalResponse
   * @return
   */
  static boolean isFailureResponse(ProposalResponse proposalResponse) {
    String responseStatus = proposalResponse.getStatus().name().toString();
    boolean isFailureResponse = responseStatus.equals(Status.FAILURE.toString());
    return isFailureResponse;
  }

  protected static void sendTxToOrderer(User userHF, Channel channel,
      Collection<ProposalResponse> successful, boolean allPeerSucces) {
    if (allPeerSucces) {
      if (userHF != null) {
        channel.sendTransaction(successful, userHF);
      } else {
        channel.sendTransaction(successful);
      }
    }
  }



}
