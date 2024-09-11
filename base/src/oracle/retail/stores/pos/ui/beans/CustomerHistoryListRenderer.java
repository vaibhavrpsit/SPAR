/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerHistoryListRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    acadar 04/06/10 - use default locale for currency, date and time display
 *    acadar 04/01/10 - use default locale for currency display
 *    abonda 01/03/10 - update header date
 *    acadar 02/25/09 - override the getDefaultLocale from JComponent
 *    acadar 02/09/09 - use default locale for display of date and time
 *    sgu    10/30/08 - refactor layaway and transaction summary object to take
 *                      localized text
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         6/12/2007 8:48:20 PM   Anda D. Cadar   SCR
           27207: Receipt changes -  proper alignment for amounts
      4    360Commerce 1.3         5/11/2007 3:40:39 PM   Mathews Kochummen use
            locale's date format
      3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:38 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
     $
     Revision 1.5  2004/04/08 20:33:02  cdb
     @scr 4206 Cleaned up class headers for logs and revisions.

     Revision 1.4  2004/03/16 17:15:22  build
     Forcing head revision

     Revision 1.3  2004/03/16 17:15:17  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:09:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Sep 06 2002 17:25:22   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 03 2002 16:05:02   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:17:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:14   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:52   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:20   dfh
 * removde Dollar sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 *
 *    Rev 1.0   Mar 18 2002 11:54:52   msg
 * Initial revision.
 *
 *    Rev 1.4   08 Feb 2002 18:52:26   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   30 Jan 2002 22:29:28   baa
 * customer ui fixex
 * Resolution for POS SCR-965: Add Customer screen UI defects
 *
 *    Rev 1.2   Jan 19 2002 10:29:34   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   05 Nov 2001 17:37:44   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:34:08   baa
 * Initial revision.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.text.DateFormat;
import java.util.Vector;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
   This is the renderer for the CustomerHistoryList.  It displays
   a list of customer transactions.
   $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//----------------------------------------------------------------------------
public class CustomerHistoryListRenderer extends AbstractListRenderer
{
    private static final long serialVersionUID = 5361166800315993425L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int TRANS_ID = 0;
    public static final int DATE = 1;
    public static final int LOCATION = 2;
    public static final int TYPE = 3;
    public static final int TOTAL = 4;

    public static final int MAX_FIELDS = 5;

    /** first line label weights (set to defaults) */
    public static int[] DEFAULT_WEIGHTS = {20,20,20,15,20};

    /** Constant used to format currency for display
    @deprecated as of release 5.5 */
    public static final String CURRENCY_FORMAT = "#,##0.00;(#,##0.00)";

    /** The property for the currency format
     @deprecated as of release 5.5 */
    protected String currencyFormat = CURRENCY_FORMAT;

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public CustomerHistoryListRenderer()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initialize the class.
     */
    protected void initialize()
    {
         setName("CustomerHistoryListRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("customerHistoryRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = TOTAL;

        super.initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *    Sets the format for objects.
     */
    protected void setPropertyFields()
    {
    }

//---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
     protected void initOptions()
     {
        labels[TRANS_ID].setHorizontalAlignment(JLabel.LEFT);
        labels[DATE].setHorizontalAlignment(JLabel.LEFT);
        labels[LOCATION].setHorizontalAlignment(JLabel.LEFT);
        labels[TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[TOTAL].setHorizontalAlignment(JLabel.RIGHT);
     }

    //--------------------------------------------------------------------------
    /**
     *     Sets the visual components of the cell
     *    @param data Object
     */
    public void setData(Object value)
    {
        TransactionSummaryIfc tsi = (TransactionSummaryIfc) value;
        String type = TransactionIfc.TYPE_DESCRIPTORS[tsi.getTransactionType()];
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String sDate = dateTimeService.formatDate(tsi.getBusinessDate().dateValue(), getDefaultLocale(), DateFormat.SHORT);

        labels[DATE].setText(sDate);
        labels[TRANS_ID].setText(tsi.getTransactionID().getTransactionIDString());
        labels[LOCATION].setText(tsi.getStore().getLocationName(getLocale()));

        labels[TYPE].setText(UIUtilities.retrieveCommonText(type,type));
        labels[TOTAL].setText(tsi.getTransactionGrandTotal().toFormattedString());
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
        TransactionIDIfc ti =
          DomainGateway.getFactory().getTransactionIDInstance();
        ti.setTransactionID("04241", "123", 25);
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID("04241");
        store.setLocationName(getLocale(), "XXXXXXXXXXXXXXXXXXXX");
        EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        Vector<String> lines = new Vector<String>();
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

        return(cell);
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
        CustomerHistoryListRenderer renderer = new CustomerHistoryListRenderer();
        renderer.initialize();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
