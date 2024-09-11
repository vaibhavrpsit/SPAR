/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftReceiptLookupBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:16 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/05/25 18:42:35  mweis
 *   @scr 4882 Returns' Gift Receipt 'description' field needs to be required
 *
 *   Revision 1.4  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:48:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:30:30   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 10 2001 17:35:56   blj
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

//----------------------------------------------------------------------------
/** This bean model is used by GifReceiptLookup bean.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class GiftReceiptLookupBeanModel extends POSBaseBeanModel
{
    /** 
        Indicates whether to clear the fields, default true.
    **/
    private boolean clearUIFields = true;
    /**
        revision number
    **/    
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
      * item number
    */
    String itemNumber = null;
    /**
      * price code field
    */
    String priceCodeField   = null;
    
    /**
     * item description
     */
    String description = null;
    
   //--------------------------------------------------------------------------
   /**
       Returns the items number.
       @return String itemNumber.
   **/
   //--------------------------------------------------------------------------
   public String getItemNumber() {
       return itemNumber;
   }
    //----------------------------------------------------------------------------
    /**
        Get the value of the PriceCode field.
        @return the value of PriceCode
    **/
    //----------------------------------------------------------------------------
    public String getPriceCode()
    {
        return priceCodeField;
    }

   //--------------------------------------------------------------------------
   /**
       Sets the item number value.
       @param value The new value for the property.
    **/
   //--------------------------------------------------------------------------
   public void setItemNumber(String value) {
      itemNumber = value;
   }
   //--------------------------------------------------------------------------
    /**
        Sets the PriceCode field
        @param priceCode the value to be set for priceCode
    **/
    //----------------------------------------------------------------------------
    public void setPriceCode(String priceCode)
    {
        priceCodeField = priceCode;
    }
    /**
        Set clearUIFields flag to determine whether to clear the date fields. <P>
        @param value Whether to clear the data fields
    **/
    //--------------------------------------------------------------------- 
    public void setclearUIFields(boolean value)
    {                                  // begin setclearUIFields()
        clearUIFields = value;
    }                                  // end setclearUIFields()

    //---------------------------------------------------------------------
    /**
        Returns the current valud of clearUIFields.
        @return value of clearUIFields flag.
    **/
    //--------------------------------------------------------------------- 
    public boolean getclearUIFields()
    {                                  // begin getclearUIFields()
        return(clearUIFields);
    }                                  // end getclearUIFields()                                  // end getEmailDetail()
    
    //----------------------------------------------------------------------------
    /**
        Returns a string representing the data in this Object
        @return a string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: GiftReceiptLookupBeanModel Revision: " + revisionNumber + "\n");
        buff.append("Item Number [" + itemNumber + "]\n");
        buff.append("Description [" + description + "]\n");
        buff.append("PriceCode [" + priceCodeField + "]\n");

        return(buff.toString());
    }
    /**
     * Get Item description
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets item description
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
} ///:~ end class GiftReceiptLookupBeanModel
