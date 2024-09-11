/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintReceiptSite.java /main/10 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/22 17:39:00  dcobb
 *   @scr 4452 Feature Enhancement: Printing
 *   Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:36   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2002 18:01:10   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 * 
 *    Rev 1.1   23 Jan 2002 11:03:06   pdd
 * Cleanup.
 * Resolution for POS SCR-138: Printer never shows offline on Device Status screen
 * 
 *    Rev 1.0   Sep 21 2001 11:22:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Print Receipt if configured.
 * 
 * @version $Revision: /main/10 $
 */
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 255782739386346392L;
    /** revision number */
    public static final String revisionNumber = "$Revision: /main/10 $";

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
        boolean printConfig = true;
        try
        {
            printConfig = pm.getBooleanValue(ParameterConstantsIfc.PRINTING_PrintReceipts);
        }
        catch (ParameterException e)
        {
            logger.error("Could not determine print setting.", e);
        }

        // print receipt if configured
        if (printConfig)
        {
            letter = new Letter(CommonLetterIfc.PRINT);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
