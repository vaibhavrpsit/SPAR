/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/MerchandiseHierarchyDataTransaction.java /main/18 2012/09/14 12:12:16 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 09/13/12 - Retrieve kit item price using PLULookup
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sgu    10/21/08 - add locale support for merchandise hierarchy
 *    sgu    10/21/08 - add locale support for merchandise hierarchy
 *    sgu    10/20/08 - add locale support merchandise hierarchy
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      8    360Commerce 1.7         5/27/2008 6:51:15 AM   Naveen Ganesh
           Converted the groupId variable type from int to String
      7    360Commerce 1.6         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      6    360Commerce 1.5         1/17/2008 5:01:56 AM   Manikandan Chellapan
           PSI MerchandiseHierarchyTest Fixes
      5    360Commerce 1.4         11/22/2007 11:09:31 PM Naveen Ganesh   PSI
           Code checkin
      4    360Commerce 1.3         6/5/2007 12:07:12 PM   Christian Greene
           Groupd id is now a String. Fix merchandise hierarchy jdbc classes
           and test class.
      3    360Commerce 1.2         3/31/2005 4:29:02 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:29 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:35 PM  Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:45  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   16 Jun 2003 02:38:34   mwright
 * Added new method fetchAllGroupIDs(MerchandiseHierarchyLevelIfc level, String itemID)
 *
 *    Rev 1.1   May 26 2003 17:12:28   mwright
 * 1. Added standard header.
 * 2. Use standard toString().
 * 3. Return null (instead of throwing exception) when fetchAllGroupIDs() finds nothing
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

// java imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.stock.ItemInfo;
import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyGroupIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelKeyIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyTreeIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;


/**
 * DataTransaction to perform persistent operations on
 * store group information.
 * <p>
 * <b>Note:</b> This class is not public which means it can
 * only be used by other classes in the MerchandiseHierarchy package -
 * this is intentional. The MerchandiseHierarchy &amp related classes
 * cache the contents of the database localy, and any direct calls to
 * the database with this class will not be reflected in the caches.<p>
 * @version $Revision: /main/18 $
**/
public class MerchandiseHierarchyDataTransaction extends DataTransaction
{
    /**
     * Serial version id
     */
    private static final long serialVersionUID = -7781810394252805758L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.MerchandiseHierarchyDataTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/18 $";

    /**
       The name that links this transaction to a command within DataScript.
    **/
    public static String dataCommandName = "MerchandiseHierarchyDataTransaction";

    /**
     * private debugging attributes
     */
    private String methodName = "MerchandiseHierarchyDataTransaction.null";

    /**
     *  default constructor
    **/
    public MerchandiseHierarchyDataTransaction()
    {
        super(dataCommandName);
    }

    /*****************************************************************************
     * String identification of this class.<p>
     * @return A string identifying the class and revision number.
     ****************************************************************************/
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader(
         "MerchandiseHierarchyDataTransaction",
         getRevisionNumber(),
         hashCode());
      return strResult.toString();
    }


    /**
     * Retrieves the source-code-control system revision number. <P>
     * @return String representation of revision number
     **/
    public String getRevisionNumber() {
      return Util.parseRevisionNumber(revisionNumber);
    }

    // Possible Parameters
    int paramHierarchyID = -1;
    String paramHierarchyName = null;


    String paramLevelName = null;
    MerchandiseHierarchyLevelKeyIfc paramLevelKey       = null;
    MerchandiseHierarchyLevelKeyIfc paramChildLevelKey  = null;
    MerchandiseHierarchyLevelKeyIfc paramParentLevelKey = null;

    String paramGroupID        = "-1";
    String paramParentGroupID  = "-1";
    String paramChildGroupID   = "-1";
    String paramGroupName   = null;

    String paramItemID = null;
    boolean paramSelectAccumulated = false;

    //POS-SIM

    String paramLevelID = null;

    LocaleRequestor localeReq = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));

    /**
     * Send the DataOperation request through the membrane for execution.
     * @param actionName  the name of the dataAction that's to to be executed.
     * @return some serializable object depending upon the dataAction.
     * @throws DataException on any database error.
     */
    private Serializable executeCommand(String actionName) throws DataException
    {
      // Make the data object to send through the membrane
      DataActionIfc[] dataActions = new DataActionIfc[1];
      DataAction da = new DataAction();
      da.setDataOperationName(actionName);

      // da.setDataObject(null);   // Don't have a dataObject to send
      dataActions[0] = da;

      // Go do it
      setDataActions(dataActions);

      return getDataManager().execute(this);
    }

    /**
     *
     */
    private void initializeParams()
    {
      this.paramHierarchyID = -1;
      this.paramHierarchyName = null;

      this.paramGroupID       = "-1";
      this.paramParentGroupID = "-1";
      this.paramChildGroupID  = "-1";
      this.paramGroupName     = null;

      this.paramLevelKey       = null;
      this.paramChildLevelKey  = null;
      this.paramParentLevelKey = null;
      this.paramLevelName      = null;
      //POS-SIM
      this.paramLevelID      = null;

      this.paramItemID = null;
      this.paramSelectAccumulated = false;
    }

