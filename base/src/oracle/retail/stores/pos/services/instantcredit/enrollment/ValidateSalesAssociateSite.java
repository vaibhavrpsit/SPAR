/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/ValidateSalesAssociateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:15 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *Revision 1.7  2004/09/23 00:07:10  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.6  2004/06/03 14:47:43  epd
 *@scr 5368 Update to use of DataTransactionFactory
 *
 *Revision 1.5  2004/04/20 13:17:05  tmorris
 *@scr 4332 -Sorted imports
 *
 *Revision 1.4  2004/04/12 18:52:57  pkillick
 *@scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *Revision 1.3  2004/02/12 16:50:42  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 18 2003 17:19:12   nrao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
 *   Check the Sales Associate entered and send to proper screen<p>
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ValidateSalesAssociateSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2669804900899090091L;


    /**
     *   Site name. <P>
     */
    public static final String SITENAME = "ValidateSalesAssociateSite";

    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    // static string constants
    public static final String SALES_ASSOC_VALIDATION = "SalesAssociateValidation";
    public static final String YES = "Y";
    public static final String VALID = "Valid";
    public static final String INVALID_ASSOC_ID = "InvalidAssocId";

    //----------------------------------------------------------------------
    /**
     *   Checks the Sales Associate. <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String employeeId = cargo.getEmployeeID();
        
        boolean validation = false;
        // check sales associate validation param        
        try
        {
            String paramString = pm.getStringValue(SALES_ASSOC_VALIDATION);
            
            if (paramString.trim().equalsIgnoreCase(YES))
            {
                validation = true;
            } 
        }
        
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }
        
        if (employeeId.length() > 0)
        {
            // if parameter set to true, then lookup sales associate id
            if (validation)
            {
                /*
                 * Lookup the employee ID in the database
                 */
                try
                {
                    EmployeeTransaction empTransaction = null;
                    
                    empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
                    
                    EmployeeIfc salesAssociate = empTransaction.getEmployee(employeeId);
                    
                    // if valid, then mail letter.
                    bus.mail(new Letter(VALID), BusIfc.CURRENT);
                }
                catch (DataException de)
                {
                    // if invalid, then show dialog
                    UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, INVALID_ASSOC_ID);
                }
            }
            // if parameter is set to false, then move to the next screen.
            else
            {
                bus.mail(new Letter(VALID), BusIfc.CURRENT);
            }
        }
    }
}
