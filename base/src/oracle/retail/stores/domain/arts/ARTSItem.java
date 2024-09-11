/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

// domain imports
import oracle.retail.stores.domain.stock.PLUItemIfc;

//--------------------------------------------------------------------------
/**
    Container class for item information.
 **/
//--------------------------------------------------------------------------
public class ARTSItem
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The PLUItem
    **/
    protected PLUItemIfc pluItem;

    /**
        The item number
    **/
    protected String itemID;

    /**
        The type of item
    **/
    protected String itemType;

    //----------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //----------------------------------------------------------------------
    public ARTSItem()
    {
    }

    //----------------------------------------------------------------------
    /**
        Returns the PLU Item.
        <p>
        @return the PLU Item.
    **/
    //----------------------------------------------------------------------
    public PLUItemIfc getPLUItem()
    {
        return(pluItem);
    }

    //----------------------------------------------------------------------
    /**
        Sets the PLU Item.
        <p>
        @param  value   the PLU Item.
    **/
    //----------------------------------------------------------------------
    public void setPLUItem(PLUItemIfc value)
    {
        pluItem = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the item ID.
        <p>
        @return the item ID.
    **/
    //----------------------------------------------------------------------
    public String getItemID()
    {
        return(itemID);
    }

    //----------------------------------------------------------------------
    /**
        Sets the item ID.
        <p>
        @param  value   the Item ID.
    **/
    //----------------------------------------------------------------------
    public void setItemID(String value)
    {
        itemID = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the item type.
        <p>
        @return the item type.
    **/
    //----------------------------------------------------------------------
    public String getItemType()
    {
        return(itemType);
    }

    //----------------------------------------------------------------------
    /**
        Sets the item type.
        <p>
        @param  value   the Item type.
    **/
    //----------------------------------------------------------------------
    public void setItemType(String value)
    {
        itemType = value;
    }
}
