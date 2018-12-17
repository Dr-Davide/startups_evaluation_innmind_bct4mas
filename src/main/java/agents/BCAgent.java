package agents;

import behaviours.*;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import model.ServiceView;
import model.StructServiceRequest;
import model.pojo.Reputation;
import model.pojo.Service;
import model.pojo.ServiceRelationAgent;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.User;
import view.BCAgentGui;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Classe che rappresenta l'agente che andrà a comporre la rete di peer della blockchain (BC-agent)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class BCAgent extends Agent {

    private static final long serialVersionUID = 5801630293298208379L;
    // private BCAgentGui bcAgentGui;
  public BCAgentGui bcAgentGui; // TODO: Temporary Public to permit evaluation, after evaluation
                                // Refactoring as a distinct behaviour we can go bakc to private
                                // bcAgentGui


  private String myName;
  private String myAddress;

  // Link with HF Blockchain
  private User user;
  private HFClient hfClient;
  private Channel hfServiceChannel;
  private Channel hfTransactionChannel;

  private String configurationType = null;

  public ArrayList<ServiceView> servicesList = new ArrayList<>();

  ////////////////////////////////////

  @Override
  protected void setup() {

    // TODO: Disaccoppiare (loosely coupled) gui dall'agente
    bcAgentGui = new BCAgentGui(this);
    bcAgentGui.showGui();

    SequentialBehaviour sequentialBehaviour = bootAgentHouseWork();
    // SequentialBehaviour sequentialBehaviour = bootAgentHouseWorkWithConfigInitialization(); //
    // DEBUG
    addBehaviour(sequentialBehaviour);


  }

  public void addMessage(ACLMessage message) {
    try {
      bcAgentGui.getMessagesTabPanel().getInBoxMessagesPanel().addMessageInTableModel(message);
    } catch (UnreadableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void deleteMessage(Integer deletingMessageIndex) {
    try {
      bcAgentGui.getMessagesTabPanel().getInBoxMessagesPanel()
          .deleteMessageInTableModel(deletingMessageIndex);
    } catch (UnreadableException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void updateManageServicesTableModelData() {

    // NEW
    bcAgentGui.getManageCompositeAndLeafServicesTabPanel().getManageServicesPanel()
        .setAgentServiceList(getServicesList());
    bcAgentGui.getManageCompositeAndLeafServicesTabPanel().getManageServicesPanel()
        .updateTableModelData();
  }

  /**
   * TODO: Da fare query corretta da chaincode
   */
  public void updateSelectLeafServicesTableModelData() {

    // NEW
    bcAgentGui.getManageCompositeAndLeafServicesTabPanel().getAddCompositeServicePanel()
        .getSelectLeafServicesPanel().setAgentServiceList(getServicesList());
    bcAgentGui.getManageCompositeAndLeafServicesTabPanel().getAddCompositeServicePanel()
        .getSelectLeafServicesPanel().updateTableModelData();
  }


  public void updateSearchServicesResultTableModelData(
      ArrayList<ServiceRelationAgent> searchServiceResult, ArrayList<model.pojo.Agent> agentList,
      ArrayList<Service> serviceList, ArrayList<Reputation> reputationList) {
    bcAgentGui.getAskServiceTabPanel().getSearchServiceResultPanel()
        .setSearchServiceResult(searchServiceResult);
    bcAgentGui.getAskServiceTabPanel().getSearchServiceResultPanel().setAgents(agentList);
    bcAgentGui.getAskServiceTabPanel().getSearchServiceResultPanel().setServices(serviceList);
    bcAgentGui.getAskServiceTabPanel().getSearchServiceResultPanel().setReputations(reputationList);
    bcAgentGui.getAskServiceTabPanel().getSearchServiceResultPanel().updateTableModelData();
  }

  public void showDenyExecution(String[] parsedMessage) {
    String serviceId = parsedMessage[0];
    String denialExecuterAgent = parsedMessage[1];
    // bcAgentGui.getMessagesTabPanel().getInBoxMessagesPanel().addMessageInTableModel(message);
    JOptionPane.showMessageDialog(bcAgentGui.getAskServiceTabPanel(),
        "Demander: " + getLocalName() + ": Your request has been denied. Denial Agent: "
            + denialExecuterAgent + ", for the service: " + serviceId + ", retry the ask",
        getLocalName() + " Request Denied", JOptionPane.INFORMATION_MESSAGE);
  }


  /**
   * Insieme di Behaviour che l'agente deve eseguire al suo avvio
   *
   * @return
   */
  private SequentialBehaviour bootAgentHouseWork() {
    SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();

    InitBcAgent initBcAgent = new InitBcAgent(this);
    sequentialBehaviour.addSubBehaviour(initBcAgent);

      AskCertificateToCAAgent askCertificateToCAAgent = new AskCertificateToCAAgent(this);
      sequentialBehaviour.addSubBehaviour(askCertificateToCAAgent);

    LoadCertificate loadCertificate = new LoadCertificate(this);
    sequentialBehaviour.addSubBehaviour(loadCertificate);

      LoadAgentInLedger loadAgentInLedger = new LoadAgentInLedger(this);
      sequentialBehaviour.addSubBehaviour(loadAgentInLedger);

     LoadServiceListFromLedger loadServiceListFromLedger = new LoadServiceListFromLedger(this);
     sequentialBehaviour.addSubBehaviour(loadServiceListFromLedger);

     RefreshBcAgentGui refreshBcAgentGui = new RefreshBcAgentGui(this);
     sequentialBehaviour.addSubBehaviour(refreshBcAgentGui);

    ReceiveMessage receiveMessage = new ReceiveMessage(this);
    sequentialBehaviour.addSubBehaviour(receiveMessage);

    return sequentialBehaviour;
  }


    /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void addMessageTrigger(ACLMessage message) {
    addBehaviour(new AddMessage(this, message));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void deleteMessageTrigger(Integer deletingMessageIndex) {
    addBehaviour(new DeleteMessage(this, deletingMessageIndex));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void addLeafServiceTrigger(String serviceName, String serviceDescription,
      String serviceCost,
      String serviceTime) {
    String emptyServiceComposition = "";
    addBehaviour(
        new AddService(this, serviceName, serviceDescription, emptyServiceComposition, serviceCost,
            serviceTime));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void addCompositeServiceTrigger(String serviceName, String serviceDescription,
      String serviceComposition, String serviceCost, String serviceTime) {
    addBehaviour(new AddService(this, serviceName, serviceDescription, serviceComposition,
        serviceCost,
            serviceTime));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void deleteServiceRelationAgentTrigger(String serviceId, String agentId) {
    addBehaviour(new DeleteServiceRelationAgent(this, serviceId, agentId));

  }

  public void updateServiceRelationAgentCostTrigger(String serviceId, String agentId,
      String newCostValue) {
    String fieldToUpdate = ServiceRelationAgent.COST;
    addBehaviour(
        new UpdateServiceRelationAgent(this, serviceId, agentId, newCostValue, fieldToUpdate));

  }

  public void updateServiceRelationAgentTimeTrigger(String serviceId, String agentId,
      String newTimeValue) {
    String fieldToUpdate = ServiceRelationAgent.TIME;
    addBehaviour(
        new UpdateServiceRelationAgent(this, serviceId, agentId, newTimeValue, fieldToUpdate));
  }

  public void updateServiceRelationAgentDescriptionTrigger(String serviceId, String agentId,
      String newTimeValue) {
    String fieldToUpdate = ServiceRelationAgent.DESCRIPTION;
    addBehaviour(
        new UpdateServiceRelationAgent(this, serviceId, agentId, newTimeValue, fieldToUpdate));
  }


  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param serviceDescription
   * @param serviceCost
   * @param serviceTime
   */
  public void showDenyExecutionTrigger(ACLMessage message) {
    addBehaviour(new ShowDenyExecution(this, message));
  }

    /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceName
   * @param heuristic
   */
  public void getServicesListTrigger(String serviceName, String heuristic) {
    addBehaviour(new GetServicesList(this, serviceName, heuristic));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param demanderAgentId
   */
  public void executeServiceTrigger(String serviceId, String demanderAgentId) {
    addBehaviour(new ExecuteService(this, serviceId, demanderAgentId));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param demanderAgentId
   */
  public void denyServiceExecutionTrigger(String serviceId, String demanderAgentId) {
    addBehaviour(new DenyServiceExecution(this, serviceId, demanderAgentId));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param serviceName
   * @param selectedStructAgent
   */
  public void askSelectedServiceTrigger(String serviceId, String serviceName,
      StructServiceRequest selectedStructAgent) {
    addBehaviour(new AskSelectedService(this, serviceId, serviceName, selectedStructAgent));
  }

  /**
   * @return the client
   */
  public HFClient getHfClient() {
    return hfClient;
  }

  /**
   * @param client the client to set
   */
  public void setHfClient(HFClient client) {
      hfClient = client;
  }

  /**
   * @return the myName
   */
  public String getMyName() {
    return myName;
  }

  /**
   * @param myName the myName to set
   */
  public void setMyName(String myName) {
    this.myName = myName;
  }

  /**
   * @return the myAddress
   */
  public String getMyAddress() {
    return myAddress;
  }

  /**
   * @param myAddress the myAddress to set
   */
  public void setMyAddress(String myAddress) {
    this.myAddress = myAddress;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * @return the channelService
   */
  public Channel getHfServiceChannel() {
    return hfServiceChannel;
  }

  /**
   * @param channelService the channelService to set
   */
  public void setHfServiceChannel(Channel channelService) {
      hfServiceChannel = channelService;
  }

  /**
   * @return the channelTrans
   */
  public Channel getHfTransactionChannel() {
    return hfTransactionChannel;
  }

  /**
   * @param channelTrans the channelTrans to set
   */
  public void setHfTransactionChannel(Channel channelTrans) {
      hfTransactionChannel = channelTrans;
  }

  /**
   * @return the configType
   */
  public String getConfigurationType() {
    return configurationType;
  }

  /**
   * @param configType the configType to set
   */
  public void setConfigurationType(String configType) {
      configurationType = configType;
  }

  /**
   * @return the servToLoad
   */
  private ArrayList<ServiceView> getServicesList() {
    return servicesList;
  }

  /**
   * @param servToLoad the servToLoad to set
   */
  public void setServicesList(ArrayList<ServiceView> servToLoad) {
      servicesList = servToLoad;
  }
}
