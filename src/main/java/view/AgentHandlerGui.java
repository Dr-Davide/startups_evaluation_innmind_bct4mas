/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in
 * compliance with the FIPA specifications. Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, version 2.1 of
 * the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *****************************************************************/
package view;

import agents.AgentHandler;
import model.RangeQueries;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import view.panel.AddAgentPanel;
import view.panel.ManageAgentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AgentHandlerGui extends JFrame {
  private static final Logger log = Logger.getLogger(AgentHandlerGui.class);


  private static final long serialVersionUID = -4621771106251570530L;
  private AgentHandler agentHandler;
  private AddAgentPanel addAgentPanel = new AddAgentPanel();
  private ManageAgentPanel manageAgentPanel = new ManageAgentPanel();

  /**
   *
   * AgentHandlerGui @param agentHandler
   */
  public AgentHandlerGui(AgentHandler agent) {
    super(agent.getLocalName());

    agentHandler = agent;

    JComponent panel = buildTabbedPane();

    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridLayout(2, 2));
    jPanel.setAlignmentX(CENTER_ALIGNMENT);
    jPanel.setAlignmentY(CENTER_ALIGNMENT);
    jPanel.setBounds(10, 10, 10, 10);
    setPreferredSize(new Dimension(520, 340));

    getContentPane().add(panel, BorderLayout.CENTER);
    pack();
    setVisible(true);
    setTitle("Agent Handler");
  }

  /**
   * Build and return the JTabbedPane (I vari tab della finestra)
   *
   * @return tabbedPane
   */
  private JComponent buildTabbedPane() {

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);

    addAgentPanel.getButtonAddAgent().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        String agentName = addAgentPanel.getPanelAgentName().getTextField().getText().trim();
        // String agentName = addAgentPanel.getComboBoxAgentNames().getSelectedItem().toString();
//        String agentType = addAgentPanel.getComboBoxAgentTypes().getSelectedItem().toString();
        String agentRoleInput = addAgentPanel.getComboBoxAgentTypes().getSelectedItem().toString();
        // TODO: Passare per decidere view
        log.info("agent Role: " + agentRoleInput);
        String expirationCertificate =
            addAgentPanel.getPanelExpirationCertificate().getTextField().getText().trim();

        // TODO: Aggiungere controllo di mettere per primo CAAgent (e controllare che
        // sia solo uno, per ora non ha senso con la CA centralizzata ma serve per poter
        // in futuro gestire CA distribuite

        // TODO: Aggiungere controllo se agente (nome) esiste già
        String agentType;
        switch (agentRoleInput) {
          case InnMindReputation.STARTUP_ROLE :
            agentType = "StartupAgent";
            break;
          case InnMindReputation.EXPERT_ROLE:
            agentType = "ExpertAgent";
            break;
          default: // should be unreachable
            IllegalStateException illegalStateException =
                    new IllegalStateException("Wrong field to update, it's not in the expected ones");
            log.error(illegalStateException);
            throw illegalStateException;
        }
//        String agentType = "BCAgent";
        agentHandler.addAgentTrigger(agentName, agentType, expirationCertificate);
      }
    });
    GridBagLayout gridBagLayout_1 = (GridBagLayout) addAgentPanel.getLayout();
    gridBagLayout_1.columnWidths = new int[] {29, 0, 0, 0, 0, 0, 0, 0};

    tabbedPane.addTab("Add", addAgentPanel);
    tabbedPane.addTab("Manage", manageAgentPanel);
    tabbedPane.setTabPlacement(SwingConstants.TOP);
    tabbedPane.setEnabledAt(1, true);
    GridBagLayout gridBagLayout = (GridBagLayout) manageAgentPanel.getLayout();
    gridBagLayout.columnWidths = new int[] {0, 160, 30, 0, 30, 114, 30};
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0};
    gridBagLayout.rowHeights = new int[] {0, 150, 0, 30, 0};

    return tabbedPane;
  }

  public void showGui() {
    // DIMENSIONS
    pack();
    // POSITION IN THE SCREEN
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int centerX = (int) screenSize.getWidth() / 2;
    int centerY = (int) screenSize.getHeight() / 2;
    setLocation(centerX - getWidth(), screenSize.height - getHeight() / 2);
    super.setVisible(true);
  }

}
