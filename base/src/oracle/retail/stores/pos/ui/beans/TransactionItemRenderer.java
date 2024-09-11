/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TransactionItemRenderer.java /main/24 2013/04/19 16:22:38 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rgour  04/17/13 - Removing Cny from line item renderer
 *    rgour  04/01/13 - CBR cleanup
 *    sgu    03/13/13 - add support to display order summary list in order
 *                      based return
 *    rsnaya 03/22/12 - cross border return changes
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    acadar 04/12/10 - use default locale for display of currency
 *    acadar 04/06/10 - use default locale for currency display
 *    acadar 04/06/10 - use default locale for currency, date and time display
 *    acadar 04/01/10 - use default locale for currency display
 *    abonda 01/03/10 - update header date
 *    acadar 02/25/09 - override the getDefaultLocale from JComponent
 *    acadar 02/25/09 - use application default locale instead of jvm locale
 *    mkochu 02/12/09 - use default locale for dates
 *    sgu    10/30/08 - refactor layaway and transaction summary object to take
 *                      localized text
 *
 * ===========================================================================

    $Log:
     8    360Commerce 1.7         6/12/2007 8:48:28 PM   Anda D. Cadar   SCR
          27207: Receipt changes -  proper alignment for amounts
     7    360Commerce 1.6         5/8/2007 11:32:30 AM   Anda D. Cadar
          currency changes for I18N
     6    360Commerce 1.5         4/25/2007 8:51:25 AM   Anda D. Cadar   I18N
          merge
     5    360Commerce 1.4         5/12/2006 5:25:36 PM   Charles D. Baker
          Merging with v1_0_0_53 of Returns Managament
     4    360Commerce 1.3         1/22/2006 11:45:29 AM  Ron W. Haight
          removed references to com.ibm.math.BigDecimal
     3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse
    $
    Revision 1.6  2004/07/13 21:23:05  jdeleau
    @scr 6226 Disable the escape/undo button for the ReturnEnterItem screen.
    Also enable the cancel button, as in the mockup.

    Revision 1.5  2004/04/08 20:33:02  cdb
    @scr 4206 Cleaned up class headers for logs and revisions.

    Revision 1.4  2004/03/16 17:15:18  build
    Forcing head revision

    Revision 1.3  2004/03/04 20:50:28  baa
    @scr 3561 returns add support for units sold

    Revision 1.2  2004/02/11 20:56:27  rhafernik
    @scr 0 Log4J conversion and code cleanup

    Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
    updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 23 2004 16:28:34   baa
 * continue return development
 *
 *    Rev 1.0   Aug 29 2003 16:12:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Sep 24 2002 14:10:18   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:19:04   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:28   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:04   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:44   dfh
 * removde Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.ItemSummaryIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
   This is the renderer for the SaleReturn Table.  It displays
   SaleReturnLineItems and makes them look like it is a table. <P>
   @version $Revision: /main/24 $
*/
//----------------------------------------------------------------------------
public class TransactionItemRenderer extends AbstractListRenderer
{
    private static final long serialVersionUID = -7955696996867743291L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/24 $";

    /**
     * date field
     */
    public static int DATE        = 0;

    /**
     *  store id/ location field
     */
    public static int STOREID     = 1;

    /**
     *  units sold
     */
    public static int UNITS      = 2;

    /**
     *  total
     */
    public static int TOTAL       = 3;

    /**
     *  max number of fields
     */
    public static int MAX_FIELDS  = 4;

    /**
     * @deprecated as of release 7.0 not displayed anymore
     */
    public static int DESCRIPTION = 2;

    /** first line label weights (set to defaults) */
    public static int[] TRANS_WEIGHTS = {15,60,10,15};

    /** Constant used to format currency for display
    *  @deprecated as of release 5.5 a currency object formats itself by using toFormattedString()*/
    //public static final String CURRENCY_FORMAT = "#,##0.00;(#,##0.00)";

    /** The property for the currency format
    *  @deprecated as of release 5.5 a currency object formats itself by using toFormattedString()*/
   // private String currencyFormat = CURRENCY_FORMAT;

