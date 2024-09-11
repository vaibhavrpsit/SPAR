/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/ConfirmCancelAction.java /main/11 2014/07/08 11:41:53 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/08/14 - refactor default timer model to default to 15
 *                         minutes timeout and be able to find parametermanager
 *                         from dispatcher
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:52:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 31 2003 14:12:32   nrao
 * Added try catch block for Instant Credit Enrollment.
 * 
 *    Rev 1.0   Aug 29 2003 16:13:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:46:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:48   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:34:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:18:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class handles the request to cancel an operation. It causes a
 * confirmation dialog to display.
 * 
 * @version $Revision: /main/11 $
 */
public class ConfirmCancelAction extends AbstractAction
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3093571137518743615L;
    /** The debug and error logger. */
    private static final Logger logger = Logger.getLogger(ConfirmCancelAction.class);

    /**
     * This method sets up the dialog bean model and calls the show screen.
     * 
     * @param evt an Action Event
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("CancelConfirm");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);

        String screenName = ui.getCurrentScreenSpecName();
        try
        {
            if (ui.getModel() != null && ui.getModel() instanceof POSBaseBeanModel)
            {
                dialogModel.setFormModel((POSBaseBeanModel)ui.getModel(screenName));
            }
        }
        catch (UIException e)
        {
            logger.error("Could not get the model for the current screen.", e);
        }

        dialogModel.setFormScreenSpecName(screenName);
        dialogModel.setUiGeneratedCancel(true);
        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (UIException uie)
        {
            logger.error("Could not show the cancel dialog.", uie);
        }
        catch (ConfigurationException ce)
        {
            logger.error("A configuration error occurred trying to show the cancel dialog.", ce);
        }
    }
}
