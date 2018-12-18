package model;

import java.util.ArrayList;

/**
 * POJO maps strutta nomeAgente-ListaServizi (offerti dall'agente)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class StructLoadFeature {

  private String agentName;
  private ArrayList<FeatureView> serviceList;

  public void setAgentName(String name) {
    agentName = name;
  }

  public String getAgentName() {
	return agentName;
  }

  public void setFeatureList(ArrayList<FeatureView> serviceList) {
	this.serviceList = serviceList;
  }

  public ArrayList<FeatureView> getFeatureList() {
	return serviceList;
  }
}
