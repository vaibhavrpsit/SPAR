/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/SafeCountParamSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    4    360Commerce 1.3         4/25/2007 8:52:34 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:17  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:00   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:44   msg
 * Initial revision.
 * 
 *    Rev 1.2   02 Mar 2002 12:47:28   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 * 
 *    Rev 1.1   23 Jan 2002 13:05:02   epd
 * Added expected amount checks for Store Safe amounts
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   12 Dec 2001 13:05:36   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

// foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks the Store Safe count parameter
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SafeCountParamSite extends PosSiteActionAdapter
{                                       // begin class SafeCountParamSite

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Checks the Store Safe count parameter. <P>
       @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        String letterName = CommonLetterIfc.YES;

        // get cargo reference
        EndOfDayCargo cargo = (EndOfDayCargo) bus.getCargo();

        boolean setDefaultSafeAmount = false;

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        String value;
        String expectedAmount;
        try
        {
            value = pm.getStringValue("CountOperatingFundsAtEndOfDay");
            if (value.equals(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_DETAIL]))
            {
                cargo.setSafeCountType(FinancialCountIfc.COUNT_TYPE_DETAIL);
            }
            else if (value.equals(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_SUMMARY]))
            {
                cargo.setSafeCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            }
            else    
            {
                cargo.setSafeCountType(FinancialCountIfc.COUNT_TYPE_NONE);
                letterName = CommonLetterIfc.NO;
                setDefaultSafeAmount = true;
            }

            expectedAmount = pm.getStringValue("OperatingFundExpectedAmount");
            if (value.length() == 0)
            {
                expectedAmount = "1000.00"; // default value;
            }
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
            amount.setStringValue(expectedAmount);
        
            FinancialTotalsIfc ft = DomainGateway.getFactory().getFinancialTotalsInstance();
            FinancialCountIfc exp = (FinancialCountIfc)ft.getEndingSafeCount().getExpected();

            TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(amount.getCountryCode());
            td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
            td.setCurrencyID(amount.getType().getCurrencyId());

            FinancialCountTenderItemIfc fcti = 
              DomainGateway.getFactory().getFinancialCountTenderItemInstance();
            fcti.setDescription(DomainGateway.getFactory()
                                             .getTenderTypeMapInstance()
                                             .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
            fcti.setSummaryDescription("");
            fcti.setSummary(true);
            fcti.setAmountIn(amount);
            fcti.setTenderDescriptor(td);

            // set entered amount to default amount if we are not counting
            if (setDefaultSafeAmount)
            {
                FinancialCountIfc ent = (FinancialCountIfc)ft.getEndingSafeCount().getEntered();
                ent.addTenderItem(fcti);
            }

            exp.addTenderItem(fcti);
            cargo.setSafeTotals(ft);
        }
        catch (ParameterException pe)
        {
            logger.error( "" + pe.getMessage() + "");

            value = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_SUMMARY]; // default as defined in requirements
        }

        // mail appropriate letter
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }                                   // end arrive()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}                                       // end class StoreStatusUpdateSite
