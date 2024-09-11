/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/StartCountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sbeesnal  02/20/09 - Ignoring case when comparing with Cash FLP Tender
 *                         while getting pickup & loan letter name.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:22 PM  Robert Pearse
 *
 *   Revision 1.5  2004/06/29 17:05:38  cdb
 *   @scr 4205 Removed merging of money orders into checks.
 *   Added ability to count money orders at till reconcile.
 *
 *   Revision 1.4  2004/06/22 00:13:24  cdb
 *   @scr 4205 Updated to merge money orders into checks during till reconcile.
 *
 *   Revision 1.3  2004/02/12 16:49:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 04 2004 18:39:08   DCobb
 * Added SUMMARY_COUNT_PICKUP and SUMMARY_COUNT_LOAN screens.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Aug 29 2003 15:56:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:30:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:50   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:28   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Dec 2001 13:02:48   epd
 * Added code to allow for counting Store Safe
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:16:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
     Test the contents of the cargo the determine the next step.<p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class StartCountSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Tests the contents of the cargo to determine the next step.<p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String lname = null;
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();

        FinancialTotalsIfc ft = cargo.getFinancialTotals();

        // Clone financial totals if necessary
        if (ft == null)
        {
            TillIfc till = cargo.getRegister().getTillByID(cargo.getTillID());
            if (till != null)
            {
                ft = (FinancialTotalsIfc)till.getTotals().clone();
            }
            else
            {
                ft = DomainGateway.getFactory().getFinancialTotalsInstance();
            }
            cargo.setFinancialTotals(ft);
        }

        FinancialCountTenderItemIfc[] tendersArray = ft.getCombinedCount().getExpected()
            .getSummaryTenderItems();
        ArrayList list = new ArrayList();
        if (tendersArray != null)
        {
            list = new ArrayList(Arrays.asList(tendersArray));
        }

        // Determine the next step, depending on values in cargo.
        int countType = cargo.getCountType();
        if (countType == PosCountCargo.TILL)
        {
            if (cargo.getCurrentActivity().equals(PosCountCargo.CHARGE))
            {
                lname = "CountCharge";
            }
            else
            {
                lname = "CountTender";
            }
        }
        else if ((countType == PosCountCargo.LOAN) ||
                 (countType == PosCountCargo.PICKUP))
        {
            cargo.setCurrentActivity(PosCountCargo.COUNT_TYPE_DESCRIPTORS[countType]);

            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            boolean operateWithSafe = true;
            try
            {
                operateWithSafe = pm.getBooleanValue("OperateWithSafe").booleanValue();
            }
            catch(ParameterException pe)
            {
                logger.error( "Could not retrieve setting for OperateWithSafe Parameter");
            }

            lname = getPickupAndLoanLetterName(cargo, operateWithSafe);
        }
        else
        {
            cargo.setCurrentActivity(PosCountCargo.COUNT_TYPE_DESCRIPTORS[countType]);
            if (cargo.getSummaryFlag())
            {
                lname = "CountSummary";
            }
            else
            {
                lname = "CountDetail";
            }
        }

        bus.mail(new Letter(lname), BusIfc.CURRENT);
    }


    //----------------------------------------------------------------------
    /**
        Determines the letter for Pickup and Loan according to the
        count detail level and the operate with safe flag.
        @param cargo
        @param operate with safe flag
        @return the letter name
    **/
    //----------------------------------------------------------------------
    protected String getPickupAndLoanLetterName(PosCountCargo cargo, boolean operateWithSafe)
    {
        // set default count detail level
        String lname = "CountSummary";

        int countType = cargo.getCountType();
        if (countType == PosCountCargo.PICKUP)
        {
            boolean cash = "Cash".equalsIgnoreCase(cargo.getCurrentFLPTender());
            int detailLevel = cargo.getPickupCountDetailLevel();

            switch (detailLevel)
            {
            case FinancialCountIfc.COUNT_TYPE_SUMMARY:
                lname = "CountSummary";
                if (cash && !operateWithSafe)
                {
                    lname = "PickupCashSummary";
                }
                break;
            case FinancialCountIfc.COUNT_TYPE_DETAIL:
                lname = "CountDetail";
                if (cash && !operateWithSafe)
                {
                    lname = "PickupCashDetail";
                }
                break;
            case FinancialCountIfc.COUNT_TYPE_NONE:
                // pickup checks only
                lname = "CountNo";
                break;
            default:
                break;
            }
        }
        else if (countType == PosCountCargo.LOAN)
        {
            if (cargo.getSummaryFlag())
            {
                if (operateWithSafe)
                {
                    lname = "CountSummary";
                }
                else
                {
                    lname = "LoanCashSummary";
                }
            }
            else
            {
                if (operateWithSafe)
                {
                    lname = "CountDetail";
                }
                else
                {
                    lname = "LoanCashDetail";
                }
            }
        }

        return lname;
    }

}
