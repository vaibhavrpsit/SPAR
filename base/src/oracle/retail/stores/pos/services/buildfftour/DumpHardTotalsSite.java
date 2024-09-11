/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/buildfftour/DumpHardTotalsSite.java /main/11 2014/02/05 15:03:09 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  01/31/14 - released the stream handles
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:41  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 20 2003 08:45:00   jgs
 * Modified to use the Domain Factory to create instances of hard totals objects.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:08   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   12 Mar 2002 16:52:38   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.0   Sep 21 2001 11:13:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.buildfftour;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Creates the transactions to build the PLU and Employee Flat files.
 * 
 * @version $Revision: /main/11 $
 */
public class DumpHardTotalsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7190500998414689877L;
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // read hard totals
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        HardTotalsIfc ht = null;

        // retrieve from hard totals
        try(RandomAccessFile outfile = new RandomAccessFile("hardtotals.txt", "rw");)
        {
            Serializable data = pda.readHardTotals();
            HardTotalsBuilderIfc builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
            builder.setHardTotalsInput(data);
            ht = (HardTotalsIfc) builder.getFieldAsClass();
            ht.setHardTotalsData(builder);
        
            outfile.writeBytes(ht.toString());

        }
        catch (DeviceException e)
        { // begin catch device exception
            logger.error("Hard totals device could not be read.", e);

            if (e.getCause() != null)
            {
                logger.error("DeviceException.NestedException:", e.getCause());
            }
        } // end catch device exception
        catch (HardTotalsFormatException htfe)
        {
            logger.error("Hard totals device could not be read due to a HardTotalsFormatException.", htfe);
        }
        catch (IOException ioe)
        {
            logger.error("Could not write to output file.", ioe);
        }

        System.exit(0);

        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
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