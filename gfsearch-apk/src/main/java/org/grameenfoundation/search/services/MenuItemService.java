package org.grameenfoundation.search.services;

import android.content.ContentValues;
import android.database.Cursor;
import org.grameenfoundation.search.model.ListObject;
import org.grameenfoundation.search.model.SearchMenu;
import org.grameenfoundation.search.model.SearchMenuItem;
import org.grameenfoundation.search.storage.DatabaseHelperConstants;
import org.grameenfoundation.search.storage.StorageManager;
import org.grameenfoundation.search.storage.search.Filter;
import org.grameenfoundation.search.storage.search.Search;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handler menu related tasks
 *
 * @author Charles Tumwebaze
 */
public class MenuItemService {

    /**
     * gets all the search menus in the system.
     *
     * @return
     */
    public List<SearchMenu> getAllSearchMenus() {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_TABLE_NAME);
        search.addSortAsc(DatabaseHelperConstants.MENU_LABEL_COLUMN);
        return buildSearchMenus(StorageManager.getInstance().getRecords(search));
    }

    private List<SearchMenu> buildSearchMenus(Cursor cursor) {
        List<SearchMenu> searchMenus = new ArrayList<SearchMenu>();
        while (cursor.moveToNext()) {
            SearchMenu searchMenu = new SearchMenu();
            searchMenu.setId(cursor.getString(cursor.getColumnIndex(DatabaseHelperConstants.MENU_ROWID_COLUMN)));
            searchMenu.setLabel(cursor.getString(cursor.getColumnIndex(DatabaseHelperConstants.MENU_LABEL_COLUMN)));

            searchMenus.add(searchMenu);
        }
        return searchMenus;
    }

    /**
     * gets the search menus starting at the given offset and ending at the given limit.
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<SearchMenu> getSearchMenus(int offset, int limit) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_TABLE_NAME);
        search.setFirstResult(offset);
        search.setMaxResults(limit);

        Cursor cursor = StorageManager.getInstance().getRecords(search);
        return buildSearchMenus(cursor);
    }

    /**
     * gets the total number of search menu items.
     *
     * @return
     */
    public int countSearchMenus() {
        return StorageManager.getInstance().recordCount(DatabaseHelperConstants.MENU_TABLE_NAME);
    }

    /**
     * saves the given search menus into the data store
     *
     * @param searchMenus
     */
    public void save(SearchMenu... searchMenus) {
        List<ContentValues> values = getContentValues(searchMenus);
        StorageManager.getInstance().replace(DatabaseHelperConstants.MENU_TABLE_NAME,
                values.toArray(new ContentValues[]{}));
    }

    private List<ContentValues> getContentValues(SearchMenu[] searchMenus) {
        List<ContentValues> values = new ArrayList<ContentValues>();
        for (SearchMenu item : searchMenus) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelperConstants.MENU_ROWID_COLUMN, item.getId());
            contentValue.put(DatabaseHelperConstants.MENU_LABEL_COLUMN, item.getLabel());

            values.add(contentValue);
        }
        return values;
    }

    /**
     * gets all the search menu items in the system.
     *
     * @return
     */
    public List<SearchMenuItem> getAllSearchMenuItems() {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        Cursor cursor = StorageManager.getInstance().getRecords(search);
        return buildSearchMenuItems(cursor);
    }

    /**
     * builds a list of search menu items from the given cursor.
     *
     * @param cursor
     * @return
     */
    private List<SearchMenuItem> buildSearchMenuItems(Cursor cursor) {
        List<SearchMenuItem> searchMenuItems = new ArrayList<SearchMenuItem>();
        while (cursor.moveToNext()) {
            SearchMenuItem searchMenuItem = new SearchMenuItem();
            searchMenuItem.setId(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_ROWID_COLUMN)));
            searchMenuItem.setLabel(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_LABEL_COLUMN)));
            searchMenuItem.setPosition(cursor.getInt(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_POSITION_COLUMN)));

            searchMenuItem.setContent(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_CONTENT_COLUMN)));

            searchMenuItem.setParentId(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN)));

            searchMenuItem.setMenuId(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN)));

            searchMenuItem.setAttachmentId(cursor.getString(cursor.
                    getColumnIndex(DatabaseHelperConstants.MENU_ITEM_ATTACHMENTID_COLUMN)));

            searchMenuItems.add(searchMenuItem);
        }
        return searchMenuItems;
    }

    /**
     * gets the search menus starting at the given offset and ending at the given limit.
     *
     * @param offset
     * @param limit
     * @return
     */
    public List<SearchMenuItem> getSearchMenuItems(int offset, int limit) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.setFirstResult(offset);
        search.setMaxResults(limit);

        Cursor cursor = StorageManager.getInstance().getRecords(search);
        return buildSearchMenuItems(cursor);
    }

    /**
     * gets the total number of search menu items in the system.
     *
     * @return
     */
    public int countSearchMenuItems() {
        return StorageManager.getInstance().recordCount(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
    }

    public List<SearchMenuItem> getSearchMenuItems(String parentMenuItemId, int offset, int limit) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN, parentMenuItemId);
        search.addSortAsc(DatabaseHelperConstants.MENU_ITEM_POSITION_COLUMN);
        search.setFirstResult(offset);
        search.setMaxResults(limit);

        Cursor cursor = StorageManager.getInstance().getRecords(search);
        return buildSearchMenuItems(cursor);
    }

    /**
     * gets the total number of search menu items for the given parent menu identifier.
     *
     * @param parentMenuItemId
     * @return
     */
    public int countSearchMenuItems(String parentMenuItemId) {
        String sql = String.format("SELECT COUNT(*) as total FROM %1$s WHERE %2$s = %3$s",
                DatabaseHelperConstants.MENU_TABLE_NAME, DatabaseHelperConstants.MENU_ITEM_TABLE_NAME,
                parentMenuItemId);
        Cursor cursor = StorageManager.getInstance().sqlSearch(sql);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        return count;
    }

    /**
     * gets the total number of search menu items for the given search menu.
     *
     * @param searchMenu
     * @return
     */
    public int countTopLevelSearchMenuItems(SearchMenu searchMenu) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN, searchMenu.getId());
        search.addFilterOr(Filter.isEmpty(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN));

        return StorageManager.getInstance().recordCount(search);
    }

    /**
     * gets the total number of search menu items whose parent is the
     * given search menu item.
     *
     * @param searchMenuItem
     * @return
     */
    public int countSearchMenuItems(SearchMenuItem searchMenuItem) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN, searchMenuItem.getId());
        search.addSortAsc(DatabaseHelperConstants.MENU_ITEM_POSITION_COLUMN);

        return StorageManager.getInstance().recordCount(search);
    }


    /**
     * saves the given search menu items into the data store
     *
     * @param searchMenuItems
     */
    public void save(SearchMenuItem... searchMenuItems) {
        List<ContentValues> values = getContentValues(searchMenuItems);

        StorageManager.getInstance().replace(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME,
                values.toArray(new ContentValues[]{}));
    }

    private List<ContentValues> getContentValues(SearchMenuItem[] searchMenuItems) {
        List<ContentValues> values = new ArrayList<ContentValues>();
        for (SearchMenuItem item : searchMenuItems) {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_ROWID_COLUMN, item.getId());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_LABEL_COLUMN, item.getLabel());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_POSITION_COLUMN, item.getPosition());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_CONTENT_COLUMN, item.getContent());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN, item.getMenuId());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN, item.getParentId());
            contentValue.put(DatabaseHelperConstants.MENU_ITEM_ATTACHMENTID_COLUMN, item.getAttachmentId());

            values.add(contentValue);
        }
        return values;
    }

    public void deleteSearchMenuItems(SearchMenuItem... searchMenuItems) {
        for (SearchMenuItem searchMenuItem : searchMenuItems) {
            StorageManager.getInstance().execSql("DELETE FROM " + DatabaseHelperConstants.MENU_ITEM_TABLE_NAME +
                    " WHERE " + DatabaseHelperConstants.MENU_ITEM_ROWID_COLUMN + " ='" + searchMenuItem.getId() + "'");
        }
    }

    public void deleteSearchMenus(SearchMenu... searchMenus) {
        for (SearchMenu searchMenu : searchMenus) {
            StorageManager.getInstance().execSql("DELETE FROM " + DatabaseHelperConstants.MENU_TABLE_NAME +
                    " WHERE " + DatabaseHelperConstants.MENU_ROWID_COLUMN + " ='" + searchMenu.getId() + "'");
        }
    }


    /**
     * delete search menu items for the given search menu.
     *
     * @param searchMenu
     */
    public void deleteSearchMenuItems(SearchMenu searchMenu) {
        StorageManager.getInstance().execSql("DELETE FROM " + DatabaseHelperConstants.MENU_ITEM_TABLE_NAME +
                " WHERE " + DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN + " ='" + searchMenu.getId() + "'");
    }

    public List<SearchMenuItem> getTopLevelSearchMenuItems(SearchMenu searchMenu) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN, searchMenu.getId());
        search.addFilterOr(Filter.isEmpty(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN));

        search.addSortAsc(DatabaseHelperConstants.MENU_ITEM_LABEL_COLUMN);
        return buildSearchMenuItems(StorageManager.getInstance().getRecords(search));
    }

    public List<SearchMenuItem> getSearchMenuItems(SearchMenuItem searchMenuItem) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);
        search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN, searchMenuItem.getId());
        search.addSortAsc(DatabaseHelperConstants.MENU_ITEM_LABEL_COLUMN);

        return buildSearchMenuItems(StorageManager.getInstance().getRecords(search));
    }

    /**
     * checks whether the given list object has children.
     *
     * @param listObject
     * @return
     */
    public boolean hasChildren(ListObject listObject) {
        Search search = new Search();
        search.setTableName(DatabaseHelperConstants.MENU_ITEM_TABLE_NAME);

        if (listObject instanceof SearchMenu) {
            search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_MENUID_COLUMN, listObject.getId());
            search.addFilterOr(Filter.isEmpty(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN));
        } else if (listObject instanceof SearchMenuItem) {
            search.addFilterEqual(DatabaseHelperConstants.MENU_ITEM_PARENTID_COLUMN, listObject.getId());
        }

        int count = StorageManager.getInstance().recordCount(search);
        return count > 0 ? true : false;
    }
}