///
///   Store Group Tree - methods
///

    /**
     * Fetch tbe nominateed MerchandiseHierarchyTree definition from the database.
     * @param hierarchyID - The unique identifier for the requested hierarchyTree
     * @return MerchandiseHierarchyTree - the requested tree.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public MerchandiseHierarchyTreeIfc fetchHierarchy(int hierarchyID) throws DataException
    {
      // figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchHierarchy(int)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchyID;

      // Go do it
      MerchandiseHierarchyTreeIfc result = (MerchandiseHierarchyTreeIfc) executeCommand("FetchMerchandiseHierarchy");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }

    /**
     * Fetch tbe nominateed MerchandiseHierarchyTree definition from the database.
     * @param hierarchyName - The name of the requested hierarchyTree
     * @return MerchandiseHierarchyTree - the requested tree.
     * @throws DataException on any database error.
     */
    public MerchandiseHierarchyTreeIfc fetchHierarchy(String hierarchyName) throws DataException
    {
      // figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchHierarchy(String)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyName = hierarchyName;

      // Go do it
      MerchandiseHierarchyTreeIfc result = (MerchandiseHierarchyTreeIfc) executeCommand("FetchMerchandiseHierarchy");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     * Fetch a list of children groups for the given MerchandiseHierarchyGroup.
     * <strong>Note:</strong> A particular MerchandiseHierarchyGroup may appear
     * in more than one MerchandiseHierarchy (Eg: All SubClasses appear in both
     * the "Sales Reporting"  and "Merchandise Management" hierarchies.  With each
     * of those hierarchies having different structures above the common SubClass
     * groups.
     *
     * @param hierarchy  The hierarchy to search for the given groups children groups.
     * @param parent The group to search the hierarchy for children.
     * @return MerchandiseHierarchyGroupIfc[] - The list of children groups.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public MerchandiseHierarchyGroupIfc[] fetchChildrenGroups(MerchandiseHierarchyTreeIfc hierarchy,
                                                              MerchandiseHierarchyGroupIfc parent) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchChildrenGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchy.getHierarchyID();
      this.paramParentGroupID = parent.getID();

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchChildrenMerchandiseHierarchyGroups");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      MerchandiseHierarchyGroupIfc[] result = new MerchandiseHierarchyGroupIfc[list.size()];
      return (MerchandiseHierarchyGroupIfc[]) list.toArray(result);
    }

   /**
    * @deprecated As of 13.1
    */
  public MerchandiseHierarchyGroupIfc fetchChildGroupNamed(MerchandiseHierarchyTreeIfc hierarchy,
                                                           MerchandiseHierarchyGroupIfc parent,
                                                           String groupName) throws DataException
  {
    // Figure out where we are
    methodName = "MerchandiseHierarchyDataTransaction.fetchChildGroupNamed()";
    if (logger.isDebugEnabled()) logger.debug( methodName);

    // Set parameters
    this.initializeParams();
    this.paramHierarchyID = hierarchy.getHierarchyID();
    this.paramParentGroupID = parent.getID();

    // Go do it
    ArrayList list =
        (ArrayList) executeCommand("FetchChildrenMerchandiseHierarchyGroups");

    // Go looking down the list
    for(int i=0; i<list.size(); i++) {
      MerchandiseHierarchyGroupIfc child = (MerchandiseHierarchyGroupIfc) list.get(i);
      if(child.getGroupName().equals(groupName)) {
        // We're done
        if (logger.isDebugEnabled()) logger.debug( methodName);
        return child;
      }
    }

    // It's not there
    if (logger.isDebugEnabled()) logger.debug( methodName);
    return null;
  }


  /**
   * Fetch a list of IDs for those items that're members of the given MerchandiseHierarchyGroup.
   * @param hierarchy - The hierarchy to be searched for the required Item IDs.
   * @param group - The group to be searched from.
   * @return Integer[] - A list of ItemIDs that're all members of the given MerchandiseHierarchyGroup.
   * <strong>Note:</strong> The returned list includes all Items that're members of groups that're children
   * of the given group, as well as those Items that're direct members of the given group.
   * @throws DataException on any database error.
   */
  public Integer[] fetchMemberItemIDs(MerchandiseHierarchyTreeIfc hierarchy,
                                        MerchandiseHierarchyGroupIfc group) throws DataException
  {
    // Figure out where we are
    methodName = "MerchandiseHierarchyDataTransaction.fetchMemberItemIDs()";
    if (logger.isDebugEnabled()) logger.debug( methodName);

    // Set parameters
    this.initializeParams();
    this.paramHierarchyID = hierarchy.getHierarchyID();
    this.paramGroupID = group.getID();

    // Go do it
    ArrayList list =
        (ArrayList) executeCommand("FetchMerchandiseHierarchyGroupItemIDs");

    // We're done
    Integer[] result = new Integer[list.size()];
    return (Integer[]) list.toArray(result);
  }

    /**
     * Fetch a list of MerchandiseHierarchyGroups in the given MerchandiseHierarchy that
     * the given ItemID is a member of.
     *
     * @param hierarchy The hierarchy to search for the required groups.
     * @param itemID The item to search against.
     * database as being accumulated.
     * @return MerchandiseHierarchyGroupIfc[] the list of groups.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public String[] fetchAllGroupIDs(MerchandiseHierarchyTreeIfc hierarchy,
                                      String itemID) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchAllGroupIDs(Item)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchy.getHierarchyID();
      this.paramItemID = itemID;

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchAllMerchandiseHierarchyGroupIDs");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // There may not be any
      if(list == null)
         return null;

      // Turn the arrayList into a proper array.
      return (String[]) list.toArray(new String[list.size()]);
    }


    /**
     * Fetch a list of all MerchandiseHierarchyGroups that're members of the
     * given MerchandiseHierarchyLevel.
     * @param level The MerchandiseHierarchyLevel to search for member groups.
     * @return MerchandiseHierarchyGroupIfc[] the list groups.
     * @throws DataException on any database error.
     */
    public String[] fetchAllGroupIDs(MerchandiseHierarchyLevelIfc level) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchAllGroups(Level)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramLevelKey = level.getLevelKey();

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchAllMerchandiseHierarchyGroupIDs");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return (String[]) list.toArray(new String[list.size()]);
    }


    /**
     * Fetch a list of MerchandiseHierarchyGroups in the given MerchandiseHierarchy that
     * the given ItemID is a member of AND are in the given level.
     *
     * @param level The MerchandiseHierarchyLevel to search for member groups.
     * @param itemID The item to search against.
     * database as being accumulated.
     * @return MerchandiseHierarchyGroupIfc[] the list of groups.
     * @throws DataException on any database error.
     */
    public String[] fetchAllGroupIDs(MerchandiseHierarchyLevelIfc level,
                                      String itemID) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchAllGroupIDs(Level, Item)";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = level.getLevelKey().getHierarchyID();
      this.paramLevelKey = level.getLevelKey();
      this.paramItemID = itemID;

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchAllMerchandiseHierarchyGroupIDs");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // There may not be any
      if(list == null)
         return null;

      // Turn the arrayList into a proper array.
      return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Create a new MerchandiseHierarchy in the database.
     * @param merchandiseHierarchyName The name of the new MerchandiseHierarchyTree to be created.
     * @return MerchandiseHierarchyTreeIfc - The newly created MerchandiseHierarchyTree.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public MerchandiseHierarchyTreeIfc createHierarchy(String merchandiseHierarchyName) throws DataException
    {
      // figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.createHierarchy()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyName = merchandiseHierarchyName;

      // Go do it
      MerchandiseHierarchyTreeIfc result = (MerchandiseHierarchyTreeIfc) executeCommand("CreateMerchandiseHierarchy");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }

    /**
     * Create a new MerchandiseHierarchyLevel in the database, as a child
     * of the given parent level.  Any existing child of the nominated parent
     * becomes a grand-child of that parent.
     * @param parentLevel The parent level for the new level.
     * @param levelName The name of the level to be created.
     * @return The newly created MerchandiseHierarchy level.
     * @throws DataException
     * @deprecated As of 13.1
     */
    public MerchandiseHierarchyLevelIfc createLevel(MerchandiseHierarchyLevelIfc parentLevel,
                                                    String levelName) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.createLevel()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set Parameters
      this.initializeParams();
      this.paramLevelName = levelName;
      this.paramParentLevelKey = parentLevel.getLevelKey();


      // Go do it
      MerchandiseHierarchyLevelIfc result =
          (MerchandiseHierarchyLevelIfc) executeCommand("CreateMerchandiseHierarchyLevel");

      // Now that it's been addded to the database, we can add it to the in-memory structure
      parentLevel.addChildLevel(result);

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return result;
    }


    /**
     * Create a new MerchandiseHierarchyGroup in the database as a child of
     * the nominated parent in the given hierarchy.
     * @param hierarchy the hierarchy the association between parent &amp; new child will be made in.
     * @param parent The parent MerchandiseHierarchyGroup for the new group.
     * @param groupName The name of the new MerchandiseHierarchyGroup
     * @return The newly made MerchandiseHierarchyGroup
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public MerchandiseHierarchyGroupIfc createGroup(MerchandiseHierarchyTreeIfc hierarchy,
                                                    MerchandiseHierarchyGroupIfc parent,
                                                    String groupName) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.createGroup()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure the parentLevel has a childLevel to put the new child into.
      MerchandiseHierarchyLevelIfc parentLevel =
          hierarchy.getLevel(parent.getLevelKey());
      if(parentLevel.getChildLevel() == null)
        throw new DataException(DataException.UNKNOWN, "No level defined for new group: " + groupName);


      // Set the parameters
      this.initializeParams();
      this.paramParentGroupID = parent.getID();
      this.paramParentLevelKey = parentLevel.getLevelKey();
      this.paramGroupName = groupName;

      // Go do it
      MerchandiseHierarchyGroupIfc result =
          (MerchandiseHierarchyGroupIfc) executeCommand("CreateMerchandiseHierarchyGroup");

        // We're done
        if (logger.isDebugEnabled()) logger.debug( methodName);
        return result;
      }

    /**
     * Remove a MerchandiseHierarchyTree (and all it's sole member groups from the database.
     * @param hierarchy The MerchandiseHierarchy to be removed.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public void removeHierarchy(MerchandiseHierarchyTreeIfc hierarchy) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.removeHierarchy()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      initializeParams();
      paramHierarchyID = hierarchy.getHierarchyID();

      // Go do it
      executeCommand("RemoveMerchandiseHierarchy");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }


    /**
     * Remove a particular level from the database. Any existing child level
     * replaces the level as parent's child.
     * @param level The level being removed.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public void removeLevel(MerchandiseHierarchyLevelIfc level) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.removeLevel()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set Parameters
      MerchandiseHierarchyLevelIfc parentLevel = level.getParentLevel();
      MerchandiseHierarchyLevelIfc childLevel  = level.getChildLevel();
      this.initializeParams();
      this.paramLevelKey = level.getLevelKey();
      if(parentLevel != null)
        this.paramParentLevelKey = parentLevel.getLevelKey();
      if(childLevel != null)
        this.paramChildLevelKey = childLevel.getLevelKey();

      // Go do it
      executeCommand("RemoveMerchandiseHierarchyLevel");

      // Parent no longer has that node as a child
      if(parentLevel != null)
        parentLevel.setChildLevel(childLevel);

      // Child no longer has that node as a parent
      if(childLevel != null)
          childLevel.setParentLevel(parentLevel);

      // Level is now a zombie:
      level.setChildLevel(null);
      level.setParentLevel(null);
      level.setHierarchy(null);
      level.setLevelName("Zombie");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }

    /**
     * Remove the given group from the hierarchy in the database.
     * <strong>Note:</strong> This not only removes the group
     * from it's parent group in the given hierarchy, but removes the group as well.
     * @param hierarchy The hierarchy that the group associations are to be removed from.
     * @param group The group to be removed.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public void removeGroup(MerchandiseHierarchyTreeIfc hierarchy,
                            MerchandiseHierarchyGroupIfc group) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.removeGroup()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchy.getHierarchyID();
      this.paramGroupID = group.getID();

      group.getLevelKey();

      // Go do it
      executeCommand("RemoveMerchandiseHierarchyGroup");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }


    /**
     * Remove the association between two groups from the database for the nominated
     * hierarchy.
     * @param hierarchy  The hierarchy in which the nominated association is to be removed.
     * @param parent The parent merchandise hierarchy group of the association to be removed.
     * @param child The child merchandise hierarchy group of the association to be removed.
     * @throws DataException on any database error.
     * @deprecated As of 13.1
     */
    public void deAssociateGroups(MerchandiseHierarchyTreeIfc hierarchy,
                                  MerchandiseHierarchyGroupIfc parent,
                                  MerchandiseHierarchyGroupIfc child) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.deAssociateGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchy.getHierarchyID();
      this.paramParentGroupID = parent.getID();
      this.paramChildGroupID  = child.getID();

      // Go do it
      executeCommand("DeAssociateMerchandiseHierarchyGroups");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }


    /**
     * Link the two given merchandise hierarchy groups as parent &amp; child
     * in the database for the given MerchandiseHierarchy.
     * @param hierarchy - The hierarchy the two groups are to be linked in.
     * @param parent - The parent group to be linked.
     * @param child - The child group to be linked.
     * @throws DataException  on any database error.
     * @deprecated As of 13.1
     */
    public void associateGroups(MerchandiseHierarchyTreeIfc hierarchy,
                                MerchandiseHierarchyGroupIfc parent,
                                MerchandiseHierarchyGroupIfc child) throws DataException
    {
      // Figure out where we are
      methodName =  "MerchandiseHierarchyDataTransaction.associateGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Make sure the parentLevel has a childLevel to put the new child into.
      MerchandiseHierarchyLevelIfc parentLevel =
          hierarchy.getLevel(parent.getLevelKey());
      if(parentLevel.getChildLevel() == null)
        throw new DataException(DataException.UNKNOWN, "No level defined for association of : " + child.getGroupName());

      // Set the parameters
      this.initializeParams();
      this.paramParentGroupID = parent.getID();
      this.paramChildGroupID  = child.getID();
      this.paramParentLevelKey = parent.getLevelKey();

      // Go do it
      executeCommand("AssociateMerchandiseHierarchyGroups");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);
      return;
    }

    //--------------------------------------------------------------------------------------------------------
    /**
     * Fetch a list of children groups for the given MerchandiseHierarchyGroup.
     * <strong>Note:</strong> A particular MerchandiseHierarchyGroup may appear
     * in more than one MerchandiseHierarchy (Eg: All SubClasses appear in both
     * the "Sales Reporting"  and "Merchandise Management" hierarchies.  With each
     * of those hierarchies having different structures above the common SubClass
     * groups.
     *
     * @param hierarchy  The hierarchy to search for the given groups children groups.
     * @param parent The group to search the hierarchy for children.
     * @return MerchandiseHierarchyGroupIfc[] - The list of children groups.
     * @throws DataException on any database error.
     * @deprecated As of release 13.1
     */
    //---------------------------------------------------------------------------------------------------------


    public ArrayList fetchChildrenGroups(int hierarchyID,String groupID) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchChildrenGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchyID;
      this.paramParentGroupID = groupID;

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchChildrenMerchandiseHierarchyGroupIDs");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);

      return list;
    }

    //--------------------------------------------------------------------------------------------------------
    /**
     * Fetch a list of children groups for the given MerchandiseHierarchyGroup.
     * <strong>Note:</strong> A particular MerchandiseHierarchyGroup may appear
     * in more than one MerchandiseHierarchy (Eg: All SubClasses appear in both
     * the "Sales Reporting"  and "Merchandise Management" hierarchies.  With each
     * of those hierarchies having different structures above the common SubClass
     * groups.
     *
     * @param hierarchyID  The hierarchy id to search for the given groups children groups.
     * @param levelID The level id to search for the given groups children groups.
     * @param groupID The group to search the hierarchy for children.
     * @return ArrayList - The list of children groups.
     * @throws DataException on any database error.
     */
    //---------------------------------------------------------------------------------------------------------

    public ArrayList fetchChildrenGroups(int hierarchyID,String levelID, String groupID, LocaleRequestor localeReq) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.fetchChildrenGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchyID;
      this.paramParentGroupID = groupID;
      this.paramLevelID = levelID;
      this.localeReq = localeReq;

      // Go do it
      ArrayList list =
          (ArrayList) executeCommand("FetchChildrenMerchandiseHierarchyGroupIDs");

      // We're done
      if (logger.isDebugEnabled()) logger.debug( methodName);

      return list;
    }

    //--------------------------------------------------------------------------------------------------------
    /**
     * Fetch a list of children groups for the given MerchandiseHierarchyGroup.
     * <strong>Note:</strong> A particular MerchandiseHierarchyGroup may appear
     * in more than one MerchandiseHierarchy (Eg: All SubClasses appear in both
     * the "Sales Reporting"  and "Merchandise Management" hierarchies.  With each
     * of those hierarchies having different structures above the common SubClass
     * groups.
     *
     * @param hierarchyID  The hierarchy id to search for the given groups children groups.
     * @param levelID The level id to search for the given groups children groups.
     * @param groupID The group to search the hierarchy for children.
     * @return ArrayList - The list of children groups.
     * @throws DataException on any database error.
     * @deprecated As of 13.1 Use {@link MerchandiseHierarchyDataTransaction#fetchChildrenGroups(int, String, String, LocaleRequestor)}
     */
    //---------------------------------------------------------------------------------------------------------

    public ArrayList fetchChildrenGroups(int hierarchyID,String levelID, String groupID) throws DataException
    {
    	Locale defLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    	return fetchChildrenGroups(hierarchyID, levelID, groupID, new LocaleRequestor(defLocale));
    }


    //----------------------------------------------------------------------------------------------------------
    /**
     * Fetch a list of items for those items that're matches the search criteria.
     * @param criteria - items search criteria
     * @return ItemInfo[] - A list of Items that're all matches search criteria.
     * @throws DataException on any database error.
     */
    //-----------------------------------------------------------------------------------------------------------

    public ItemInfo[] fetchAllLevelItemIDs(ItemInquirySearchCriteriaIfc criteria) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(criteria);
        dataAction.setDataOperationName("FetchAllLevelItems");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);

        // get the result
        ItemInfo[] list = (ItemInfo[]) getDataManager().execute(this);
        for(ItemInfo item : list)
        {                 
            // If it is a kit item get the kit components and calculate prices
            if (item.getKitHeaderCode() == ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER)
            {
                SearchCriteriaIfc inquiry = new SearchCriteria();
                inquiry.setStoreNumber(criteria.getStoreNumber());
                inquiry.setItemID(item.getItemID());
                inquiry.setSearchItemByItemID(true);
                inquiry.setGeoCode(criteria.getGeoCode());
                
                dataAction = new DataAction();
                dataAction.setDataObject(inquiry);
                dataAction.setDataOperationName("PLULookup");

                dataActions = new DataActionIfc[1];
                dataActions[0] = dataAction;

                setDataActions(dataActions);

                // get the result
                PLUItemIfc[] pluItems = (PLUItemIfc[]) getDataManager().execute(this);
                PLUItem plu = (PLUItem) pluItems[0];
                item.setCurrentPrice(plu.getSellingPrice());
                item.setActualPrice(plu.getPermanentPrice());

            }
        }

        // send back the result
        return list;
      }

    //-------------------------------------------------------------------------------------------------------
    /**
     * Fetches an item's actual and promotion end date
     * @param criteria - items search criteria
     * @return ItemInfoIfc - The item infomation
     * @throws DataException on any database error.
     */
    //-------------------------------------------------------------------------------------------------------e

    public ItemInfoIfc fetchItemPriceInfo(ItemInquirySearchCriteriaIfc criteria) throws DataException
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(criteria);
        dataAction.setDataOperationName("FetchItemPriceInfo");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;

        setDataActions(dataActions);

        // Get the item price info
        ItemInfoIfc item = (ItemInfoIfc) getDataManager().execute(this);

        // send back the result
        return item;
      }

    //-------------------------------------------------------------------------------------------------
    /**
     * Fetches levels for the given MerchandiseHierarchy.
     * @param hierarchyID - The hierarchy id
     * @throws DataException  on any database error.
     */
    //-------------------------------------------------------------------------------------------------

    public ArrayList getMerchandiseHierarchyLevels(int hierarchyID, LocaleRequestor localeReq) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.getMerchandiseHierarchyLevels()";
      if (logger.isDebugEnabled()) logger.debug(methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchyID;
      this.localeReq = localeReq;

      // Fetch the levels
      ArrayList level = (ArrayList) executeCommand("FetchMerchandiseHierarchyLevels");

      if (logger.isDebugEnabled()) logger.debug(methodName);

      // return the levels
      return level;
    }

    //-------------------------------------------------------------------------------------------------
    /**
     * Fetches levels for the given MerchandiseHierarchy.
     * @param hierarchyID - The hierarchy id
     * @throws DataException  on any database error.
     * @deprecated As of 13.1 Use {@link MerchandiseHierarchyDataTransaction#getMerchandiseHierarchyLevels(int, LocaleRequestor)}
     */
    //-------------------------------------------------------------------------------------------------

    public ArrayList getMerchandiseHierarchyLevels(int hierarchyID) throws DataException
    {
    	Locale defLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    	return getMerchandiseHierarchyLevels(hierarchyID, new LocaleRequestor(defLocale));
    }

