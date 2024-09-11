package max.retail.stores.domain.utility;

// java imports
import java.util.Enumeration;
import java.util.HashMap;

import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

//------------------------------------------------------------------------------
/**
 * This is the interface for the CodeListMap class.
 * <P>
 * 
 * @version $Revision: 3$
 * @see com.extendyourstore.domain.utility.CodeListMap
 * @see com.extendyourstore.domain.utility.EYSDomainIfc
 **/
// ------------------------------------------------------------------------------
public interface MAXCodeListMapIfc extends EYSDomainIfc { // Begin interface
														// CodeListMapIfc

	/**
	 * revision number supplied by Team Connection
	 **/
	public static String revisionNumber = "$Revision: 3$";

	// ---------------------------------------------------------------------
	/**
	 * Adds a CodeListIfc object to the map. The list's description is used as
	 * the key. If no duplicates exist, the key is associated with the list
	 * value and null is returned. If a duplicate exists, the map is not
	 * modified and the current entry associated with the key is returned.
	 * <P>
	 * 
	 * @param value
	 *            CodeListIfc object to be added
	 * @return duplicate CodeListIfc object, if it is in the map; otherwise null
	 *         is returned
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc add(CodeListIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Puts a CodeListIfc object into the map. If the list doesn't exist,
	 * associate the list with the key and return null; otherwise replace the
	 * first value associated with the key and return the old value.
	 * <P>
	 * 
	 * @param value
	 *            CodeListIfc object to be added
	 * @return duplicate CodeListIfc object, if it is in the map; otherwise null
	 *         is returned
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc put(CodeListIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Returns the CodeListIfc object associated with the key. If object is not
	 * found, null is returned.
	 * <P>
	 * 
	 * @param key
	 *            key to match CodeListIfc object's list description
	 * @return CodeListIfc object if found; otherwise null is returned
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc get(String key);

	// ---------------------------------------------------------------------
	/**
	 * Returns an enumeration of values in map.
	 * <P>
	 * 
	 * @return enumeration of lists
	 **/
	// ---------------------------------------------------------------------
	public Enumeration elements();

	// ---------------------------------------------------------------------
	/**
	 * Returns all the code lists in the map.
	 * <P>
	 * 
	 * @return all the code lists in the map
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc[] getLists();

	// ---------------------------------------------------------------------
	/**
	 * Inserts an array of code lists in the map.
	 * <P>
	 * 
	 * @param value
	 *            array of code lists
	 **/
	// ---------------------------------------------------------------------
	public void setLists(CodeListIfc[] value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves hash map.
	 * <P>
	 * 
	 * @return hash map
	 **/
	// ---------------------------------------------------------------------
	public HashMap getMap();

	// ---------------------------------------------------------------------
	/**
	 * Sets hash map. If null value is submitted, the map is cleared.
	 * <P>
	 * 
	 * @param value
	 *            hash map
	 **/
	// ---------------------------------------------------------------------
	public void setMap(HashMap value);

	// ---------------------------------------------------------------------
	/**
	 * Returns instance of CodeListIfc.
	 * <P>
	 * 
	 * @return instance of CodeListIfc
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc getCodeListInstance();

	// ---------------------------------------------------------------------
	/**
	 * Returns instance of CodeListIfc instantiated with specified description.
	 * <P>
	 * 
	 * @param desc
	 *            list description
	 * @return instance of CodeListIfc
	 **/
	// ---------------------------------------------------------------------
	public CodeListIfc getCodeListInstance(String desc);

	// ---------------------------------------------------------------------
	/**
	 * Returns number of lists in the map.
	 * <P>
	 * 
	 * @return number of lists in the map
	 **/
	// ---------------------------------------------------------------------
	public int getNumberOfLists();

} // End interface CodeListMapIfc
