package com.dataservicios.plantilla.repo;

import android.content.Context;

import com.dataservicios.plantilla.db.DatabaseHelper;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.AuditRoadStore;
import com.dataservicios.plantilla.model.Store;


import java.sql.SQLException;
import java.util.List;

/**
 * Created by jcdia on 22/05/2017.
 */

public class AuditRoadStoreRepo implements Crud {
    private DatabaseHelper helper;

    public AuditRoadStoreRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        AuditRoadStore object = (AuditRoadStore) item;
        try {
            index = helper.getAuditRoadStoreDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        AuditRoadStore object = (AuditRoadStore) item;

        try {
            helper.getAuditRoadStoreDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        AuditRoadStore object = (AuditRoadStore) item;

        try {
            helper.getAuditRoadStoreDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<AuditRoadStore> items = null;
        int counter = 0;
        try {
            items = helper.getAuditRoadStoreDao().queryForAll();

            for (AuditRoadStore object : items) {
                // do something with object
                helper.getAuditRoadStoreDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        AuditRoadStore wishList = null;
        try {
            wishList = helper.getAuditRoadStoreDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<AuditRoadStore> items = null;

        try {
            items = helper.getAuditRoadStoreDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }

    public List<AuditRoadStore> findByStoreId(int store_id) {

        List<AuditRoadStore> wishList = null;
        try {
            wishList = helper.getAuditRoadStoreDao().queryBuilder().where().eq("store_id",store_id).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }

}