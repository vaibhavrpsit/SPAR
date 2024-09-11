/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/instantcredit/EnrollmentReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/06/10 16:09:11 ohorne Exp $
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
 * 8    360Commerce 1.7         3/17/2008 10:49:49 PM  Manikandan Chellapan
 *      CR#30250 House Account Expiration Date appears in clear text for
 *      Instant credit.
 * 7    360Commerce 1.6         12/18/2007 5:47:48 PM  Alan N. Sinton  CR
 *      29661: Changes per code review.
 * 6    360Commerce 1.5         5/30/2007 9:01:57 AM   Anda D. Cadar   code
 *      cleanup
 * 5    360Commerce 1.4         5/18/2007 9:19:18 AM   Anda D. Cadar   always
 *      use decimalValue toString
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
 *    Rev 1.5   Jan 19 2004 16:35:58   nrao
 * Fix for Special Order and Layaway Transaction. If not split final tender or final tender, then get tender amount from cargo.
 * Resolution for 3466: House Account Enrollment with in a Special Order Transaction or Layaway is incorrect.
 * 
 *    Rev 1.4   Jan 14 2004 16:04:56   nrao
 * Set value of balance based on different conditions.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.instantcredit;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;

//--------------------------------------------------------------------------
/**
    Loads data into shuttle
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public class EnrollmentReturnShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.tender.instantcredit.EnrollmentReturnShuttle.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    protected InstantCreditCargo bCargo;

    //----------------------------------------------------------------------
    /**
       Loads data from DollarOffReward service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        bCargo = (InstantCreditCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Loads data for ModifyTransaction service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        CurrencyIfc balance = null;
        
        // if split tender -- another tender is applied before Instant Credit is applied
        if (bCargo.isSplitFinalTender())
        {
            balance = ((TenderableTransactionIfc)bCargo.getTransaction())
                                                               .getTenderTransactionTotals()
                                                               .getBalanceDue();
        }
        
        // if not split tender and only Instant Credit is used for payment
        else if (bCargo.isFinalTender())
        {
            balance = ((TenderableTransactionIfc)bCargo.getTransaction())
                                                               .getTenderTransactionTotals()
                                                               .getGrandTotal();
        }
        
        // TODO -- if split tender and Instant Credit is applied first
        else balance = bCargo.getTenderAmount();

        // put instant credit info into tender attributes
        cargo.getTenderAttributes().put(TenderConstants.AMOUNT, balance.getDecimalValue().toString());
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.HOUSE_ACCOUNT);
        InstantCreditIfc ic = ((InstantCreditTransactionIfc)bCargo.getTransaction()).getInstantCredit();

        if (ic != null)
        {
            if (ic.getAccountNumber() != null)
            {
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, ic.getAccountNumber()); 
                cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, ic.getEncipheredCardData());
            }
             
            if(ic.getEncipheredCardData().getEncryptedExpirationDate() != null)
            {
                cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE,ic.getEncipheredCardData().getEncryptedExpirationDate());   
            }
        }
    }
}
