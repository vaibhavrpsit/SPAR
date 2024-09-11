/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/salesassociate/ValidateEmployeeIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    4    360Commerce 1.3         3/25/2008 4:05:49 AM   Vikram Gopinath CD
 *         #29942, ported changes from v12x. Fetch the employee information
 *         inspite of the validation parameter.
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/21 19:27:00  rsachdeva
 *   @scr  3906 Comment Added
 *
 *   Revision 1.4  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.3  2004/04/13 01:34:56  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.2  2004/03/29 21:49:56  rsachdeva
 *   @scr 3906 Sales Associate
 *
 *   Revision 1.1  2004/03/29 21:36:02  rsachdeva
 *   @scr 3906 Sales Associate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.salesassociate;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site validates the employee ID with SalesAssociateValidation
    parameter <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ValidateEmployeeIDSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
       sales associate validation parameter
    **/
    public static final String SALES_ASSOC_VALIDATION = "SalesAssociateValidation";
    /** 
       yes
    **/
    public static final String YES = "Y";

    //----------------------------------------------------------------------
    /**
       Looks up the sales associate in the database and sets on cargo
       Validation is done as per SalesAssociateValidation Parameter<P>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
        boolean validation = false;
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
            logger.warn(pe.getStackTraceAsString());
        }
        if (!setSalesAssociateInCargo(validation, bus))
        {
            letter = new Letter(CommonLetterIfc.FAILURE);
        } 
        bus.mail(letter, BusIfc.CURRENT);
    }

    //---------------------------------------------------------------------
    /**
       Sets Sales Associate in cargo depending on SalesAssociateValidation 
       parameter<P>
       @param validation true if validation is required, otherwise false
       @param  bus Service Bus
       @return boolean true if Sales Associate is set in the cargo
     **/
    //---------------------------------------------------------------------
    public boolean setSalesAssociateInCargo(boolean validation,
                                            BusIfc bus)
    {
        EmployeeIfc employee = null;
        LetterIfc letter = null;
        ModifyTransactionSalesAssociateCargo cargo = (ModifyTransactionSalesAssociateCargo) bus.getCargo();
        String employeeID = cargo.getEmployeeID();
        boolean noError = true;

            try
            {
                EmployeeTransaction empTransaction = null;
                
                empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
                employee = empTransaction.getEmployee(employeeID);
                cargo.setEmployee(employee); 
            }
            catch (DataException de)
            {
            	 if (!validation)
            	 {
                     //No validation being done
                     PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
                     employee = DomainGateway.getFactory().getEmployeeInstance();
                     employee.setEmployeeID(employeeID);
                     name.setFirstName(employeeID);
                     employee.setPersonName(name);
                     cargo.setEmployee(employee); 
            	 }
            	 else
            	 {
                     cargo.setDataExceptionErrorCode(de.getErrorCode());
                     noError = false;
            	 }
            }

        return noError;
    }
}
