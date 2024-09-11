/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/CancelActionListener.java /main/5 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/18/09 - set repsonses to null during the updateModel
 *    cgreene   09/17/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * An action listener to respond to a "Cancel" button on a popup dialog. 
 * 
 * @author cgreene
 * @since 13.1
 */
public class CancelActionListener extends ConfirmActionListener
{

    private static final Logger logger = Logger.getLogger(CancelActionListener.class);


    /**
     * Overridden in order to call {@link PromptAndResponseModel#setCanceled(boolean)}
     * with true.
     *
     * @see oracle.retail.stores.pos.ui.behavior.ConfirmActionListener#updateModel(UIModelIfc, String)
     */
    @Override
    protected void updateModel(POSBaseBeanModel model, String actionCommand)
    {
        super.updateModel(model, actionCommand);
        PromptAndResponseModel parModel = ((POSBaseBeanModel)model).getPromptAndResponseModel();
        parModel.setCanceled(true);
    }

    /**
     * Get the current model, set it as canceled and set the responses as null.
     * @deprecated as of 14.0. Use {@link #updateModel(UIModelIfc)} instead.
     */
    @Override
    protected void updateModel()
    {
        UISubsystem ui = UISubsystem.getInstance();
        try
        {
            updateModel((POSBaseBeanModel)ui.getModel(), "Cancel");
        }
        catch (UIException ex)
        {
            logger.error("Unable to get the dialog's screen model.", ex);
        }
    }
}
