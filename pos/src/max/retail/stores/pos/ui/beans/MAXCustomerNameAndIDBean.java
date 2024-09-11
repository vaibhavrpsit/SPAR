/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   Rev 1.0  25/05/2013	Tanmaya		Initial Draft: Changes for Store Credit
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerNameAndIDBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;


public class MAXCustomerNameAndIDBean extends CustomerNameAndIDBean
{
	private static final long serialVersionUID = -4730912922949730027L;

	protected void updateBean()
    {

        firstNameField.setText(beanModel.getFirstName());
        setupComponent(firstNameField,true,true);
        
        lastNameField.setText(beanModel.getLastName());
        setupComponent(lastNameField,true,true);
        
        int index= -1;

        if (beanModel.getIDTypes() != null)
        {
            Vector idTypeList = beanModel.getIDTypes();
            Vector idTypeListTextEntries = UIUtilities.getReasonCodeTextEntries(idTypeList);
            idTypeField.setModel(new ValidatingComboBoxModel(idTypeListTextEntries));
            if (beanModel.getSelectedIDType() > -1)
            {
                idTypeField.setSelectedIndex(beanModel.getSelectedIDType());
            }
            else
            {
            	idTypeField.setSelectedIndex(0);	
            }
            setComboBoxModel(idTypeListTextEntries,idTypeField,index);
        }       

    }
	
	 public void updateModel()
	    {
	       
	        beanModel.setFirstName(firstNameField.getText());
	        beanModel.setLastName(lastNameField.getText());
	        beanModel.setSelectedIDType(0);
	    }

}