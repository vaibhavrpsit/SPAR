/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/SecurityCargo.java /main/12 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:32  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 19 2003 16:17:46   crain
 * Remove references to code list map
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.1   Jan 13 2003 08:25:36   pjf
 * Make SecurityCargo implement DBErrorCargoIfc, deprecate codeListMap accessors, remove AbstractFinancialCargo reference from LookupCodeListMapSite.
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:37:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Jan 2002 17:23:26   baa
 * set operator
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:10:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;

/**
 * The SecurityCargo holds data for the security service.
 * 
 * @version $Revision: /main/12 $
 */
public class SecurityCargo extends UserAccessCargo implements DBErrorCargoIfc, TourCamIfc
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * The user's login ID
     */
    protected String employeeID;

    /**
     * The financial data for the store
     */
    protected StoreStatusIfc storeStatus;

    /**
     * The register at which operations are being performed
     */
    protected RegisterIfc register = null;

    /**
     * data exception error code
     */
    protected int dataExceptionErrorCode;

    /**
     * Returns the user's login identifier.
     * 
     * @return The user's login identifier.
     */
    public String getEmployeeID()
    {
        return employeeID;
    }

    /**
     * Sets the user's login identifier.
     * 
     * @param value The user's login identifier.
     */
    public void setEmployeeID(String value)
    {
        employeeID = value;
    }

    /**
     * Returns the store status.
     * 
     * @return The store status.
     */
    public StoreStatusIfc getStoreStatus()
    {
        return storeStatus;
    }

    /**
     * Sets the store status.
     * 
     * @param value The store status.
     */
    public void setStoreStatus(StoreStatusIfc value)
    {
        storeStatus = value;
    }

    /**
     * Returns the register at which operations are being performed.
     * 
     * @return RegisterIfc object
     */
    public RegisterIfc getRegister()
    {
        return (register);
    }

    /**
     * Sets the register at which operations are to be performed.
     * 
     * @param register The register where operations are being performed.
     */
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     * 
     * @return an object which stores the current state of the cargo.
     * @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
     */
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    /**
     * Reset the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     * 
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
        SecurityCargo savedCargo = (SecurityCargo) snapshot.restoreObject();
        employeeID = savedCargo.getEmployeeID();
        register = savedCargo.getRegister();
    }

    /**
     * Returns the data exception error code.
     * 
     * @return The data exception error code.
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the data exception error code.
     * 
     * @param value The data exception error code.
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  SecurityCargo (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
