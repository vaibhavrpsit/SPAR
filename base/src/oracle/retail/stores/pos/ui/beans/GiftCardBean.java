/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftCardBean.java /rgbustores_13.4x_generic_branch/4 2011/07/26 16:57:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/02/08 - Changes for duplicate GC issue for transactionv
 *                         having issue and reload at the same time
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         7/11/2007 11:07:31 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    5    360Commerce 1.4         5/30/2007 9:01:58 AM   Anda D. Cadar   code
 *         cleanup
 *    4    360Commerce 1.3         1/22/2006 11:45:24 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/08 19:58:16  lzhao
 *   @scr 5405: add gift card amount in work panel if issue fail
 *
 *   Revision 1.4  2004/03/30 20:34:12  bwf
 *   @scr 4165 Gift Card Rework
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
 *    Rev 1.6   Feb 06 2004 16:43:24   lzhao
 * change display message for different request.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.5   Feb 05 2004 14:50:04   lzhao
 * add remaining balance for gift card display and reprint.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Jan 30 2004 14:14:10   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 16 2003 11:39:28   lzhao
 * code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 08 2003 09:26:34   lzhao
 * remove expiration date
 *
 *    Rev 1.1   Oct 30 2003 09:48:04   lzhao
 * add feature to display reload gift card amount on working area.
 *
 *    Rev 1.0   Aug 29 2003 16:10:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:17:44   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:10   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:53:08   vxs
 * Added labels[INIT_BALANCE].setText("");
 * fields[INIT_BALANCE].setText("");
 * Resolution for POS SCR-914: Gift Card - the 'Sell Gift Card' screen is displaying 'Initial Balance not available'
 *
 *    Rev 1.0   Mar 18 2002 11:55:14   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 24 2002 13:45:14   mpm
 * Externalized text for default, common and giftcard config files.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import javax.swing.JLabel;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to display information about Gift Cards. No user input.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/4 $ $EKW;
 */
public class GiftCardBean extends BaseBeanAdapter
{
    private static final long serialVersionUID = 4756042170066328861L;

    /** version number from revision system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/4 $ $EKW;";

    // label and field placeholder constants
    public static final int CARD_NUMBER        = 0;
    public static final int INIT_BALANCE       = 1;
    public static final int CARD_AMOUNT        = 2;
    public static final int MAX_FIELDS         = 3;

    public static final String NOT_AVAILABLE      = "not available";
    public static final String NOT_AVAILABLE_TAG  = "NotAvailableLabel";
    public static final String NOT_APPLICABLE_TAG = "NotApplicable";

    public static final String LabelTagRemainingBalance ="RemainingBalance";
    public static final String LabelTextRemainingBalance ="Remaining Balance:";



    public static final String[] labelText =
    {
        "Gift Card Number:", "Initial Balance:", "Gift Card Amount:"
    };

    public static final String[] labelTags =
    {
        "GiftCardNumberLabel", "InitialBalanceLabel", "GiftCardAmountLabel"
    };

    /** array of labels */
    protected JLabel[] labels = null;

    /** array of display fields */
    protected JLabel[] fields = null;

    /** the bean model */
    protected GiftCardBeanModel beanModel = null;

    /**
     * Default constructor.
     */
    public GiftCardBean()
    {
        super();
        initialize();
    }



    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("GiftCardInquiryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();

    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {

        labels = new JLabel[MAX_FIELDS];
        fields = new JLabel[MAX_FIELDS];

        for(int i=0; i<MAX_FIELDS; i++)
        {
            labels[i] = uiFactory.createLabel(labelTags[i], labelText[i], null, UI_LABEL);
            fields[i] = uiFactory.createLabel(labelTags[i]+"Field", NOT_AVAILABLE, null, UI_LABEL);
        }
    }

    /**
     *    Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    /**
     *    Sets the information to be shown by this bean.
     *    @param model UIModelIfc
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set GiftCardBean model to null");
        }
        if (model instanceof GiftCardBeanModel)
        {
            beanModel = (GiftCardBeanModel) model;
            updateBean();
        }
    }

    /**
     * Update the bean if the model has changed.
     */
    protected void updateBean()
    {
        labels[CARD_NUMBER].setText("");
        labels[INIT_BALANCE].setText("");
        labels[CARD_AMOUNT].setText("");

        fields[CARD_NUMBER].setText("");
        fields[INIT_BALANCE].setText("");
        fields[CARD_AMOUNT].setText("");

        if ( (beanModel.getGiftCardStatus()!=null) )
        {
            if (beanModel.getGiftCardStatus() == StatusCode.Reload ||
                beanModel.getGiftCardStatus() == StatusCode.Active)
            {
                if (beanModel.getGiftCardAmount() != null)
                {
                    //I18N change - remove ISO code
                    String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(), getDefaultLocale());
                    fields[CARD_AMOUNT].setText(amount);
                     labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT],
                                                              labelText[CARD_AMOUNT]));
                }
            }
            else // inquiry, return any status, if fail, the status is empty string
            {
                if (beanModel.getGiftCardNumber() != null)
                {
                    labels[CARD_NUMBER].setText(retrieveText(labelTags[CARD_NUMBER],
                                                             labelText[CARD_NUMBER]));
                    fields[CARD_NUMBER].setText(beanModel.getGiftCardNumber());
                }
                // Retrieve currency format
                if(beanModel.getGiftCardInitialBalance() != null)
                {
                    labels[INIT_BALANCE].setText(retrieveText(labelTags[INIT_BALANCE],
                                                              labelText[INIT_BALANCE]));
                    if ( beanModel.isValidInquriy() )
                    {
                        String balance = getCurrencyService().formatCurrency(beanModel.getGiftCardInitialBalance(),getDefaultLocale());
                        fields[INIT_BALANCE].setText(balance);
                    }
                    else
                    {
                        fields[INIT_BALANCE].setText("");
                    }
                }
                if (beanModel.getGiftCardAmount() != null)
                {
                    labels[CARD_AMOUNT].setText(retrieveText(LabelTagRemainingBalance,
                                                             LabelTextRemainingBalance));
                    if ( beanModel.isValidInquriy() )
                    {
                        String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(),getDefaultLocale());
                        fields[CARD_AMOUNT].setText(amount);
                    }
                    else
                    {
                        fields[CARD_AMOUNT].setText("");
                    }
                }
            }
        }
        else
        {
            // activation fail, request reenter gift card number.
            // show gift card amount at this time.
            if (beanModel.getGiftCardAmount() != null && beanModel.getGiftCardInitialBalance() != null)
            {
            	String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardInitialBalance(),getDefaultLocale());
                fields[CARD_AMOUNT].setText(amount);
                labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT],
                        labelText[CARD_AMOUNT]));
            }
        }
     }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     * 
     * @return the POSBaseBeanModel associated with this bean.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        for(int i = 0; i < MAX_FIELDS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));

            // check for not available
            if (Util.isObjectEqual(fields[i].getText(),
                                   NOT_AVAILABLE))
            {
                fields[i].setText(retrieveText(NOT_AVAILABLE_TAG,
                                               fields[i]));
            }
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: GiftCardBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
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
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        GiftCardBeanModel model = new GiftCardBeanModel();
        model.setGiftCardNumber("20020012");
        model.setGiftCardInitialBalance(new BigDecimal("100.00"));
        model.setGiftCardAmount(new BigDecimal("49.99"));
        model.setGiftCardStatus(StatusCode.Active);

        GiftCardBean bean = new GiftCardBean();
        bean.setModel(model);

        UIUtilities.doBeanTest(bean);

    }
}
