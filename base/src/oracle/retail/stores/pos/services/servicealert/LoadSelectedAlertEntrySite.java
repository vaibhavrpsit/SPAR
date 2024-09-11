/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/LoadSelectedAlertEntrySite.java /main/11 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nganesh   02/02/10 - Service Alert is modified to Fulfillment
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 09 2004 13:00:48   kll
 * handle null
 * Resolution for 2971: Service alert hangs, sometimes crashes after viewing help
 *
 *    Rev 1.0   Aug 29 2003 16:06:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:03:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:05:30   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Loads the selected alert entry for processing.
 * 
 * @version $Revision: /main/11 $
 */
public class LoadSelectedAlertEntrySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 849301736936981067L;

    public static final String SITENAME = "LoadSelectedAlertEntrySite";

    /**
     * Identify the type of alert entry that was selected and mail the
     * appropriate letter.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        AlertEntryIfc entry = cargo.getSelectedEntry();
        Letter letter = null;

        if(entry != null)
        {
            switch (entry.getAlertType())
            {
                case AlertEntryIfc.ALERT_TYPE_EMAIL:
                letter = new Letter("Email");
                break;

                case AlertEntryIfc.ALERT_TYPE_ORDER_PICKUP:
                letter = new Letter("Pickup");
                break;
                case AlertEntryIfc.ALERT_TYPE_UNDEFINED:
                default:
                logger.error( "Unknown Alert Entry type selected in Fulfillment.");
            }
        }
        else
        {
            // send to ListNewEntriesSite
            letter = new Letter(CommonLetterIfc.OK);
            logger.error( "Unknown Alert Entry type selected in Fulfillment.");
        }

        bus.mail(letter, BusIfc.CURRENT);

    }
}
