/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/POSJTableHandlerBean.java /main/16 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

//----------------------------------------------------------------------------
/**
    This class extends BaseBeanAdapter. It prevents any JTables that are added
    to the BaseBeanAdapter JPanel from trapping the ESC and F2 Keys. Subclasses can
    disable additional keys by calling the setKeyStrokesToDisable method prior
    to activation.

    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public abstract class POSJTableHandlerBean extends BaseBeanAdapter
{
    protected HashMap   disabledKeyActions = new HashMap(1);
    protected InputMap  inputMap           = null;

    //---------------------------------------------------------------------
    /**
        POSJTableHandlerBean constructor - retrieves the InputMap for JTable
        from the swing ui manager. Adds the F2 and Escape keys to the list of
        keystrokes to disable.
    **/
    //---------------------------------------------------------------------
    public POSJTableHandlerBean()
    {
        super();
        UI_PREFIX = "Table";
        
        // retrieve the input map from the swing ui manager
        inputMap  = (InputMap)UIManager.get("Table.ancestorInputMap");

        //add the Escape and F2 keystrokes to the disabled actions keyset
        //since this is done in the constructor,
        //these keys will be disabled by default for all subclasses of this bean
        disabledKeyActions.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),null);
        disabledKeyActions.put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),null);
    }

    //---------------------------------------------------------------------
    /**
        The UI Framework calls this method before displaying the screen.
        Attempts to disable any keystrokes that are contained in the keyset of
        the disabledKeyActions hash.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        Iterator   iterator  = disabledKeyActions.keySet().iterator();
        KeyStroke  keystroke = null;

        while (iterator.hasNext())
        {
            //get each keystroke that is used as a key in the map
            keystroke = (KeyStroke) iterator.next();

            //save the action from the inputMap and associate it with its keystroke
            disabledKeyActions.put(keystroke, inputMap.get(keystroke));

            //remove the action from the input map
            inputMap.remove(keystroke);
        }

    }

    //---------------------------------------------------------------------
    /**
        The UI Framework calls this method after removing the screen from
        the display. Restores the KeyActions in the inputMap that were
        disabled by the call to activate.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        Iterator   iterator  = disabledKeyActions.keySet().iterator();
        KeyStroke  keystroke = null;
        Object     action    = null;

        while (iterator.hasNext())
        {
            //get each keystroke that has been disabled
            keystroke = (KeyStroke) iterator.next();

            //restore its associated action in the input map
            action = disabledKeyActions.get(keystroke);
            inputMap.put(keystroke, action);
        }

    }

    //---------------------------------------------------------------------
    /**
       Sets the keystrokes to disable.  Should be called in the initialize or
       constructor methods of any subclasses that need to disable key
       actions other than F2 or Escape.

       @param disableKeys - int array containing constants for key codes.
       (KeyEvent.VK_ENTER, etc.)
       @see java.awt.event.KeyEvent;
    */
    //---------------------------------------------------------------------
    protected void disableKeys(int[] disableKeys)
    {
        if (disableKeys != null)
        {
            KeyStroke  keystroke = null;

            //for each keystroke in the array
            for (int i = 0; i < disableKeys.length; i++)
            {
                keystroke = KeyStroke.getKeyStroke(disableKeys[i],0);

                //add it to the list of keystrokes to disable
                //and associate it with a null entry
                disabledKeyActions.put(keystroke,null);
            }
        }

    }
}
