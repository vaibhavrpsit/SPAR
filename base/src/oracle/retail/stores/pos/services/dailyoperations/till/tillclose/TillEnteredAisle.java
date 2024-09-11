/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/TillEnteredAisle.java /main/11 2013/08/05 17:25:17 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   08/01/13 - Making the till id to uppercase to have consistency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/4/2007 11:03:57 PM  Robinson Joseph
 *         Changed code to trim the input till id.
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/07/01 15:57:38  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Code cleanup.
 *
 *   Revision 1.9  2004/06/24 20:08:10  cdb
 *   @scr 3980 Till must be open on the register on which it is closed,
 *   regardless of where it was opened.
 *
 *   Revision 1.8  2004/06/24 18:29:02  cdb
 *   @scr 3980 Added check to make sure till on another register is suspended.
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Mar 10 2003 17:10:08   DCobb
 * Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.3   Mar 07 2003 10:34:32   DCobb
 * Code cleanup from code review.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.2   Feb 17 2003 15:43:28   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Dec 20 2002 11:32:06   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.0   Apr 29 2002 15:29:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:29:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:18:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
     Validates the entered till ID. If the entered till ID is not valid, the
     appropriate letter is issued, and the service re-prompts the operator for
     the till ID or exits.<P>
     Here is how the till ID validation proceeds: <P>
     <UL>
     <LI>If the till ID is greater than 5 alphanumeric characters, a TillIDError
     letter is issued.
     <LI>If the the till associated with this till ID is not open, a
     TillClosedError letter is issued.
     <LI>If the till associated with this till ID is not the cashier's till,
     a CashierError letter is issued.
     </UL>
     If all the checks are passed, the till is set to this current till and the
     till's id is set to this till ID. A Continue letter is issued and the system
     proceeds to the OpenDrawer site.
    <P>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class TillEnteredAisle extends PosLaneActionAdapter
{                                                                               // begin class TillEnteredAisle
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
       lane name constant
    **/
    public static final String LANENAME = "TillEnteredAisle";

    //--------------------------------------------------------------------------
    /**
       Ensures the the entered till ID is valid, this till is open, and
       the cashier is assigned to this till, then then proceeds by issuing the
       Continue letter.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        String letterName = CommonLetterIfc.CONTINUE;

        // initialize till
        TillIfc t = DomainGateway.getFactory().getTillInstance();

        boolean bOk = true;

        // Local reference to till cargo
        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = (RegisterIfc) cargo.getRegister().clone();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Get Input (till id) from UI
        String tillID = ui.getInput().trim().toUpperCase();

        // Is this a valid till ID
        if (t.isIdValid(tillID) != true)
        {
            letterName = TillLetterIfc.TILL_ERROR;
            cargo.setErrorType(TillCargo.TILL_ID_INVALID_ERROR_TYPE);
            bOk = false;
        }

        // Local reference to entered till (if found)
        TillIfc till = null;
        if (bOk)
        {    
            till = register.getTillByID(tillID);
        }

        // Check if the till is suspended from another register
        // We don't care if suspended on another register to close because it must
        // be in this register in order to be closed regardless of where it was opened.

        // Only open tills can be closed
        if (bOk &&
            (till != null) &&
            (till.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN))
        {
            letterName = TillLetterIfc.TILL_ERROR;
            cargo.setErrorType(TillCargo.TILL_CLOSED_ERROR_TYPE);
            cargo.setTillFatalError();   // fatal error like cancel
            bOk = false;
        }

        // Is there a till with this till ID?
        if (bOk)
        {
            if (till == null)
            {
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_ID_INVALID_ERROR_TYPE);
                bOk = false;
            }
        }
    
        // If cashier accountability, check if the entered till was opened 
        // by the login operator
        if (bOk 
            && till.getRegisterAccountability() == AbstractStatusEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            if (!cargo.getOperator().getEmployeeID().equals(till.getSignOnOperator().getEmployeeID()))
            {    
                letterName = TillLetterIfc.TILL_ERROR;
                cargo.setErrorType(TillCargo.TILL_CASHIER_ERROR_TYPE);
                bOk = false;
            }
        }


        // If everything ok, set till and tillID in cargo
        if (bOk)
        {
            cargo.setTill(till);
            cargo.setTillID(tillID);
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
                                 // end getRevisionNumber()
}
