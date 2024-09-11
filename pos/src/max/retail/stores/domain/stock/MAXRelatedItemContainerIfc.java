/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 2005 360Commerce, Inc.    All Rights Reserved.

     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:57 PM  Barry A. Pape   
     $
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.stock;

import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * This is the parent of the relatedItem structures. The relatedItemContainer
 * contains groups. A group is uniquely defined by a typeCode and a groupNumber.
 * 
 * @since NEP67
 * @author jdeleau $Revision: 1$
 */
public interface MAXRelatedItemContainerIfc extends EYSDomainIfc {
	/**
	 * Get a specific relatedItemGroup.
	 * 
	 * @param relatedItemTypeCode
	 *            The type code, defined as a constant in the
	 *            RelatedItemGroupIfc.
	 * @param groupNumber
	 *            The number of the group
	 * @return RelatedItemGroup
	 * @since NEP67
	 */
	public RelatedItemGroupIfc getRelatedItemGroup(String relatedItemTypeCode, int groupNumber);

	/**
	 * Get a list of all RelatedItemGroups that match the given typeCode. Every
	 * entry in the array thats returned will have a different group number.
	 * 
	 * @param relatedItemTypeCode
	 *            The type code, defined as a constant in RelatedItemGroupIfc
	 * @return array of related item groups.
	 * @since NEP67
	 */
	public RelatedItemGroupIfc[] getRelatedItemGroups(String relatedItemTypeCode);

	/**
	 * Get all related item groups, regardless of type code or group number.
	 * 
	 * @return List of all related item groups.
	 * @since NEP67
	 */
	public RelatedItemGroupIfc[] getAllRelatedItemGroups();

	/**
	 * Add a related item to the container. The related item will automatically
	 * be put in the correct RelatedItemGroup. If the group doesn't exist, it is
	 * created.
	 * 
	 * @param relatedItemTypeCode
	 *            Type code for the related item being added.
	 * @param groupNumber
	 *            Group number for the related item being added.
	 * @param relatedItem
	 *            RelatedItem to add to the container.
	 * @since NEP67
	 */
	public void addRelatedItem(String relatedItemTypeCode, int groupNumber, RelatedItemIfc relatedItem);

	/**
	 * Get all related items for a given type code. This will get related items
	 * regardless of the type code.
	 * 
	 * @param relatedItemTypeCode
	 * @return list of related items.
	 * @since NEP67
	 */
	public RelatedItemIfc[] getRelatedItems(String relatedItemTypeCode);
}
