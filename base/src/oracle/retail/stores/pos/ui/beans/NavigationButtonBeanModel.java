/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NavigationButtonBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    asinton   06/02/10 - changed adding of ButtonSpecs to modifyButtons list
 *                         so that only one ButtonSpec is added by actionName.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   05/04/09 - memory and performance based improvements
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This class defines the navigation button bean model for the Point of Service
 * application. 
 */
public class NavigationButtonBeanModel implements Serializable
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7609452281390527647L;

    /** An array of button specs to add to the button bean */
    protected ArrayList<ButtonSpec> newButtons = null;

    /** An array of button specs used to modify existing buttons */
    protected ArrayList<ButtonSpec> modifyButtons = null;

    /**
     * @deprecated as of 13.1 not used
     */
    protected Color btnText = null;

    /**
     * Default constructor.
     */
    public NavigationButtonBeanModel()
    {
    }

    /**
     * Gets the array of new buttons.
     * 
     * @return an array of button specs; null if empty.
     */
    public ButtonSpec[] getNewButtons()
    {
        ButtonSpec[] array = null;
        if (newButtons != null)
        {
            array = new ButtonSpec[newButtons.size()];
            newButtons.toArray(array);
        }
        return array;
    }

    /**
     * Gets the array of modify buttons.
     * 
     * @return an array of button specs; null if empty.
     */
    public ButtonSpec[] getModifyButtons()
    {
        ButtonSpec[] array = null;
        if (modifyButtons != null)
        {
            array = new ButtonSpec[modifyButtons.size()];
            modifyButtons.toArray(array);
        }
        return array;
    }

    /**
     * This is a convenience method to set the enable state of a button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param enabled the enabled state of the button.
     */
    public void setButtonEnabled(String actionName, boolean enabled)
    {
        ButtonSpec spec = findButtonSpec(actionName);
        spec.setEnabled(enabled);
    }

    /**
     * This is a convenience method to set the enable state of a button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param enabled the enabled state of the button.
     * @param labelTag as String
     */
    public void setButtonEnabled(String actionName, String labelTag, boolean enabled)
    {
        ButtonSpec spec = findButtonSpec(actionName);
        String label = UIUtilities.retrieveCommonText(labelTag, labelTag);
        spec.setLabel(label);
        spec.setEnabled(enabled);
    }

    /**
     * This is a convenience method to set the label of a button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param label the label to display on the button.
     */
    public void setButtonLabel(String actionName, String label)
    {
        ButtonSpec spec = findButtonSpec(actionName);
        spec.setLabel(label);
    }

    /**
     * This is a convenience method to set the key label of a button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param keyName the key label to display on the button.
     */
    public void setButtonKeyLabel(String actionName, String keyName)
    {
        ButtonSpec spec = findButtonSpec(actionName);
        spec.setKeyName(keyName);
    }

    /**
     * This is a convenience method to check if the letter sent from a site is
     * from one of the buttons.
     * 
     * @param letter as String.
     * @return boolean
     */
    public boolean checkLetter(String letter)
    {
        boolean letterFound = false;

        if (modifyButtons != null)
        {
            for (int i = 0; i < modifyButtons.size(); i++)
            {
                if (letter.equals(modifyButtons.get(i).getActionName()))
                {
                    letterFound = true;
                    break;
                }
            }

        }

        if (!letterFound)
        {
            if (newButtons != null)
            {
                for (int i = 0; i < newButtons.size(); i++)
                {
                    if (letter.equals(newButtons.get(i).getActionName()))
                    {
                        letterFound = true;
                        break;
                    }
                }

            }
        }

        return letterFound;
    }

    /**
     * This is a convenience method to add a new to the bean button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param label the label to display on the button.
     * @param enabled true if button is enabled.
     * @param actionListenerName special function listener; should be 'null' if
     *            mailing a letter.
     * @param keyName key name, i.e. "F1", "F2", "Enter".
     * @param iconName icon name; should be 'null' if label is not.
     */
    public void addButton(String actionName, String label, boolean enabled, String actionListenerName, String keyName,
            String iconName)
    {
        ButtonSpec spec = new ButtonSpec();
        spec.setActionListenerName(actionListenerName);
        spec.setKeyName(keyName);
        spec.setIconName(iconName);
        spec.setActionName(actionName);
        spec.setLabel(label);
        spec.setEnabled(enabled);
        getNewSpecList().add(spec);
    }

    /**
     * This is a convenience method to add a new to the bean button.
     * 
     * @param actionName the action name of the button; corresponds to the name
     *            of the letter the button mails.
     * @param label the label to display on the button.
     * @param label tag.
     * @param enabled true if button is enabled.
     * @param keyName key name, i.e. "F1", "F2", "Enter".
     */
    public void addButton(String actionName, String label, String labelTag, boolean enabled, String keyName)
    {

        ButtonSpec spec = new ButtonSpec();
        spec.setKeyName(keyName);
        spec.setActionName(actionName);
        spec.setLabel(label);
        spec.setEnabled(enabled);
        spec.setLabelTag(labelTag);
        getNewSpecList().add(spec);
    }

    /**
     * Lazily init and return the new button spec list.
     * 
     * @return
     */
    ArrayList<ButtonSpec> getNewSpecList()
    {
        if (newButtons == null)
        {
            newButtons = new ArrayList<ButtonSpec>(1);
        }
        return newButtons;
    }

    /**
     * Lazily init and return the new button spec list.
     * 
     * @return
     */
    ArrayList<ButtonSpec> getModifySpecList()
    {
        if (modifyButtons == null)
        {
            modifyButtons = new ArrayList<ButtonSpec>(1);
        }
        return modifyButtons;
    }


    /**
     * This method finds the ButtonSpec in modifyButtons array.  If one is not found,
     * a new ButtonSpec is added.
     * 
     * @param actionName the action name of the button; corresponds to the
     *                      name of the letter the button mails.
     *                      This is essentially the lookup key.
     */
    protected ButtonSpec findButtonSpec(String actionName)
    {
        ButtonSpec returnSpec = null;
        final int size = getModifySpecList().size();
        ButtonSpec thisSpec = null;
        for (int i = 0; i < size && returnSpec == null; i++)
        {
            thisSpec = getModifySpecList().get(i);
            if (actionName != null && actionName.equals(thisSpec.getActionName()))
            {
                returnSpec = thisSpec;
            }
        }
    
        // not found - add new button spec
        if (returnSpec == null)
        {
            returnSpec = new ButtonSpec();
            returnSpec.setActionName(actionName);  
            getModifySpecList().add(returnSpec);
        }
        
        return returnSpec;
    }
        
}
