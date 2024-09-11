/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AbstractTotalsBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:38 mszekely Exp $
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
 *    5    360Commerce 1.4         5/7/2007 4:11:02 PM    Alan N. Sinton  CR
 *         26483 - Modified to use the Gateway.getBooleanProperty() method.
 *    4    360Commerce 1.3         4/30/2007 3:45:30 PM   Alan N. Sinton  Merge
 *          from v12.0_temp.
 *    3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:21 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/05/05 01:10:13  tfritz
 *   @scr 3751 - The fields are can no longer have focus.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 08 2003 17:30:28   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:09:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Aug 14 2002 18:16:42   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 07 2002 19:34:00   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 05 2002 17:58:40   baa
 * code conversion and reduce number of color settings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:47:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 25 2002 10:51:16   mpm
 * Internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.GridBagConstraints;

import javax.swing.JTextField;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 *  This bean displays a series of fields with header labels. Typically it
 *  is used to display a set of summary total fields below a list.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public abstract class AbstractTotalsBean extends BaseHeaderBean
{
    /** Revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** Quantity string format */
    protected static final String QUANTITY_FORMAT = "#.00;(#.00)";

    /** an array of text fields to display */
    protected JTextField[] fields = null;

    /** the totals bean model */
    protected TotalsBeanModel beanModel = null;

    /** flag to indicate tax inclusive */
    protected static boolean taxInclusiveFlag =
            Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);

    //--------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public AbstractTotalsBean()
    {
        super();
        UI_PREFIX = "Table";
        initFields();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("TotalsBean");
        uiFactory.configureUIComponent(this, "TotalsBean");

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initialize the components.
     */
    protected void initComponents()
    {
        super.initComponents();
        updatePropertyFields();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the display fields. Subclasses must implement this
     *  method for specific fields.
     */
    protected abstract void initFields();

    //--------------------------------------------------------------------------
    /**
     * Initialize the layout and lay out the components.
     */
    protected void initLayout()
    {
        super.initLayout();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy   = 1;
        constraints.fill    = GridBagConstraints.BOTH;
        constraints.anchor  = GridBagConstraints.CENTER;
        constraints.weightx = 0.2;
        constraints.weighty = 1.0;

        if(fields != null)
        {
            for(int i=0; i<fields.length; i++)
            {
                add(fields[i], constraints);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures a display text field.
     *  @param field the text field
     */
    protected void configureField(JTextField field)
    {
        uiFactory.configureUIComponent(field, UI_PREFIX + ".field");
        field.setRequestFocusEnabled(false);
        field.setFocusable(false);

        int align = UIManager.getInt(UI_PREFIX + ".field.alignment");
        field.setHorizontalAlignment(align);
    }

    //--------------------------------------------------------------------------
    /**
     *  Gets the bean model.
     *  @return the bean model
     */
    public TotalsBeanModel getModel()
    {
        if(beanModel == null)
        {
            beanModel = new TotalsBeanModel();
        }
        return beanModel;
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the bean model.
     *  @param model the bean model
     */
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException(
                "Attempt to set " +
                Util.getSimpleClassName(this.getClass()) +
                " model to null.");
        }
        // update the bean
        if (model instanceof TotalsBeanModel)
        {
            beanModel = (TotalsBeanModel)model;
            updateBean();
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Updates the model if It's been changed. Subclasses must implement.
     */
    public abstract void updateBean();

    //--------------------------------------------------------------------------
    /**
     *  Returns default display string.
     *  @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());

    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

}
