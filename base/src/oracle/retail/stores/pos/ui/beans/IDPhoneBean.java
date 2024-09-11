/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IDPhoneBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/10/09 - Changes to include country specific phone format for
 *                         cases where country/state is not specified in the
 *                         flow
 *    mkochumm  01/30/09 - changes to use country specific phone formats
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse
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
 *    Rev 1.2   Dec 11 2003 15:43:20   bwf
 * Update per code review.
 *
 *    Rev 1.1   Nov 14 2003 12:55:20   bwf
 * Fixed validation of length.
 *
 *    Rev 1.0   Nov 07 2003 16:18:58   bwf
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//java imports
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
   This bean is used to capture the ID's phone number. <P>
   @version
   @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
//---------------------------------------------------------------------
public class IDPhoneBean extends ValidatingBean
{
    /**
        Check Entry Bean model
    */
    protected CheckEntryBeanModel beanModel =
        new CheckEntryBeanModel();

    /**
        Fields and labels that contain check enter ID data
    */
    protected JLabel telephoneLabel;
    protected ValidatingFormattedTextField telephoneField = null;

    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "";

    //---------------------------------------------------------------------
    /**
       Default Constructor.
    */
    //---------------------------------------------------------------------
    public IDPhoneBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
       Initializes the fields.
    */
    //---------------------------------------------------------------------
    protected void initializeFields()
    {
    	telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
    }

    //---------------------------------------------------------------------
    /**
       Initializes the labels.
    */
    //---------------------------------------------------------------------
    protected void initializeLabels()
    {
        telephoneLabel = uiFactory.createLabel(retrieveText("TelephoneLabel", telephoneLabel),
                                               null,
                                               UI_LABEL);
    }

    //----------------------------------------------------------------------------
    /**
        Returns the base bean model.<P>
        @return POSBaseBeanModel
    */
    //----------------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }


    //---------------------------------------------------------------------
    /**
        Updates the model from the screen.
    */
    //---------------------------------------------------------------------
    public void updateModel()
    {
    	beanModel.setPhoneNumber((String)(telephoneField.getFieldValue()));
    }

    //---------------------------------------------------------------------
    /**
       Sets the model property  value.<P>
       @param model UIModelIfc the new value for the property.
    */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set CheckEntryBeanModel" +
                "model to null");
        }
        else
        {
            if (model instanceof  CheckEntryBeanModel)
            {
                beanModel = (CheckEntryBeanModel)model;
                updateBean();
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the information displayed on the screen's if the model's
        been changed.
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        int countryIndex = beanModel.getCountryIndex();
        String phoneFormat = "";
        String phoneValidationRegexp = "";
        UtilityManager util = (UtilityManager) Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        //countryindex is -1 when IDType is not drivers license.
        if (countryIndex != -1)
        {
            String countryCode = beanModel.getCountry(countryIndex).getCountryCode();
            phoneFormat = util.getPhoneFormat(countryCode);
            phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
        }
        else
        {
          Locale defLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
          String countryCode = defLocale.getCountry();
          phoneFormat = util.getPhoneFormat(countryCode);
          phoneValidationRegexp = util.getPhoneValidationRegexp(countryCode);
        }
        telephoneField.setFormat(phoneFormat);
        telephoneField.setValidationRegexp(phoneValidationRegexp);
        telephoneField.setValue(beanModel.getPhoneNumber());
    }

    //---------------------------------------------------------------------
    /**
        Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("IDPhoneBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();

        JLabel[] labels = new JLabel[]
        {
            telephoneLabel
        };

        JComponent[] components = new JComponent[]
        {
            telephoneField
        };
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }

    //----------------------------------------------------------------------------
     /**
     * Override the tab key ordering scheme of the default focus manager where
     * appropriate.  The default is to move in a zig-zag pattern from left to right
     * across the screen. In some cases, however, it makes more sense to move down
     * column one on the screen then start at the top of column 2.
     */
    //----------------------------------------------------------------------------
    protected void setTabOrder()
    {
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        telephoneLabel.setText(retrieveText("TelephoneLabel", telephoneLabel));
        telephoneField.setLabel(telephoneLabel);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
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

        CheckEntryBeanModel aModel = new CheckEntryBeanModel();
        aModel.setPhoneNumber("5124912000");

        IDPhoneBean aBean = new IDPhoneBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}
