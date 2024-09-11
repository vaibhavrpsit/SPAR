/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxRateActionSite.java /main/10 2011/02/16 09:13:27 cgreene Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse   
 * $
 * Revision 1.9  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.8  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.7  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.6  2004/03/11 20:21:31  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/09 16:45:14  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.3  2004/03/09 15:52:16  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/08 23:37:03  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:07 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:51:47 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:02:06 CSchellenger Initial revision.
 * 
 * Rev 1.2 Mar 26 2003 15:06:58 RSachdeva Removed use of CodeEntry getCode() method Resolution for POS SCR-2103: Remove
 * uses of deprecated items in POS.
 * 
 * Rev 1.1 Sep 18 2002 17:15:24 baa country/state changes Resolution for POS SCR-1740: Code base Conversions
 * 
 * Rev 1.0 Apr 29 2002 15:18:16 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:38:12 msg Initial revision.
 * 
 * Rev 1.1 Feb 05 2002 16:42:48 mpm Modified to use IBM BigDecimal. Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 * Rev 1.0 Sep 21 2001 11:29:36 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Site used for processing data from tax rate override screen.
 * 
 * @version $Revision: /main/10 $
 */
public class TaxRateActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -8875260518686061383L;
    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * This aisle is executed when a tax rate is entered at the UI and the Next
     * button is pressed. This aisle will set the tax rate in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get cargo handle
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        ItemTaxModControllerIfc cntl = cargo.getController();

        cntl.processTaxRate(bus);

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
