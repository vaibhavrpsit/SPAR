/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/12/2007 8:48:22 PM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    4    360Commerce 1.3         5/8/2007 11:32:26 AM   Anda D. Cadar
 *         currency changes for I18N
 *    3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:30 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/13 20:45:44  mweis
 *   @scr 6057 (Subset of 7012) Inquiry panels now correctly morph based on Inventory module parameters.
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
 *    Rev 1.0   Aug 29 2003 16:11:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 06 2002 17:25:24   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:52   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:52:50   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:46   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:43:52   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   30 Jan 2002 17:07:08   baa
 * replace getPrice() method for  getSellingPrice()
 * Resolution for POS SCR-978: Kit Price doesn't display on the Item List screen
 *
 *    Rev 1.0   28 Jan 2002 10:49:20   baa
 * Initial revision.
 * Resolution for POS SCR-920: Inventory inquiry - the 'Item Inventory' screen is incorrect
 *
 *    Rev 1.1   Jan 19 2002 10:30:40   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   05 Nov 2001 17:43:56   baa
 * Initial revision.
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:34:08   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.util.Properties;

import javax.swing.JLabel;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
//-------------------------------------------------------------------------
/**
   This is the renderer for an item when performing an inventory inquiry.
  $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class ItemRenderer extends AbstractListRenderer
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static int ID          = 0;
    public static int DESCRIPTION = 1;
    public static int UOM         = 2;
    public static int PRICE       = 3;
    public static int MAX_FIELDS  = 4;

    public static int[] ITEM_WEIGHTS = {20,40,20,20};


    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public ItemRenderer()
    {
        super();
        setName("ItemInventoryRenderer");

        // set default in case lookup fails
        firstLineWeights = ITEM_WEIGHTS;

        setFirstLineWeights("topLabelWeights");

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
        labels[ID].setHorizontalAlignment(JLabel.LEFT);
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.CENTER);
        labels[UOM].setHorizontalAlignment(JLabel.LEFT);
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);

     //   setCurrencyFormat();
    }
    //---------------------------------------------------------------------
    /**
     *  Update the fields based on the properties
     */
    //---------------------------------------------------------------------
    protected void setPropertyFields()  { }
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        this.props = props;
        //setCurrencyFormat();
    }




    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
       PLUItemIfc item = (PLUItemIfc) value;

       labels[ID].setText(item.getItemID());
       labels[DESCRIPTION].setText(item.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
       String unit = item.getUnitOfMeasure().getUnitID().trim();
       if ( unit.equals("UN"))
       {
          unit = UIUtilities.retrieveCommonText("Each","Each");
       }
       labels[UOM].setText(unit);
       labels[PRICE].setText(item.getSellingPrice().toFormattedString());
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
        String dummy = "dummy";
        PLUItemIfc item = DomainGateway.getFactory().getPLUItemInstance();
        item.setItemID("12345");
        item.setPrice(DomainGateway.getBaseCurrencyInstance());

        UnitOfMeasureIfc uom = DomainGateway.getFactory().getUnitOfMeasureInstance();
        uom.setUnitID("UN");

        item.setUnitOfMeasure(uom);
        item.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), "test item");

        return(item);
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency.
    */
    //---------------------------------------------------------------------
   /* protected void setCurrencyFormat()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
            currencyFormat = props.getProperty("CurrencyIfc.DisplayFormat", CURRENCY_FORMAT);
        }


    }*/

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ItemInventoryRenderer (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
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

        ItemRenderer bean = new ItemRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }
}
