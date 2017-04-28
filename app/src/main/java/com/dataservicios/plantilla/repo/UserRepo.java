package com.dataservicios.plantilla.repo;

import android.content.Context;
import com.dataservicios.plantilla.db.DatabaseHelper;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.User;
import java.sql.SQLException;
import java.util.List;
/**
 * Created by jcdia on 21/03/2017.
 */
public class UserRepo implements Crud {
    private DatabaseHelper helper;

    public UserRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        User object = (User) item;
        try {
            index = helper.getUserDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        User object = (User) item;

        try {
            helper.getUserDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        User object = (User) item;

        try {
            helper.getUserDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<User> items = null;
        int counter = 0;
        try {
            items = helper.getUserDao().queryForAll();

            for (User object : items) {
                // do something with object
                helper.getUserDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        User wishList = null;
        try {
            wishList = helper.getUserDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<User> items = null;

        try {
            items = helper.getUserDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }



}