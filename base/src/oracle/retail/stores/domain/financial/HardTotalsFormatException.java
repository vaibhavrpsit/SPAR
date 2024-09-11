/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotalsFormatException.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:17 PM  Robert Pearse   
 *
 *   Revision 1.3.2.1  2004/11/30 16:05:48  kll
 *   @scr 7777: public scope for constructor
 *
 *   Revision 1.3  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.2  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:52:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:00:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:21:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:14:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:37:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

//------------------------------------------------------------------------------
/**
    A class will throw this exception when encounters an error in formatting
    hard totals information.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class HardTotalsFormatException extends java.lang.Exception
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 904069395593085339L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

        //---------------------------------------------------------------------
        /**
                Constructs the object. <P>
                @param String description
        **/
        //---------------------------------------------------------------------
    public HardTotalsFormatException(String desc)
    {
        super(desc);
    }
}
