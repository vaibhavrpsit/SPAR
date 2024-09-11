/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/EmployeeFindForUpdateTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:21:18 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
 *
 Revision 1.1  2004/04/15 16:36:59  tmorris
 @scr 4332 -Class created to replace overloaded instantiation(new) of EmployeeTransaction objects with Factory call.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

//------------------------------------------------------------------------------
/**
 The EmployeeFindForUpdateTransaction implements the Update employee lookup 
 **/
//------------------------------------------------------------------------------
public class EmployeeFindForUpdateTransaction extends EmployeeTransaction
{
    
    public EmployeeFindForUpdateTransaction()
    { //begin EmployeeFindForUpdateTransaction()
        super(findForUpdateName);
    } //end EmployeeFindForUpdateTransaction()

}
