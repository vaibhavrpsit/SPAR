/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CheckRegisterStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    This site checks the status of the system to determine
    whether the preconditions for running the tillclose service are met.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CheckRegisterStatusSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "CheckStatusSite";

    //--------------------------------------------------------------------------
    /**
       Checks pre-conditions for till reconciling procedures.  If one of the
       pre-conditions are not met, the appropriate letter is issued, and
       the service exits to the TillOptions screen.<P>
       Here is how the status checks proceed: <P>
       <UL>
       <LI>If the register is not open, a RegisterClosed letter is issued.
       </UL>
       If all the checks are passed, a Continue letter is issued and operation
       proceeds to the EnterTill site.<P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Status flag
        boolean bOk = true;

        // Set default letter name
        String letterName = CommonLetterIfc.CONTINUE;

        // Local reference to cargo
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = (RegisterIfc) cargo.getRegister();

        // Local reference to login cashier
        EmployeeIfc cashier = cargo.getOperator();

        // Don't allow till close if the register is not open
        if (register.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            letterName = "RegisterClosedError";
            cargo.setErrorType(TillReconcileCargo.TILL_REGISTER_CLOSED_ERROR_TYPE);
            cargo.setTillFatalError();
            bOk = false;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
