//-----------------------------------------------------------------------------
// $Id$
// $Source$
//-----------------------------------------------------------------------------

package utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

//-----------------------------------------------------------------------------

public class GuiUtils
{
    public static int PAD = 5;

    public static int SMALL_PAD = 2;

    public static Border createEmptyBorder()
    {
        if (m_emptyBorder == null)
            m_emptyBorder = BorderFactory.createEmptyBorder(PAD, PAD,
                                                            PAD, PAD);
        return m_emptyBorder;
    }

    public static Border createSmallEmptyBorder()
    {
        if (m_smallEmptyBorder == null)
            m_smallEmptyBorder =
                BorderFactory.createEmptyBorder(SMALL_PAD, SMALL_PAD,
                                                SMALL_PAD, SMALL_PAD);
        return m_smallEmptyBorder;
    }

    private static Border m_emptyBorder;

    private static Border m_smallEmptyBorder;
}

//-----------------------------------------------------------------------------
