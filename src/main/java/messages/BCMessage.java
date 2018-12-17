package messages;

import jade.lang.acl.ACLMessage;

/**
 * Wrapper of the Jade's ACLMessage, only add the two constants for message type definition
 * 
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 *
 */
public class BCMessage extends ACLMessage {

  public static final String STANDARD_MESSAGE = "MSG";
  public static final String SERVICE_EXECUTION = "REQ";

  public BCMessage(int perf) {
    super(perf);
  }

  @Deprecated
  public BCMessage() {
    super();

  }

}
