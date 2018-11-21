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

import agents.CAAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Class to create JADE user interface
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class CAAgentGui extends JFrame {

  private static final long serialVersionUID = -7049094175705454768L;
  private CAAgent myAgent;

  public CAAgentGui(CAAgent a) {
    super(a.getLocalName());

    myAgent = a;

    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    setPreferredSize(new Dimension(200, 150));
    setTitle("CAAgent - Welcome agent " + a.getLocalName().toString() + "!");


    getContentPane().add(jPanel, BorderLayout.CENTER);

    // JButton addButton = new JButton("Add");
    // addButton.addActionListener(new ActionListener() {
    //
    // @Override
    // public void actionPerformed(ActionEvent ev) {
    // try {
    // myAgent.addBehaviour(new CAAgentCyclicBehaviour(myAgent));
    // } catch (Exception e) {
    // JOptionPane.showMessageDialog(CAAgentGui.this,
    // "Failed Adding behaviour " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    // }
    // }
    // });
    // jPanel = new JPanel();
    // jPanel.add(addButton);
    getContentPane().add(jPanel, BorderLayout.SOUTH);
    JLabel label = new JLabel("CA Agent GUI");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    getContentPane().add(label, BorderLayout.CENTER);

    // Make the agent terminate when the user closes
    // the GUI using the button on the upper right corner
    addWindowListener(new WindowAdapter() {

      @Override
      public void windowClosing(WindowEvent e) {
        myAgent.doDelete();
      }
    });

    setResizable(false);
  }

  public void showGui() {
    // DIMENSIONS
    pack();
    // POSITION ON THE SCREEN
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int centerX = (int) screenSize.getWidth() / 2;
    int centerY = (int) screenSize.getHeight() / 2;
    // setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
    setLocation(centerX - getWidth() / 2, 0);
    super.setVisible(true);
  }
}
