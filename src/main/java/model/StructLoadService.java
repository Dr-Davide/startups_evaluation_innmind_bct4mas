package model;

import java.util.ArrayList;

/**
 * POJO maps strutta nomeAgente-ListaServizi (offerti dall'agente)
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class StructLoadService {

  private String agentName;
  private ArrayList<ServiceView> serviceList;

  public void setAgentName(String name) {
    agentName = name;
  }

  public String getAgentName() {
	return agentName;
  }

  public void setServiceList(ArrayList<ServiceView> serviceList) {
	this.serviceList = serviceList;
  }

  public ArrayList<ServiceView> getServiceList() {
	return serviceList;
  }
}
