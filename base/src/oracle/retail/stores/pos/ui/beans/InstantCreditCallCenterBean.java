/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditCallCenterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
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
 *Revision 1.4  2004/04/08 20:33:02  cdb
 *@scr 4206 Cleaned up class headers for logs and revisions.
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:26  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 21 2003 14:40:14   nrao
 * Externalized strings.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// swing imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;


//---------------------------------------------------------------------
/**
 *      Work panel bean for instant credit info entry
 *         $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class InstantCreditCallCenterBean extends BaseBeanAdapter
{
    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected JLabel serviceNumberLabel = null;
    protected JLabel referenceNumberLabel = null;

    protected JLabel serviceNumber = null;
    protected JLabel referenceNumber = null;

    // The bean model
    protected InstantCreditCallCenterBeanModel beanModel = null;

    /** Store instance of logger here **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.InstantCreditCallCenterBean.class);

    public InstantCreditCallCenterBean ()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the class.
     */
    public void configure()
    {
        setName("InstantCreditCallCenterBean");
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
        serviceNumberLabel = uiFactory.createLabel("serviceNumberLabel", null, UI_LABEL);
        referenceNumberLabel = uiFactory.createLabel("referenceNumberLabel", null, UI_LABEL);

        serviceNumber = uiFactory.createLabel("", null, UI_LABEL);
        referenceNumber = uiFactory.createLabel("", null, UI_LABEL);
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
                serviceNumberLabel, referenceNumberLabel,
            },
            new JComponent[]
            {
                serviceNumber, referenceNumber
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
            if (model instanceof InstantCreditCallCenterBeanModel)
            {
                beanModel = (InstantCreditCallCenterBeanModel) model;
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
        serviceNumber.setText(beanModel.getServiceNumber());
        referenceNumber.setText(beanModel.getReferenceNumber());
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        serviceNumberLabel.setText(retrieveText("ServiceNumber", serviceNumberLabel));
        referenceNumberLabel.setText(retrieveText("ReferenceNumber", referenceNumberLabel));
        
        serviceNumber.setLabelFor(serviceNumberLabel);
        referenceNumber.setLabelFor(referenceNumberLabel);
    }
    
    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (POSBaseBeanModel)beanModel;
    }
    
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: InstantCreditCallCenterBean(Revision " +
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

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        InstantCreditCallCenterBean bean = new InstantCreditCallCenterBean();

        UIUtilities.doBeanTest(bean);
    }
}
