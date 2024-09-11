/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftPriceBeanModel.java /main/13 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/22/2006 11:45:25 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:48:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:26   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * 
 *    Rev 1.0   Mar 18 2002 11:55:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   Feb 05 2002 16:43:46   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.1   Jan 19 2002 10:30:28   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Oct 29 2001 11:43:04   blj
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:36:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.foundation.utility.Util;
import java.math.BigDecimal;

//----------------------------------------------------------------------------
/**
 *  This class is the data model for ItemInfoBean.
 *     @version $Revision: /main/13 $
 */
//----------------------------------------------------------------------------
public class GiftPriceBeanModel extends POSBaseBeanModel
{
    /**
     *   Revision Number furnished by TeamConnection.
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    public static final String EACH = "Each";
    /**
     *   BigDecimal price of item.
    **/
    protected BigDecimal price = BigDecimal.ZERO;
    //------------------------------------------------------------------------
    /**
     *  Gets the item price.
     *  @return BigDecimal the item price
     */
    //------------------------------------------------------------------------
    public BigDecimal getPrice()
    {
        return price;
    }
    //------------------------------------------------------------------------
    /**
     *  Sets the item price.
     *  @param price the item price
     */
    //------------------------------------------------------------------------
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }
}
