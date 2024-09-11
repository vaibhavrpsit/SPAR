/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditInquiryBean.java /rgbustores_13.4x_generic_branch/2 2011/05/24 19:03:16 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/23/11 - move inquiry for payment into instantcredit service
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    abondala  01/03/10 - update header date
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/11/2007 4:22:05 PM   Mathews Kochummen use
 *          locale's date format
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse
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
 *    Rev 1.4   Dec 04 2003 15:26:18   nrao
 * Code Review Changes.
 *
 *    Rev 1.3   Nov 24 2003 12:51:56   nrao
 * Changed account number label.
 *
 *    Rev 1.2   Nov 21 2003 14:38:56   nrao
 * Changed label for account number.
 *
 *    Rev 1.1   Nov 20 2003 17:41:44   nrao
 * Added first name, last name and account number fields.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
 *      Work panel bean for instant credit inquiry
 */
//---------------------------------------------------------------------
public class InstantCreditInquiryBean extends ValidatingBean
{
    /**
     *
     */
    private static final long serialVersionUID = -3761515914657144549L;

    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    public static final String DATE_FORMAT = "MM/dd/yyyy";

    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel accountNumberLabel = null;
    protected JLabel currentBalanceLabel = null;
    protected JLabel creditLimitLabel = null;
    protected JLabel creditAvailableLabel = null;

    protected JLabel firstName = null;
    protected JLabel lastName = null;
    protected JLabel accountNumber = null;
    protected JLabel currentBalance = null;
    protected JLabel creditLimit = null;
    protected JLabel creditAvailable = null;

    // The bean model
    protected InstantCreditInquiryBeanModel beanModel = null;

    public InstantCreditInquiryBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the class.
     */
    //--------------------------------------------------------------------------
    public void configure()
    {
        setName("InstantCreditInquiryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initialize the components in this bean.
     */
    //--------------------------------------------------------------------------
    protected void initComponents()
    {
        firstNameLabel = uiFactory.createLabel("firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", null, UI_LABEL);
        accountNumberLabel = uiFactory.createLabel("accountNumberLabel", null, UI_LABEL);
        currentBalanceLabel = uiFactory.createLabel("currentBalanceLabel", null, UI_LABEL);
        creditLimitLabel = uiFactory.createLabel("creditLimitLabel", null, UI_LABEL);
        creditAvailableLabel = uiFactory.createLabel("creditAvailableLabel", null, UI_LABEL);

        firstName = uiFactory.createLabel("", null, UI_LABEL);
        lastName = uiFactory.createLabel("", null, UI_LABEL);
        accountNumber = uiFactory.createLabel("", null, UI_LABEL);
        currentBalance = uiFactory.createLabel("", null, UI_LABEL);
        creditLimit = uiFactory.createLabel("", null, UI_LABEL);
        creditAvailable = uiFactory.createLabel("", null, UI_LABEL);
    }

    //--------------------------------------------------------------------------
    /**
     *  Create this bean's layout and layout the components.
     */
    //--------------------------------------------------------------------------
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel
        (
            this,
            new JLabel[]
            {
                firstNameLabel, lastNameLabel, accountNumberLabel, currentBalanceLabel, creditLimitLabel, creditAvailableLabel
            },
            new JComponent[]
            {
                firstName, lastName, accountNumber, currentBalance, creditLimit, creditAvailable
            }
        );
    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    //--------------------------------------------------------------------------
    public void activate()
    {

    }

    //----------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
    }

    //------------------------------------------------------------------------
    /**
       Gets the model associated with the current screen information.
       @return the model for the information currently in the bean
    */
    //------------------------------------------------------------------------
    public void updateModel()
    {

    }

    //---------------------------------------------------------------------
    /**
     * Sets the model property
     * @param model UIModelIfc
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set InstantCreditInquiryBeanModel model to null");
        }
        else
        {
            if (model instanceof InstantCreditInquiryBeanModel)
            {
                beanModel = (InstantCreditInquiryBeanModel) model;
                updateBean();
            }
        }
    }

    //------------------------------------------------------------------------
    /**
       Gets the model associated with the current screen information.
       @return the model for the information currently in the bean
    */
    //------------------------------------------------------------------------
    public void updateBean()
    {
        if (beanModel.getFirstName() != null)
        {
            firstName.setText(beanModel.getFirstName());
        }

        if (beanModel.getLastName() != null)
        {
            lastName.setText(beanModel.getLastName());
        }

        if (beanModel.getAccountNumber() != null)
        {
            accountNumber.setText(beanModel.getAccountNumber());
        }


        if (beanModel.getCurrentBalance() != null)
        {
            currentBalance.setText(beanModel.getCurrentBalance().toFormattedString());
        }

        if (beanModel.getCreditLimit() != null)
        {
            creditLimit.setText(beanModel.getCreditLimit().toFormattedString());
        }

        if (beanModel.getCreditAvailable() != null)
        {
            creditAvailable.setText(beanModel.getCreditAvailable().toFormattedString());
        }
    }

    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));
        accountNumberLabel.setText(retrieveText("InqAcctNumber", accountNumberLabel));
        currentBalanceLabel.setText(retrieveText("InqCurrentBalance", currentBalanceLabel));
        creditLimitLabel.setText(retrieveText("InqCreditLimit", creditLimitLabel));
        creditAvailableLabel.setText(retrieveText("InqCreditAvailable", creditAvailableLabel));


        firstName.setLabelFor(firstNameLabel);
        lastName.setLabelFor(lastNameLabel);
        accountNumber.setLabelFor(accountNumberLabel);
        currentBalance.setLabelFor(currentBalanceLabel);
        creditLimit.setLabelFor(creditLimitLabel);
        creditAvailable.setLabelFor(creditAvailableLabel);
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: InstantCreditInquiryBean(Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
