/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ValidateEmployeeIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/06/25 17:16:54  rsachdeva
 *   @scr 4857 SalesAssociateValidation Parameter Added
 *
 *   Revision 1.8  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.7  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.6  2004/05/13 18:28:24  aachinfiev
 *   Fixed defect 4858. 
 *   User can now skip SalesAssociateID screen just by pressing enter. 
 *   Default employee will be created in that case.
 *
 *   Revision 1.5  2004/04/20 13:05:35  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:05:10   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:00:14   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// domain imports
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
    This site validates the employee ID.
    <p>
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
       Looks up the employee in the database. Sends a Success
       letter if found, sends a Failure letter if not found.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
        boolean validation = false;
        
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
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
      parameter and User wants to skip SalesAssociateID screen<P>
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
        EmployeeCargoIfc cargo = (EmployeeCargoIfc)bus.getCargo();
        String employeeID = cargo.getEmployeeID();
        boolean noError = true;
        if (validation)
        {
            try
            {
                EmployeeTransaction empTransaction = null;
                
                empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
                employee = empTransaction.getEmployee(employeeID);
                cargo.setEmployee(employee); 
            }
            catch (DataException de)
            {
                if (EmployeeEnteredRoad.DEFAULT_ID.equals(employeeID))
                {
                    // User wants to skip SalesAssociateID screen
                    // Create default employee.                
                    employee = DomainGateway.getFactory().getEmployeeInstance();
                    employee.setEmployeeID(EmployeeEnteredRoad.DEFAULT_ID);
                    employee.setPersonName(DomainGateway.getFactory().getPersonNameInstance());
                    employee.setRole(DomainGateway.getFactory().getRoleInstance());

                }
                else
                {
                    // log the error; set the error code in the cargo for future use.
                    logger.error(
                            "EmployeeID '" + employeeID + "' error: " + de.getMessage() + "\n\t Error code = " + Integer.toString(de.getErrorCode()) + "");
                    
                    cargo.setDataExceptionErrorCode(de.getErrorCode());
                    noError = false;
                }
            }
        }
        else
        {
            //No validation being done
            PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
            employee = DomainGateway.getFactory().getEmployeeInstance();
            employee.setEmployeeID(employeeID);
            name.setFirstName(employeeID);
            employee.setPersonName(name);
            cargo.setEmployee(employee); 
        }
        return noError;
    }
}
