/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/KeyDialogAction.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/14/10 - show or hide keyboard depending on whether its
 *                         showing
 *    cgreene   01/14/10 - update to not call showOnscreenKeyboard(boolean)
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - add method call to showOnScreenKeyboard
 *    cgreene   12/07/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;

/**
 * This class handles the request to display or hide the key dialog.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class KeyDialogAction extends AbstractAction
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3093571137518743615L;

    /** the logger for error logging */
    private static final Logger logger = Logger.getLogger(KeyDialogAction.class);
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt)
    {
        UISubsystem ui = UISubsystem.getInstance();
        try
        {
            ui.showOnScreenKeyboard(!ui.isOnScreenKeyboardVisible());
        }
        catch (UIException e)
        {
            logger.error("Unable to display popup keyboard dialog.", e);
        }
    }
}
