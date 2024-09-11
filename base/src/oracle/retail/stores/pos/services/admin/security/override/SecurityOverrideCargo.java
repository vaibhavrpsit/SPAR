/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/override/SecurityOverrideCargo.java /main/12 2013/11/19 09:42:41 cgreene Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:53 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:25:07 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:14:07 PM  Robert Pearse   
 *
 *Revision 1.4  2004/06/25 22:54:54  cdb
 *@scr 1642 Updated so that Undo selected from Operator ID screen returns
 *to the calling service rather than prompting for another security override.
 *
 *Revision 1.3  2004/02/12 16:49:03  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:37:44  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:21:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Oct 2001 17:00:34   pdd
 * Added SCR association.
 * Resolution for POS SCR-219: Add Tender Limit Override
 * 
 *    Rev 1.0   22 Oct 2001 15:04:30   pdd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.override;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

/**
 * Security Override service cargo.
 *
 * @version $Revision: /main/12 $
 */
public class SecurityOverrideCargo extends UserAccessCargo
{
    // revision number of this class
    public static final String revisionNumber = "$Revision: /main/12 $";

    // Last operator
    protected EmployeeIfc lastOperator = null;
    // Indicates if undo was selected in OperatorID Service
    protected boolean undoSelected = false;

    /**
     * Sets the last operator.
     * 
     * @param value EmployeeIfc
     */
    public void setLastOperator(EmployeeIfc value)
    {
        lastOperator = value;
    }

    /**
     * Returns the last operator.
     * 
     * @return EmployeeIfc value
     */
    public EmployeeIfc getLastOperator()
    {
        return lastOperator;
    }

    /**
     * Sets if undo was selected in OperatorID Service.
     * 
     * @param value
     */
    public void setUndoSelected(boolean value)
    {
        undoSelected = value;
    }

    /**
     * Returns if undo was selected in OperatorID Service.
     * 
     * @return
     */
    public boolean isUndoSelected()
    {
        return undoSelected;
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    /**
     * Overridden to provide context ID from {@link #lastOperator}.
     *
     * @see oracle.retail.stores.commerceservices.logging.MappableContextIfc#getContextValue()
     */
    @Override
    public Object getContextValue()
    {
        if (lastOperator != null)
        {
            return lastOperator.getContextValue();
        }
        return getClass().getSimpleName() + "@" + hashCode();
    }
}
