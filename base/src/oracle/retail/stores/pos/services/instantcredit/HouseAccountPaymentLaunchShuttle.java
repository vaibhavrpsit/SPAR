/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/HouseAccountPaymentLaunchShuttle.java /main/13 2013/07/02 13:09:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/01/13 - Fixed failure to cancel House Account Transaction
 *                         when cancel button pressed or timout occurs.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:20 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:22:01 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:18 PM  Robert Pearse
 *
 *Revision 1.5  2004/09/23 00:07:15  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.4  2004/04/09 16:56:02  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:50:40  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 04 2003 15:31:24   nrao
 * Code Review Changes.
 *
 *    Rev 1.0   Dec 01 2003 18:35:02   nrao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.houseaccount.payment.PayHouseAccountCargo;

import org.apache.log4j.Logger;

/**
 * Launch Shuttle for House Account Payment
 * 
 * @version $Revision: /main/13 $
 */
public class HouseAccountPaymentLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7254200309893796708L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(HouseAccountPaymentLaunchShuttle.class);

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "HouseAccountPaymentLaunchShuttle";

    /**
     * Instant Credit Cargo Now, the load only consists of getting the cargo
     * value. The unload can get whatever values it wants from this object.
     */
    protected InstantCreditCargo iCargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        iCargo = (InstantCreditCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        PayHouseAccountCargo cargo = (PayHouseAccountCargo)bus.getCargo();
        cargo.setCashier(iCargo.getOperator());
        cargo.setEmployee(iCargo.getEmployee());
        cargo.setInstantCredit(iCargo.getInstantCredit());
        cargo.setEncipheredCardData(iCargo.getInstantCredit().getEncipheredCardData());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}