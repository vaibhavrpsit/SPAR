/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ModifyItemBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:03 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:33 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:39 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:11:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:00   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:56:10   msg
 * Initial revision.
 * 
 *    Rev 1.4   Jan 19 2002 10:30:58   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   13 Nov 2001 14:48:50   sfl
 * Make changes to let multiple items to be displayed after
 * they have been selected from Sell Item screen and brought
 * into ModifyItem service.
 * Resolution for POS SCR-282: Multiple Item Selection
 *
 *    Rev 1.0   Sep 21 2001 11:36:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:17:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;

//--------------------------------------------------------------------------
/**
    This bean controls the Item Options screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyItemBean extends BaseBeanAdapter
{
    /**
        The revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** The bean model **/
    protected ModifyItemBeanModel   beanModel = null;

    /** The main panel **/
    protected JScrollPane           itemArea = null;
    /**  The item list  **/
    protected MultiSelectList       itemList = null;
    /**  The panel header  **/
    protected SaleHeaderBean        saleHeader = null;
    /**  The list element renderer **/
    protected SaleLineItemRenderer  lineItemRenderer = null;
    /**  The layout  **/
    protected BorderLayout          modifyItemBeanBorderLayout = null;
    /**  The list model  **/
    protected DefaultListModel      listModel = new DefaultListModel();

    //---------------------------------------------------------------------
    /**
        Class Constructor.
    **/
    //---------------------------------------------------------------------
    public ModifyItemBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
     * Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        // Intialize the panel
        setName("ModifyItemBean");
        initModifyItemBeanBorderLayout();
        setLayout(modifyItemBeanBorderLayout);
        setBackground(new Color(255,255,255));
        setMaximumSize(new Dimension(2147483647, 2147483647));
        setSize(new Dimension(507, 343));
        setPreferredSize(new Dimension(520, 150));
        setBounds(new Rectangle(0, 0, 507, 343));
        setSize(507, 343);
        setMinimumSize(new Dimension(520, 150));

        // Intialize the header
        initSaleHeader();
        add(saleHeader, "North");

        // Initialize the item list
        initItemList();
        initItemArea();
        itemArea.setViewportView(itemList);
        add(itemArea, "Center");

        // Initialize the connections
        itemList.setCellRenderer(lineItemRenderer);
        itemList.setModel(listModel);
    }

    //---------------------------------------------------------------------
    /**
     * Return the ItemArea property value.
     * @return JScrollPane
     */
    //---------------------------------------------------------------------
    protected void initItemArea()
    {
        itemArea = new JScrollPane();
        itemArea.setName("ItemArea");
        itemArea.setPreferredSize(new Dimension(259, 131));
        itemArea.setOpaque(true);
        itemArea.setMinimumSize(new Dimension(259, 131));
    }

    //---------------------------------------------------------------------
    /**
     * Return the ItemList property value.
     * @return JList
     */
    //---------------------------------------------------------------------
    protected void initItemList()
    {
        initLineItemRenderer();
        itemList = new MultiSelectList(lineItemRenderer);
        itemList.setName("ItemList");
        itemList.setBounds(new Rectangle(0, 0, 160, 120));
        itemList.setSize(new Dimension(160, 120));
        itemList.setBounds(0, 0, 504, 321);
    }

    //---------------------------------------------------------------------
    /**
     * Return the LineItemRenderer property value.
     * @return oracle.retail.stores.pos.ui.beans.SaleLineItemRenderer
     */
    //---------------------------------------------------------------------
    protected void initLineItemRenderer()
    {
        lineItemRenderer = new SaleLineItemRenderer();
        lineItemRenderer.setName("LineItemRenderer");
        lineItemRenderer.setBounds(27, 391, 445, 41);
    }

    //---------------------------------------------------------------------
    /**
     * Return the modifyItemBeanBorderLayout property value.
     * @return BorderLayout
     */
    //---------------------------------------------------------------------
    protected void initModifyItemBeanBorderLayout()
    {
        modifyItemBeanBorderLayout = new BorderLayout();
        modifyItemBeanBorderLayout.setVgap(0);
        modifyItemBeanBorderLayout.setHgap(0);
    }

    //---------------------------------------------------------------------
    /**
     * Return the SaleHeade property value.
     * @return oracle.retail.stores.pos.ui.beans.SaleHeaderBean
     */
    //---------------------------------------------------------------------
    protected void initSaleHeader()
    {
        saleHeader = new SaleHeaderBean();
        saleHeader.setName("SaleHeader");
        saleHeader.setOpaque(false);
        saleHeader.setBackground(new Color(255,255,255));
        saleHeader.setMaximumSize(new Dimension(2147483647, 2147483647));
        saleHeader.setSize(new Dimension(520, 19));
        saleHeader.setPreferredSize(new Dimension(520, 19));
        saleHeader.setBounds(new Rectangle(0, 0, 520, 19));
        saleHeader.setMinimumSize(new Dimension(520, 19));
    }

    //---------------------------------------------------------------------
    /**
       This method sets the bean model.
       @param beanModel ModifyItemBeanModel
    */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model instanceof ModifyItemBeanModel)
        {
            beanModel = (ModifyItemBeanModel) model;
            listModel = new DefaultListModel();

            SaleReturnLineItemIfc[] items = beanModel.getLineItems();

            if (items != null)
            {
                if (items.length == 1)
                {
                    listModel.addElement(beanModel.getLineItem());
                    if (beanModel.getItemHighlightFlag())
                    {
                       itemList.enable(true);
                       itemList.setRequestFocusEnabled(true);
                    }
                }
                if (items.length > 1)
                {
                    for (int i = 0; i < items.length; i++)
                    {
                         listModel.addElement(items[i]);
                    }
                    if (!beanModel.getItemHighlightFlag())
                    {
                        itemList.disable();
                        itemList.setRequestFocusEnabled(false);
                    }
                    else
                    {
                       itemList.enable(true);
                       itemList.setRequestFocusEnabled(true);
                    }
                }
            }
            itemList.setModel(listModel);
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
        String strResult = new String("Class: ModifyItemBean (Revision " +
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
            java.awt.Frame frame = new java.awt.Frame();
            ModifyItemBean aModifyItemBean;
            aModifyItemBean = new ModifyItemBean();
            frame.add("Center", aModifyItemBean);
            frame.setSize(aModifyItemBean.getSize());
            frame.setVisible(true);
    }
}
