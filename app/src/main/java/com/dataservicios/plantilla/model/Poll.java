package com.dataservicios.plantilla.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;

/**
 * Created by jcdia on 26/05/2017.
 */

public class Poll {
    @DatabaseField(id = true)
    private int     id;
    @DatabaseField
    private int     company_audit_id;
    @DatabaseField
    private String  question;
    @DatabaseField
    private int     orden;
    @DatabaseField
    private int     sino;
    @DatabaseField
    private int     options;
    @DatabaseField
    private int     media;
    @DatabaseField
    private int     publicity;
    @DatabaseField
    private int     categoryProduct;
    @DatabaseField
    private int     product;
    @DatabaseField
    private String  created_at;
    @DatabaseField
    private String  updated_at;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompany_audit_id() {
        return company_audit_id;
    }

    public void setCompany_audit_id(int company_audit_id) {
        this.company_audit_id = company_audit_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public int getSino() {
        return sino;
    }

    public void setSino(int sino) {
        this.sino = sino;
    }

    public int getOptions() {
        return options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getPublicity() {
        return publicity;
    }

    public void setPublicity(int publicity) {
        this.publicity = publicity;
    }

    public int getCategoryProduct() {
        return categoryProduct;
    }

    public void setCategoryProduct(int categoryProduct) {
        this.categoryProduct = categoryProduct;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }


}
