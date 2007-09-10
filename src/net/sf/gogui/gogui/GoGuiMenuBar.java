//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.gogui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import net.sf.gogui.gui.Bookmark;
import net.sf.gogui.gui.Program;
import net.sf.gogui.gui.RecentFileMenu;
import net.sf.gogui.util.Platform;

/** Menu bar for GoGui. */
public class GoGuiMenuBar
    extends JMenuBar
{
    /** Listener to events that are not handled by GoGuiActions. */
    public interface Listener
    {
        void actionGotoBookmark(int i);

        void actionAttachProgram(int i);
    }

    public GoGuiMenuBar(GoGuiActions actions,
                        RecentFileMenu.Listener recentListener,
                        RecentFileMenu.Listener recentGtpListener,
                        GoGuiMenuBar.Listener bookmarkListener)
    {
        m_listener = bookmarkListener;
        add(createMenuFile(actions, recentListener));
        add(createMenuGame(actions));
        add(createMenuProgram(actions));
        add(createMenuGo(actions));
        add(createMenuEdit(actions));
        add(createMenuView(actions));
        m_menuBookmarks = createMenuBookMarks(actions);
        add(m_menuBookmarks);
        add(createMenuTools(actions, recentGtpListener));
        add(createMenuHelp(actions));
        setHeaderStyleSingle(true);
    }

    public void addRecent(File file)
    {
        try
        {
            File canonicalFile = file.getCanonicalFile();
            if (canonicalFile.exists())
                file = canonicalFile;
        }
        catch (IOException e)
        {
        }
        m_recent.add(file);
    }

    public void addRecentGtp(File file)
    {
        try
        {
            File canonicalFile = file.getCanonicalFile();
            if (canonicalFile.exists())
                file = canonicalFile;
        }
        catch (IOException e)
        {
        }
        m_recentGtp.add(file);
    }

    public void setBookmarks(ArrayList bookmarks)
    {
        for (int i = 0; i < m_bookmarkItems.size(); ++i)
            m_menuBookmarks.remove((JMenuItem)m_bookmarkItems.get(i));
        if (m_bookmarksSeparator != null)
        {
            m_menuBookmarks.remove(m_bookmarksSeparator);
            m_bookmarksSeparator = null;
        }
        if (bookmarks.isEmpty())
            return;
        m_bookmarksSeparator = new JSeparator();
        m_menuBookmarks.add(m_bookmarksSeparator);
        for (int i = 0; i < bookmarks.size(); ++i)
        {
            Bookmark bookmark = (Bookmark)bookmarks.get(i);
            JMenuItem item = new JMenuItem(bookmark.m_name);
            final int bookmarkIndex = i;
            item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        m_listener.actionGotoBookmark(bookmarkIndex);
                    }
                });
            if (bookmark.m_file != null)
            {
                StringBuffer toolTip = new StringBuffer(256);
                toolTip.append(bookmark.m_file.toString());
                if (bookmark.m_move > 0)
                {
                    toolTip.append(" (move ");
                    toolTip.append(bookmark.m_move);
                    toolTip.append(')');
                }
                if (! bookmark.m_variation.trim().equals(""))
                {
                    toolTip.append(" (variation ");
                    toolTip.append(bookmark.m_variation);
                    toolTip.append(')');
                }
                item.setToolTipText(toolTip.toString());
            }
            m_menuBookmarks.add(item);
            m_bookmarkItems.add(item);
        }
    }

    public void setPrograms(ArrayList programs)
    {
        m_menuAttach.setEnabled(! programs.isEmpty());
        for (int i = 0; i < m_programItems.size(); ++i)
            m_menuAttach.remove((JMenuItem)m_programItems.get(i));
        if (programs.isEmpty())
            return;
        for (int i = 0; i < programs.size(); ++i)
        {
            Program program = (Program)programs.get(i);
            JMenuItem item = new JMenuItem(program.m_label);
            final int index = i;
            item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        m_listener.actionAttachProgram(index);
                    }
                });
            StringBuffer toolTip = new StringBuffer(128);
            if (program.m_name != null)
                toolTip.append(program.m_name);
            if (program.m_version != null && ! program.m_version.equals("")
                && program.m_version.length() < 40)
            {
                toolTip.append(' ');
                toolTip.append(program.m_version);
            }
            if (program.m_command != null)
            {
                toolTip.append(" (");
                toolTip.append(program.m_command);
                toolTip.append(')');
            }
            item.setToolTipText(toolTip.toString());
            m_menuAttach.add(item);
            m_programItems.add(item);
        }
    }

    /** Is it a single menu bar or does a tool bar exist? */
    public final void setHeaderStyleSingle(boolean isSingle)
    {
        // For com.jgoodies.looks
        putClientProperty("jgoodies.headerStyle",
                          isSingle ? "Single" : "Both");
    }

    public void update(boolean isProgramAttached, boolean isTreeShown,
                       boolean isShellShown)
    {
        if (isProgramAttached)
            m_recentGtp.updateEnabled();
        else
            m_recentGtp.getMenu().setEnabled(false);
        m_recent.updateEnabled();
        m_computerColor.setEnabled(isProgramAttached);
        m_menuViewTree.setEnabled(isTreeShown);
        m_menuViewShell.setEnabled(isShellShown);
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sf.net
    */
    private static final long serialVersionUID = 0L; // SUID

    private final Listener m_listener;

    private final MenuChecked m_menuBookmarks;

    private MenuChecked m_menuAttach;

    private MenuChecked m_menuViewTree;

    private MenuChecked m_menuViewShell;

    private JSeparator m_bookmarksSeparator;

    private RecentFileMenu m_recent;

    private RecentFileMenu m_recentGtp;

    private final ArrayList m_bookmarkItems = new ArrayList();

    private final ArrayList m_programItems = new ArrayList();

    private JMenu m_computerColor;

    private MenuChecked createBoardSizeMenu(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Board Size", KeyEvent.VK_S);
        ButtonGroup group = new ButtonGroup();
        menu.addRadioItem(group, actions.m_actionBoardSize9);
        menu.addRadioItem(group, actions.m_actionBoardSize11);
        menu.addRadioItem(group, actions.m_actionBoardSize13);
        menu.addRadioItem(group, actions.m_actionBoardSize15);
        menu.addRadioItem(group, actions.m_actionBoardSize17);
        menu.addRadioItem(group, actions.m_actionBoardSize19);
        menu.addRadioItem(group, actions.m_actionBoardSizeOther);
        return menu;
    }

    private MenuChecked createClockMenu(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Clock", KeyEvent.VK_K);
        menu.addItem(actions.m_actionClockStart, KeyEvent.VK_S);
        menu.addItem(actions.m_actionClockHalt, KeyEvent.VK_H);
        menu.addItem(actions.m_actionClockResume, KeyEvent.VK_R);
        menu.addItem(actions.m_actionClockRestore, KeyEvent.VK_E);
        return menu;
    }

    private MenuChecked createComputerColorMenu(GoGuiActions actions)
    {
        ButtonGroup group = new ButtonGroup();
        MenuChecked menu = createMenu("Computer Color", KeyEvent.VK_C);
        menu.addRadioItem(group, actions.m_actionComputerBlack, KeyEvent.VK_B);
        menu.addRadioItem(group, actions.m_actionComputerWhite, KeyEvent.VK_W);
        menu.addRadioItem(group, actions.m_actionComputerBoth, KeyEvent.VK_T);
        menu.addRadioItem(group, actions.m_actionComputerNone, KeyEvent.VK_N);
        return menu;
    }

    private MenuChecked createHandicapMenu(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Handicap", KeyEvent.VK_H);
        ButtonGroup group = new ButtonGroup();
        menu.addRadioItem(group, actions.m_actionHandicapNone);
        menu.addRadioItem(group, actions.m_actionHandicap2);
        menu.addRadioItem(group, actions.m_actionHandicap3);
        menu.addRadioItem(group, actions.m_actionHandicap4);
        menu.addRadioItem(group, actions.m_actionHandicap5);
        menu.addRadioItem(group, actions.m_actionHandicap6);
        menu.addRadioItem(group, actions.m_actionHandicap7);
        menu.addRadioItem(group, actions.m_actionHandicap8);
        menu.addRadioItem(group, actions.m_actionHandicap9);
        return menu;
    }

    private MenuChecked createMenu(String name, int mnemonic)
    {
        MenuChecked menu = new MenuChecked(name);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private MenuChecked createMenuBookMarks(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Bookmarks", KeyEvent.VK_B);
        menu.addItem(actions.m_actionAddBookmark, KeyEvent.VK_A);
        menu.addItem(actions.m_actionEditBookmarks, KeyEvent.VK_E);
        return menu;
    }

    private MenuChecked createMenuConfigureShell(GoGuiActions actions)
    {
        m_menuViewShell = new MenuChecked("Shell");
        m_menuViewShell.setMnemonic(KeyEvent.VK_H);
        GoGuiCheckBoxMenuItem itemCompletion =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleCompletion);
        m_menuViewShell.addItem(itemCompletion, KeyEvent.VK_P);
        GoGuiCheckBoxMenuItem itemAutonumber =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleAutoNumber);
        m_menuViewShell.addItem(itemAutonumber, KeyEvent.VK_A);
        GoGuiCheckBoxMenuItem itemTimestamp =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleTimeStamp);
        m_menuViewShell.addItem(itemTimestamp, KeyEvent.VK_T);
        return m_menuViewShell;
    }

    private MenuChecked createMenuConfigureTree(GoGuiActions actions)
    {
        m_menuViewTree = new MenuChecked("Tree");
        m_menuViewTree.setMnemonic(KeyEvent.VK_E);
        MenuChecked menuLabel = createMenu("Labels", KeyEvent.VK_L);
        ButtonGroup group = new ButtonGroup();
        menuLabel.addRadioItem(group, actions.m_actionTreeLabelsNumber,
                               KeyEvent.VK_N);
        menuLabel.addRadioItem(group, actions.m_actionTreeLabelsMove,
                               KeyEvent.VK_M);
        menuLabel.addRadioItem(group, actions.m_actionTreeLabelsNone,
                               KeyEvent.VK_O);
        m_menuViewTree.add(menuLabel);
        MenuChecked menuSize = createMenu("Size", KeyEvent.VK_S);
        group = new ButtonGroup();
        menuSize.addRadioItem(group, actions.m_actionTreeSizeLarge,
                              KeyEvent.VK_L);
        menuSize.addRadioItem(group, actions.m_actionTreeSizeNormal,
                              KeyEvent.VK_N);
        menuSize.addRadioItem(group, actions.m_actionTreeSizeSmall,
                              KeyEvent.VK_S);
        menuSize.addRadioItem(group, actions.m_actionTreeSizeTiny,
                              KeyEvent.VK_T);
        m_menuViewTree.add(menuSize);
        GoGuiCheckBoxMenuItem itemShowSubtreeSizes =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowSubtreeSizes);
        m_menuViewTree.addItem(itemShowSubtreeSizes, KeyEvent.VK_S);
        return m_menuViewTree;
    }

    private MenuChecked createMenuEdit(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Edit", KeyEvent.VK_E);
        menu.addItem(actions.m_actionFind, KeyEvent.VK_F);
        menu.addItem(actions.m_actionFindNext, KeyEvent.VK_N);
        menu.addSeparator();
        menu.addItem(actions.m_actionMakeMainVariation, KeyEvent.VK_M);
        menu.addItem(actions.m_actionDeleteSideVariations, KeyEvent.VK_D);
        menu.addItem(actions.m_actionKeepOnlyPosition, KeyEvent.VK_K);
        menu.addItem(actions.m_actionTruncate, KeyEvent.VK_T);
        menu.addItem(actions.m_actionTruncateChildren, KeyEvent.VK_C);
        menu.addSeparator();
        GoGuiCheckBoxMenuItem itemSetupBlack =
            new GoGuiCheckBoxMenuItem(actions.m_actionSetupBlack);
        menu.addItem(itemSetupBlack, KeyEvent.VK_B);
        GoGuiCheckBoxMenuItem itemSetupWhite =
            new GoGuiCheckBoxMenuItem(actions.m_actionSetupWhite);
        menu.addItem(itemSetupWhite, KeyEvent.VK_W);
        return menu;
    }

    private MenuChecked createMenuExport(GoGuiActions actions)
    {
        MenuChecked menu = new MenuChecked("Export");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.addItem(actions.m_actionExportSgfPosition, KeyEvent.VK_S);
        menu.addItem(actions.m_actionExportLatexMainVariation, KeyEvent.VK_L);
        menu.addItem(actions.m_actionExportLatexPosition, KeyEvent.VK_P);
        menu.addItem(actions.m_actionExportTextPosition, KeyEvent.VK_T);
        menu.addItem(actions.m_actionExportTextPositionToClipboard,
                     KeyEvent.VK_C);
        return menu;
    }

    private MenuChecked createMenuFile(GoGuiActions actions,
                                        RecentFileMenu.Listener listener)
    {
        MenuChecked menu = createMenu("File", KeyEvent.VK_F);
        menu.addItem(actions.m_actionOpen, KeyEvent.VK_O);
        menu.add(createRecentMenu(listener));
        menu.addItem(actions.m_actionSave, KeyEvent.VK_S);
        menu.addItem(actions.m_actionSaveAs, KeyEvent.VK_A);
        menu.addSeparator();
        menu.add(createMenuImport(actions));
        menu.add(createMenuExport(actions));
        menu.addSeparator();
        menu.addItem(actions.m_actionPrint, KeyEvent.VK_P);
        if (! Platform.isMac())
        {
            menu.addSeparator();
            menu.addItem(actions.m_actionQuit, KeyEvent.VK_Q);
        }
        return menu;
    }

    private MenuChecked createMenuGame(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Game", KeyEvent.VK_A);
        menu.addItem(actions.m_actionNewGame, KeyEvent.VK_N);
        menu.addSeparator();
        menu.add(createBoardSizeMenu(actions));
        menu.add(createHandicapMenu(actions));
        menu.addItem(actions.m_actionGameInfo, KeyEvent.VK_G);
        menu.addSeparator();
        m_computerColor = createComputerColorMenu(actions);
        menu.add(m_computerColor);
        menu.addSeparator();
        menu.addItem(actions.m_actionPass, KeyEvent.VK_P);
        menu.add(createClockMenu(actions));
        menu.addItem(actions.m_actionScore, KeyEvent.VK_O);
        return menu;
    }

    private MenuChecked createMenuGo(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Go", KeyEvent.VK_G);
        menu.addItem(actions.m_actionBeginning, KeyEvent.VK_B);
        menu.addItem(actions.m_actionBackwardTen, KeyEvent.VK_W);
        menu.addItem(actions.m_actionBackward, KeyEvent.VK_K);
        menu.addItem(actions.m_actionForward, KeyEvent.VK_F);
        menu.addItem(actions.m_actionForwardTen, KeyEvent.VK_R);
        menu.addItem(actions.m_actionEnd, KeyEvent.VK_E);
        menu.addItem(actions.m_actionGoto, KeyEvent.VK_O);
        menu.addSeparator();
        menu.addItem(actions.m_actionNextVariation, KeyEvent.VK_N);
        menu.addItem(actions.m_actionPreviousVariation, KeyEvent.VK_P);
        menu.addItem(actions.m_actionNextEarlierVariation, KeyEvent.VK_X);
        menu.addItem(actions.m_actionPreviousEarlierVariation, KeyEvent.VK_L);
        menu.addItem(actions.m_actionBackToMainVariation, KeyEvent.VK_M);
        menu.addItem(actions.m_actionGotoVariation, KeyEvent.VK_V);
        return menu;
    }

    private MenuChecked createMenuHelp(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Help", KeyEvent.VK_H);
        menu.addItem(actions.m_actionDocumentation, KeyEvent.VK_G);
        menu.addItem(actions.m_actionAbout, KeyEvent.VK_A);
        return menu;
    }

    private MenuChecked createMenuImport(GoGuiActions actions)
    {
        MenuChecked menu = new MenuChecked("Import");
        menu.setMnemonic(KeyEvent.VK_I);
        menu.addItem(actions.m_actionImportTextPosition, KeyEvent.VK_T);
        menu.addItem(actions.m_actionImportTextPositionFromClipboard,
                     KeyEvent.VK_C);
        return menu;
    }

    private MenuChecked createMenuProgram(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("Program", KeyEvent.VK_P);
        m_menuAttach = createMenu("Attach", KeyEvent.VK_A);
        m_menuAttach.setEnabled(false);
        menu.add(m_menuAttach);
        menu.addSeparator();
        menu.addItem(actions.m_actionDetachProgram, KeyEvent.VK_D);
        menu.addItem(actions.m_actionReattachProgram, KeyEvent.VK_R);
        menu.addSeparator();
        menu.addItem(actions.m_actionPlaySingleMove, KeyEvent.VK_S);
        menu.addItem(actions.m_actionInterrupt, KeyEvent.VK_I);
        menu.addSeparator();
        menu.addItem(actions.m_actionNewProgram, KeyEvent.VK_N);
        menu.addItem(actions.m_actionEditPrograms, KeyEvent.VK_E);
        return menu;
    }

    private MenuChecked createMenuTools(GoGuiActions actions,
                                         RecentFileMenu.Listener listener)
    {
        MenuChecked menu = createMenu("Tools", KeyEvent.VK_T);
        menu.addItem(actions.m_actionShowTree, KeyEvent.VK_R);
        menu.addItem(actions.m_actionShowAnalyzeDialog, KeyEvent.VK_A);
        menu.addItem(actions.m_actionShowShell, KeyEvent.VK_S);
        menu.addSeparator();
        menu.addItem(actions.m_actionShellSave, KeyEvent.VK_L);
        menu.addItem(actions.m_actionShellSaveCommands, KeyEvent.VK_C);
        menu.addItem(actions.m_actionShellSendFile, KeyEvent.VK_F);
        m_recentGtp = new RecentFileMenu("Send Recent",
                                         "net/sf/gogui/recentgtpfiles",
                                         listener);
        m_recentGtp.getMenu().setMnemonic(KeyEvent.VK_R);
        menu.add(m_recentGtp.getMenu());
        return menu;
    }

    private MenuChecked createMenuView(GoGuiActions actions)
    {
        MenuChecked menu = createMenu("View", KeyEvent.VK_V);
        GoGuiCheckBoxMenuItem itemToggleShowToolbar =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowToolbar);
        menu.addItem(itemToggleShowToolbar, KeyEvent.VK_T);
        GoGuiCheckBoxMenuItem itemToggleShowInfoPanel =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowInfoPanel);
        menu.addItem(itemToggleShowInfoPanel, KeyEvent.VK_I);
        menu.addSeparator();
        GoGuiCheckBoxMenuItem itemShowCursor =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowCursor);
        menu.addItem(itemShowCursor, KeyEvent.VK_C);
        GoGuiCheckBoxMenuItem itemShowGrid =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowGrid);
        menu.addItem(itemShowGrid, KeyEvent.VK_G);
        GoGuiCheckBoxMenuItem itemShowLastMove =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowLastMove);
        menu.addItem(itemShowLastMove, KeyEvent.VK_L);
        GoGuiCheckBoxMenuItem itemShowVariations =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleShowVariations);
        menu.addItem(itemShowVariations, KeyEvent.VK_V);
        GoGuiCheckBoxMenuItem itemBeepAfterMove =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleBeepAfterMove);
        menu.addItem(itemBeepAfterMove, KeyEvent.VK_P);
        GoGuiCheckBoxMenuItem itemToggleCommentMonoFont =
            new GoGuiCheckBoxMenuItem(actions.m_actionToggleCommentMonoFont);
        menu.addItem(itemToggleCommentMonoFont, KeyEvent.VK_F);
        menu.addSeparator();
        menu.add(createMenuConfigureTree(actions));
        menu.add(createMenuConfigureShell(actions));
        return menu;
    }

    private JMenu createRecentMenu(RecentFileMenu.Listener listener)
    {
        m_recent = new RecentFileMenu("Open Recent",
                                      "net/sf/gogui/recentfiles",
                                      listener);
        JMenu menu = m_recent.getMenu();
        menu.setMnemonic(KeyEvent.VK_R);
        return menu;
    }
}

