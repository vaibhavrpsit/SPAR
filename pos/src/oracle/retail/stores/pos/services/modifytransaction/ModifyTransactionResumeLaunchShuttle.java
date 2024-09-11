/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionResumeLaunchShuttle.java /main/13 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:30  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;

/**
 * Launch shuttle class for ModifyTransactionResume service.
 * 
 * @version $Revision: /main/13 $
 */
public class ModifyTransactionResumeLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -1697210304367295002L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionResumeLaunchShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * the transaction
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * Loads from child (ModifyTransactionResume) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        transaction = cargo.getTransaction();
    }

    /**
     * Unloads to parent (ModifyTransaction) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        cargo.setTransaction(transaction);
        cargo.setCustomerInfo(cargo.getCustomerInfo());
        //cargo.getCustomerInfo().getPhoneNumber().setPhoneNumber(cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber());
        //cargo.getCustomerInfo();
    }

    /**
     * Returns the string representation of the object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ModifyTransactionResumeLaunchShuttle").append(" (Revision ")
                .append(getRevisionNumber()).append(")").append(hashCode());
        return (strResult.toString());
    }

    /**
     * Returns the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}