/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ModifyItemBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:03 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
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
 *    Rev 1.0   Aug 29 2003 16:11:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:52:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:12   msg
 * Initial revision.
 * 
 *    Rev 1.3   Jan 19 2002 10:30:58   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   13 Nov 2001 14:49:16   sfl
 * Make changes to let multiple items to be displayed after
 * they have been selected from Sell Item screen and brought
 * into ModifyItem service.
 * Resolution for POS SCR-282: Multiple Item Selection
 *
 *    Rev 1.0   Sep 21 2001 11:36:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// foundation imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

//--------------------------------------------------------------------------
/**
    This is the bean model that is used by the ModifyItemBean. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyItemBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        text field role name
    **/
    protected SaleReturnLineItemIfc lineItem = null;
    protected SaleReturnLineItemIfc[] lineItems = null;
    protected boolean itemHighlightFlag = true;


    //----------------------------------------------------------------------------
    /**
        ModifyItemBeanModel constructor comment.
    **/
    //----------------------------------------------------------------------------
    public ModifyItemBeanModel()
    {
            super();
    }

    //----------------------------------------------------------------------------
    /**
        Gets the line item property (java.lang.String) value.
        @return SaleReturnLineItemIfc contains the line item information.
    **/
    //----------------------------------------------------------------------------
    public SaleReturnLineItemIfc getLineItem()
    {
            return lineItem;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the line item property (java.lang.String) value.
        @param lineItem contains the line item information.
    **/
    //----------------------------------------------------------------------------
    public void setLineItem(SaleReturnLineItemIfc lineItem)
    {
            this.lineItem = lineItem;
    }
    //----------------------------------------------------------------------------
    /**
        Gets the line items.
        @return SaleReturnLineItemIfc[] contains the a list of line item information.
    **/
    //----------------------------------------------------------------------------
    public SaleReturnLineItemIfc[] getLineItems()
    {
            return lineItems;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the line item list.
        @param lineItems contains the list of line item information.
    **/
    //----------------------------------------------------------------------------
    public void setLineItems(SaleReturnLineItemIfc[] lineItems)
    {
            this.lineItems = lineItems;
    }

    //----------------------------------------------------------------------------
    /**
        Gets the item highlight flag.
        @return boolean to indicate if the item list displayed in Item Options screen needs a default highlight.
    **/
    //----------------------------------------------------------------------------
    public boolean getItemHighlightFlag()
    {
            return itemHighlightFlag;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the item highlight flag.
        @param flag  A boolean value tells if the items displayed in Item Options screen needs a default highlight.
    **/
    //----------------------------------------------------------------------------
    public void setItemHighlightFlag(boolean flag)
    {
            this.itemHighlightFlag = flag;
    }

    //----------------------------------------------------------------------------
        /**
                Method to default display string function. <P>
            @return String representation of object
        **/
   //---------------------------------------------------------------------
    public String toString()
    {
        // result string
         String strResult = new String("Class: ModifyItemBeanModel (Revision "
                + getRevisionNumber() + ")" + hashCode());

       // pass back result
         return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
