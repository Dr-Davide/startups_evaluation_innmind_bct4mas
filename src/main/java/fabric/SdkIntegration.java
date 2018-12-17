package fabric;

import agents.BCAgent;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import start.HFJson2Pojo;
import start.StartClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SdkIntegration {

  private static final String TEST_FIXTURES_PATH = "chaincode"; // src/test/fixture
  private static final String CHAIN_CODE_FILEPATH = "chaincode";  // sdkintegration/gocc/sample1
  private static final String CHAIN_CODE_NAME = "trustreputationledger";
  private static final String CHAIN_CODE_PATH = "github.com/trustreputationledger";
  // github.com/example_cc
  private static final String CHAIN_CODE_VERSION = "1.0";
  private static final TransactionRequest.Type CHAIN_CODE_LANG = TransactionRequest.Type.GO_LANG;
  private static final long DEPLOYWAITTIME = 1000;


  private static final Logger log = Logger.getLogger(SdkIntegration.class);


  // TODO: Learn How to Install Chaincode from SDK instead that from cli
  public static void installChaincodeProposalRequest(HFClient client, Channel channel)
      throws InvalidArgumentException, ProposalException {
    // create ChaincodeID
    ChaincodeID chaincodeID;
    ChaincodeID.Builder chaincodeIDBuilder =
        ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION);
    chaincodeID = chaincodeIDBuilder.build();

    SampleOrg sampleOrg = new SampleOrg("peerOrg1", "Org1MSP");


    Collection<ProposalResponse> responses;
    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    ////////////////////////////
    // Install Proposal Request
    //
    boolean installFromDirectory = true;

    client.setUserContext(sampleOrg.getPeerAdmin());

    log.info("Creating install proposal");

    InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
    installProposalRequest.setChaincodeID(chaincodeID);

    if (installFromDirectory) {
      // on foo chain install from directory.

      //// For GO language and serving just a single user, chaincodeSource is mostly likely the
      //// users GOPATH
      installProposalRequest.setChaincodeSourceLocation(Paths.get(CHAIN_CODE_FILEPATH).toFile());

      //      if (testConfig.isFabricVersionAtOrAfter("1.1")) { // Fabric 1.1 added support for META-INF
      // in the chaincode image.

      // This sets an index on the variable a in the chaincode // see
      // http://hyperledger-fabric.readthedocs.io/en/master/couchdb_as_state_database.html#using-couchdb-from-chaincode
      // The file IndexA.json as part of the META-INF will be packaged with the source to
      // create the index.
      installProposalRequest
          .setChaincodeMetaInfLocation(new File("src/test/fixture/meta-infs/bct4mas"));
      //      }
    } else {
      // On bar chain install from an input stream.

      // For inputstream if indicies are desired the application needs to make sure the META-IaNF
      // is provided in the stream.
      // The SDK does not change anything in the stream.

      if (CHAIN_CODE_LANG.equals(TransactionRequest.Type.GO_LANG)) {

        //        installProposalRequest.setChaincodeInputStream(Util.generateTarGzInputStream(
        //            (Paths.get(TEST_FIXTURES_PATH, CHAIN_CODE_FILEPATH, "src", CHAIN_CODE_PATH).toFile()),
        //            Paths.get("src", CHAIN_CODE_PATH).toString()));
        //      } else {
        //        installProposalRequest.setChaincodeInputStream(Util.generateTarGzInputStream(
        //            (Paths.get(TEST_FIXTURES_PATH, CHAIN_CODE_FILEPATH).toFile()), "src"));
        //      }
      }

      installProposalRequest.setChaincodeVersion(CHAIN_CODE_VERSION);
      installProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);

      log.info("Sending install proposal");

      ////////////////////////////
      // only a client from the same org as the peer can issue an install request
      int numInstallProposal = 0;
      // Set<String> orgs = orgPeers.keySet();
      // for (SampleOrg org : testSampleOrgs) {

      Collection<Peer> peers = channel.getPeers();
      numInstallProposal = numInstallProposal + peers.size();
      responses = client.sendInstallProposal(installProposalRequest, peers);

      for (ProposalResponse response : responses) {
        if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
          log.info("Successful install proposal response Txid: " + response.getTransactionID()
              + " from peer " + response.getPeer().getName());
          successful.add(response);
        } else {
          failed.add(response);
        }
      }

      // }
      log.info(
          "Received " + numInstallProposal + " install proposal responses. Successful+verified: "
              + successful.size() + " . Failed: " + failed.size());

      if (failed.size() > 0) {
        ProposalResponse first = failed.iterator().next();
        log.error(
            "Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
      }

      // Note installing chaincode does not require transaction no need to
      // send to Orderers
    }
  }

  // TODO: Learn How to Instantiate Chaincode from SDK instead that from cli
  public static void instantiateChaincode(HFClient client, Channel channel, ChaincodeID chaincodeID)
      throws InvalidArgumentException {
    ///////////////
    //// Instantiate chaincode.
    InstantiateProposalRequest instantiateProposalRequest =
        client.newInstantiationProposalRequest();
    //    instantiateProposalRequest.setChaincodeEndorsementPolicy("OR ('Org1MSP.member','Org2MSP.member')");
    instantiateProposalRequest.setProposalWaitTime(DEPLOYWAITTIME);
    instantiateProposalRequest.setChaincodeID(chaincodeID);
    instantiateProposalRequest.setChaincodeLanguage(CHAIN_CODE_LANG);
    instantiateProposalRequest.setFcn("init");
    instantiateProposalRequest.setArgs(new String[] {});
    Map<String, byte[]> tm = new HashMap<>();
    tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
    tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
    instantiateProposalRequest.setTransientMap(tm);
  }

  /**
   * Call to enrollAdmin.js (Node SDK) TODO: VOGLIAMO SOLO 1 ADMIN (SE C'È UN ADMIN NON PUOI
   * REGISTRARNE ALTRI) + CRITERIO DISCRIMINATORIO PER REGISTRAZIONE ADMIN
   * 
   * @throws Exception
   */
  public static void enrollAdminInHF() throws Exception {
    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String caUrl = hfJson2Pojo.getCaUrl(); // "http://localhost:7054"

    // TODO: HFCAClient ca = sampleOrg.getCAClient();
    HFCAClient caClient = SdkIntegration.getHfCaClient(caUrl, null);
    UserImplementation admin = SdkIntegration.getAdmin(caClient);

    log.info("CA INFO: " + caClient.info());
    log.info("CA INFO NAME: " + caClient.info().getCAName());


  }

  /**
   * Get new Fabric-CA client
   *
   * @param caUrl The fabric-ca-server endpoint url
   * @param caClientProperties The fabri-ca client properties. Can be null.
   * @return new client instance. never null.
   * @throws Exception
   */
  private static HFCAClient getHfCaClient(String caUrl, Properties caClientProperties)
      throws Exception {
    CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
    HFCAClient caClient = HFCAClient.createNewInstance(caUrl, caClientProperties);
    caClient.setCryptoSuite(cryptoSuite);
    return caClient;
  }

  /**
   * Create new HLF client
   *
   * @return new HLF client instance. Never null.
   * @throws CryptoException
   * @throws InvalidArgumentException
   */
  static HFClient getHfClient() throws Exception {
    // initialize default cryptosuite
    CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
    // setup the client
    HFClient client = HFClient.createNewInstance();
    client.setCryptoSuite(cryptoSuite);
    return client;
  }

  /**
   * Enroll admin into fabric-ca using {@code admin/adminpw} credentials. If AppUser object already
   * exist serialized on fs it will be loaded and new enrollment will not be executed.
   *
   * @param caClient The fabric-ca client
   * @return AppUser instance with userid, affiliation, mspId and enrollment set
   * @throws Exception
   */
  private static UserImplementation getAdmin(HFCAClient caClient) throws Exception {
    // String adminCertName = "admin";
    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String orgName = hfJson2Pojo.getOrgName(); // "org1"
    String mspId = hfJson2Pojo.getMspId(); // "Org1MSP"
    String adminCertName = hfJson2Pojo.getAdminCertName(); // "admin"
    log.info("ORG NAME: " + orgName);
    log.info("MSP ID: " + mspId);
    log.info("ADMIN CERT NAME: " + adminCertName);


    UserImplementation userAdmin = Utils.tryDeserialize(adminCertName);
    if (userAdmin == null) {
      try {

        Enrollment adminEnrollment = caClient.enroll(adminCertName, "adminpw");
        userAdmin = new UserImplementation(adminCertName, orgName, mspId, adminEnrollment);
      } catch (Exception e) {
        log.error(e);
        e.printStackTrace();
      }
      // admin = new AppUser("admin", "org1", "Org1MSP", adminEnrollment);

      Utils.serialize(userAdmin);
    }
    return userAdmin;
  }

  /**
   * Register the user in the HF network, if the user already exist will only get that user
   *
   * @param agentToRegister
   * @throws Exception
   */
  public static User registerOrGetUserInHF(String agentToRegister) throws Exception {

    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String caUrl = hfJson2Pojo.getCaUrl(); // "http://localhost:7054"

    HFCAClient caClient = SdkIntegration.getHfCaClient(caUrl, null);

    // get admin
    UserImplementation userAdmin = SdkIntegration.getAdmin(caClient);
    log.info(userAdmin);

    // register and enroll new user
      UserImplementation user = SdkIntegration.getUser(caClient, userAdmin, agentToRegister);
    log.info(user);

    return user;
  }

  /**
   * Register and enroll user with userId. If UserImplementation object with the name already exist
   * on it will be loaded and registration and enrollment will be skipped.
   *
   * @param caClient The fabric-ca client.
   * @param registrar The registrar to be used.
   * @param userId The user id.
   * @return AppUser instance with userId, affiliation,mspId and enrollment set.
   * @throws Exception
   */
  static public UserImplementation getUser(HFCAClient caClient, UserImplementation registrar,
      String userId) throws Exception {
    HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
    String orgName = hfJson2Pojo.getOrgName(); // "org1"
    String mspId = hfJson2Pojo.getMspId(); // "Org1MSP"

    // String certFolderPath = "bc-mas-app/hfc-key-store/";
    UserImplementation appUser = Utils.tryDeserialize(userId);
    if (appUser == null) {
      RegistrationRequest registrationRequest = new RegistrationRequest(userId, orgName);
      String enrollmentSecret = caClient.register(registrationRequest, registrar);
      Enrollment enrollment = caClient.enroll(userId, enrollmentSecret);
      // appUser = new AppUser(userId, "org1", "Org1MSP", enrollment);
      appUser = new UserImplementation(userId, orgName, mspId, enrollment);

      Utils.serialize(appUser);
    }
    return appUser;
  }

  public static String setChaincodeEventListener(Channel channel, String expectedEventName,
      Vector<ChaincodeEventCapture> chaincodeEvents) throws InvalidArgumentException {

    // chaincode events.
    // lambda expression
    String eventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
        Pattern.compile(Pattern.quote(expectedEventName)), (handle, blockEvent, chaincodeEvent) -> {

          chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

          // Ternary operator

          String eventHubName = blockEvent.getPeer() != null ? blockEvent.getPeer().getName()
              : blockEvent.getEventHub().getName();
          //            Utils.out(
          //              "RECEIVED Chaincode event with handle: %s, chaincode Id: %s, chaincode event name: %s, "
          //                  + "transaction id: %s, event payload: \"%s\", from eventhub: %s",
          //              handle, chaincodeEvent.getChaincodeId(), chaincodeEvent.getEventName(),
          //              chaincodeEvent.getTxId(), new String(chaincodeEvent.getPayload()), es);
          log.info("RECEIVED Chaincode event with handle: " + handle + ", chaincode Id: "
              + chaincodeEvent.getChaincodeId() + ", chaincode event name: " + chaincodeEvent
              .getEventName() + ", " + "transaction id: " + chaincodeEvent.getTxId()
              + ", event payload: \"" + new String(chaincodeEvent.getPayload())
              + "\", from eventhub: " + eventHubName);

        });
    return eventListenerHandle;
  }



    /**
     * wait for the event that confirm the commit in the bct of the transaction, true if committed, false if no event arrived in the timeout
     *
     * @param timeout                      in tenth of a second (pass zero timeout to not having the timer)
     * @param channel
     * @param chaincodeEvents
     * @param chaincodeEventListenerHandle
     * @return
     * @throws InvalidArgumentException
     */
    public static boolean waitForChaincodeEvent(Integer timeout, Channel channel,
        Vector<ChaincodeEventCapture> chaincodeEvents, String chaincodeEventListenerHandle,
        String expectedEventName)
        throws InvalidArgumentException {
      if (timeout < 0) {
        InvalidArgumentException invalidArgumentException = new InvalidArgumentException(
            "Insert a positive Integer as a valid timeout (waiting time for the event), insert 0 to an have infinite timeout");
        log.error(invalidArgumentException);
        throw invalidArgumentException;
      }
        boolean eventDone = false;
        if (chaincodeEventListenerHandle != null) {


            int numberEventsExpected = channel.getEventHubs().size() + channel
                .getPeers(EnumSet.of(Peer.PeerRole.EVENT_SOURCE)).size();
            log.info("numberEventsExpected: " + numberEventsExpected);
            //just make sure we get the notifications
            if (timeout.equals(0)) {
                // get event without timer
                while (chaincodeEvents.size() != numberEventsExpected) {
                    // do nothing
                }
                eventDone = true;
            } else {
                // get event with timer
                for (int i = 0; i < timeout; i++) {
                    if (chaincodeEvents.size() == numberEventsExpected) {
                        eventDone = true;
                        break;
                    } else {
                        try {
                            double j = i;
                            j = j / 10;
                            log.info(
                                "Wait " + expectedEventName + " arrival for: " + j + " second");
                            Thread.sleep(100); // wait for the events for one tenth of second.
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            log.info("chaincodeEvents.size(): " + chaincodeEvents.size());

            // unregister event listener
            channel.unregisterChaincodeEventListener(chaincodeEventListenerHandle);
            int i = 1;
            // arrived event handling
            for (ChaincodeEventCapture chaincodeEventCapture : chaincodeEvents) {
              // pretty print on log
              if (i == 1) {
                log.info(
                    "-------------------------------------------------------------------------");
              }
                log.info("Evento num. " + i);
                log.info("event capture object: " + chaincodeEventCapture.toString());
                log.info("Event Handle: " + chaincodeEventCapture.getHandle());
                log.info("Event TxId: " + chaincodeEventCapture.getChaincodeEvent().getTxId());
                log.info("Event Name: " + chaincodeEventCapture.getChaincodeEvent().getEventName());
                log.info("Event Payload: " + chaincodeEventCapture.getChaincodeEvent()
                    .getPayload()); // byte
                log.info("Event ChaincodeId: " + chaincodeEventCapture.getChaincodeEvent()
                    .getChaincodeId());
                BlockEvent blockEvent = chaincodeEventCapture.getBlockEvent();
                try {
                    log.info("Event Channel: " + blockEvent.getChannelId());
                } catch (InvalidProtocolBufferException e) {
                  log.error(e);
                  e.printStackTrace();
                }
                log.info("Event Hub: " + blockEvent.getEventHub());
                log.info(
                    "-------------------------------------------------------------------------");
                i++;
            }

        } else {
            log.info("chaincodeEvents.isEmpty(): " + chaincodeEvents.isEmpty());
        }
        log.info("eventDone: " + eventDone);
        return eventDone;
    }

    /**
     * setChaincodeEventListener Implementation without lambda
     *
     * @param channel
     * @param expectedEventName
     * @param chaincodeEvents
     * @return
     * @throws InvalidArgumentException
     */
    public static String setChaincodeEventListenerwithoutLambda(Channel channel,
        String expectedEventName, Vector<ChaincodeEventCapture> chaincodeEvents)
        throws InvalidArgumentException {

        ChaincodeEventListener chaincodeEventListener = new ChaincodeEventListener() {

            @Override public void received(String handle, BlockEvent blockEvent,
                ChaincodeEvent chaincodeEvent) {
                chaincodeEvents.add(new ChaincodeEventCapture(handle, blockEvent, chaincodeEvent));

                // Ternary operator
                String es = blockEvent.getPeer() != null ?
                    blockEvent.getPeer().getName() :
                    blockEvent.getEventHub().getName();
                Utils.out(
                    "RECEIVED Chaincode event with handle: %s, chaincode Id: %s, chaincode event name: %s, "
                        + "transaction id: %s, event payload: \"%s\", from eventhub: %s", handle,
                    chaincodeEvent.getChaincodeId(), chaincodeEvent.getEventName(),
                    chaincodeEvent.getTxId(), new String(chaincodeEvent.getPayload()), es);
            }
        };
        // chaincode events.
        String eventListenerHandle = channel.registerChaincodeEventListener(Pattern.compile(".*"),
            Pattern.compile(Pattern.quote(expectedEventName)), chaincodeEventListener);
        return eventListenerHandle;
    }

    private static ArrayList<Peer> createPeerList(HFClient clientHF)
        throws InvalidArgumentException, JsonSyntaxException, JsonIOException,
        FileNotFoundException {
        HFJson2Pojo hfJson2Pojo = StartClass.getHfJsonConfig(StartClass.HF_CONFIG_FILE);
        List<String> configPeerNames = hfJson2Pojo.getPeerNames();
        List<String> configPeerGrpcURLs = hfJson2Pojo.getPeerGrpcURLs();
        ArrayList<Peer> peerList = new ArrayList<>();
        for (int i = 0; i < configPeerNames.size(); i++) {
            peerList.add(clientHF.newPeer(configPeerNames.get(i), configPeerGrpcURLs.get(i)));
        }
        return peerList;
    }


    /**
     * initialize channels from the json file of configuration. For now two channel will be istantiated, servicech to interact with the serviceLedger
     * and transch with the transactionLedger
     *
     * @param bcAgent
     * @param hfJson2Pojo
     * @param bcHfClient
     * @throws InvalidArgumentException
     * @throws TransactionException
     * @throws FileNotFoundException
     * @throws JsonIOException
     * @throws JsonSyntaxException
     */
    public static void initializeChannels(BCAgent bcAgent, HFJson2Pojo hfJson2Pojo,
        HFClient bcHfClient)
        throws InvalidArgumentException, TransactionException, JsonSyntaxException, JsonIOException,
        FileNotFoundException {
        Channel generalChannel;
        for (int i = 0; i < hfJson2Pojo.getChannelNames().size(); i++) {
          // TODO: Call the one that initialize the channel in java
            generalChannel = bcHfClient.newChannel(hfJson2Pojo.getChannelNames().get(i));

            // setup service channel
            if (i == 0) {
                ArrayList<Peer> servicePeerList2 = createPeerList(bcHfClient);
                bcAgent.setHfServiceChannel(generalChannel);
                addPeersToHFChannel(bcAgent.getHfServiceChannel(), servicePeerList2);
                bcAgent.getHfServiceChannel().addOrderer(bcHfClient
                    .newOrderer(hfJson2Pojo.getOrdererNames().get(0),
                        hfJson2Pojo.getOrdererGrpcURLs().get(0)));
                bcAgent.getHfServiceChannel().initialize();
            }

            // setup transaction channel
            if (i == 1) {
                ArrayList<Peer> transactionPeerList2 = createPeerList(bcHfClient);
                bcAgent.setHfTransactionChannel(generalChannel);
                addPeersToHFChannel(bcAgent.getHfTransactionChannel(), transactionPeerList2);
                bcAgent.getHfTransactionChannel().addOrderer(bcHfClient
                    .newOrderer(hfJson2Pojo.getOrdererNames().get(0),
                        hfJson2Pojo.getOrdererGrpcURLs().get(0)));
                bcAgent.getHfTransactionChannel().initialize();
            }
        }
    }

    /**
     * Add all the given peers in the peerList in the given channelHF
     *
     * @param channelHF
     * @param peerList
     * @throws InvalidArgumentException
     */
    private static void addPeersToHFChannel(Channel channelHF, ArrayList<Peer> peerList)
        throws InvalidArgumentException {

        for (int i = 0; i < peerList.size(); i++) {
            // TODO: Aggiungere controllo esistenza del peer
            channelHF.addPeer(peerList.get(i));
        }
    }
}
