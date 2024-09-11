/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/LayawayShuttle.java /main/14 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  02/01/10 - fix for display of cash drawer warning message for
 *                         layaway transactions
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:17 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:50:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:20:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Common shuttle used to transfer layaway related data between layaway
 * services.
 * 
 * @version $Revision: /main/14 $
 */
public class LayawayShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -648696812294874679L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * layaway cargo reference
     */
    protected LayawayCargo layawayCargo = null;

    /**
     * Loads the cargo data in to the shuttle. If used as both a launch and
     * return shuttle, this cargo will reset the references in the calling
     * service to refer to the same objects they originally transferred.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        layawayCargo = (LayawayCargo)bus.getCargo();
    }

    /**
     * Unloads the shuttle data into the cargo. If used as both a launch and
     * return shuttle, this cargo will reset the references in the calling
     * service to refer to the same objects they originally transferred.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        cargo.setOperator(layawayCargo.getOperator());
        cargo.setLayawaySearchID(layawayCargo.getLayawaySearchID());
        cargo.setSaleTransaction(layawayCargo.getSaleTransaction());
        cargo.setCustomer(layawayCargo.getCustomer());
        cargo.setSalesAssociate(layawayCargo.getSalesAssociate());
        cargo.setInitialLayawayTransaction(layawayCargo.getInitialLayawayTransaction());
        cargo.setTenderableTransaction(layawayCargo.getTenderableTransaction());
        cargo.setSeedLayawayTransaction(layawayCargo.getSeedLayawayTransaction());
        cargo.setRegister(layawayCargo.getRegister());
        cargo.setLayawayOperation(layawayCargo.getLayawayOperation());
        cargo.setDataExceptionErrorCode( layawayCargo.getDataExceptionErrorCode() );
        cargo.setCashDrawerUnderWarning(layawayCargo.isCashDrawerUnderWarning());
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: " + getClass().getName() + "(Revision " + getRevisionNumber() + ") @"
                + hashCode());

        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
