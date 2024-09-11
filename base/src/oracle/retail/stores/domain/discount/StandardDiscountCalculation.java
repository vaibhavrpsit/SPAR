/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/StandardDiscountCalculation.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:22 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:50:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:58:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:18:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:12:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:36:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;
// Domain imports

//------------------------------------------------------------------------------ 
/**
    This is the class for standard discount calculations. <P>
    @see oracle.retail.stores.domain.discount.DiscountCalculationIfc
    @see oracle.retail.stores.domain.discount.AbstractDiscountCalculation
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------ 
public class StandardDiscountCalculation
extends AbstractDiscountCalculation
implements DiscountCalculationIfc
{                                       // begin class StandardDiscountCalculation
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3425790451872068129L;

                         
    /**
        revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


}                                      // end class StandardDiscountCalculation

