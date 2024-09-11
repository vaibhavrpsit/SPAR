/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CheckCountFloatSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.2.2.1  2004/11/04 22:40:39  mwisbauer
 *   @scr 7605  Added calling updatestatus for float count.
 *
 *   Revision 1.2  2004/07/06 21:24:33  dcobb
 *   @scr 2028 Till Closing Discrepancy Confirmation screen
 *   Determine expected closing float from the entered starting float.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckCountFloatSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "CheckCountFloatSite";

    //----------------------------------------------------------------------
    /**
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Get Till Close Cargo.
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();

        RegisterIfc register = cargo.getRegister();

        // OK indicator.
        boolean setDefaultFloatAmount = false;

        // Set default letter.
        LetterIfc letter = new Letter(CommonLetterIfc.NO);

        int countType = register.getTillCountFloatAtReconcile();

        if (countType == FinancialCountIfc.COUNT_TYPE_NONE)
        {
            cargo.setFloatCountType(FinancialCountIfc.COUNT_TYPE_NONE);
            letter = new Letter(CommonLetterIfc.NO);
            setDefaultFloatAmount = true;
        }
        else if (countType == FinancialCountIfc.COUNT_TYPE_SUMMARY)
        {
            cargo.setFloatCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            letter = new Letter(CommonLetterIfc.YES);
        }
        else if (countType == FinancialCountIfc.COUNT_TYPE_DETAIL)
        {
            cargo.setFloatCountType(FinancialCountIfc.COUNT_TYPE_DETAIL);
            letter = new Letter(CommonLetterIfc.YES);
        }
        else
        {
            // set to default as defined in requirements
            cargo.setFloatCountType(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            letter = new Letter(CommonLetterIfc.YES);
        }


        TillIfc t = register.getTillByID(cargo.getTillID());

        //FinancialTotalsIfc ft = t.getTotals();
        FinancialTotalsIfc ft = DomainGateway.getFactory()
                                             .getFinancialTotalsInstance();

        // Build expected float amount
        FinancialCountTenderItemIfc fcti = DomainGateway.getFactory()
                                                        .getFinancialCountTenderItemInstance();

        fcti.setDescription(DomainGateway.getFactory()
                                         .getTenderTypeMapInstance()
                                         .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
        fcti.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        fcti.setSummaryDescription("");
        fcti.setSummary(true);
        fcti.setAmountOut(t.getTotals().getStartingFloatCount().getEntered().getAmount());

        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setCountryCode(register.getTillFloatAmount().getCountryCode());
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setCurrencyID(register.getTillFloatAmount().getType().getCurrencyId());
        td.setTenderSubType("");
        fcti.setTenderDescriptor(td);

        // Set the expected float in the till
        FinancialCountIfc exp = (FinancialCountIfc)ft.getEndingFloatCount().getExpected();
        exp.addTenderItem(fcti);

//      set ending Float amount value to that of the TillFloatAmt setting
        // if "TillCountFloatAtClose is set to "No"
        if (setDefaultFloatAmount)
        {
            // set "entered" amount
            FinancialCountIfc ent = (FinancialCountIfc)ft.getEndingFloatCount().getEntered();
            ent.addTenderItem(fcti);
        }
            cargo.setFloatTotals(ft);
            cargo.getRegister().getTillByID(cargo.getTillID()).addTotals(ft);
            cargo.getRegister().addTotals(ft);
            cargo.setTillTotals(ft);
        bus.mail(letter, BusIfc.CURRENT);
    }
}
