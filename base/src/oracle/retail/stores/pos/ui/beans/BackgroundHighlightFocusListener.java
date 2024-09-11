/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header:
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rrkohl 06/23/11 - removing synchronized block and making listener final
 *    rrkohl 06/13/11 - field Highlighting CR
 *    rrkohl 06/13/11 - Field highlighting CR
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.UIManager;

/**
 * Highlights the field in POS screens whenever the focus comes to that and
 * reverts to non highlighted field when focus is lost from that field.
 */
public class BackgroundHighlightFocusListener implements FocusListener
{
  private static final BackgroundHighlightFocusListener listener = new BackgroundHighlightFocusListener();

  private Color focusBackground = null;

  private Color nonFocusBackground = null;

  private BackgroundHighlightFocusListener()
  {
    focusBackground = UIManager.getColor("focusBackground");
    nonFocusBackground = UIManager.getColor("nonFocusBackground");
  }

  /**
   * Called when focus is present is the field and field background is
   * highlighted.
   */
  public void focusGained(FocusEvent e)
  {
    e.getComponent().setBackground(focusBackground);
  }

  /**
   * Called when focus is lost from the field and background color is reset
   */
  public void focusLost(FocusEvent e)
  {
    e.getComponent().setBackground(nonFocusBackground);
  }

  /**
   * returns the BackgroundHighlightFocusListener object.
   */
  public static BackgroundHighlightFocusListener getFocusListner()
  {
      return listener;
  }
}
