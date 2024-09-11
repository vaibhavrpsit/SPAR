/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/ModifyTransactionDiscountPercentLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *Revision 1.4  2004/04/09 16:56:02  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
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
 *    Rev 1.3   Dec 04 2003 15:42:34   nrao
 * Added "protected" qualifier.
 * 
 *    Rev 1.2   Nov 24 2003 19:45:12   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

//--------------------------------------------------------------------------
/**
    Moves cargo for ModifyTransactionDiscountPercent service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionDiscountPercentLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.instantcredit.enrollment.ModifyTransactionDiscountPercentLaunchShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "";

    /**
        Instant Credit Cargo
        Now, the load only consists of getting the cargo value.
        The unload can get whatever values it wants from this object.
    **/    
    protected InstantCreditCargo iCargo;

    //----------------------------------------------------------------------
    /**
       Loads data from service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        iCargo = (InstantCreditCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads data to service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ModifyTransactionDiscountCargo cargo =
            (ModifyTransactionDiscountCargo)bus.getCargo();
        cargo.setOriginalTransaction((SaleReturnTransactionIfc)iCargo.getTransaction());
        cargo.setEmployeeDiscountID(iCargo.getEmployeeID());
        cargo.setInstantCreditDiscount(true);
        cargo.setDiscountType(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
    }
}
