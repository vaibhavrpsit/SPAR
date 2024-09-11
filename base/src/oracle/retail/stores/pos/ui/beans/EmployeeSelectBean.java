/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeSelectBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:17:40   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:51:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:54:28   msg
 * Initial revision.
 * 
 *    Rev 1.3   Feb 23 2002 15:04:16   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   31 Jan 2002 15:37:50   baa
 * fix select employe screens
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.DefaultListModel;
import javax.swing.ListCellRenderer;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//-------------------------------------------------------------------------
/**
 * Contains the visual presentation of the Employee Selection list
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
   @deprecated As of release 5.0.0, replaced by functionality in {@link DualListBean}
 */
//-------------------------------------------------------------------------
public class EmployeeSelectBean extends CustomerSelectBean
{
    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //---------------------------------------------------------------------
    /**
     * Constructor
     */
    //---------------------------------------------------------------------
    public EmployeeSelectBean()
    {
        super();
        beanModel = new EmployeeSelectBeanModel();
        setName("EmployeeSelectBean");
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    /*protected void initialize()
    {
      super.initialize();
      setName("EmployeeSelectBean");
    }*/

    //--------------------------------------------------------------------------
    /**
     *  Gets the renderer for this list.
     *  @return a renderer object
     */
    protected ListCellRenderer getRenderer()
    {
        // lazy instantiation
        if(renderer == null)
        {
            renderer = new EmployeeRenderer();
        }
        return renderer;
    }

    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        //this is the only piece of data that will change.
        ((EmployeeSelectBeanModel)beanModel).setSelectedEmployee(topList.getSelectedIndex());
    }

    //------------------------------------------------------------------------
    /**
     * Sets the model for the current settings of this bean.
     * @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set EmployeeSelectBean" +
                                           " model to null");
        }
        Object oldValue = beanModel;

        if (model instanceof EmployeeSelectBeanModel)
        {
            beanModel=(EmployeeSelectBeanModel)model;
 //           dirtyModel = true;
            updateBean();
        }

    }

    //--------------------------------------------------------------------------
    /**
     *     Updates the bean if It's been changed
     */
    protected void updateBean()
    {
        EmployeeSelectBeanModel m = (EmployeeSelectBeanModel)beanModel;

        DefaultListModel qModel = (DefaultListModel)list.getModel();

        int selected = m.getSelectedEmployee();

        qModel.removeAllElements();
        qModel.addElement((Object) m.getQueryEmployee());

        topList.setListData(m.getMatchlist());

        if(selected > -1)
        {
            topList.setSelectedIndex(selected);
        }
    }

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EmployeeSelectBean (Revision " +
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

        EmployeeSelectBean bean = new EmployeeSelectBean();
        bean.setLabelText("Name,ID,Role");
        bean.setLabelWeights("50,25,25");

        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
