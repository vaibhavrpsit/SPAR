/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Dec 13, 2016		Ashish Yadav		Home Delivery Send FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.ShippingMethodConstantsIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;

//----------------------------------------------------------------------------
/**
    This is the model used to pass customer information
    @version $Revision: 4$
**/
//----------------------------------------------------------------------------
public class MAXShippingMethodBeanModel extends MailBankCheckInfoBeanModel
{
    // list of available shipping methods
    protected ShippingMethodIfc methodsList[];

    // indicate wether the db is offline
    protected boolean offline = false;

   EYSDate expectedDeliveryDate = null;
   
   EYSTime expectedDeliveryTime = null;

    protected int  selectedIndex = 0;

    protected CurrencyIfc defaultShippingCharge = null;
    
    public CurrencyIfc getDefaultShippingCharge() {
		return defaultShippingCharge;
	}

	public void setDefaultShippingCharge(CurrencyIfc defaultShippingCharge) {
		this.defaultShippingCharge = defaultShippingCharge;
	}

	protected String calculationType = "";
    protected CurrencyIfc shippingCharge = null;
    protected CurrencyIfc itemsCharge;
    //-------------------------------------------------------------------------
    /**
       Get the calculation type from the parameter value
       @return the value of offline
    **/
    //-------------------------------------------------------------------------
     public String getCalculationType()
     {
        return calculationType;
     }

     public void  setCalculationType(String value)
     {
        calculationType = value;
     }
    //-------------------------------------------------------------------------
    /**
       Get the calculated shipcharge
       @return the value of offline
    **/
    //-------------------------------------------------------------------------
     public CurrencyIfc getItemsShippingCharge()
     {
        return itemsCharge;
     }


    //-------------------------------------------------------------------------
    /**
       Get the value of offline
       @return the value of offline
    **/
    //-------------------------------------------------------------------------
     public boolean isOffline()
     {
        return offline;
     }
    //-------------------------------------------------------------------------
    /**
       Get the value of the Selected Shipping method
       @return the value of methodsList
    **/
    //-------------------------------------------------------------------------
    public ShippingMethodIfc getSelectedShipMethod()
    {
        return methodsList[selectedIndex];
    }
   

    //-------------------------------------------------------------------------
    /**
       Get the value of the shipVia field
       @return the value of shipMethod
    **/
    //----------------------------------------------------------------------------
    public ShippingMethodIfc[] getShipMethodsList()
    {
       return methodsList;
    }

    //----------------------------------------------------------------------------
    /**
       Get the value of the ShippingCharge field
       @return the value of shippingCharge
    **/
    //----------------------------------------------------------------------------
    public CurrencyIfc getShippingCharge()
    {
    	// Changes start for Rev 1.0 (Ashish : Send)

       // calculate shiping charge base on parameter
       /*CurrencyIfc baseCharge = methodsList[selectedIndex].getBaseShippingCharge();
       CurrencyIfc weightRate = methodsList[selectedIndex].getShippingChargeRateByWeight();
       if ( !offline)
       {
          if (calculationType.compareTo(ShippingMethodConstantsIfc.FLAT_RATE) == 0)
          {
             shippingCharge = methodsList[selectedIndex].getFlatRate();
          }
          else if (calculationType.compareTo(ShippingMethodConstantsIfc.WEIGHT) == 0)
          {
             //  shippingCharge = baseCharge +  (itemsWeight * weightRate)
             shippingCharge = baseCharge.add(itemsCharge.multiply(weightRate));
          }
          else if (calculationType.compareTo(ShippingMethodConstantsIfc.DOLLAR_AMOUNT) == 0)
          {
            //  shippingCharge = baseCharge +  itemsCharge;
            shippingCharge = baseCharge.add(itemsCharge);
          }
          else
          {
              shippingCharge = DomainGateway.getBaseCurrencyInstance("0.00");
          }
       }
       else
       {
         shippingCharge = getDefaultShippingCharge();
       }
       return shippingCharge;*/
    	{
    		CurrencyIfc baseCharge = methodsList[selectedIndex].getBaseShippingCharge();
    		if ( offline ){
    		shippingCharge = DomainGateway.getBaseCurrencyInstance("0.00");
    		}
    		else{
    			shippingCharge = methodsList[selectedIndex].getBaseShippingCharge();
    		}
    		return shippingCharge;
    	}
    	// Changes start for Rev 1.0 (Ashish : Send)
    }

    //-------------------------------------------------------------------------
    /**
       Sets the itemSum  shipping charge
       @param value Currency
     **/
    //-------------------------------------------------------------------------
     public void setItemsShippingCharge(CurrencyIfc value)
     {
        itemsCharge = value;
     }



    //-------------------------------------------------------------------------
    /**
       Sets offline status
       @param offline boolean
    **/
    //-------------------------------------------------------------------------
     public void setOffline(boolean value)
     {
        offline = value;
     }
    //-------------------------------------------------------------------------
    /**
       Set the index of the selected ship method
       @param int the index value
    **/
    //-------------------------------------------------------------------------
    public void setSelectedShipMethod(int value)
    {
        selectedIndex = value;
    }

    

    //-------------------------------------------------------------------------
    /**
       Sets the value of the shipVia field
       @param String  shipMethod
    **/
    //----------------------------------------------------------------------------
    public void setShipMethodsList(ShippingMethodIfc value[])
    {
       methodsList = value;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the value of the ShippingCharge field
       @param String shippingCharge
    **/
    //----------------------------------------------------------------------------
    public void setShippingCharge(CurrencyIfc value)
    {
       if (value == null)
       {
          shippingCharge = DomainGateway.getBaseCurrencyInstance("0.00");
       }
       else
       {
          shippingCharge = value;
       }
       if(methodsList!=null && selectedIndex!=0 && methodsList[selectedIndex]!=null)
    	   methodsList[selectedIndex].setCalculatedShippingCharge(shippingCharge);
    }
    

    //----------------------------------------------------------------------------
    /**
       Converts to a string representing the data in this Object
       @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
       StringBuffer buff = new StringBuffer();
       buff.append("Class: ShippingMethodBeanModel Revision: " + revisionNumber + "\n");
       if (methodsList != null && 
               selectedIndex < methodsList.length &&
               methodsList[selectedIndex].getBaseShippingCharge() !=null)
       {
          buff.append("ShippingCharge [" + methodsList[selectedIndex].getBaseShippingCharge().toString() + "]\n");
       }
       else
       {
          buff.append("ShippingCharge [null]\n");
       }
       buff.append("Offline [" + offline + "]\n");
   
       return(buff.toString());
    }

	public EYSDate getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public EYSTime getExpectedDeliveryTime() {
		return expectedDeliveryTime;
	}

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime) {
		this.expectedDeliveryTime = expectedDeliveryTime;
	}
}
