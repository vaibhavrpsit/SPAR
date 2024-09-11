/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnItemsBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         4/4/2008 3:12:27 AM    Sujay Beesnalli
 *        Forward porting CR# 30354 from v12x. Added flags to determine
 *        highlighting of rows.
 *   4    360Commerce 1.3         3/26/2008 4:42:03 AM   VIVEKANAND KINI
 *        Forward porting GAP defects. 
 *   3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse   
 *
 *  Revision 1.9.2.2  2004/11/05 22:42:00  cdb
 *  @scr 7367 Strict checking of P and R text and existing items in list.
 *
 *  Revision 1.9.2.1  2004/10/19 21:35:54  cdb
 *  @scr 7367 This fix modifies fix for SCR 6526 without breaking 6526.
 *  Enter button is enabled as long as their is either text selected or one or more items have been entered.
 *
 *  Revision 1.9  2004/09/16 20:09:20  jdeleau
 *  @scr 6526 Prevent the user from entering "Enter" when nothing is selected
 *
 *  Revision 1.8  2004/08/10 22:18:37  jdeleau
 *  @scr 6800 Do not allow tabbing out of Prompt&Response Bean for the 
 *  return item screen.  Make sure the items in the receipt can be selected
 *  via keyboard.
 *
 *  Revision 1.7  2004/03/22 06:17:50  baa
 *  @scr 3561 Changes for handling deleting return items
 *
 *  Revision 1.6  2004/03/18 15:55:00  baa
 *  @scr 3561 Add changes to support giftcard returns
 *
 *  Revision 1.5  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.4  2004/03/15 21:43:30  baa
 *  @scr 0 continue moving out deprecated files
 *
 *  Revision 1.3  2004/03/15 16:51:06  baa
 *  @scr 0 Move deprecated pos files.
 *
 *  Revision 1.2  2004/03/05 14:39:37  baa
 *  @scr 3561  Returns
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 16 2003 17:53:04   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ListCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.pos.ui.OnlineStatusContainer;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    The ReturnItemsBean presents a list of items from a transaction that
    the user can select to return.
 **/
//--------------------------------------------------------------------------
public class ReturnItemsBean extends SaleBean implements ListSelectionListener
{
    /** revision number **/
    public static final String revisionNumber           = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    protected boolean textEntered = false;

    //---------------------------------------------------------------------
    /**
        Constructor
    **/
    //---------------------------------------------------------------------
    public ReturnItemsBean()
    {
        super();
        setName("ReturnItemsBean");
    }

