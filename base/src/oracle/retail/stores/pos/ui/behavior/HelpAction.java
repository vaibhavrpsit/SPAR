/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/HelpAction.java /main/11 2013/06/20 14:46:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/20/13 - code cleanup
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         6/19/2007 5:39:40 AM   Manikandan Chellapan
 *         #CR27262 added setModel method call after showDialog call to timout
 *          after closing help window.
 *    3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:18 PM  Robert Pearse   
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
 *    Rev 1.1   27 Jan 2004 18:47:48   Tim Fritz
 * Changed the performAction() method.
 * 
 *    Rev 1.0   Aug 29 2003 16:13:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:30   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:32:52   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.1   12 Oct 2001 12:01:52   jbp
 * Changed for screen level help
 * Resolution for POS SCR-211: HTML Help Functionality
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DisplayHtmlBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This class handles the request to cancel an opertion. It causes a
 * confirmation dialog to display.
 * 
 * @version $Revision: /main/11 $
 */
public class HelpAction extends AbstractAction
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -7187926871682635359L;

    protected static final Logger logger = Logger.getLogger(HelpAction.class);

    /**
     * This method sets up the dialog bean model and calls the show screen.
     * 
     * @param evt an Action Event
     */
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        DisplayHtmlBeanModel dialogModel = new DisplayHtmlBeanModel();
        String activeScreenID = null;

        // get the active screen ID
        try
        {
            activeScreenID = ui.getActiveScreenID();
        }
        catch (UIException uie)
        {
            logger.error("HelpAction could not get the ActiveScreenID.", uie);
        }

        if (activeScreenID != null)
        {
            // Save data from the active screen
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
                logger.error("HelpAction could not get the model for \"" + screenName + "\".", e);
            }

            dialogModel.setDisplayURL(dialogModel.getURLForScreen(activeScreenID));
            dialogModel.setResourceID("HelpDialog");
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            dialogModel.setFormScreenSpecName(activeScreenID);
        }

        dialogModel.setUiGeneratedCancel(true);
        try
        {
            ui.showDialog(POSUIManagerIfc.HELP, dialogModel);
        }
        catch (UIException uie)
        {
            logger.error("HelpAction could not show dialog for HELP.", uie);
        }
        catch (Exception e)
        {
            logger.error("HelpAction could not show dialog for HELP.", e);
        }
        try
        {
            ui.setModel(ui.getCurrentScreenSpecName(), ui.getModel());
        }
        catch (UIException uie)
        {
            logger.error("HelpAction could not set model back to current.", uie);
        }
        catch (Exception e)
        {
            logger.error("HelpAction could not set model back to current.", e);
        }
    }
}
