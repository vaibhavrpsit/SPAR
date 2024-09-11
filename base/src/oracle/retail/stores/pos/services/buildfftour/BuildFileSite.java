/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/buildfftour/BuildFileSite.java /main/10 2011/02/16 09:13:25 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:40 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/10 14:21:35  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
 *
 *   Revision 1.7  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:11:00  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/08 17:54:17  pkillick
 *   @scr Changed scr number below from 4232 to 4332.
 *
 *   Revision 1.4  2004/04/08 16:23:39  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
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
 *    Rev 1.1   Sep 12 2002 17:09:56   sspiars
 * Added new method call to BuildFlatFileTransaction.buildTaxRuleFlatFile()
 * to write Tax Rules to local Flat Files.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:36:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:08:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:21:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:13:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.buildfftour;

import oracle.retail.stores.domain.arts.BuildFlatFileTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Creates the transactions to build the PLU and Employee Flat files.
 * 
 * @version $Revision: /main/10 $
 */
public class BuildFileSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -735023096256120320L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        BuildFlatFileTransaction trans = null;
        
        trans = (BuildFlatFileTransaction) DataTransactionFactory.create(DataTransactionKeys.BUILD_FLAT_FILE_TRANSACTION);

        try
        {
            trans.buildPLUFlatFile();
            trans.buildEmployeeFlatFile();
            // trans.buildTaxRuleFlatFile();
            trans.buildNewTaxRuleFlatFile();
        }
        catch (DataException de)
        {
            logger.error("Error building PLU File.", de);
        }

        // In Windows 95/98 the foundation does not exit correctly when the
        // top service exits.  This exit makes sure that buildff.bat quits
        // when its job is done.
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
