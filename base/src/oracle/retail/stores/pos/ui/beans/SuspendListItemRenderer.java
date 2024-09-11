/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SuspendListItemRenderer.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *    kulu      02/23/09 - Fix the bug that POS Suspend List screen label
 *                         Customer does not have padding translation
 *    ddbaker   11/21/08 - Updated for code review.
 *    ddbaker   11/20/08 - Updates for clipping problems
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         6/12/2007 8:48:25 PM   Anda D. Cadar   SCR
 *        27207: Receipt changes -  proper alignment for amounts
 *   5    360Commerce 1.4         5/8/2007 11:32:29 AM   Anda D. Cadar
 *        currency changes for I18N
 *   4    360Commerce 1.3         4/25/2007 8:51:26 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:25:42 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:36 PM  Robert Pearse
 *
 *  Revision 1.4  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   May 08 2003 15:29:32   bwf
 * Got correct text from UIUtilities and did not get description.
 * Resolution for 2281: Internationlization- Item Search and Suspended List Screen have "tags"
 *
 *    Rev 1.2   Sep 06 2002 17:25:36   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:19:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:02   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:42   dfh
 * removde Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
   This is the renderer for the SuspendTransaction list.
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
*/
//-------------------------------------------------------------------------------
public class SuspendListItemRenderer extends AbstractListRenderer
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static int REGISTER    = 0;
    public static int TRANS_ID    = 1;
    public static int DESCRIPTION = 2;
    public static int TOTAL       = 3;
    public static int CUSTOMER    = 4;
    public static int MAX_FIELDS  = 5;

    /** first line label weights (set to defaults) */
    public static int[] SUSPEND_WEIGHTS = {24,24,32,20};

    //---------------------------------------------------------------------
    /**
       Constructor
    */
    //---------------------------------------------------------------------
    public SuspendListItemRenderer()
    {
        super();
        setName("SuspendListItemRenderer");

        // set default in case lookup fails
        firstLineWeights = SUSPEND_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("suspendItemRendererWeights");

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
        labels[REGISTER].setHorizontalAlignment(JLabel.LEFT);
        labels[TRANS_ID].setHorizontalAlignment(JLabel.LEFT);
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[TOTAL].setHorizontalAlignment(JLabel.RIGHT);
        labels[CUSTOMER].setHorizontalAlignment(JLabel.LEFT);

        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // add the second line
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(labels[CUSTOMER], constraints);
    }

    //--------------------------------------------------------------------------
    /**
     *     Sets the visual components of the cell
     *    @param data Object
     */
    public void setData(Object value)
    {
        TransactionSummaryIfc tsi = (TransactionSummaryIfc)value;

        String registerText = tsi.getRegisterID();
        String [] parms = {registerText, tsi.getTillID()};
        String pattern = UIUtilities.retrieveCommonText("registerIDTillID","{0}/{1}");
        if (tsi.getTillID().length() > 0)
        {
             registerText = LocaleUtilities.formatComplexMessage(pattern, parms);
        }
        labels[REGISTER].setText(registerText);
        labels[TRANS_ID].setText(tsi.getTransactionID().getTransactionIDString());
        labels[DESCRIPTION].setText(tsi.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));

        CurrencyIfc subTotal = tsi.getTransactionGrandTotal();
        subTotal = subTotal.subtract(tsi.getTransactionTaxTotal());
        labels[TOTAL].setText(subTotal.toFormattedString());

        PersonNameIfc customer = tsi.getCustomerName();
        if (customer == null)
        {
            labels[CUSTOMER].setText("");
        }
        else
        {
            parms[0] =UIUtilities.retrieveText("Common", BundleConstantsIfc.LAYAWAY_BUNDLE_NAME, "CustomerLabel", "Customer:");
            parms[1]= customer.getFirstLastName();
            pattern = UIUtilities.retrieveCommonText("CustomerName","{0} {1}");
            labels[CUSTOMER].setText(LocaleUtilities.formatComplexMessage(pattern, parms));
        }
    }

    //--------------------------------------------------------------------------
    /**
     *     Creates the prototype cell to speed updates
     *     @return TransactionSummaryIfc the prototype value
     */
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        TransactionIDIfc ti =
          DomainGateway.getFactory().getTransactionIDInstance();
        ti.setTransactionID("04241", "123", 25);

        // Trans summary
        TransactionSummaryIfc cell =
          DomainGateway.getFactory().getTransactionSummaryInstance();
        cell.setTransactionID(ti);
        cell.setRegisterID("0129");
        cell.setTillID("012345");
        LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();
        descriptions.initialize(LocaleMap.getSupportedLocales(), "Item Description");
        cell.setLocalizedDescriptions(descriptions);
        PersonNameIfc testName =
          DomainGateway.getFactory().getPersonNameInstance();
        testName.setFirstName("XXXXXXXXXXXXXXXX");
        testName.setLastName("YYYYYYYYYYYYYYYYYYYY");
        cell.setCustomerName(testName);
        cell.setTransactionGrandTotal(DomainGateway.getBaseCurrencyInstance("88888888.88"));

        return cell;
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
        String strResult = new String("Class:  SuspendListItemRenderer (Revision " +
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
        SuspendListItemRenderer renderer = new SuspendListItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
