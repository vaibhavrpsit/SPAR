/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TenderLineItemRenderer.java /main/17 2013/10/28 09:04:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    cgreene   09/14/11 - reduse usage of depreacted method getAccountNumber
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/05/10 - use default locale for currency and date/time
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   16   360Commerce 1.15        1/10/2008 1:05:19 PM   Alan N. Sinton  CR
 *        29761:  Code review changes per Tony Zgarba and Jack Swan.
 *   15   360Commerce 1.14        12/16/2007 5:57:17 PM  Alan N. Sinton  CR
 *        29598: Fixes for various areas broke from PABP changes.
 *   14   360Commerce 1.13        12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *        29761: Removed non-PABP compliant methods and modified card RuleIfc
 *        to take an instance of EncipheredCardData.
 *   13   360Commerce 1.12        6/12/2007 8:48:27 PM   Anda D. Cadar   SCR
 *        27207: Receipt changes -  proper alignment for amounts
 *   12   360Commerce 1.11        5/8/2007 11:32:29 AM   Anda D. Cadar
 *        currency changes for I18N
 *   11   360Commerce 1.10        4/25/2007 8:51:25 AM   Anda D. Cadar   I18N
 *        merge
 *   10   360Commerce 1.9         3/17/2006 5:21:24 AM   Dinesh Gautam   Use
 *        number snippet for store credit
 *   9    360Commerce 1.8         3/6/2006 2:52:52 AM    Akhilashwar K. Gupta
 *        CR-3867: Updated as per QA review done by Deepanshu
 *   8    360Commerce 1.7         3/2/2006 11:13:56 PM   Akhilashwar K. Gupta
 *        CR-3867: Modified as per QA comments. The getNumber() is returning
 *        the complete number while on POS Tender List it's displaying 4
 *        digits in case of Check, GiftCertificate, Mall Certificate and blank
 *         in case of Traveller Check (US and Canadian dollers).
 *   7    360Commerce 1.6         2/27/2006 6:48:10 AM   Akhilashwar K. Gupta
 *        CR-3867: Extra import removed
 *   6    360Commerce 1.5         2/27/2006 6:45:55 AM   Akhilashwar K. Gupta
 *        CR-3867, again fixed as per QA Test review. Fixed for Mall
 *        certificate also.
 *   5    360Commerce 1.4         2/17/2006 2:56:50 AM   Akhilashwar K. Gupta
 *        CR-3867 - Fixed for Canadian Travelar Check
 *   4    360Commerce 1.3         1/25/2006 4:11:51 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:01 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:55 PM  Robert Pearse
 *:
 *   4    .v700     1.2.1.0     12/28/2005 11:52:48    Deepanshu       CR 3867:
 *        Get appropriate numbers for gift cert., check and traveler check
 *   3    360Commerce1.2         3/31/2005 15:30:25     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:26:01     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:14:55     Robert Pearse
 *
 *  Revision 1.5  2004/06/24 21:55:52  bwf
 *  @scr 5434 Put approved in the tender item if it is approved.
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
 *    Rev 1.0   Aug 29 2003 16:12:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Jul 20 2003 12:47:18   bwf
 * Leave altText alone and store in a finalText.
 * Resolution for 3171: 2nd line of tender summaries for Canadian split tender are identical
 *
 *    Rev 1.6   Jul 10 2003 10:37:40   DCobb
 * Added checks to test for alternate currency. Corrected second argument in altText string.
 * Resolution for POS SCR-2372: Incomplete tender summary appears if split tender has Canadian check
 *
 *    Rev 1.5   Apr 09 2003 16:54:54   bwf
 * Database Internationalization CleanUp
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Mar 05 2003 10:55:28   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Sep 24 2002 14:10:20   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 03 2002 16:08:04   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:19:02   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:12   msg
 * Initial revision.
 *
 *    Rev 1.2   27 Mar 2002 17:07:12   dfh
 * cleanup, removed Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Renders tender line items
 * 
 * @version $Revision: /main/17 $
 */
