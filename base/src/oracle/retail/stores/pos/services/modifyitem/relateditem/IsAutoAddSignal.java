package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
  This signal determines if the transaction is going to add auto related items.
  <P>
  $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class IsAutoAddSignal implements TrafficLightIfc
{
  /**
      revision number of this class
  **/
  public static final String revisionNumber = "$Revision: /main/1 $";

  /**
      Signal name for toString
  **/
  public static final String SIGNALNAME = "IsAutoAddSignal";

  
  //----------------------------------------------------------------------
  /**
      Determines whether it is safe for the bus to proceed.
      <p>
      @param bus the bus trying to proceed
      @return true if Customer add find ; false otherwise
  **/
  //----------------------------------------------------------------------
  public boolean roadClear(BusIfc bus)
  {
      RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
      return cargo.isAddAutoRelatedItem();
  }

  //----------------------------------------------------------------------
  /**
      Returns the revision number of the class.
      <P>
      @return String representation of revision number
  **/
  //----------------------------------------------------------------------
  public String getRevisionNumber()
  {                                   // begin getRevisionNumber()
      // return string
      return(revisionNumber);
  }                                   // end getRevisionNumber()

  //----------------------------------------------------------------------
  /**
      Returns a string representation of the object.
      <P>
      @return String representation of object
  **/
  //----------------------------------------------------------------------
  public String toString()
  {                                   // begin toString()
      String strResult = new String("Class:  " + SIGNALNAME + " (Revision " +
                                    getRevisionNumber() +
                                    ")" + hashCode());

      return(strResult);
  }                                   // end toString()
}

