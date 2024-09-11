/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/CheckOfflineSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:57 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:49:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

//--------------------------------------------------------------------------
/**
    This site makes sure the data manager is online and that the Persistent
    Queue(s) is empty.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated As of release 4.5.0, replaced by {@oracle.retail.stores.pos.services.common.CheckOfflineSite}
**/
//--------------------------------------------------------------------------
public class CheckOfflineSite extends oracle.retail.stores.pos.services.common.CheckOfflineSite
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

}
