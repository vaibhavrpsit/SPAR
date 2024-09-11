/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/queue/QueueCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:29 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse   
 *
 *Revision 1.3  2004/02/12 16:48:52  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:35:20  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Oct 02 2003 10:06:34   bwf
 * Removed deprecation because flow has been reinstated.  Also removed unused imports.
 * 
 *    Rev 1.1   Sep 25 2003 12:25:18   bwf
 * Deprecated.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 * 
 *    Rev 1.0   Aug 29 2003 15:53:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   19 Nov 2001 12:18:32   pdd
 * Added security override.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.queue;

// Domain imports
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;

//--------------------------------------------------------------------------
/**
    Cargo that carries the data for the queue service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class QueueCargo extends UserAccessCargo
{        
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Returns the appropriate function ID.
        @return int RoleFunctionIfc.QUEUE
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.QUEUE;
    }
    
}
