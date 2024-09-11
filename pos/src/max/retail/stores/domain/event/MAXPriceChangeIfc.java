/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/event/MAXPriceChangeIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.event;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;

// domain imports

import oracle.retail.stores.domain.event.PriceChangeIfc;

//----------------------------------------------------------------------------
/**
 * Interface for PriceChange class.
 * <P>
 * 
 * @see com.extendyourstore.domain.event.PriceChange
 * @see com.extendyourstore.domain.utility.EYSDomainIfc
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 **/
// ----------------------------------------------------------------------------
public interface MAXPriceChangeIfc extends PriceChangeIfc { // begin interface
															// PriceChangeIfc

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves MaximumRetailPriceChange Details
	 * <P>
	 * 
	 * @return exact maximum retail price setting
	 * @since 12.0.9IN
	 **/
	// ----------------------------------------------------------------------------
	public MAXMaximumRetailPriceChangeIfc getMaximumRetailPriceChange();

	// ----------------------------------------------------------------------------
	/**
	 * Sets MaximumRetailPriceChange Details
	 * <P>
	 * 
	 * @param value
	 *            maximum retail price setting
	 * @since 12.0.9IN
	 **/
	// ----------------------------------------------------------------------------
	public void setMaximumRetailPriceChange(MAXMaximumRetailPriceChangeIfc value);
	
	public String getSpclEmpDis();
	public void setSpclEmpDis(String spclEmpDisc);

} // end interface PriceChangeIfc
