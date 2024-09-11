/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/instantcredit/EnrollmentLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/25/2007 8:52:44 AM   Anda D. Cadar   I18N
 *      merge
 *      
 * 3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *Revision 1.2  2004/04/09 16:56:02  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.1  2004/04/06 20:22:50  epd
 *@scr 4263 Updates to move instant credit enroll to sub tour
 *
 *Revision 1.3  2004/02/12 16:48:22  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:22:51  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Jan 19 2004 16:33:16   nrao
 * Added additional condition for final tender. Fix for Special Order and Layaway Transaction.
 * Resolution for 3466: House Account Enrollment with in a Special Order Transaction or Layaway is incorrect.
 * 
 *    Rev 1.2   Jan 14 2004 16:02:58   nrao
 * Added comments.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.instantcredit;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

//--------------------------------------------------------------------------
/**
    Loads data into shuttle. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnrollmentLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.tender.instantcredit.EnrollmentLaunchShuttle.class);
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected TenderCargo tCargo;

    //----------------------------------------------------------------------
    /**
       Loads data into shuttle.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        tCargo = (TenderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads data from shuttle. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();

        CurrencyIfc currentAmt = DomainGateway.getBaseCurrencyInstance();
        String currentAmtStr = (String)tCargo.getTenderAttributes().get(TenderConstants.AMOUNT);
        currentAmt = DomainGateway.getBaseCurrencyInstance(currentAmtStr);

        TenderableTransactionIfc trans = (TenderableTransactionIfc)((ADO)tCargo
                        .getCurrentTransactionADO()).toLegacy();

        cargo.setTransaction(trans);
        cargo.setTenderAmount(currentAmt);
        
        // if split tender -- another tender is applied before Instant Credit is applied
        if (currentAmt.compareTo(trans.getTenderTransactionTotals().getGrandTotal()) != 0 &&
            currentAmt.compareTo(trans.getTransactionTotals().getBalanceDue()) == 0 )
        {
            cargo.setSplitFinalTender(true);
        }
        // if not split tender and only Instant Credit is used for payment
        else if (currentAmt.compareTo(trans.getTenderTransactionTotals().getGrandTotal()) == 0 &&
                 currentAmt.compareTo(trans.getTransactionTotals().getBalanceDue()) == 0)
        {
            cargo.setFinalTender(true);
        }
    }
}
