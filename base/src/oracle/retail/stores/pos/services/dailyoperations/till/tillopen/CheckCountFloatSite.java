/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/CheckCountFloatSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *    4    360Commerce 1.3         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Dec 20 2002 11:32:06   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Apr 29 2002 15:27:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:29:22   msg
 * Initial revision.
 *
 *    Rev 1.3   02 Mar 2002 12:47:54   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.2   01 Mar 2002 09:25:40   epd
 * Fixed problem with tenderdescriptor
 * Resolution for POS SCR-1449: Lockup opening till at 'Close Cash Drawer'
 *
 *    Rev 1.1   29 Oct 2001 16:15:00   epd
 * Updated files to remove reference to Till related parameters.  This information, formerly contained in parameters, now resides as register settings obtained from the RegisterIfc class.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:18:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
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
        site name
    **/
    public static final String SITENAME = "CheckCountFloatSite";

    //----------------------------------------------------------------------
    /**
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TillOpenCargo cargo = (TillOpenCargo) bus.getCargo();

        // ok indicator
        boolean setDefaultFloatAmount = false;

        // set default letter
        LetterIfc letter = new Letter(CommonLetterIfc.NO);

        RegisterIfc register = cargo.getRegister();
        int countType = register.getTillCountFloatAtOpen();
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


        FinancialCountTenderItemIfc fcti =
          DomainGateway.getFactory().getFinancialCountTenderItemInstance();
        fcti.setDescription(
            DomainGateway.getFactory()
                         .getTenderTypeMapInstance()
                         .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));
        fcti.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        fcti.setSummaryDescription("");
        fcti.setSummary(true);
        fcti.setAmountIn(register.getTillFloatAmount());

        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
        td.setCountryCode(DomainGateway.getBaseCurrencyInstance().getCountryCode());
        td.setTenderSubType("");
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        fcti.setTenderDescriptor(td);

        FinancialTotalsIfc ft =  DomainGateway.getFactory().getFinancialTotalsInstance();
        FinancialCountIfc exp = (FinancialCountIfc)ft.getStartingFloatCount().getExpected();
        exp.addTenderItem(fcti);

        // set starting Float amount value to that of the TillFloatAmt parameter
        // if "TillCountFloatAtOpen is set to "No"
        if (setDefaultFloatAmount)
        {
            FinancialCountIfc ent = (FinancialCountIfc)ft.getStartingFloatCount().getEntered();
            ent.addTenderItem(fcti);
        }

        cargo.getTill().setTotals((FinancialTotalsIfc)ft);

        bus.mail(letter, BusIfc.CURRENT);
    }
}