/** Menu with assertions for unique mnemonics. */
class MenuChecked
    extends JMenu
{
    public MenuChecked(String text)
    {
        super(text);
    }

    public JMenuItem addItem(JMenuItem item, int mnemonic)
    {
        item.setIcon(null);
        setMnemonic(item, mnemonic);
        item.setToolTipText(null);
        add(item);
        return item;
    }

    public JMenuItem addItem(AbstractAction action, int mnemonic)
    {
        JMenuItem item = new JMenuItem(action);
        addItem(item, mnemonic);
        return item;
    }

    public JMenuItem addRadioItem(ButtonGroup group, AbstractAction action,
                                  int mnemonic)
    {
        JMenuItem item = addRadioItem(group, action);
        setMnemonic(item, mnemonic);
        return item;
    }

    public JMenuItem addRadioItem(ButtonGroup group, AbstractAction action)
    {
        JMenuItem item = new GoGuiRadioButtonMenuItem(action);
        group.add(item);
        item.setIcon(null);
        add(item);
        return item;
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for use with serialver.sf.net
    */
    private static final long serialVersionUID = 0L; // SUID

    private final ArrayList m_mnemonics = new ArrayList();

    private void setMnemonic(JMenuItem item, int mnemonic)
    {
        item.setMnemonic(mnemonic);
        Integer integer = new Integer(mnemonic);
        if (m_mnemonics.contains(integer))
        {
            System.err.println("Warning: duplicate mnemonic item "
                               + item.getText());
            assert false;
        }
        m_mnemonics.add(integer);
    }
}

/** Radio menu item with additional "selected" action property. */
class GoGuiRadioButtonMenuItem
    extends JRadioButtonMenuItem
{
    public GoGuiRadioButtonMenuItem(AbstractAction action)
    {
        super(action);
        action.addPropertyChangeListener(new PropertyChangeListener() {
                public void  propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals("selected"))
                        setSelected(((Boolean)e.getNewValue()).booleanValue());
                }
            });
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sf.net
    */
    private static final long serialVersionUID = 0L; // SUID
}

/** Checkbox item with additional "selected" action property. */
class GoGuiCheckBoxMenuItem
    extends JCheckBoxMenuItem
{
    public GoGuiCheckBoxMenuItem(AbstractAction action)
    {
        super(action);
        action.addPropertyChangeListener(new PropertyChangeListener() {
                public void  propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals("selected"))
                        setSelected(((Boolean)e.getNewValue()).booleanValue());
                }
            });
    }

    /** Serial version to suppress compiler warning.
        Contains a marker comment for serialver.sf.net
    */
    private static final long serialVersionUID = 0L; // SUID
}