    //---------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public TransactionItemRenderer()
    {
        super();
        setName("TransactionItemRenderer");

        // set default in case lookup fails
        firstLineWeights = TRANS_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("lineWeights");
        fieldCount = MAX_FIELDS;
        lineBreak = TOTAL;

        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {

        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[STOREID].setHorizontalAlignment(JLabel.LEFT);
        labels[UNITS].setHorizontalAlignment(JLabel.LEFT);
        labels[TOTAL].setHorizontalAlignment(JLabel.RIGHT);
       
    }

    //--------------------------------------------------------------------------
    /**
     *     Sets the visual components of the cell
     *    @param value Object
     */
    //--------------------------------------------------------------------------
    public void setData(Object value)
    {
        if (value instanceof TransactionSummaryIfc)
        {
            setDataFromTransaction((TransactionSummaryIfc)value);
        }
        else
        {
            setDataFromOrder((OrderSummaryEntryIfc)value);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     *    Sets the visual components of the cell from a transaction summary
     *    @param tsi TransactionSummaryIfc
     */
    //--------------------------------------------------------------------------
    public void setDataFromTransaction(TransactionSummaryIfc tsi)
    {
        String sDate = tsi.getBusinessDate().toFormattedString(getDefaultLocale());
        labels[DATE].setText(sDate);
        StringBuffer storeLoc = new StringBuffer();
        storeLoc.append(tsi.getStore().getStoreID())
                .append("/")
                .append(tsi.getStore().getLocationName(getLocale()));
        labels[STOREID].setText(storeLoc.toString());
        // Iterate through the list of items to get the qty.
        BigDecimal units = BigDecimal.ZERO;
        if (tsi.getItemSummaries() != null)
        {
            Iterator list = tsi.getItemSummaryIterator();
            while  (list != null && list.hasNext())
            {
                ItemSummaryIfc item = (ItemSummaryIfc)list.next();
                // Returns (negative numbers) don't count in the total
                if(item.getUnitsSold().signum() == CurrencyIfc.POSITIVE)
                {
                    units = units.add(item.getUnitsSold());
                }
            }
        }
        labels[UNITS].setText(LocaleUtilities.formatDecimalForWholeNumber(units,getLocale()));
        labels[TOTAL].setText(tsi.getTransactionGrandTotal().toFormattedString());
       
    }
    
    //--------------------------------------------------------------------------
    /**
     *    Sets the visual components of the cell from an order summary
     *    @param osei OrderSummaryEntryIfc
     */
    //--------------------------------------------------------------------------
    public void setDataFromOrder(OrderSummaryEntryIfc osei)
    {
        String sDate = osei.getTimestampCreated().toFormattedString(getDefaultLocale());
        labels[DATE].setText(sDate);
        labels[STOREID].setText(osei.getRecordingTransactionID().getStoreID());
        labels[TOTAL].setText(osei.getOrderTotal().toFormattedString());
        
    }

  //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a item summary.
        ItemSummaryIfc item1 = DomainGateway.getFactory().getItemSummaryInstance();
        item1.setUnitsSold(BigDecimal.ONE);
        ItemSummaryIfc item2 = DomainGateway.getFactory().getItemSummaryInstance();
        item2.setUnitsSold(BigDecimal.ONE);
        ItemSummaryIfc item3 = DomainGateway.getFactory().getItemSummaryInstance();
        item3.setUnitsSold(BigDecimal.ONE);
        ItemSummaryIfc[] itemList = {item1, item2,item3};

//      Build objects that go into a transaction summary.
        TransactionIDIfc ti =   DomainGateway.getFactory().getTransactionIDInstance();
        ti.setTransactionID("04241", "123", 25);
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID("04241");
        store.setLocationName(getLocale(), "XXXXXXXXXXXXXXXXXXXX");
        EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        Vector lines = new Vector();
        String dummy = "dummy";
        lines.addElement(dummy);
        lines.addElement(dummy);
        address.setLines(lines);
        address.setCity(dummy);
        address.setState(dummy);
        address.setPostalCode(dummy);
        address.setPostalCodeExtension(dummy);
        address.setCountry(dummy);
        address.setState(dummy);
        address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_OTHER);
        store.setAddress(address);

        // Trans summary
        TransactionSummaryIfc cell = DomainGateway.getFactory().getTransactionSummaryInstance();
        cell.setTransactionID(ti);
        cell.setStore(store);
        cell.setBusinessDate(date);
        LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();
        descriptions.initialize(LocaleMap.getSupportedLocales(), "XXXXXXXXXXXXXXXXXXXX");
        cell.setLocalizedDescriptions(descriptions);
        cell.setTransactionGrandTotal(DomainGateway.getBaseCurrencyInstance("88888888.88"));
        cell.setItemSummaries(itemList);
        return(cell);
    }

    //--------------------------------------------------------------------------
    /**
     *    Sets the format for objects.
     */
    protected void setPropertyFields()
    {
    }

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
        String strResult = new String("Class:  TransactionItemRenderer (Revision " +
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
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        TransactionItemRenderer renderer = new TransactionItemRenderer();
        renderer.initialize();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
