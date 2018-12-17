package agents;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BootAgentHouseWorkTest {

  @Test
  public void test() {
    AgentHandler agentHandlerTest = new AgentHandler();
    agentHandlerTest.addAgentTrigger("stocazzo","BCAgent","");
    assertEquals("stocazzo","stocazzo");
  }
}
