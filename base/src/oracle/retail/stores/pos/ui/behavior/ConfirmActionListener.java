/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/ConfirmActionListener.java /main/6 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    rsnayak   03/25/11 - XbranchMerge rsnayak_bug-11686414 from main
 *    rsnayak   03/25/11 - class cast exception fix
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.EYSButton;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.UIAction;

/**
 * An action listener used to respond to "OK" or "Yes" buttons on popup dialogs.
 * It causes the dialog to close via {@link Window#dispose()}.
 * 
 * @author cgreene
 * @since 13.1
 */
public class ConfirmActionListener implements ActionListener
{

    private static final Logger logger = Logger.getLogger(ConfirmActionListener.class);

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        UISubsystem ui = UISubsystem.getInstance();
        try
        {
            if (ui.getDialogModel() instanceof POSBaseBeanModel)
            {
                String actionCommand = getLetterFromAction(e, ui.getDialogModel());
                updateModel((POSBaseBeanModel)ui.getDialogModel(), actionCommand);
            }
            ui.closeDialog();
        }
        catch (UIException ex)
        {
            logger.error("Unable to close the dialog.", ex);

        }
    }

    /**
     * Return the letter, if set, that will be used as the action command for
     * this event. If not set, the regular action command it used.
     * 
     * @param e
     * @param ui
     * @return
     * @throws UIException
     */
    protected String getLetterFromAction(ActionEvent e, UIModelIfc model) throws UIException
    {
        String actionCommand = e.getActionCommand();
        // if the model is from a dialog, the letter may have been changed.
        if (model instanceof DialogBeanModel)
        {
            EYSButton button = null;
            if (e.getSource() instanceof EYSButton)
            {
                button = (EYSButton)e.getSource();
            }
            else
            {
                UIAction source = (UIAction)e.getSource();
                button = (EYSButton)source.getSource();
            }
            String letter = (String)button.getClientProperty("letter");
            if (!Util.isEmpty(letter))
            {
                // mail the dialog letter instead of the action command
                actionCommand = letter;
            }
        }
        return actionCommand;
    }

    /**
     * Allows the action to update the screen's model contents if needed. This
     * implementation sets the response to the button's action command.
     *
     * @param model
     * @param actionCommand
     */
    protected void updateModel(POSBaseBeanModel model, String actionCommand)
    {
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        if (parModel == null)
        {
            parModel = new PromptAndResponseModel();
            model.setPromptAndResponseModel(parModel);
        }
        parModel.setResponseBytes(null);
        parModel.setResponseText(null);
        parModel.setResponseCommand(actionCommand);
    }

    /**
     * Dispose of the dialog
     * @param e
     * @deprecated as of 13.4. Replaced by calling {@link UISubsystem#closeDialog()} instead.
     */
    protected void closeDialog(UIAction action)
    {
        Window w = SwingUtilities.getWindowAncestor((Component)action.getSource());
        w.dispose();
    }

    /**
     * Get the current model in order to cause it to be updated 
     * @deprecated as of 13.4. Replaced by calling {@link UISubsystem#closeDialog()} instead.
     */
    protected void updateModel()
    {
        //
        UISubsystem ui = UISubsystem.getInstance();
        try
        {
            ui.getModel();
        }
        catch (UIException ex)
        {
            logger.error("Unable to get the dialog's screen model.", ex);
        }
    }
}
