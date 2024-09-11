/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/LookupTenderLimitsSite.java /main/12 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/24 16:43:10  bwf
 *   @scr 1882 Log tenderlimits parameter missing.
 *
 *   Revision 1.4  2004/02/17 18:36:03  epd
 *   @scr 0
 *   Code cleanup. Returned unused local variables.
 *
 *   Revision 1.3  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:24:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 30 2003 14:22:22   rwh
 * Refactored classes in Main to use RegisterADO in place of VirtualRegisterADO. Added methods to RegisterADO, tender limits and operator. Moved read financials method from VirtualRegisterADO to MainTDO
 * Resolution for POS SCR-3653: RegisterADO Refactor
 * 
 *    Rev 1.0   Dec 15 2003 09:30:28   bjosserand
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:01:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   23 May 2002 17:44:04   vxs
 * Removed unneccessary concatenations in logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:19:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:00   msg
 * Initial revision.
 *
 *    Rev 1.2   12 Mar 2002 16:52:50   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.1   11 Feb 2002 19:19:10   pdd
 * Modified to use AbstractFinancialCargo so site can be used in different service.
 * Resolution for POS SCR-1023: Mod Effect for MaximumCashAccepted incorrect
 *
 *    Rev 1.0   Sep 21 2001 11:22:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Query the database for store status. <>P>
 * 
 * @version $Revision: /main/12 $
 */
public class LookupTenderLimitsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 6516403115376416671L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Send a store status lookup inquiry to the database manager.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter; // letter to mail to determine next destination
        // ok indicator
        boolean bOk = true;
        // set default letter
        letter = new Letter(CommonLetterIfc.SUCCESS);
        // look up parameter that is present.
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        TenderLimitsIfc limits = DomainGateway.getFactory().getTenderLimitsInstance();
        String currentKey = null;
        try
        {                               // begin get tender limits try block
            int size = TenderLimitsIfc.TENDER_LIMIT_KEYS.length;

            for (int i = 0; i < size; i++)
            {                           // begin loop through tender limits keys
                currentKey = TenderLimitsIfc.TENDER_LIMIT_KEYS[i];
                String parm = pm.getStringValue(currentKey);
                limits.setCurrencyLimit(currentKey, parm);
                if (logger.isInfoEnabled()) logger.info(
                            "Parameter read:  " + TenderLimitsIfc.TENDER_LIMIT_KEYS[i] + "=[" + parm + "]");
            }                           // end loop through tender limits keys
            size = TenderLimitsIfc.TENDER_PERCENT_LIMIT_KEYS.length;

            for (int i = 0; i < size; i++)
            {                           // begin loop through tender percent keys
                currentKey = TenderLimitsIfc.TENDER_PERCENT_LIMIT_KEYS[i];
                String parm = pm.getStringValue(currentKey);
                limits.setPercentageLimit(currentKey,parm);
                if (logger.isInfoEnabled()) logger.info(
                            "Parameter read:  " + TenderLimitsIfc.TENDER_PERCENT_LIMIT_KEYS[i] + "=[" + parm + "]");
            }                           // end loop through tender percent keys
        }                               // end get tender limits try block
        catch (ParameterException e)
        {
            if (logger.isInfoEnabled())
            {
                logger.error("The tender limits parameter data, " + currentKey + ", was not found: " + Util.throwableToString(e) + "");
            }
            letter = new Letter("ParameterError");
            bOk = false;
            // set error code as if data were not found
            //cargo.setDataExceptionErrorCode(DataException.NO_DATA);
        }

        // if no error, set limits in cargo
        if (bOk)
        {
            //cargo.setTenderLimits(limits);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}