/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LayawayBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:15 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 10 2003 10:39:48   baa
 * set default selected index to 0
 * Resolution for 2890: Layaway List screen - local navigation buttons disabled after selecting Help
 * 
 *    Rev 1.1   Aug 14 2002 18:17:56   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:50:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   13 Mar 2002 17:07:58   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.1   Jan 19 2002 10:30:50   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:36:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:17:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

// javax imports
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;

// pos imports

//------------------------------------------------------------------------------
/**
 *      The LayawayBean presents a list of items from an Layaway that
 *      the user can select to modify.
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 *      @deprecated on release 6.0 replace by listBean
 */
//------------------------------------------------------------------------------
public class LayawayBean extends SimpleListBean
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
//------------------------------------------------------------------------------
/**
 *  Default Constructor.
 */
    public LayawayBean()
    {
        super();   
        setName("LayawayBean");
    }    
    
//------------------------------------------------------------------------------
/**
 *      Configures this bean.
 */
        public void configure()
        {
                super.configure();
                list.setPrototypeCellValue(
                        ((LayawayItemRenderer)renderer).createPrototype());
        }
            
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
            headerBean = new LayawayHeaderBean();
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
            renderer = new LayawayItemRenderer();
        }
        return renderer;
    }
        
//------------------------------------------------------------------------------
/**
 *  Updates the bean with new data from the model.
 */
    public void updateBean()
    {
        LayawayBeanModel m = (LayawayBeanModel)model;   
        LayawaySummaryEntryIfc[] newList = m.getLayawayList();  
         
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
    
//------------------------------------------------------------------------------
/**
 *  Updates the bean model before sending it to the ui.
 */
    public void updateModel()
    {
        ((LayawayBeanModel)model).setSelectedRow(list.getSelectedIndex());
    }


//------------------------------------------------------------------------------
/**
 *  Returns a string representation of this object.
 *  @return String representation of object
 */
    public String toString()
    {                                   
        // result string
        String strResult = new String("Class:  LayawayBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   

//------------------------------------------------------------------------------
/**
 *  Returns the revision number of the class.
 *  @return String representation of revision number
 */
    public String getRevisionNumber()
    {                                   
        // return string
        return(revisionNumber);
    }                                   

//------------------------------------------------------------------------------
/**
 *  Entry point for testing.
 *  @param args command line parameters
 */
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        
        LayawayBean bean = new LayawayBean();
        bean.configure();
        frame.add("Center", bean);
        frame.setSize(512, 262);
        frame.setVisible(true);
    }
    
    
                
}
