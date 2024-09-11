/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/CloseDialogAction.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
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
 *    Rev 1.0   Aug 29 2003 16:13:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:46:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:24   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:32:48   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   12 Oct 2001 12:00:16   jbp
 * Initial revision.
 * Resolution for POS SCR-211: HTML Help Functionality
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

// Java imports
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;

//---------------------------------------------------------------------------
/**
   This class handles the request to cancel an opertion.  It causes a
   confirmation dialog to display.
   
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//---------------------------------------------------------------------------
public class CloseDialogAction extends AbstractAction
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -89484382750907662L;

    /**
        The logger to which log messages will be sent.
    **/
    protected Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.behavior.CloseDialogAction.class);
    
    //---------------------------------------------------------------------------    
    /**
        This method sets up the dialog bean model and calls the show screen.
        @param evt an Action Event
    */
    //---------------------------------------------------------------------------    
    public void actionPerformed(ActionEvent evt)
    {
        POSJFCUISubsystem        ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        try
        {
            ui.closeDialog();
        }
        catch (UIException uie)
        {
            
            logger.error( "HelpAction.performAction() showScreen failed ");
        }
/*        catch (ConfigurationException ce)
        {
            Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.behavior.CloseDialogAction.class);
            logger.error( "HelpAction.performAction() showScreen failed ");
        }
        */
  }
}
