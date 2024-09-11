/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSRequiredBoxUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:35 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/02/12 16:52:14  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:46:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 13:59:56   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

// java imports
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentInputMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
//------------------------------------------------------------------------------
/**
 *  Implements a required box around a combo box ui
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class EYSRequiredBoxUI extends BasicComboBoxUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // keystroke constants
    public static KeyStroke KEY_UP    = KeyStroke.getKeyStroke("UP");
    public static KeyStroke KEY_DOWN  = KeyStroke.getKeyStroke("DOWN");

    public static int MOVE_DOWN = 1;
    public static int MOVE_UP = -1;


    //--------------------------------------------------------------------------
    /**
     *  Creates required combo box UI object
     *  @param c the required combo box as a JComponent
     *  @returns the required combo box UI
     */
    //--------------------------------------------------------------------------
    public static ComponentUI createUI(JComponent c)
    {
        return new EYSRequiredBoxUI();
    }

    //--------------------------------------------------------------------------
    /**
     * Repaints the required combo box
     * @param g the combo box's graphics object
     * @param  c the combo box as a JComponent
     */
    //--------------------------------------------------------------------------
    public void update(Graphics g, JComponent c)
    {
        if (c.isOpaque())
        {
            g.setColor(c.getBackground());
            g.fillRect(0, 0, c.getWidth()-EYSBorderFactory.REQUIRED_INSET, c.getHeight());
            g.setColor(c.getParent().getBackground());
            g.fillRect(c.getWidth()-EYSBorderFactory.REQUIRED_INSET, 0, EYSBorderFactory.REQUIRED_INSET, c.getHeight());
        }
        paint(g, c);
    }


                        //======================================//
                        //   Begin Keyboard Action Management   //
                        //======================================//


    //------------------------------------------------------------------------------
    /**
     * Unregister keyboard actions for the up and down arrow keys.
     * This method is called at uninstallUI() time - subclassess should
     * ensure that all of the keyboard actions registered at installUI
     * time are removed here.
     *
     * @see #selectPreviousIndex
     * @see #selectNextIndex
     * @see #installUI
     */
    //------------------------------------------------------------------------------
    protected void uninstallKeyboardActions()
    {
        SwingUtilities.replaceUIActionMap(comboBox, null);
        SwingUtilities.replaceUIInputMap(comboBox, JComponent.WHEN_FOCUSED, null);
    }


    //------------------------------------------------------------------------------
    /**
     * Register keyboard actions for the up and down arrow keys.  The
     * actions just call out to protected methods, subclasses that
     * want to override or extend keyboard behavior should consider
     * just overriding those methods.  This method is called at
     * installUI() time.
     *
     * @see #selectPreviousIndex
     * @see #selectNextIndex
     * @see #installUI
     */
    //------------------------------------------------------------------------------
    protected void installKeyboardActions()
    {
        // normal map for focused list
        InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
        SwingUtilities.replaceUIInputMap(comboBox, JComponent.WHEN_FOCUSED,inputMap);

         // replace the action map
        ActionMap map = getActionMap();
        if (map != null)
        {
            SwingUtilities.replaceUIActionMap(comboBox, map);
        }
    }

    //------------------------------------------------------------------------------
    /**
     *  Creates a custom input map for eys lists.
     *  @param condition
     */
    //------------------------------------------------------------------------------
    InputMap getInputMap(int condition)
    {
        InputMap map = (InputMap)UIManager.get("ComboBox.ancestorInputMap");
        if (map == null)
        {
           map =  new ComponentInputMapUIResource(comboBox);
        }
        // register up and down keys to the input map
        map.put(KEY_UP, "movePrevious");
        map.put(KEY_DOWN, "moveNext");
        return map;
    }

    //------------------------------------------------------------------------------
    /**
     *  Retrieves the list action map from the ui.
     */
    //------------------------------------------------------------------------------
    ActionMap getActionMap()
    {
        ActionMap map = createActionMap();
        return map;
    }

    //------------------------------------------------------------------------------
    /**
     *  Appends the custom eys RequiredBox actions to the standard comboBox actions.
     */
    //------------------------------------------------------------------------------
    ActionMap createActionMap()
    {
        // to avoid stepping on normal JList behavior,
        // we just append the current action map (if it exists)
        ActionMap map = (ActionMap)UIManager.get("ComboBox.actionMap");

        if(map == null)
        {
            map = new ActionMapUIResource();
        }
        // new eys actions
        map.put("movePrevious", new EYSIncrementAction("movePrevious", MOVE_UP));
        map.put("moveNext", new EYSIncrementAction("moveNext", MOVE_DOWN));

        return map;
    }

                    //--------------------------------------//
                    //      Keyboard navigation actions     //
                    //--------------------------------------//


    //------------------------------------------------------------------------------
    /**
     *  Action that increments the highlighted item.
     */
    //------------------------------------------------------------------------------
    private static class EYSIncrementAction extends AbstractAction
    {
        int amount;

        //------------------------------------------------------------------------------
        /**
         *  Default constructor
         */
        //------------------------------------------------------------------------------
        private EYSIncrementAction(String name, int amount)
        {
            super(name);
            this.amount = amount;
        }

        //------------------------------------------------------------------------------
        /**
        *  Returns the next index to select. This is based on the lead
        *  selected index and the <code>amount</code> ivar.
        *  @param list the list to operate against.
        */
        //------------------------------------------------------------------------------
        protected int getNextIndex(JComboBox list)
        {
            int index = ((ValidatingComboBox)list).getSelectedIndex();
            int size = list.getModel().getSize();

            if (index == -1)
            {
                if (size > 0)
                {
                    if (amount == MOVE_DOWN) // going down
                    {
                        index = 0;
                    }
                    else  // going up
                    {
                        index = size - 1;
                    }
                }
            }
            else
            {
                if (amount == MOVE_DOWN)
                {
                   if ( list.isEnabled() )
                   {
               BasicComboBoxUI ui = (BasicComboBoxUI)list.getUI();
               if ( !ui.isPopupVisible(list) )
               {
                  ui.setPopupVisible( list, true );  //open the drop down box
                          index = 0;
                       }
                       else
                       {
                           index++;
                       }
                   }
        }
                else
                {
                   index--;
                }
            }
            return index;
        }

        //------------------------------------------------------------------------------
        /**
        *  Retrieves the current index and marks that item selected on the list
        *  @param e the action event.
        */
        //------------------------------------------------------------------------------
        public void actionPerformed(ActionEvent e)
        {
            ValidatingComboBox list = (ValidatingComboBox)e.getSource();
            int index = getNextIndex(list);

            if (index >= 0 && index < list.getModel().getSize())
            {
                list.setSelectedIndex(index);

            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Retrieves the PVCS revision number.
     *    @return String representation of revision number
     */
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
