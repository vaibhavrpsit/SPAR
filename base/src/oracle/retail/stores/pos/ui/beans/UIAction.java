/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/UIAction.java /main/19 2012/10/22 16:40:03 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    cgreene   08/31/11 - ensure event is sent off to other action listeners
 *                         on button
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/05/10 - implement parameter enabled bean spec
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - added getSource method to allow listeners to
 *                         determine original event's source
 *    cgreene   04/15/09 - add better toString for debugging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:29 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:20 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   06 Jun 2002 14:35:30   vxs
 * Removed unnecessary String instantiation within setButtonLabel()
 *
 *    Rev 1.0   Apr 29 2002 14:56:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:42   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 23 2002 15:04:18   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.behavior.DefaultMailAction;

/**
 * This is the class action that maintains the association between the current
 * bus and all the buttons in the JFC UI.
 *
 * @version $Revision: /main/19 $
 */
public class UIAction extends AbstractAction
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3350095717398205367L;

    /** Revision number */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** Default button name */
    public static final String DEFAULT_BUTTON_NAME = "ButtonPressed";
    /** Static for KEYSTROKE constant */
    public static final String KEYSTROKE="KEYSTROKE";
    /** Button text associated with empty buttons  */
    protected static final String NULL_BUTTON = "NullButton";

    /** Listener to mail letter */
    protected ActionListener defaultListener = null;
    /** the current listener */
    protected ActionListener listener;

    /** NOTE this is duplicate storage, but is being used for possible
    performance improvement when events are fired */
    protected String actionName = null;
    /** Holds key label for rebuilding the HTML Button */
    protected String keyName = null;
    /** Holds optional parameter name that might make this button invisible. */
    protected String parameterName = null;
    /** Holds key label for rebuilding the HTML Button */
    protected String buttonName = null;
    /** Holds key label tag for rebuilding the HTML Button */
    protected String buttonNameTag = null;
    /** Holds button number. When there are an unknown number of Buttons
        built on the fly this number can be used to identify which
        button was pressed. */
    protected int buttonNumber = 0;
    /** If true, this action should trigger a popup menu. */
    protected boolean menu;
    /** Reference to source of {@link ActionEvent} if listener needs it. */
    protected transient Object source;

    /**
     * Constructor
     *
     * @param actionName action name
     * @param keyName key name
     * @param buttonName button name
     * @param buttonTag button name tag
     * @param icon Icon object to be used for the button
     * @param isEnabled flag indicating button is enabled
     * @param keyEvent key event to be handled
     * @param actionListener action listener
     */
    public UIAction(String actionName,
                    String keyName,
                    String buttonName,
                    String buttonTag,
                    Icon icon,
                    boolean isEnabled,
                    int keyEvent,
                    ActionListener actListener)
    {
        // Set string and boolean attributes.
        setActionName(actionName);
        buttonNameTag = buttonTag;
        setButtonLabel(keyName, buttonName, isEnabled);

        // Put the button name and Icon in the hashtable
        if(icon!=null)
        {
            putValue(NAME, buttonName);
            putValue(SMALL_ICON,icon);
        }

        // Set up the keystroke
        putValue(KEYSTROKE, keyName);

        // If an action listener has been passed into the constuctor
        // it is the default.
        if(actListener != null)
        {
            listener        = actListener;
            defaultListener = actListener;
        }
        else
        {
            // Otherwise the mail letter listener is the default
            defaultListener = new DefaultMailAction();
            listener        = defaultListener;
        }
    }

    /**
     * Sets the Action Listener
     *
     * @param l the action listener
     */
    public void setActionListener(ActionListener l)
    {
        listener = l;
    }

    /**
     * Resets the action listener to the default listener
     */
    public void resetActionListener()
    {
        listener = defaultListener;
    }

    /**
     * Tests the this object for equality to another object
     *
     * @param o the object to test
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof UIAction))
        {
            return false;
        }

        UIAction a = (UIAction)o;
        return buttonName.equals(a.getButtonName());
    }

    /**
     * Called when the button associated with this action is pressed.
     *
     * @param evt an action event
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        ActionEvent newEvt = new ActionEvent(this, evt.getID(), getActionName(), evt.getModifiers());
        source = evt.getSource();
        if (source instanceof AbstractButton)
        {
            // buttons may have other listeners attached via <CONNECTION> configurations.
            // Notify those other listeners.
            for (ActionListener l : ((AbstractButton)source).getActionListeners())
            {
                if (l != this)
                {
                    l.actionPerformed(newEvt);
                }
            }
        }

        // now forward on the event to the listener that this action has.
        listener.actionPerformed(newEvt);
    }

    /**
     * Set the key name.
     *
     * @param value contains the key name
     */
    public void setKeyName(String value)
    {
        setButtonLabel(value, buttonName, isEnabled());
    }

    /**
     * Set the button name.
     *
     * @param value contains the button name
     */
    public void setButtonName(String value)
    {
        setButtonLabel(keyName, value, isEnabled());
    }

    /**
     * Gets the button name.
     *
     * @return the button name
     */
    public String getButtonName()
    {
        return buttonName;
    }

    /**
     * Set the HTML button text.
     *
     * @param keyText first line of the button text
     * @param buttonText second line of the button text
     * @param enabled button color and enabled behavior
     */
    public void setButtonLabel(String keyText,
                               String buttonText,
                               boolean enabled)
    {
        // Verify the button text is valid.
        if (buttonText == null || buttonText.equals(NULL_BUTTON))
        {
            buttonText = "";
        }
        // Set the button properties
        buttonName = buttonText;
        keyName = keyText;

        String labelText;

        // if we have key text, concat the label
        if (!Util.isEmpty(keyText))
        {
            labelText = keyText + '\n' + buttonText;
        }
        else
        {
            labelText = buttonText;
        }
        putValue(SHORT_DESCRIPTION, labelText);

        // wait to set this value until the description has been changed.
        setEnabled(enabled);
    }

    /**
     * Set the key value.
     *
     * @param value contains the key value.
     */
    public void setKeyEvent(int value)
    {
        KeyStroke stroke;
        if(value == KeyEvent.VK_DELETE)
        {
            stroke = KeyStroke.getKeyStroke(value,1);
        }
        else
        {
            stroke=KeyStroke.getKeyStroke(value,0);
        }
        putValue(KEYSTROKE,stroke);
    }

    /**
     * Gets the button number.
     *
     * @return the button number
     */
    public int getButtonNumber()
    {
        return buttonNumber;
    }

    /**
     * Sets the button number.
     *
     * @param value the button number
     */
    public void setButtonNumber(int value)
    {
        buttonNumber = value;
    }

    /**
     * Gets the button name tag.
     *
     * @return the button name tag
     */
    public String getButtonNameTag()
    {
        return buttonNameTag;
    }

    /**
     * Sets the button name tag.
     *
     * @param value the button name tag
     */
    public void setButtonNameTag(String value)
    {
        buttonNameTag = value;
    }

    /**
     * Set the action name.
     *
     * @param value contains the action name
     */
    public void setActionName(String value)
    {
        actionName = value;
    }

    /**
     * Gets the action name.
     *
     * @return the action name
     */
    public String getActionName()
    {
        return actionName;
    }

    /**
     * @return the parameterName
     */
    public String getParameterName()
    {
        return parameterName;
    }

    /**
     * @param parameterName the parameterName to set
     */
    public void setParameterName(String parameterName)
    {
        this.parameterName = parameterName;
    }

    /**
     * @return the menu
     */
    public boolean isMenu()
    {
        return menu;
    }

    /**
     * @param menu the menu to set
     */
    public void setMenu(boolean menu)
    {
        this.menu = menu;
    }

    /**
     * Return the source of the {@link ActionEvent} that this action passes
     * on to the listener.
     *
     * @return
     */
    public Object getSource()
    {
        return source;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("UIAction[actionName=");
        strResult.append(getActionName());
        strResult.append(",buttonName=");
        strResult.append(getButtonName());
        strResult.append(",buttonNumber");
        strResult.append(getButtonNumber());
        strResult.append(",keyName=");
        strResult.append(keyName);
        strResult.append(",menu=");
        strResult.append(menu);
        strResult.append("]");
        return strResult.toString();
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
