/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/CheckStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *    4    360Commerce 1.3         8/16/2007 5:08:43 PM   Ashok.Mondal    CR
 *         19548 : Merge from V7x to trunk.
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.4  2004/03/03 23:15:08  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:57  mcs
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
 *    Rev 1.0   Aug 29 2003 15:57:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:28:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.TillCargo;

//------------------------------------------------------------------------------
/**
    This site checks the status of the system to determine
    whether the preconditions for running the tillclose service are met.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CheckStatusSite extends PosSiteActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Checks pre-conditions for till closing procedures.  If one of the
       pre-conditions are not met, the appropriate letter is issued, and
       the service exits to the TillOperations screen.<P>
       Here is how the status checks proceed: <P>
       <UL>
       <LI>If the register is not open, a TillError letter is issued, the
       error dialog is displayed and the service exits when the operator 
       acknowledges the error.
       <LI>If there are not tills open in this register, a TillError letter
       is issued, the error dialog is displayed and the service exits when 
       the operator acknowledges the error.
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
        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();

        // Local reference to register
        RegisterIfc register = (RegisterIfc) cargo.getRegister().clone();

        // Local reference to login cashier
        EmployeeIfc cashier = cargo.getOperator();

        // Don't allow till close if the register is not open
        if (register.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            letterName = TillLetterIfc.TILL_ERROR;
            cargo.setErrorType(TillCargo.TILL_REGISTER_CLOSED_ERROR_TYPE);
            cargo.setTillFatalError();
            bOk = false;
        }

        // Check if there is ANY till open in this register
        if(bOk == true && (register.getCurrentTillID()).equals(TillLetterIfc.NO_TILL_OPEN))
        {
            letterName = TillLetterIfc.TILL_ERROR;
            cargo.setErrorType(TillCargo.TILL_NONE_OPEN_ERROR_TYPE);
            cargo.setTillFatalError();
            bOk = false;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
