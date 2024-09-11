/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditBean.java /main/13 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse   
 *
 *Revision 1.2  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Dec 05 2003 13:04:22   nrao
 * Changed Swipe card screen for Instant Credit Enrollment.
 * 
 *    Rev 1.2   Nov 21 2003 14:23:04   nrao
 * Changed copyright message and revision number.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.eys.EYSBorderFactory;

/**
 * Work panel bean for instant credit info entry
 * 
 * @version $Revision: /main/13 $
 */
public class InstantCreditBean extends ValidatingBean
{
    private static final long serialVersionUID = -5265118298604149703L;

    // Revision number
    public static final String revisionNumber = "$Revision: /main/13 $";

    protected InstantCreditBeanModel beanModel = null;

    /**
     * Constructor
     */
    public InstantCreditBean()
    {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("InstantCreditBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
    }

    /**
     * Lays out the components.
     */
    protected void initLayout()
    {
    }

    /**
     * Updates the model property
     */
    public void updateModel()
    {
    }

    /**
     * Sets the model property
     * 
     * @param model UIModelIfc
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ChargeBean model to null");
        }
        if (model instanceof InstantCreditBeanModel)
        {
            beanModel = (InstantCreditBeanModel) model;
            updateBean();
        }
    }

    /**
     * Do actual updating of bean from the model
     */
    protected void updateBean()
    {

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
     * This method provides support to make the credit card number field and
     * expiration field (with associated labels) visible or hidden. For credit
     * card entry, these fields are visible, for debit these fields are hidden.
     * 
     * @param flag to indicate whether to make the fields visible
     */
    protected void displayFields(boolean flag)
    {

        setOpaque(flag);
        if (!flag)
        {
            setBorder(EYSBorderFactory.getEmptyBorder(0));
        }
        else
        {
            setBorder(EYSBorderFactory.getArcAllBorder());
        }
    }

    /**
     * Receive MSR data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     **/
    public void setMSRData(DeviceModelIfc data)
    {
        MSRModel msrModel = (MSRModel) data;

        if (beanModel != null)
        {
            beanModel.setCardSwiped(true);
            beanModel.setMSRModel(msrModel);
            beanModel.setCardNumber(msrModel.getAccountNumber());
            beanModel.setExpirationDate(msrModel.getExpirationDate());
            beanModel.setFirstName(msrModel.getFirstName());
            beanModel.setLastName(msrModel.getSurname());
        }

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: InstantCreditBean (Revision " + getRevisionNumber() + ") @" + hashCode());
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

        InstantCreditBean bean = new InstantCreditBean();

        UIUtilities.doBeanTest(bean);
    }
}
