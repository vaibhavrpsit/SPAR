/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IDTypeBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/29/2007 7:32:03 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         7    .v8x      1.3.1.2     3/13/2007 2:01:07 PM   Brett J. Larsen
 *         CR 4530
 *         - adding support for default id type
 *         6    .v8x      1.3.1.1     3/6/2007 2:53:08 PM    Brett J. Larsen
 *         CR 4530
 *         - reverting to prior version - misunderstood requirements
 *         5    .v8x      1.3.1.0     3/5/2007 4:46:17 PM    Brett J. Larsen
 *         CR 4530
 *         - id type should never have a default value - always setting index
 *         to "-1"
 *    4    360Commerce 1.3         7/28/2006 6:06:56 PM   Brett J. Larsen CR
 *         4530 - default reason code fix
 *         v7x->360Commerce merge
 *    3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse
 *
 *
 *    5    .v7x      1.2.1.1     7/17/2006 10:55:23 AM  Michael Wisbauer
 *         changed code to set default to none and also commented out code
 *         that was causing cotrol to use db values instead of from bundles
 *    4    .v7x      1.2.1.0     6/23/2006 4:55:54 AM   Dinesh Gautam   CR
 *         4530: Fix for reason code
 *
 *   Revision 1.6  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.5  2004/06/19 15:47:00  bwf
 *   @scr 3566 The id entry field is not supposed to be editable.  It is a
 *                     drop down box only.  Removed previous changes.
 *
 *   Revision 1.4  2004/06/13 20:42:26  khassen
 *   @scr 3566 - Modified the editable property so that Document Listeners are created properly.
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
 *    Rev 1.2   Jan 07 2004 09:21:52   cdb
 * Modified so that ID field has focus when screen is entered.
 * Resolution for 3540: ID Type field on Enter ID screen has no focus
 *
 *    Rev 1.1   Nov 13 2003 15:30:08   bwf
 * Put blank for id type.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 07 2003 16:19:00   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

//java imports
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
   This bean is used to capture the ID type.<P>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
   @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
//---------------------------------------------------------------------
public class IDTypeBean extends ValidatingBean
{
    /**
        Check Entry Bean model
    */
    protected CheckEntryBeanModel beanModel = new CheckEntryBeanModel();

    /**
        Fields and labels that contain check ID type data
    */
    protected JLabel idTypeLabel;

    protected ValidatingComboBox idTypeField;

    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Default Constructor.
    */
    //---------------------------------------------------------------------
    public IDTypeBean()
    {
        super();
        initialize();
        setTabOrder();
    }

    //---------------------------------------------------------------------
    /**
       Initializes the fields.
    */
    //---------------------------------------------------------------------
    protected void initializeFields()
    {
        idTypeField = uiFactory.createValidatingComboBox("IdTypeField", "false", "15");
    }

    //---------------------------------------------------------------------
    /**
       Initializes the labels.
    */
    //---------------------------------------------------------------------
    protected void initializeLabels()
    {
        idTypeLabel = uiFactory.createLabel("IDTypeLabel", null, UI_LABEL);
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
        beanModel.setSelectedIDType(idTypeField.getSelectedIndex());
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
                beanModel = ( CheckEntryBeanModel)model;
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
        if (beanModel.getIDTypes() != null)
        {
            Vector<String> idTypes = beanModel.getIDTypes();
            ValidatingComboBoxModel listModel =
                  new ValidatingComboBoxModel(idTypes);
            idTypeField.setModel(listModel);
        }

        if (beanModel.getSelectedIDType() != CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
        {
            idTypeField.setSelectedIndex(beanModel.getSelectedIDType());
        }
        else
        {
            idTypeField.setSelectedIndex(beanModel.getDefaultIDType());
        }

        idTypeField.setRequired(true);

    }

    //---------------------------------------------------------------------
    /**
        Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        setName("IDTypeBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();

        JLabel[] labels = new JLabel[]
        {
            idTypeLabel
        };

        JComponent[] components = new JComponent[]
        {
            idTypeField
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
        idTypeLabel.setText(retrieveText("IdTypeLabel", idTypeLabel));
        idTypeField.setLabel(idTypeLabel);
    }

    //--------------------------------------------------------------------------
    /**
     *    Overrides JPanel setVisible() method to request focus.
     */
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(idTypeField);
        }
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

        CheckEntryBeanModel model = new CheckEntryBeanModel();
        model.setIDNumber("12345");
        model.setStateIndex(10);
        //aModel.setPhoneNumber("5124912000");

        IDTypeBean aBean = new IDTypeBean();
        aBean.configure();
        aBean.setModel(model);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}