    //---------------------------------------------------------------------
    /**
        Initializes the line item renderer.  This gives extending classes
        the oportunity to set up their own renderer.
        @return the header panel
    **/
    //---------------------------------------------------------------------
    protected ListCellRenderer getRenderer()
    {
        renderer = new ReturnLineItemRenderer();
        return renderer;
    }
    //---------------------------------------------------------------------
    /**
        Updates the model if It's been changed
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {

        // verify that the beanModel is of the expected type
        if (LineItemsModel.class.isInstance(beanModel))
        {                               // Begin bean model is expected type
            LineItemsModel model = (LineItemsModel)beanModel;
            StatusBeanModel statusBeanModel = model.getStatusBeanModel();

            if ( statusBeanModel != null )
            {
                OnlineStatusContainer onlineStatusContainer = statusBeanModel.getStatusContainer();

                if ( onlineStatusContainer != null )
                {
                    Hashtable hashtable = onlineStatusContainer.getStatusHash();
                    Object object = hashtable.get(new Integer(POSUIManagerIfc.TRAINING_MODE_STATUS));
                    if ( object != null )
                    {
                        Boolean trainingMode = (Boolean) object;
                        setApplicationBackground(trainingMode.booleanValue());
                    }
                }
            }


            POSListModel listModel = model.getListModel();
            getList().setModel(listModel);

            // If there is a line item ...
            if (listModel != null && listModel.getSize() > 0)
            {
                list.clearSelection();

                if(!model.isHighlightItem() && model.isDisplayTransDetailScreen()) 
                {
                	list.setSelectedIndex(EYSList.NO_SELECTION);
                }
                else
                {
	                // Check for highlight position
	                if (model.getMoveHighlightToTop())
	                {
	                    list.setSelectedIndex(0);
	                }
	                else
	                {
	                    if (model.getSelectedRows() != null && (model.getSelectedRows().length > 0))
	                    {
	                        list.setSelectedIndices(model.getSelectedRows());
	                    }
	                    else
	                    {
	                        list.setSelectedIndex(model.getSelectedRow());
	                    }
	                    // If nothing is selected, then highlight the first item in the list
	                    if(getList().getSelectionModel() instanceof EYSListSelectionModel)
	                    {
	                        EYSListSelectionModel selectionModel = (EYSListSelectionModel) getList().getSelectionModel();
	                        if(selectionModel.getHighlightItem() < 0 && list.getModel().getSize() > 0)
	                        {
	                            selectionModel.setHighlightItem(0);
	                        }
	                    }
	                }
                }
            }
            else
            {
                getList().setSelectedIndex(EYSList.NO_SELECTION);
            }
            // Do not use this bean to update the screen again.
            model.setBeanHasBeenUpdated(true);

            // Tell the Scanner Session that we're ready to handle scanner data
            notifyReadyForData();

            //After bean has been updated, set "clear" button appropriately
            manageClearButton();
            
            manageNextButton();
            
        }                               // End bean model is expected type
            
    }
    //---------------------------------------------------------------------
    /**
        Activate this screen.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        if (currentWindow != null)
        {
            currentWindow.addWindowFocusListener(this);
        }
        if(scrollPane == null)
        {
            scrollPane = getScrollPane();
        }
        headerBean.activate();
        if (list != null)
        {
             list.addFocusListener(this);
             list.setEnabled(true);
        }

    }
    
    //--------------------------------------------------------------------------
    /**
     *  Sets the totals bean.
     *  @param propValue the class name of the totals bean
     */
    public void setTotalsBean(String propValue)
    {
        // leave empty to avoid totals table in return screens
    }
    //---------------------------------------------------------------------
    /**
        Determines if the response field has text and sets the "Clear"
         button appropriately.
        @param evt the document event
    **/
    //---------------------------------------------------------------------
    public void checkAndEnableButtons(DocumentEvent evt)
    {
    	int size = 0;
    	if (LineItemsModel.class.isInstance(beanModel)){                              
            LineItemsModel listItemsModel = (LineItemsModel)beanModel;
            if(listItemsModel != null){
            	POSListModel posListModel = listItemsModel.getListModel();
        		size = posListModel.getSize();   //null check not required, posListModel can not be null
            }
        }
    	
        if (evt.getDocument().getLength() > 0)
        {
            textEntered = true;
        }
        else
        {
            textEntered = false;
        }
        
	    if(!textEntered && (size==0)){
	        globalButtonListener.enableButton(CLEAR_ACTION, false);
	        globalButtonListener.enableButton(NEXT_ACTION, false);
		}else{
			globalButtonListener.enableButton(CLEAR_ACTION, true);
	        globalButtonListener.enableButton(NEXT_ACTION, true);
		}
      
    }   
    
    //---------------------------------------------------------------------
    /**
        Determines if the "Next" button should be enabled.
    **/
    //---------------------------------------------------------------------
    public void manageNextButton()
    {
        if(globalButtonListener != null)
        {
            if (list.getModel().getSize() > 0 || textEntered)
            {
                globalButtonListener.enableButton(NEXT_ACTION, true);
            }
            else
            {
                globalButtonListener.enableButton(NEXT_ACTION, false);
            }
        }
    }

    /**
     * Implement the ListSelectionListener for this bean
     *  
     *  @param lse
     */
    public void valueChanged(ListSelectionEvent lse)
    {
        if(!lse.getValueIsAdjusting())
        {
            // If there is at least one item on the list, then next button
            // should be enabled.
            if(getList().getModel().getSize() > 0)
            {
                globalButtonListener.enableButton(NEXT_ACTION, true);
            }
        }
        
    }
    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    //---------------------------------------------------------------------
    public void setProps(Properties props)
    {
        getHeaderBean().setProps(props);

        if (renderer != null && renderer instanceof AbstractListRenderer)
        {
           ((AbstractListRenderer)renderer).setProps(props);
        }
    }

    //---------------------------------------------------------------------
    /**
        Starts the part when it is run as an application
        <p>
        @param args command line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        java.awt.Frame frame = new java.awt.Frame();
        ReturnItemsBean bean = new ReturnItemsBean();
        frame.add("Center", bean);
        frame.setSize(bean.getSize());
        frame.setVisible(true);
    }
}
