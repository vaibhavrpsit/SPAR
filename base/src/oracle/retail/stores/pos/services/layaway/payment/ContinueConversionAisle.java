/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/ContinueConversionAisle.java /main/11 2012/02/21 14:35:49 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/21/12 - XbranchMerge asinton_bug-13719807 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   02/21/12 - deprecated this aisle
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

// java imports
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


 
//--------------------------------------------------------------------------
/**
    Changes letter from complete to ExitPayment 
    @deprecated As of 13.4.1, do not use this Aisle.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ContinueConversionAisle extends PosLaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
      Changes letter from Complete to ExitPayment.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
         bus.mail(new Letter("ExitLayaway"), BusIfc.CURRENT);        
    }
}
