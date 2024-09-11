/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.1		Feb 16, 2017		Nadia Arora		fix : In ADV search, search the item with item desc 
 	and if we click on item detail application comming to the main screen
 *
 ********************************************************************************/
package max.retail.stores.pos.ui.beans;

import java.util.Locale;


// Java imports
import javax.swing.JLabel;

import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AbstractListRenderer;
//-------------------------------------------------------------------------
/**
   This is the renderer for the SaleReturn Table with MRP.  It displays
   SaleReturnLineItems with the MRP and makes them look like it is a table. <P>
  $Revision: /rgbustores_12.0.9in_branch/2 $
*/
//----------------------------------------------------------------------------
// Chnages starts for code merging(commenting below line)
//public class MAXItemListRendererWithMRP extends ItemListRendererWithMRP
// Changes ends for code merging
public class MAXItemListRendererWithMRP extends AbstractListRenderer
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/2 $";

    public static int DESCRIPTION = 0;
    public static int ITEM_ID    = 1;
  //  public static int DEPARTMENT    = 2;
   // public static int MAXIMUM_RETAIL_PRICE    =3;
   // public static int PRICE       = 4;

    //public static int MAX_FIELDS    = 5;

   // public static int[] ITEM_WEIGHTS = {55,20,22,18,16};
    public static int MAXIMUM_RETAIL_PRICE = 2;
    public static int PRICE = 3;
    public static int MAX_FIELDS = 4;
  // Changes starts for code merging(added below variables as it is not present in base 14)
    /** the ext_price column */
    public static int EXT_PRICE   = 5;
    /** the tax column */
    public static int TAX         = 6;
  // Changes ends for code merging
    public static int ITEM_WEIGHTS[] = {
        77, 20, 18, 16
    };
    
    // Changes starts for code merging(below variable added as it is not present in base 14)
    protected String giftCardLabel = "Gift Card ID:";
    protected String serialLabel = "Serial #";
    protected String giftRegLabel = "GiftReg.#";
    protected String salesAssocLabel = "Sales Assoc:";
    protected String sendLabel = "Send";
    protected String giftReceiptLabel = "Gift Receipt";
    // Changes ends for code merging
// Chnages starts for code merging(below line added as per MAX)
    Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
 // Chnage ends for code merging

    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public MAXItemListRendererWithMRP()
    {
        super();
        setName("ItemListRendererWithMRP");

        firstLineWeights = ITEM_WEIGHTS;
        fieldCount = MAX_FIELDS;
        lineBreak  = MAX_FIELDS;
        initialize();
    }

     //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {

        labels[ITEM_ID].setHorizontalAlignment(JLabel.CENTER);
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
   // Changes starts for cod emerging(commenting below line as DEPARTMENT is not declared above)
        //labels[DEPARTMENT].setHorizontalAlignment(JLabel.CENTER);
   // Chnages ends for code merging
        labels[MAXIMUM_RETAIL_PRICE].setHorizontalAlignment(JLabel.CENTER);
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);
    }
    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
    	/* Changes for Rev 1.1 starts*/
    	SaleReturnLineItemIfc lineItem = null;
    	PLUItemIfc item =  null;
    	if(value instanceof SaleReturnLineItemIfc)
        {
    		lineItem = (SaleReturnLineItemIfc)value;
    		item = lineItem.getPLUItem();
        }
    	else if(value instanceof PLUItem)
    	{
    		item = (PLUItem) value;
    	}
    	/* Changes for Rev 1.1 ends*/
       labels[ITEM_ID].setText(item.getItemID());
  // Chnages starts for code merging(commenting below line)
       //labels[DESCRIPTION].setText(item.getDescription());
       labels[DESCRIPTION].setText(item.getDescription(locale));
  // Chnages ends for code merging
  // Changes starts for cod emerging(commenting below line as DEPARTMENT is not declared above)
       //labels[DEPARTMENT].setText(item.getDepartment().getDescription());
  // Changes ends for code merging  
       labels[MAXIMUM_RETAIL_PRICE].setText(((MAXPLUItemIfc) item).getMaximumRetailPrice().toGroupFormattedString(getLocale()));
       labels[PRICE].setText(item.getPermanentPrice(DomainGateway.getFactory().getEYSDateInstance()).getStringValue());
   }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
   // Changes starts for code merging(commenting below line)
        //PLUItemIfc item = DomainGateway.getFactory().getPLUItemInstance();
    	MAXPLUItemIfc item = (MAXPLUItemIfc) DomainGateway.getFactory().getPLUItemInstance();
    // Chnages ends for code merging
        item.setItemID("12345");
        item.setPrice(DomainGateway.getBaseCurrencyInstance());
        item.setMaximumRetailPrice(DomainGateway.getBaseCurrencyInstance());
        item.setDepartmentID("dept");
 // Changes starts for cod emerging(commenting below line)
        //item.setDescription("test item");
        item.setDescription(locale, "test item");
     // Changes ends for cod emerging(commenting below line)       

        return(item);
    }


    //---------------------------------------------------------------------
    /**
       main entrypoint - starts the part when it is run as an application
       @param args String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();
// Chnages start for cod emerging(commenting below line)
        //ItemListRendererWithMRP bean = new ItemListRendererWithMRP();
        MAXItemListRendererWithMRP bean = new MAXItemListRendererWithMRP();
 // chnages ends for code merging
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
    //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields()  { }
}
