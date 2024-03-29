package agents;

import behaviours.*;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import model.FeatureView;
import model.StructFeatureRequest;
import model.pojo.InnMindReputation;
import model.pojo.Feature;
import model.pojo.FeatureRelationAgent;
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
    // private BCAgentGui expertAgentGui;
  public BCAgentGui bcAgentGui; // TODO: Temporary Public to permit evaluation, after evaluation
                                // Refactoring as a distinct behaviour we can go bakc to private
                                // expertAgentGui


  private String myName;
  private String myAddress;

  // Link with HF Blockchain
  private User user;
  private HFClient hfClient;
  private Channel hfFeatureChannel;
  private Channel hfTransactionChannel;

  private String configurationType = null;

  public ArrayList<FeatureView> featuresList = new ArrayList<>();

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

  public void updateManageFeaturesTableModelData() {

    // NEW
    bcAgentGui.getManageCompositeAndLeafFeaturesTabPanel().getManageFeaturesPanel()
        .setAgentFeatureList(getFeaturesList());
    bcAgentGui.getManageCompositeAndLeafFeaturesTabPanel().getManageFeaturesPanel()
        .updateTableModelData();
  }

  /**
   * TODO: Da fare query corretta da chaincode
   */
  public void updateSelectLeafFeaturesTableModelData() {

    // NEW
    bcAgentGui.getManageCompositeAndLeafFeaturesTabPanel().getAddCompositeFeaturePanel()
        .getSelectLeafFeaturesPanel().setAgentFeatureList(getFeaturesList());
    bcAgentGui.getManageCompositeAndLeafFeaturesTabPanel().getAddCompositeFeaturePanel()
        .getSelectLeafFeaturesPanel().updateTableModelData();
  }


  public void updateSearchFeaturesResultTableModelData(
      ArrayList<FeatureRelationAgent> searchFeatureResult, ArrayList<model.pojo.Agent> agentList,
      ArrayList<Feature> serviceList, ArrayList<InnMindReputation> innMindReputationList) {
    bcAgentGui.getAskFeatureTabPanel().getSearchFeatureResultPanel()
        .setSearchFeatureResult(searchFeatureResult);
    bcAgentGui.getAskFeatureTabPanel().getSearchFeatureResultPanel().setAgents(agentList);
    bcAgentGui.getAskFeatureTabPanel().getSearchFeatureResultPanel().setFeatures(serviceList);
    bcAgentGui.getAskFeatureTabPanel().getSearchFeatureResultPanel().setInnMindReputations(innMindReputationList);
    bcAgentGui.getAskFeatureTabPanel().getSearchFeatureResultPanel().updateTableModelData();
  }

  public void showDenyExecution(String[] parsedMessage) {
    String serviceId = parsedMessage[0];
    String denialExecuterAgent = parsedMessage[1];
    // expertAgentGui.getMessagesTabPanel().getInBoxMessagesPanel().addMessageInTableModel(message);
    JOptionPane.showMessageDialog(bcAgentGui.getAskFeatureTabPanel(),
        "Demander: " + getLocalName() + "\nyour request has been denied.\nDenial Agent: "
            + denialExecuterAgent + "\nfor the service: " + serviceId + "\n retry the ask",
        "401: Request Denied", JOptionPane.INFORMATION_MESSAGE);
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

     LoadFeatureListFromLedger loadFeatureListFromLedger = new LoadFeatureListFromLedger(this);
     sequentialBehaviour.addSubBehaviour(loadFeatureListFromLedger);

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
  public void addLeafFeatureTrigger(String serviceName, String serviceDescription,
      String serviceCost,
      String serviceTime) {
    String emptyFeatureComposition = "";
    addBehaviour(
        new AddFeature(this, serviceName, serviceDescription, emptyFeatureComposition, serviceCost,
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
  public void addCompositeFeatureTrigger(String serviceName, String serviceDescription,
      String serviceComposition, String serviceCost, String serviceTime) {
    addBehaviour(new AddFeature(this, serviceName, serviceDescription, serviceComposition,
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
  public void deleteFeatureRelationAgentTrigger(String serviceId, String agentId) {
    addBehaviour(new DeleteFeatureRelationAgent(this, serviceId, agentId));

  }

  public void updateFeatureRelationAgentCostTrigger(String serviceId, String agentId,
      String newCostValue) {
    String fieldToUpdate = FeatureRelationAgent.COST;
    addBehaviour(
        new UpdateFeatureRelationAgent(this, serviceId, agentId, newCostValue, fieldToUpdate));

  }

  public void updateFeatureRelationAgentTimeTrigger(String serviceId, String agentId,
      String newTimeValue) {
    String fieldToUpdate = FeatureRelationAgent.TIME;
    addBehaviour(
        new UpdateFeatureRelationAgent(this, serviceId, agentId, newTimeValue, fieldToUpdate));
  }

  public void updateFeatureRelationAgentDescriptionTrigger(String serviceId, String agentId,
      String newTimeValue) {
    String fieldToUpdate = FeatureRelationAgent.DESCRIPTION;
    addBehaviour(
        new UpdateFeatureRelationAgent(this, serviceId, agentId, newTimeValue, fieldToUpdate));
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
  public void getFeaturesListTrigger(String serviceName, String heuristic) {
    addBehaviour(new GetFeaturesList(this, serviceName, heuristic));
  }

  public void getFeaturesListStartupTrigger(String serviceName, String heuristic) {
    addBehaviour(new GetFeaturesListStartup(this, serviceName, heuristic));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param demanderAgentId
   */
  public void executeFeatureTrigger(String serviceId, String demanderAgentId) {
    addBehaviour(new StartupReviewFeature(this, serviceId, demanderAgentId));
  }

  public void acceptReviewFeatureTrigger(String serviceId, String demanderAgentId) {
    addBehaviour(new AcceptReviewFeature(this, serviceId, demanderAgentId));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param demanderAgentId
   */
  public void denyFeatureExecutionTrigger(String serviceId, String demanderAgentId) {
    addBehaviour(new DenyFeatureExecution(this, serviceId, demanderAgentId));
  }

  /**
   * Trigger used from the agent's related GUI to completely separate it from the agent's behaviours
   *
   * @param serviceId
   * @param serviceName
   * @param selectedStructAgent
   */
  public void askSelectedFeatureTrigger(String serviceId, String serviceName,
      StructFeatureRequest selectedStructAgent) {
    addBehaviour(new AskSelectedFeature(this, serviceId, serviceName, selectedStructAgent));
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
   * @return the channelFeature
   */
  public Channel getHfFeatureChannel() {
    return hfFeatureChannel;
  }

  /**
   * @param channelFeature the channelFeature to set
   */
  public void setHfFeatureChannel(Channel channelFeature) {
      hfFeatureChannel = channelFeature;
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
  private ArrayList<FeatureView> getFeaturesList() {
    return featuresList;
  }

  /**
   * @param servToLoad the servToLoad to set
   */
  public void setFeaturesList(ArrayList<FeatureView> servToLoad) {
      featuresList = servToLoad;
  }
}