public class TenderLineItemRenderer extends AbstractListRenderer
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8783860632744769986L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/17 $";

    public static int TYPE = 0;
    public static int NUM = 1;
    public static int AMOUNT = 2;
    public static int ALT = 3;
    public static int AUTH = 4;
    public static int MAX_FIELDS = 5;

    public static int[] TENDER_WEIGHTS = { 60, 20, 20 };
    protected String altText = "** {0} Received Amt. {1}";
    protected String authText = "Approved";

    /**
     * Constructor
     */
    public TenderLineItemRenderer()
    {
        super();
        setName("TenderLineItemRenderer");

        // set default in case lookup fails
        firstLineWeights = TENDER_WEIGHTS;
        // look up the label weights
        setFirstLineWeights("tenderItemRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = AMOUNT;

        initialize();
    }

    /**
     * Initializes this renderer's components.
     */
    protected void initOptions()
    {

        labels[TYPE].setHorizontalAlignment(JLabel.LEFT);
        labels[ALT].setHorizontalAlignment(JLabel.LEFT);

        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        constraints.gridy = 1;
        constraints.weightx = 0.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(labels[ALT], constraints);
        add(labels[AUTH]);
    }

    /**
     * sets the visual components of the cell
     * 
     * @param data Object
     */
    public void setData(Object value)
    {
        if (!(value instanceof TenderLineItemIfc))
        {
            throw new ClassCastException("Expected a TenderLineItemIfc");
        }

        TenderLineItemIfc lineItem = (TenderLineItemIfc)value;

        String tempDesc = lineItem.getTypeDescriptorString();
        labels[TYPE].setText(UIUtilities.retrieveCommonText(tempDesc));
        if (lineItem instanceof TenderCheckIfc)
        {
            labels[NUM].setText(getNumberSnippet(((TenderCheckIfc)lineItem).getAccountNumberEncipheredData().getMaskedNumber()));
        }
        else if (lineItem instanceof TenderGiftCertificateIfc)
        {
            labels[NUM].setText(getNumberSnippet(((TenderGiftCertificateIfc)lineItem).getGiftCertificateNumber()));
        }
        else if (lineItem instanceof TenderStoreCreditIfc)
        {
            labels[NUM].setText(getNumberSnippet(((TenderStoreCreditIfc)lineItem).getStoreCreditID()));
        }
        else if (lineItem instanceof TenderChargeIfc)
        {
            String truncatedNumber = ((TenderChargeIfc)lineItem).getEncipheredCardData().getTruncatedAcctNumber();
            if (truncatedNumber != null)
            {
                int length = truncatedNumber.length();
                labels[NUM].setText(truncatedNumber.substring(length - 4, length));
            }
            else
            {
                labels[NUM].setText(truncatedNumber);
            }
        }
        else
        {
            labels[NUM].setText(new String(lineItem.getNumber()));
        }

        if (lineItem.getAmountTender().signum() == CurrencyIfc.ZERO)
        {
            labels[AMOUNT].setText("");
        }
        else
        {
            labels[AMOUNT].setText(lineItem.getAmountTender().toGroupFormattedString());
        }

        // possibly set alternate tender field with currency and amount
        if ((lineItem instanceof TenderCashIfc || lineItem instanceof TenderTravelersCheckIfc || lineItem instanceof TenderCheckIfc)
                && ((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered() != null)
        {
            CurrencyIfc tliAlt = ((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered();
            String currencyString = tliAlt.toFormattedString();
            String[] parms = { tliAlt.getCurrencyCode(), currencyString };
            String finalText = LocaleUtilities.formatComplexMessage(altText, parms);
            labels[ALT].setText(finalText);
        }
        else
        // reset alternate field to empty string...
        {
            labels[ALT].setText("");
        }

        // if authorizable and authorized
        if (lineItem instanceof AuthorizableTenderIfc
                && ((AuthorizableTenderIfc)lineItem).getAuthorizationResponse() != null)
        {
            labels[AUTH].setText(authText);
        }
        else
        {
            labels[AUTH].setText("");
        }
    }

    /**
     * Sets the format for printing out currency and quantities. Gets the format
     * string spec from the UI model properties.
     */
    protected void setPropertyFields()
    {
        if (props != null)
        {
            altText = props.getProperty("AltCurrencyTenderLabel", altText);
            authText = props.getProperty("ApprovedLabel", authText);

        }
    }

    /**
     * creates the prototype cell to speed updates
     * 
     * @return TenderLineItemIfc the prototype cell
     */
    public Object createPrototype()
    {
        TenderChargeIfc cell = DomainGateway.getFactory().getTenderChargeInstance();

        cell.setCardNumber("1234567890123456");
        cell.setAmountTender(DomainGateway.getBaseCurrencyInstance("8888888888.88"));

        return (cell);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Retrieves snippet of number passed in for display (in this case, the last
     * four digits).
     * 
     * @param number String
     * @return String number snippet
     */
    protected String getNumberSnippet(String number)
    {
        if (number == null)
        {
            return "";
        }

        int length = number.length();
        if (length > 4)
        {
            return (number.substring(length - 4, length));
        }
        
        return number;
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        TenderLineItemRenderer renderer = new TenderLineItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
