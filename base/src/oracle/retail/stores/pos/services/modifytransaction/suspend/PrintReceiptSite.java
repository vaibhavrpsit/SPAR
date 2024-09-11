/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/PrintReceiptSite.java /main/12 2012/04/16 12:48:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     04/16/12 - Clean up code from forward port.
 *    icole     04/11/12 - Forward port of spurkaya_bug-13114278 , SUSPENDED
 *                         TRANSACTIONS THAT ARE ALLOWED TO TIMEOUT CAUSE A
 *                         QUEUE EXCEPTION.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/15 16:34:22  kmcbride
 *   @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions
 *
 *   Revision 1.3  2004/02/12 16:51:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:46   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 10 2003 17:09:04   DCobb
 * Added printer offline behavior.
 * Resolution for POS SCR-1892: The printer offline message does not appear for transaction suspension or retrieval
 * 
 *    Rev 1.0   Apr 29 2002 15:15:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:34   msg
 * Initial revision.
 * 
 *    Rev 1.5   11 Mar 2002 11:41:08   jbp
 * Journal before saveing transaction
 * Resolution for POS SCR-1450: Date/Timestamp appears in wrong place for a suspended sale that has advanced pricing items on it
 *
 *    Rev 1.4   Mar 10 2002 08:56:34   mpm
 * Removed unnecessary reference to PromptAndResponseModel.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   28 Jan 2002 11:22:58   sfl
 * When a transaction is suspended, add the send item
 * marking in the E-Journal if the suspended transaction
 * contains the send items.
 * Resolution for POS SCR-867: Send - ejournal entry for a suspended transaction incorrect
 *
 *    Rev 1.2   Dec 17 2001 16:45:02   vxs
 * Older call to UtilityManagerIfc.printReceipt() was deprecated.
 * Resolution for POS SCR-390: Actual receipt has different format for Trans S/R  than mockup
 *
 *    Rev 1.1   Dec 12 2001 10:21:40   vxs
 * Removed Printing Reports screen - not necessary.
 * Resolution for POS SCR-379: After successfully suspending a transaction, next screen is Report Printing, not defined in requirements
 *
 *    Rev 1.0   Sep 21 2001 11:31:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.suspend;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;

/**
 * Print receipt for suspended transaction.
 * 
 * @version $Revision: /main/12 $
 */
public class PrintReceiptSite extends PosSiteActionAdapter
    implements ParameterConstantsIfc
{
    private static final long serialVersionUID = 2325860595988728350L;

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * site name constant
     */
    public static final String SITENAME = "PrintReceiptSite";

    /**
     * Print Receipt if configured.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Letter letter = null;
        // See if printing is configured
        boolean printConfig = false;
        
        try
        {
            printConfig = pm.getBooleanValue(PRINTING_PrintReceipts);   
        }
        catch(ParameterException pe)
        {
            logger.error(pe);   
        }
        
        // print receipt if configured
        if (printConfig)
        {
            letter = new Letter(CommonLetterIfc.PRINT);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }

        bus.mail(letter, BusIfc.CURRENT);

    }                                   // end arrive()

}                                       // end class class PrintReceiptSite

