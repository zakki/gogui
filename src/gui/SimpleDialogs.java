//-----------------------------------------------------------------------------
// $Id$
// $Source$
//-----------------------------------------------------------------------------

package gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.util.*;
import sgf.*;
import latex.*;

//-----------------------------------------------------------------------------

public class SimpleDialogs
{
    public static final int FILE_OPEN = 0;

    public static final int FILE_SAVE = 1;

    public static final int FILE_SELECT = 2;

    public static void showError(Component frame, String message)
    {
        JOptionPane.showMessageDialog(frame, message,
                                      m_titlePrefix + "Error",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static void showError(Component frame, String message, Exception e)
    {
        String m = e.getMessage();
        String c = e.getClass().getName();
        if (m == null)
            showError(frame, message + "\n" + e.getClass().getName());
        else
            showError(frame, message + "\n" + m);
    }

    public static void showInfo(Component frame, String message)
    {
        JOptionPane.showMessageDialog(frame, message,
                                      m_titlePrefix + "Info",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    public static File showOpen(Component parent, String title)
    {
        String dir = System.getProperties().getProperty("user.dir");
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setDialogTitle(title);
        chooser.setMultiSelectionEnabled(false);
        int ret = chooser.showOpenDialog(parent);
        if (ret != JFileChooser.APPROVE_OPTION)
            return null;
        return chooser.getSelectedFile();
    }

    public static File showOpenSgf(Component frame)
    {
        return showSgfFileChooser(frame, FILE_OPEN, true, null);
    }

    public static boolean showQuestion(Component frame, String message)
    {
        int r = JOptionPane.showConfirmDialog(frame, message,
                                              m_titlePrefix + "Question",
                                              JOptionPane.YES_NO_OPTION);
        return (r == 0);
    }

    public static File showSaveSgf(Component frame)
    {
        return showSgfFileChooser(frame, FILE_SAVE, true, null);
    }

    public static File showSelectFile(Component frame, String title)
    {
        return showSgfFileChooser(frame, FILE_SELECT, false, title);
    }

    public static void showWarning(Component frame, String message)
    {
        JOptionPane.showMessageDialog(frame, message,
                                      m_titlePrefix + "Warning",
                                      JOptionPane.WARNING_MESSAGE);
    }

    public static void setLastFile(File file)
    {
        m_lastFile = file;
    }

    private static final String m_titlePrefix = "GoGui: ";

    private static File m_lastFile;

    private static File showSgfFileChooser(Component frame, int type,
                                           boolean setSgfFilter, String title)
    {
        if (m_lastFile == null)
        {
            String userDir = System.getProperties().getProperty("user.dir");
            m_lastFile = new File(userDir);
        }
        JFileChooser chooser = new JFileChooser(m_lastFile);
        chooser.setMultiSelectionEnabled(false);
        javax.swing.filechooser.FileFilter sgfFilter = new sgf.Filter();
        chooser.addChoosableFileFilter(sgfFilter);
        if (type == FILE_SAVE)
            chooser.addChoosableFileFilter(new latex.Filter());
        if (setSgfFilter)
            chooser.setFileFilter(sgfFilter);
        else
            chooser.setFileFilter(chooser.getAcceptAllFileFilter());
        int ret;
        switch (type)
        {
        case FILE_SAVE:
            ret = chooser.showSaveDialog(frame);
            break;
        case FILE_OPEN:
            ret = chooser.showOpenDialog(frame);
            break;
        default:
            if (title != null)
                chooser.setDialogTitle(title);
            ret = chooser.showDialog(frame, "Select");
            break;
        }
        if (ret != JFileChooser.APPROVE_OPTION)
            return null;
        File file = chooser.getSelectedFile();
        m_lastFile = file;
        return file;
    }
}

//-----------------------------------------------------------------------------
