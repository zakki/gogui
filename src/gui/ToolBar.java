//-----------------------------------------------------------------------------
// $Id$
// $Source$
//-----------------------------------------------------------------------------

package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import game.*;
import utils.Preferences;

//-----------------------------------------------------------------------------

class ToolBar
    extends JToolBar
{
    ToolBar(ActionListener listener, Preferences prefs)
    {
        setRollover(true);
        m_listener = listener;
        m_buttonNew = addButton("filenew.png", "new-game", "New game");
        m_buttonOpen = addButton("fileopen.png", "open", "Load game");
        m_buttonSave = addButton("filesave2.png", "save", "Save game");
        add(new JToolBar.Separator());
        m_buttonBeginning = addButton("beginning.png", "beginning",
                                      "Beginning of game");
        m_buttonBackward10 = addButton("backward10.png",
                                       "backward-10", "Take back 10");
        m_buttonBackward = addButton("back.png", "backward", "Take back");
        m_buttonForward = addButton("forward.png", "forward",
                                    "Replay");
        m_buttonForward10 = addButton("forward10.png", "forward-10",
                                      "Replay 10");
        m_buttonEnd = addButton("end.png", "end", "End of game");
        add(new JToolBar.Separator());
        m_buttonNextVariation =
            addButton("down.png", "next-variation", "Next variation");
        m_buttonPreviousVariation =
            addButton("up.png", "previous-variation", "Previous variation");
        add(new JToolBar.Separator());
        m_buttonPass = addButton("pass.png", "pass", "Pass");
        m_buttonEnter = addButton("next.png", "play", "Computer play");
        m_buttonInterrupt = addButton("stop.png", "interrupt", "Interrupt");
        add(new JToolBar.Separator());
        m_buttonInfo = addButton("info.png", "game-info", "Game information");
    }

    public void setComputerEnabled(boolean enabled)
    {
        m_computerButtonsEnabled = enabled;
        m_buttonEnter.setEnabled(enabled);
    }

    public void updateGameButtons(Node node)
    {
        boolean hasFather = (node.getFather() != null);
        boolean hasChildren = (node.getNumberChildren() > 0);
        boolean hasNextVariation = (node.getNextVariation() != null);
        boolean hasPreviousVariation = (node.getPreviousVariation() != null);
        m_buttonBeginning.setEnabled(hasFather);
        m_buttonBackward.setEnabled(hasFather);
        m_buttonBackward10.setEnabled(hasFather);
        m_buttonForward.setEnabled(hasChildren);
        m_buttonForward10.setEnabled(hasChildren);
        m_buttonEnd.setEnabled(hasChildren);
        m_buttonNextVariation.setEnabled(hasNextVariation);
        m_buttonPreviousVariation.setEnabled(hasPreviousVariation);
    }

    public void enableAll(boolean enable, Node node)
    {
        m_buttonBeginning.setEnabled(enable);
        m_buttonBackward.setEnabled(enable);
        m_buttonBackward10.setEnabled(enable);
        m_buttonEnd.setEnabled(enable);
        m_buttonEnter.setEnabled(enable);
        m_buttonForward.setEnabled(enable);
        m_buttonForward10.setEnabled(enable);
        m_buttonInfo.setEnabled(enable);
        m_buttonInterrupt.setEnabled(false);
        m_buttonNew.setEnabled(enable);
        m_buttonNextVariation.setEnabled(enable);
        m_buttonOpen.setEnabled(enable);
        m_buttonPass.setEnabled(enable);
        m_buttonPreviousVariation.setEnabled(enable);
        m_buttonSave.setEnabled(enable);
        if (enable)
        {
            if (! m_computerButtonsEnabled)
                setComputerEnabled(false);
            updateGameButtons(node);
        }
    }

    public void setCommandInProgress(boolean isInterruptSupported)
    {
        enableAll(false, null);
        if (isInterruptSupported)
            m_buttonInterrupt.setEnabled(true);
    }

    private boolean m_computerButtonsEnabled = true;

    private ActionListener m_listener;

    private JButton m_buttonBeginning;

    private JButton m_buttonBackward;

    private JButton m_buttonBackward10;

    private JButton m_buttonEnd;

    private JButton m_buttonEnter;

    private JButton m_buttonForward;

    private JButton m_buttonForward10;

    private JButton m_buttonInfo;

    private JButton m_buttonInterrupt;

    private JButton m_buttonNew;

    private JButton m_buttonNextVariation;

    private JButton m_buttonOpen;

    private JButton m_buttonPass;

    private JButton m_buttonPreviousVariation;

    private JButton m_buttonSave;

    private JButton addButton(String icon, String command, String toolTip)
    {
        JButton button = new JButton();
        button.setActionCommand(command);
        button.setToolTipText(toolTip);
        button.addActionListener(m_listener);
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL url = classLoader.getResource("images/" + icon);
        if (url != null)
            button.setIcon(new ImageIcon(url, command));
        else
            button.setText(command);
        Insets insets = new Insets(1, 1, 1, 1);
        button.setMargin(insets);
        button.setEnabled(false);
        button.setFocusable(false);
        add(button);
        return button;
    }
}

//-----------------------------------------------------------------------------
