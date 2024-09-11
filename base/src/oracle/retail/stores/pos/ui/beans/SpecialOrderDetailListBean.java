/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderDetailListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:12:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 14 2002 18:18:44   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:50:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:53:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:00   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Dec 10 2001 19:24:42   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// javax imports
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

//------------------------------------------------------------------------------
/**
 *  This bean is used for displaying the Special Order Detail list.
 */
//------------------------------------------------------------------------------
public class SpecialOrderDetailListBean extends SimpleListBean
{
    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
//------------------------------------------------------------------------------
/**
 *  Gets the header bean. 
 *  @return a JPanel used for a header
 */
    protected JPanel getHeaderBean()
    {
        // lazy instantiation
        if(headerBean == null)
        {
            headerBean = new SpecialOrderDetailListHeader();
        }
        return headerBean;
    }

//------------------------------------------------------------------------------
/**
 *  Gets the renderer for this list.
 *  @return a renderer object
 */
    protected ListCellRenderer getRenderer()
    {
        // lazy instantiation
        if(renderer == null)
        {
            renderer = new SpecialOrderDetailListRenderer();
        }
        return renderer;
    }

//------------------------------------------------------------------------------
/**
 *  Updates the bean with new data from the model.
 */
    public void updateBean()
    {
        if (model instanceof SpecialOrderDetailListBeanModel)
        {       
            SpecialOrderDetailListBeanModel m = 
                (SpecialOrderDetailListBeanModel)model;   
                
            Object[] newList = m.getOrderDetailList();  
             
            if(newList != null)
            {     
                list.setModel(array2Model(newList));
        
                // if the model's selected row is valid, select it on screen
                if(list.getModel().getSize() > m.getSelectedRow())
                {
                    list.setSelectedIndex(m.getSelectedRow());
                }
            }
        }
    }
    
//------------------------------------------------------------------------------
/**
 *  Updates the bean model before sending it to the ui.
 */
    public void updateModel()
    {
        ((SpecialOrderDetailListBeanModel)model).setSelectedRow(list.getSelectedIndex());
    }

//------------------------------------------------------------------------------
/**
 *  Entry point for testing.
 *  @param args command line parameters
 */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        
        SpecialOrderDetailListBean bean = new SpecialOrderDetailListBean();
        bean.configure();
        frame.getContentPane().add(bean);
        frame.setSize(512, 262);
        frame.setVisible(true);
    }
                

}
