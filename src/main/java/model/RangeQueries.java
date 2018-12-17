package model;

import com.google.protobuf.ByteString;
import model.dao.AgentDAO;
import model.dao.ServiceDAO;
import model.pojo.Activity;
import model.pojo.Reputation;
import model.pojo.Service;
import model.pojo.ServiceRelationAgent;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Functions to interact with the Service Ledger (SL) (Are wrapper of the underlying chaincode's
 * invoke functions)
 *
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class RangeQueries extends GeneralLedgerInteraction {
  private static final Logger log = Logger.getLogger(RangeQueries.class);
    private static String chaincodeName = "trustreputationledger";

    public RangeQueries() {
    }

  private static ServiceDAO serviceDAO = new ServiceDAO();
  private static AgentDAO agentDAO = new AgentDAO();

  // FIRST PART: ALL CREATE FUNCTIONS:
    // TODO: Eliminare parte 1 e 2 (USARE DAO) e far rimanere solo le range queries


    /**
   * @deprecated Couple a Service to an Agent (the underlying chaincode function already does the
   *             reference integrity check)
   * 
   * @param clientHF
   * @param userHF
   * @param channel
   * @param serviceId
   * @param agentId
   * @param cost
   * @param time
   * @param agentReputation
   * @return
   * @throws Exception
   */
  @Deprecated
  public static boolean coupleServiceWithAgent(HFClient clientHF, User userHF, Channel channel,
      String serviceId, String agentId, String cost, String time, Float agentReputation)
      throws Exception {

    // TODO: SISTEMARE CHIAMATA AL CHAINCODE
    String chaincodeFunctionName = "CreateServiceAgentRelation";
    // TODO: Gestire creazione ID(incrementale)

    String[] chaincodeArguments =
        new String[] {serviceId, agentId, cost, time, agentReputation.toString()};

      Collection<ProposalResponse> successful = new LinkedList<>();
      Collection<ProposalResponse> failed = new LinkedList<>();

    Collection<ProposalResponse> invokePropResp = GeneralLedgerInteraction.writeBlockchain(clientHF,
        userHF, channel, chaincodeName, chaincodeFunctionName, chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSucces = printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info(agentId + ": successfully received transaction proposal responses.");


    sendTxToOrderer(userHF, channel, successful, allPeerSucces);

    return allPeerSucces;
  }

  /**
   * Couple a Service to an Agent, if the service doesn't already exist it will create the service
   * (and relative indexes) first with the standard value of reputation defined inside the
   * chaincode's function
   * 
   * @param clientHF
   * @param userHF
   * @param channel
   * @param serviceId TODO
   * @param serviceName
   * @param serviceDescription TODO
   * @param agentId
   * @param cost
   * @param time
   * @return
   * @throws Exception
   */
  public static boolean createServiceAndCoupleWithAgentWithStandardValue(HFClient clientHF,
      User userHF, Channel channel, String serviceId, String serviceName, String serviceDescription,
      String agentId, String cost, String time) throws Exception {

    // Chaincode function that initialize reputation with standard value
    // Standard Reputation Value ("6.0") Controlled by the chaincode itself
    String chaincodeFunctionName = "CreateServiceAndServiceAgentRelationWithStandardValue";
    // TODO: Gestire creazione ID(incrementale)

    String[] chaincodeArguments =
        new String[] {serviceId, serviceName, serviceDescription, agentId, cost, time};

      Collection<ProposalResponse> successful = new LinkedList<>();
      Collection<ProposalResponse> failed = new LinkedList<>();

    Collection<ProposalResponse> invokePropResp = GeneralLedgerInteraction.writeBlockchain(clientHF,
        userHF, channel, chaincodeName, chaincodeFunctionName, chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSucces = printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info(agentId + ": successfully received transaction proposal responses.");


    sendTxToOrderer(userHF, channel, successful, allPeerSucces);

    return allPeerSucces;
  }

  /**
   * Couple a Service to an Agent, if the service doesn't already exist it will create the service
   * (and relative indexes) first
   * 
   * @param clientHF
   * @param userHF
   * @param channel
   * @param serviceId
   * @param serviceName
   * @param serviceDescription
   * @param agentId
   * @param cost
   * @param time
   * @param initReputationValue
   * @return
   * @throws Exception
   */
  public static boolean createServiceAndCoupleWithAgent(HFClient clientHF, User userHF,
      Channel channel, String serviceId, String serviceName, String serviceDescription,
      String agentId, String cost, String time, String initReputationValue) throws Exception {


    String chaincodeFunctionName = "CreateServiceAndServiceAgentRelation";
    // TODO: Gestire creazione ID(incrementale)

    String[] chaincodeArguments = new String[] {serviceId, serviceName, serviceDescription, agentId,
        cost, time, initReputationValue};

      Collection<ProposalResponse> successful = new LinkedList<>();
      Collection<ProposalResponse> failed = new LinkedList<>();

    Collection<ProposalResponse> invokePropResp = GeneralLedgerInteraction.writeBlockchain(clientHF,
        userHF, channel, chaincodeName, chaincodeFunctionName, chaincodeArguments);
    // itero ogni risposta dei peer
    boolean allPeerSucces = printWriteProposalResponse(successful, failed, invokePropResp, channel);

    log.info(agentId + ": successfully received transaction proposal responses.");


    sendTxToOrderer(userHF, channel, successful, allPeerSucces);

    return allPeerSucces;
  }

  // SECOND PART: ALL SIMPLE QUERY (GET) FUNCTIONS:

  /**
   * try to call servicemarbles helloWorld function
   * 
   * @param hfClient
   * @param channel
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  public static void callHelloWorld(HFClient hfClient, Channel channel)
      throws ProposalException, InvalidArgumentException {
    String chaincodeFunction = "HelloWorld";

    String emptyString = "";

    String[] chaincodeArguments = new String[] {emptyString};

    Collection<ProposalResponse> proposalResponses =
        queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);
    printProposalResponses(proposalResponses);



    List list = new ArrayList();
    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();

    int i = 0;
    for (ProposalResponse prop : proposalResponses) {
      String payload = null;
      JSONArray jsonarray = null;
      try {
        payload = new String(prop.getChaincodeActionResponsePayload());
        jsonarray = (JSONArray) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }
      Iterator<JSONObject> iterator = jsonarray.iterator();
      JSONObject jsonChildObject;
      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
        while (iterator.hasNext()) {
          jsonChildObject = iterator.next();
          // dal payload del cc prende solo l'idAgent e il cost ("Weight")
          list.add(jsonChildObject.get("hello"));
          firstPeerAnswer = true;
        }
      }
      log.info("response from peer: " + prop.getPeer().getName() + ": " + payload);
      i++;


    }
  }

  /**
   * Query to search the service in the SL given the serviceId
   * 
   * @param hfClient
   * @param channel
   * @param serviceId
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  public static Service getServiceNotFoundError(HFClient hfClient, Channel channel,
      String serviceId) throws ProposalException, InvalidArgumentException {
    String chaincodeFunction = "GetServiceNotFoundError";

    String[] chaincodeArguments = new String[] {serviceId};


    Service servicePojo = new Service();

    log.info("Querying for " + serviceId);
    Collection<ProposalResponse> proposalResponseCollection = null;
    // proposalResponseCollection contiene le risposte dei 3 peer
    proposalResponseCollection =
        queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {

      // Error response (FAILURE) is the chaincode "not found asset in ledger")
      if (isFailureResponse(proposalResponse)) {
        // Empty string first list element (usually the key(id)) is my "not found asset in ledger"
        String emptyString = "";
        servicePojo.setServiceId(emptyString);
        servicePojo.setName(emptyString);
        servicePojo.setDescription(emptyString);
        firstPeerAnswer = true;
        return servicePojo;
      }

      String payload = null;
      JSONObject jsonObject = null;
      try {
        payload = new String(proposalResponse.getChaincodeActionResponsePayload());
        // JSONObject perchè so che arriverà solo un valore (non JSONArray)
        jsonObject = (JSONObject) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }

      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
        servicePojo.setServiceId(jsonObject.get("ServiceId"));
        servicePojo.setName(jsonObject.get("Name"));
        servicePojo.setDescription(jsonObject.get("Description"));
        firstPeerAnswer = true;
      }
      System.out
          .println("response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
      i++;
    }
    return servicePojo;
  }

  /**
   * Query to search the agent in the SL given the agentId
   * 
   * @param hfClient
   * @param channel
   * @param agentId
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  public static model.pojo.Agent getAgentNotFoundError(HFClient hfClient, Channel channel,
      String agentId) throws ProposalException, InvalidArgumentException {
    String chaincodeFunction = "GetAgentNotFoundError";

    String[] chaincodeArguments = new String[] {agentId};


    model.pojo.Agent agentPojo = new model.pojo.Agent();

    Collection<ProposalResponse> proposalResponseCollection = null;
    // proposalResponseCollection contiene le risposte dei 3 peer
    proposalResponseCollection =
        queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {

      // Error response (FAILURE) is the chaincode "not found asset in ledger")
      if (isFailureResponse(proposalResponse)) {
        // Empty string first list element (usually the key(id)) is my "not found asset in ledger"
        String emptyString = "";
        agentPojo.setAgentId(emptyString);
        agentPojo.setName(emptyString);
        agentPojo.setAddress(emptyString);
        firstPeerAnswer = true;
        return agentPojo;
      }

      String payload = null;
      JSONObject jsonObject = null;
      try {
        payload = new String(proposalResponse.getChaincodeActionResponsePayload());
        // JSONObject perchè so che arriverà solo un valore (non JSONArray)
        jsonObject = (JSONObject) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }

      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
        agentPojo.setAgentId(jsonObject.get("AgentId"));
        agentPojo.setName(jsonObject.get("Name"));
        agentPojo.setAddress(jsonObject.get("Address"));
        firstPeerAnswer = true;
      }
      System.out
          .println("response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
      i++;
    }
    return agentPojo;
  }

  /**
   * Query to search the serviceRelationAgent (mapping service-agent record) in the SL given the
   * relationId
   * 
   * @param hfClient
   * @param channel
   * @param relationId
   * @return
   * @throws InvalidArgumentException
   * @throws ProposalException
   */
  public static ServiceRelationAgent getServiceRelationAgent(HFClient hfClient, Channel channel,
      String relationId) throws ProposalException, InvalidArgumentException {

    String chaincodeFunction = "GetServiceRelationAgent";

    String[] chaincodeArguments = new String[] {relationId};

    ServiceRelationAgent serviceRelationAgentArray = new ServiceRelationAgent();

    log.info("Querying for " + relationId);
    Collection<ProposalResponse> proposalResponseCollection = null;
    // proposalResponseCollection contiene le risposte dei 3 peer
    proposalResponseCollection =
        queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {
      // Error response (FAILURE) is the chaincode "not found asset in ledger")
      if (isFailureResponse(proposalResponse)) {
        // Empty string first list element (usually the key(id)) is my "not found asset in ledger"
        String emptyString = "";
        serviceRelationAgentArray.setRelationId(emptyString);
        firstPeerAnswer = true;
        return serviceRelationAgentArray;
      }
      String payload = null;
      JSONObject jsonObject = null;
      try {
        payload = new String(proposalResponse.getChaincodeActionResponsePayload());
        // JSONObject perchè so che arriverà solo un valore (non JSONArray)
        jsonObject = (JSONObject) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }

      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
        serviceRelationAgentArray.setRelationId(jsonObject.get("RelationId"));
        serviceRelationAgentArray.setServiceId(jsonObject.get("ServiceId"));
        serviceRelationAgentArray.setAgentId(jsonObject.get("AgentId"));
        serviceRelationAgentArray.setCost(jsonObject.get("Cost"));
        serviceRelationAgentArray.setTime(jsonObject.get("Time"));
        firstPeerAnswer = true;
      }
      System.out
          .println("response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
      i++;
    }
    return serviceRelationAgentArray;
  }

  // THIRD PART: ALL RANGE QUERY FUNCTIONS:

  /**
   * Query to search agents that offer a certain service (serviceId) and return a list of
   * service-agent mappings (relationId, serviceId, agentId, cost, time)
   * 
   * @param hfClient
   * @param channel
   * @param serviceId
   * @return
   */
  public static ArrayList<ServiceRelationAgent> getAgentsByService(HFClient hfClient,
      Channel channel, String serviceId) {

    String chaincodeFunction = "GetAgentsByService";

    String[] chaincodeArguments = new String[] {serviceId};


      ArrayList<ServiceRelationAgent> serviceRelationAgentlist = new ArrayList<>();

    log.info("Querying for " + serviceId);
    Collection<ProposalResponse> proposalResponseCollection = null;
    try {
      // proposalResponseCollection contiene le risposte dei 3 peer
      proposalResponseCollection =
          queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);
    } catch (Exception e) {
      e.printStackTrace();
    }

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {
      // Error response (FAILURE) is the chaincode "not found asset in ledger")
      if (isFailureResponse(proposalResponse)) {
        // Empty string first list element (usually the key(id)) is my "not found asset in ledger"
        String emptyString = "";
        ServiceRelationAgent singleResult = new ServiceRelationAgent();
        singleResult.setAgentId(emptyString); // RelationId
        singleResult.setServiceId(emptyString); // ServiceId
        singleResult.setAgentId(emptyString); // AgentId
        singleResult.setCost(emptyString); // Cost
        singleResult.setTime(emptyString); // Time
        firstPeerAnswer = true;
        return serviceRelationAgentlist;
      }
      String payload = null;
      JSONArray jsonarray = null;
      try {
        payload = new String(proposalResponse.getChaincodeActionResponsePayload());
        jsonarray = (JSONArray) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }
      Iterator<JSONObject> iterator = jsonarray.iterator();
      JSONObject jsonChildObject;

      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
          while (iterator.hasNext()) {
              jsonChildObject = iterator.next();

              ServiceRelationAgent singleResult = new ServiceRelationAgent();
              singleResult.setRelationId(jsonChildObject.get("RelationId"));
              singleResult.setServiceId(jsonChildObject.get("ServiceId"));
              singleResult.setAgentId(jsonChildObject.get("AgentId"));
              singleResult.setCost(jsonChildObject.get("Cost"));
              singleResult.setTime(jsonChildObject.get("Time"));

              serviceRelationAgentlist.add(singleResult);
          }
          firstPeerAnswer = true;
      }
      System.out
          .println("response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
      i++;
    }
    return serviceRelationAgentlist;
  }

  /**
   * Query to search services offered by a certain agent (agentId) and return a list of
   * service-agent mappings (relationId, serviceId, agentId, cost, time, agentReputation)
   * 
   * @param hfClient
   * @param channel
   * @param agentId
   * @return
   */
  public static ArrayList<ServiceRelationAgent> getServicesByAgent(HFClient hfClient,
      Channel channel, String agentId) {

    String chaincodeFunction = "GetServicesByAgent";

    String[] chaincodeArguments = new String[] {agentId};


      ArrayList<ServiceRelationAgent> serviceRelationAgentlist = new ArrayList<>();

    Collection<ProposalResponse> proposalResponseCollection = null;
    try {
      // proposalResponseCollection contiene le risposte dei 3 peer
      // se agente non ha servizi il chaincode ritorna errore (500) che diventa
      // proposalResponseCollection = [], quindi anche se male, funziona il codice
      proposalResponseCollection =
          queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);
      log.info(proposalResponseCollection);
    } catch (Exception e) {
      e.printStackTrace();
    }

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
      //    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {
        //        if (proposalResponse.isVerified()
        //            && proposalResponse.getStatus() == ChaincodeResponse.Status.SUCCESS) {
      // Error response (FAILURE) is the chaincode "not found asset in ledger")
      if (isFailureResponse(proposalResponse)) {

        return serviceRelationAgentlist;
      }
      String payload = null;
      JSONArray jsonarray = null;
      try {
        payload = new String(proposalResponse.getChaincodeActionResponsePayload());
        jsonarray = (JSONArray) parser.parse(payload);
      } catch (Exception e) {
        e.printStackTrace();
      }
      Iterator<JSONObject> iterator = jsonarray.iterator();
      JSONObject jsonChildObject;

      // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
      // TODO: Aggiungere controllo uguaglianza risposte peer?
      if (!firstPeerAnswer) {
          while (iterator.hasNext()) {
              jsonChildObject = iterator.next();
              ServiceRelationAgent singleResult = new ServiceRelationAgent();

              singleResult.setRelationId(jsonChildObject.get("RelationId"));
              singleResult.setServiceId(jsonChildObject.get("ServiceId"));
              singleResult.setAgentId(jsonChildObject.get("AgentId"));
              singleResult.setCost(jsonChildObject.get("Cost"));
              singleResult.setTime(jsonChildObject.get("Time"));

              serviceRelationAgentlist.add(singleResult);
          }
          firstPeerAnswer = true;
      }
      System.out
          .println("response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
        //      i++;
    }

      return serviceRelationAgentlist;
  }


  public static ArrayList<Service> getServicesByServiceName(HFClient hfClient, Channel channel,
      String serviceName) {

    String chaincodeFunction = "GetServicesByName";

    String[] chaincodeArguments = new String[] {serviceName};

    ArrayList<Service> servicesQueryResultList = new ArrayList<>();

    Collection<ProposalResponse> proposalResponseCollection = null;
    try {
      // proposalResponseCollection contiene le risposte dei 3 peer
      // se agente non ha servizi il chaincode ritorna errore (500) che diventa
      // proposalResponseCollection = [], quindi anche se male, funziona il codice
      proposalResponseCollection =
          queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction, chaincodeArguments);
      log.info(proposalResponseCollection);
    } catch (Exception e) {
      e.printStackTrace();
    }

    boolean firstPeerAnswer = false;
    JSONParser parser = new JSONParser();
    //    int i = 0;
    // prendo ognuna (itero) delle risposte dai peer
    for (ProposalResponse proposalResponse : proposalResponseCollection) {

      if (proposalResponse.isVerified()
          && proposalResponse.getStatus() == ChaincodeResponse.Status.SUCCESS) {
        ByteString payload = proposalResponse.getProposalResponse().getResponse().getPayload();
        try (JsonReader jsonReader = Json
            .createReader(new ByteArrayInputStream(payload.toByteArray()))) {
          // parse response
          log.info("PAYLOAD IS EMPTY: " + payload.isEmpty());
          log.info("PAYLOAD SIZE: " + payload.size());
          String payloadString = new String(proposalResponse.getChaincodeActionResponsePayload());
          log.info("PAYLOAD STRING: " + payloadString);
          if (payloadString.equals("null")) {
            log.info("NULL PAYLOAD");
          } else {
            JsonArray jsonArrayGetNameResult = jsonReader.readArray();
            //              JsonObject jsonObject = jsonReader.readObject();
            log.info("BY NAME SERVICES JSON ARRAY: " + jsonArrayGetNameResult);
            log.info("BY NAME SERVICES SIZE: " + jsonArrayGetNameResult.size());

            int i = 0;
            if (!firstPeerAnswer) {
              while (i < jsonArrayGetNameResult.size()) {
                JsonObject jsonChildObject = jsonArrayGetNameResult.getJsonObject(i);
                log.info("JSON CHILD OBJECT: " + jsonChildObject);
                Service singleResult = new Service();

                singleResult.setServiceId(jsonChildObject.getString("ServiceId"));
                singleResult.setName(jsonChildObject.getString("Name"));
                singleResult.setDescription(jsonChildObject.getString("Description"));
                singleResult.setServiceComposition(Utils.parseServiceComposition(jsonChildObject));

                servicesQueryResultList.add(singleResult);

                i++;
              }
              firstPeerAnswer = true;
            }
          }
          log.info(
              "reputation history from peer: " + proposalResponse.getPeer().getName() + ": "
                  + payloadString);
        } catch (InvalidArgumentException e) {
          e.printStackTrace();
        }
      } else {
        log.error("response failed. status: " + proposalResponse.getStatus().getStatus());
      }
    }

    return servicesQueryResultList;
  }
    /**
     * Query to search activities with the related demander-executer-timestamp
     *
     * @param hfClient
     * @param channel
     * @param demanderAgentId
     * @param executerAgentId
     * @param timestamp
     * @return
     */
    public static ArrayList<Activity> getActivitiesByDemanderExecuterTimestamp(HFClient hfClient,
        Channel channel, String demanderAgentId, String executerAgentId, String timestamp) {
        String chaincodeFunction = "GetActivitiesByDemanderExecuterTimestamp";

        String[] chaincodeArguments = new String[] {demanderAgentId, executerAgentId, timestamp};


        ArrayList<Activity> activitieslist = new ArrayList<>();

        Collection<ProposalResponse> proposalResponseCollection = null;
        try {
            // proposalResponseCollection contiene le risposte dei 3 peer
            proposalResponseCollection =
                queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction,
                    chaincodeArguments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean firstPeerAnswer = false;
        JSONParser parser = new JSONParser();
        int i = 0;
        // prendo ognuna (itero) delle risposte dai peer
        for (ProposalResponse proposalResponse : proposalResponseCollection) {
            // Error response (FAILURE) is the chaincode "not found asset in ledger")
            if (isFailureResponse(proposalResponse)) {
                // Empty string first list element (usually the key(id)) is my "not found asset in ledger"
                String emptyString = "";
                Activity singleResult = new Activity();
                singleResult.setEvaluationId(emptyString); // EvaluationId
                singleResult.setWriterAgentId(emptyString); // WriterAgentId
                singleResult.setDemanderAgentId(emptyString); // DemanderAgentId
                singleResult.setExecuterAgentId(emptyString); // ExecuterAgentId
                singleResult.setExecutedServiceId(emptyString); // ExecutedServiceId
                singleResult.setExecutedServiceTxId(emptyString); // ExecutedServiceTxId (NULL)
                singleResult.setExecutedServiceTimestamp(emptyString); // ExecutedServiceTimestamp
                singleResult.setValue(emptyString); // Value
                activitieslist.add(singleResult);
                firstPeerAnswer = true;
                return activitieslist;
            }
            String payload = null;
            JSONArray jsonarray = null;
            try {
                payload = new String(proposalResponse.getChaincodeActionResponsePayload());

                jsonarray = (JSONArray) parser.parse(payload); // TODO: Gestire Payload Null
            } catch (Exception e) {
                e.printStackTrace();
            }
            Iterator<JSONObject> iterator = jsonarray.iterator();
            JSONObject jsonChildObject;

            // aggiunge alla lista solo la prima risposta (suppone che siano tutte uguali)
            // TODO: Aggiungere controllo uguaglianza risposte peer?
            if (!firstPeerAnswer) {
                while (iterator.hasNext()) {
                    jsonChildObject = iterator.next();
                    Activity singleResult = new Activity();
                    singleResult.setEvaluationId(jsonChildObject.get("EvaluationId"));
                    singleResult.setWriterAgentId(jsonChildObject.get("WriterAgentId"));
                    singleResult.setDemanderAgentId(jsonChildObject.get("DemanderAgentId"));
                    singleResult.setExecuterAgentId(jsonChildObject.get("ExecuterAgentId"));
                    singleResult.setExecutedServiceId(jsonChildObject.get("ExecutedServiceId"));
                    singleResult.setExecutedServiceTxId(jsonChildObject.get("ExecutedServiceTxid"));
                    singleResult.setExecutedServiceTimestamp(
                        jsonChildObject.get("ExecutedServiceTimestamp"));
                    singleResult.setValue(jsonChildObject.get("Value"));
                    activitieslist.add(singleResult);
                    firstPeerAnswer = true;
                }
            }
          log.info(
                "response from peer: " + proposalResponse.getPeer().getName() + ": " + payload);
            i++;
        }
        return activitieslist;
    }


    /**
     * Query to search activities with the related demander-executer-timestamp return empty array list ([]) with size = 0 in case of not existing reputationId history
     *
     *
     * @param hfClient
     * @param channel
     * @param reputationId
     * @return
     * @throws ParseException
     */
    public static ArrayList<Reputation> getReputationHistory(HFClient hfClient, Channel channel,
        String reputationId) {

        String chaincodeFunction = "GetReputationHistory";

        String[] chaincodeArguments = new String[] {reputationId};


        // IF WORKS, size of reputationsList will be <= 1.
        ArrayList<Reputation> reputationHistoryList = new ArrayList<>();

        Collection<ProposalResponse> proposalResponseCollection = null;
        try {
            // proposalResponseCollection contiene le risposte dei 3 peer
            proposalResponseCollection =
                queryBlockChain(hfClient, channel, chaincodeName, chaincodeFunction,
                    chaincodeArguments);
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean firstPeerAnswer = false;
      //        JSONParser parser = new JSONParser();
        // prendo ognuna (itero) delle risposte dai peer
      for (ProposalResponse proposalResponse : proposalResponseCollection) {

        if (proposalResponse.isVerified()
            && proposalResponse.getStatus() == ChaincodeResponse.Status.SUCCESS) {
          ByteString payload = proposalResponse.getProposalResponse().getResponse().getPayload();
          try (JsonReader jsonReader = Json
              .createReader(new ByteArrayInputStream(payload.toByteArray()))) {
            // parse response
            log.info("PAYLOAD IS EMPTY: " + payload.isEmpty());
            log.info("PAYLOAD SIZE: " + payload.size());
            String payloadString = new String(proposalResponse.getChaincodeActionResponsePayload());
            log.info("PAYLOAD STRING: " + payloadString);
            if (payloadString.equals("null")) {
              log.info("NULL PAYLOAD");
            } else {
              JsonArray jsonArrayReputationHistory = jsonReader.readArray();
              //              JsonObject jsonObject = jsonReader.readObject();
              log.info("REPUTATION HISTORY JSON ARRAY: " + jsonArrayReputationHistory);
              log.info("REPUTATION HISTORY SIZE: " + jsonArrayReputationHistory.size());

              int i = 0;
              if (!firstPeerAnswer) {
                while (i < jsonArrayReputationHistory.size()) {
                  JsonObject jsonChildObject = jsonArrayReputationHistory.getJsonObject(i);
                  log.info("JSON CHILD OBJECT: " + jsonChildObject);
                    Reputation singleResult = new Reputation();

                  singleResult.setReputationId(jsonChildObject.getString("ReputationId"));
                  singleResult.setAgentId(jsonChildObject.getString("AgentId"));
                  singleResult.setServiceId(jsonChildObject.getString("ServiceId"));
                  singleResult.setAgentRole(jsonChildObject.getString("AgentRole"));
                  singleResult.setValue(jsonChildObject.getString("Value"));

                    reputationHistoryList.add(singleResult);

                  i++;
                }
                firstPeerAnswer = true;
                }
            }
            log.info(
                "reputation history from peer: " + proposalResponse.getPeer().getName() + ": "
                    + payloadString);
          } catch (InvalidArgumentException e) {
            e.printStackTrace();
          }
        } else {
          log.error("response failed. status: " + proposalResponse.getStatus().getStatus());
        }
        }
      log.info("REPUTATION HISTORY LIST VALUE: " + reputationHistoryList);
        return reputationHistoryList;
    }
}
