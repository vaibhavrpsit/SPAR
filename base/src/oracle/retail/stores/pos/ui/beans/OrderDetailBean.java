/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderDetailBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
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
 *    tzgarba   02/25/09 - Removed test class dependencies from shipping
 *                         source.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
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
 *    Rev 1.0   Aug 29 2003 16:11:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:06   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:32   msg
 * Initial revision.
 * 
 *    Rev 1.2   Jan 19 2002 10:31:12   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Jan 15 2002 22:04:04   dfh
 * updates for new domain
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 24 2001 11:19:22   MPM
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:16:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.BorderLayout;
import java.util.Properties;

import javax.swing.DefaultListModel;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    The OrderDetailBean displays the details of an order using data retrieved
    from an OrderBean model.
    @version $KW=@(#); $Ver=pos_4.5.0:33; $EKW;
**/
//--------------------------------------------------------------------------
public class OrderDetailBean extends ListBean
{
    /** revision number **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /** Totals bean **/
    protected AbstractTotalsBean totalsBean = null;

    /** Defines the model to be used for this list  **/
    protected DefaultListModel orderItemsModel = new DefaultListModel();

    // Models
    protected OrderBeanModel beanModel = null;

    //--------------------------------------------------------------------
    /**
        Constructor
     **/
    //--------------------------------------------------------------------
    public OrderDetailBean()
    {
        super();
        setName("OrderDetailBean");
    }

    //---------------------------------------------------------------------
    /**
        Activate this screen.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        getTotalsBean().activate();
        list.setEnabled(true);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the totals bean.
     *  @param propValue the class name of the totals bean
     */
    public void setTotalsBean(String propValue)
    {
        if(propValue != null)
        {
            totalsBean =
                (AbstractTotalsBean)UIUtilities.getNamedClass(propValue);
        }
        if(totalsBean == null)
        {
            totalsBean = new SaleTotalsBean();
        }
        add(totalsBean, BorderLayout.SOUTH);
    }

    //---------------------------------------------------------------------
    /**
        Gets the totals bean.
    **/
    //---------------------------------------------------------------------
    protected AbstractTotalsBean getTotalsBean()
    {
        if(totalsBean == null)
        {
            // Initialize the totals panel
            setTotalsBean(null);
        }
        return totalsBean;
    }

    //---------------------------------------------------------------------
    /**
        Adds an item to the model.
        The selected row will be the value of <b>index</b>.
        The internal highlight variable will be ON after the call returns.
        <p>
        @param index The index to insert the item at.
        @param item the item to insert.
    **/
    //---------------------------------------------------------------------
    public void addItem(int index, Object item)
    {
        ((DefaultListModel)list.getModel()).add(index, item);
        list.ensureIndexIsVisible(index);
        scrollPane.validate();
    }

    //---------------------------------------------------------------------
    /**
        Modifies an item in the model.
        The selected row will be <em>index</em>.
        The internal highlight variable will be ON after the call returns.
        <p>
        @param index The index to modify.
        @param item the item to replace with.
    **/
    //---------------------------------------------------------------------
    public void modifyItem(int index, Object item)
    {
        ((DefaultListModel)list.getModel()).setElementAt(item, index);
    }

    //---------------------------------------------------------------------
    /**
        Returns the index of the selected row (the current cursor position).
        <p>
        @return int<index>
    **/
    //---------------------------------------------------------------------
    public int getSelectedRow()
    {
        return list.getSelectedRow();
    }

    //---------------------------------------------------------------------
    /**
       Sets the selected row.
       This now means the current cursor position in the list of items.
       The indices of the selected items are maintained in the OrderItemList
       <UL>
       <LI> If the internal variable, highlighted, is true,
            it also makes sure the row is highlighted.
       <LI> If the internal variable, highlighted, is false,
            it simply sets the internal selectedRow variable to the index.
       <LI> If the index is <0 or >orderItemList.getSize(),
            then the internal selectedRow variavle is set to -1.
       </UL>
       @param index The index of the selected row.
    **/
    //---------------------------------------------------------------------
    public void setSelectedRow(int index)
    {
        list.setSelectedIndex(index);
    }

    //---------------------------------------------------------------------
    /**
        Sets the model for this bean.
        This bean requires a LineItemsModel as its model.
        <p>
        @param beanModel A LineItemsModel
        @see oracle.retail.stores.pos.ui.beans.LineItemsModel
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set OrderBean model to null.");
        }
        if (model instanceof OrderBeanModel)
        {
            beanModel = (OrderBeanModel)model;
            updateBean();
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the model if it has been changed.
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {                                                //get size of current DefaultListModel
        int                 oldNumberOfItems    =   orderItemsModel.getSize();
        DefaultListModel    listModel           =   new DefaultListModel();
        AbstractTransactionLineItemIfc[]  values = beanModel.getLineItems();

        listModel.clear();

        for(int i = 0; i < values.length; i++)
        {
            listModel.addElement((SaleReturnLineItemIfc) values[i]);
        }

        //get multi select list and sets its model to listModel
        list.setModel(listModel);

        int index = listModel.getSize() - 1;
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);

    }

    //---------------------------------------------------------------------
    /**
        This method is called everytime the length of the input area
        on the QuarryTopPanel changes.
        <p>
        @param length the new length of the string
    **/
    //---------------------------------------------------------------------

    public void inputAreaLengthChanged(int length)
    {

    }

     //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        if(headerBean != null)
        {
            headerBean.setProps(props);
        }

        if (renderer != null)
        {
            ((AbstractListRenderer)renderer).setProps(props);
        }
    }
}
