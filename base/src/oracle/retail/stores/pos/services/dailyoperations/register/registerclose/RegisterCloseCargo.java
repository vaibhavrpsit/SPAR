/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registerclose/RegisterCloseCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:25:28 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/25/2006 4:11:42 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/10/2005 11:26:28    Jason L. DeLeau 5783:
 *         Fix issues with closing the device for unleashed.
 *    3    360Commerce1.2         3/31/2005 15:29:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:38     Robert Pearse
 *
 *   Revision 1.4  2004/09/27 22:32:05  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:49:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 23 2003 06:50:04   jgs
 * Modified to delay end transaction journal entry.
 * Resolution for 2543: Modify EJournal to put entries into a JMS Queue on the store server.
 *
 *    Rev 1.0   Apr 29 2002 15:29:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:15:00   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:40   msg
 * Initial revision.
 *
 *    Rev 1.1   14 Nov 2001 11:51:28   epd
 * Added Security Access code and flow
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:17:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registerclose;

import oracle.retail.stores.pos.services.common.StoreStatusCargo;
import oracle.retail.stores.pos.services.common.StoreStatusCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;

//------------------------------------------------------------------------------
/**
 * This cargo holds the information necessary to Daily Operations service.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
// ------------------------------------------------------------------------------
public class RegisterCloseCargo extends StoreStatusCargo implements StoreStatusCargoIfc
{ // begin class RegisterCloseCargo
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Employee attempting Access
     **/
    protected EmployeeIfc accessEmployee;

    /**
     * tillClose Register object clone in case user cancelled
     **/
    protected RegisterIfc tillCloseRegisterClone = null;

    /**
     * Contains transaction info; saved for journaling.
     **/
    protected TransactionIfc transaction = null;

    // ----------------------------------------------------------------------
    /**
     * Sets the access employee.
     * 
     * @param value EmployeeIfc
     **/
    // ----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the access employee.
     * 
     * @return the EmployeeIfc value
     **/
    // ----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the tillclose Register.
     * <P>
     * 
     * @return The tillclose Register.
     **/
    // ----------------------------------------------------------------------
    public RegisterIfc getTillCloseRegister()
    {
        return tillCloseRegisterClone;
    }

    // ----------------------------------------------------------------------
    /**
     * Set the tillclose Register.
     * <P>
     * 
     * @param value The tillclose Register.
     **/
    // ----------------------------------------------------------------------
    public void setTillCloseRegister(RegisterIfc tillCloseReg)
    {
        tillCloseRegisterClone = tillCloseReg;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the function ID whose access is to be checked.
     * 
     * @return int function ID
     **/
    // ----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.CLOSE_REGISTER;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the transaction.
     * 
     * @return TransactionIfc
     */
    // ----------------------------------------------------------------------
    public TransactionIfc getTransaction()
    {
        return transaction;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the transaction.
     * 
     * @param transaction The transaction to set
     */
    // ----------------------------------------------------------------------
    public void setTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     * 
     * @return String representation of object
     **/
    // ----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult = new String("Class:  RegisterCloseCargo (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        strResult += "\n" + abstractToString();
        // pass back result
        return (strResult);
    } // end toString()

    // ----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @return String representation of revision number
     **/
    // ----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

} // end class RegisterCloseCargo
