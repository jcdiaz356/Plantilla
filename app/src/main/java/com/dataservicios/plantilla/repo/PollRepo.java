package com.dataservicios.plantilla.repo;

import android.content.Context;

import com.dataservicios.plantilla.db.DatabaseHelper;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.Poll;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jcdia on 26/05/2017.
 */

public class PollRepo implements Crud {
    private DatabaseHelper helper;

    public PollRepo(Context context) {

        DatabaseManager.init(context);
        helper = DatabaseManager.getInstance().getHelper();
    }

    @Override
    public int create(Object item) {
        int index = -1;
        Poll object = (Poll) item;
        try {
            index = helper.getPollDao().create(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return index;
    }


    @Override
    public int update(Object item) {

        int index = -1;

        Poll object = (Poll) item;

        try {
            helper.getPollDao().update(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }


    @Override
    public int delete(Object item) {

        int index = -1;

        Poll object = (Poll) item;

        try {
            helper.getPollDao().delete(object);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return index;
    }

    @Override
    public int deleteAll() {

        List<Poll> items = null;
        int counter = 0;
        try {
            items = helper.getPollDao().queryForAll();

            for (Poll object : items) {
                // do something with object
                helper.getPollDao().deleteById(object.getId());
                counter ++ ;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counter;
    }


    @Override
    public Object findById(int id) {

        Poll wishList = null;
        try {
            wishList = helper.getPollDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishList;
    }


    @Override
    public List<?> findAll() {

        List<Poll> items = null;

        try {
            items = helper.getPollDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;

    }



}