//  -------------------------------------------------------------------------------------------------
    /**
     * Fetches children for a given MerchandiseHierarchy level.
     * @param hierarchyID - The merchandise hierarchy's id
     * @param levelID - The merchandise hierarchy level
     * @throws DataException  on any database error.
     */
    //-------------------------------------------------------------------------------------------------
    public ArrayList getMerchandiseHierarchyLevelGroups(int hierarchyID,String levelID,LocaleRequestor localeReq) throws DataException
    {
      // Figure out where we are
      methodName = "MerchandiseHierarchyDataTransaction.getMerchandiseHierarchyLevelGroups()";
      if (logger.isDebugEnabled()) logger.debug( methodName);

      // Set parameters
      this.initializeParams();
      this.paramHierarchyID = hierarchyID;
      this.paramLevelID = levelID;
      this.localeReq = localeReq;

      // Get the groups
      ArrayList groups = (ArrayList) executeCommand("FetchMerchandiseHierarchyLevelGroups");

      if (logger.isDebugEnabled()) logger.debug( methodName);

      // return the groups
      return groups;
    }

    //-------------------------------------------------------------------------------------------------
    /**
     * Fetches children for a given MerchandiseHierarchy level.
     * @param hierarchyID - The merchandise hierarchy's id
     * @param levelID - The merchandise hierarchy level
     * @throws DataException  on any database error.
     * @deprecated As of 13.1 Use {@link MerchandiseHierarchyDataTransaction#getMerchandiseHierarchyLevelGroups(int, String, LocaleRequestor)}
     */
    //-------------------------------------------------------------------------------------------------
    public ArrayList getMerchandiseHierarchyLevelGroups(int hierarchyID,String levelID) throws DataException
    {
    	Locale defLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    	return getMerchandiseHierarchyLevelGroups(hierarchyID, levelID, new LocaleRequestor(defLocale));
    }
  }
