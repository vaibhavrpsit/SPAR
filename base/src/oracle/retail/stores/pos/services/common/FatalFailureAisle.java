/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/FatalFailureAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:01 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/14 22:43:59  cdb
 *   @scr 5318 Updated such that failure writing to hard totals causes
 *   the application to exit. Main Functional Requirements v2 section 2.3 # 40.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
   The user has acknowledged a fatal failure

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class FatalFailureAisle extends PosLaneActionAdapter
{
    public static final String LANENAME = "FatalFailureAisle";

    //--------------------------------------------------------------------------
    /**
       Exit the application

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        System.exit(0);
    }

}
