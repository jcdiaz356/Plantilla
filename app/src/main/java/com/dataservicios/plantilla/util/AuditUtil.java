package com.dataservicios.plantilla.util;

import android.content.Context;
import android.util.Log;


import com.dataservicios.plantilla.model.Audit;
import com.dataservicios.plantilla.model.AuditRoadStore;
import com.dataservicios.plantilla.model.Company;
import com.dataservicios.plantilla.model.Poll;
import com.dataservicios.plantilla.model.PollOption;
import com.dataservicios.plantilla.model.Route;
import com.dataservicios.plantilla.model.Store;
import com.dataservicios.plantilla.model.User;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Jaime on 28/08/2016.
 */
public class AuditUtil {
    public static final String LOG_TAG = AuditUtil.class.getSimpleName();
    //private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private Context context;

    public AuditUtil(Context context) {
        this.context = context ;
    }


    /**
     * Validate user and password for login
     * @param userName
     * @param password
     * @param imei
     * @return
     */
    public static User userLogin(String userName, String password , String imei){

        int success ;
        User user = new User();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("username", String.valueOf(userName));
            params.put("password", String.valueOf(password));
            params.put("imei", String.valueOf(imei));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginMovil" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nulo");
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    user.setId(json.getInt("id"));
                    user.setEmail(userName);
                    user.setFullname(json.getString("fullname"));
                    user.setImage("use");
                    user.setPassword(password);
                }else{
                    Log.d(LOG_TAG, "No se pudo iniciar sesión");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  user;
    }


    public static ArrayList<Route> getListRoutes(int user_id, int company_id){
        int success ;

        ArrayList<Route> list = new ArrayList<Route>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id", String.valueOf(user_id));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsTotal" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("roads");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Route route = new Route();
                            route.setId(Integer.valueOf(obj.getString("id")));
                            route.setFullname(String.valueOf(obj.getString("fullname")));
                            route.setAudit(Integer.valueOf(obj.getString("auditados")));
                            route.setTotal_store(Integer.valueOf(obj.getString("pdvs")));
                            list.add(i,route);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  list;
    }

    public static ArrayList<Store> getListStores(int route_id, int company_id){
        int success ;

        ArrayList<Store> stores= new ArrayList<Store>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id", String.valueOf(route_id));
            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/JsonRoadsDetail" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("roadsDetail");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Store store = new Store();
                            store.setId(obj.getInt("id"));
                            store.setRoute_id(obj.getInt("road_id"));
                            if(obj.isNull("fullname")) store.setFullname("");  else store.setFullname(obj.getString("fullname"));;
                            if(obj.isNull("cadenaRuc")) store.setCadenRuc("");  else store.setCadenRuc(obj.getString("cadenaRuc"));;
                            if(obj.isNull("documento")) store.setDocument("");  else store.setDocument(obj.getString("documento"));;
                            if(obj.isNull("tipo_documento")) store.setTypo_document("");  else store.setTypo_document(obj.getString("tipo_documento"));;
                            if(obj.isNull("region")) store.setRegion("");  else store.setRegion(obj.getString("region"));;
                            if(obj.isNull("tipo_bodega")) store.setTypeBodega("");  else store.setTypeBodega(obj.getString("tipo_bodega"));
                            if(obj.isNull("address")) store.setAddress("");  else store.setAddress(obj.getString("address"));
                            if(obj.isNull("district")) store.setDistrict("");  else store.setDistrict(obj.getString("district"));
                            store.setStatus(obj.getInt("status"));
                            if(obj.isNull("codclient")) store.setCodCliente("");  else store.setCodCliente(obj.getString("codclient"));
                            if(obj.isNull("urbanization")) store.setUrbanization("");  else store.setUrbanization(obj.getString("urbanization"));
                            if(obj.isNull("type")) store.setType("");  else store.setType(obj.getString("type"));
                            if(obj.isNull("ejecutivo")) store.setEjecutivo("");  else store.setEjecutivo(obj.getString("ejecutivo"));
                            if(obj.isNull("latitude")) store.setLatitude(0.0);  else store.setLatitude(obj.getDouble("latitude"));
                            if(obj.isNull("longitude")) store.setLongitude(0.0);  else store.setLongitude(obj.getDouble("longitude"));
                            if(obj.isNull("telephone")) store.setTelephone("");  else store.setTelephone(obj.getString("telephone"));
                            if(obj.isNull("cell")) store.setCell("");  else store.setCell(obj.getString("cell"));
                            if(obj.isNull("comment")) store.setComment("");  else store.setComment(obj.getString("comment"));
                            if(obj.isNull("owner")) store.setOwner("");  else store.setOwner(obj.getString("owner"));
                            if(obj.isNull("fnac")) store.setFnac("");  else store.setFnac(obj.getString("fnac"));


                            stores.add(i,store);
                        }

                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  stores;
    }

    public static Company getCompany(int active,int visible,String app_id){
        int success ;

        Company company = new Company();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("app_id", String.valueOf(app_id));
            params.put("active", String.valueOf(active));
            params.put("visible", String.valueOf(visible));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_company_app.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{


                success = json.getInt("success");
                if (success == 1) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("company");
                    // looping through All Products
                    if(ObjJson.length() > 0) {
                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);

                            company.setId(obj.getInt("id"));
                            company.setFullname(obj.getString("fullname"));
                            company.setActive(obj.getInt("active"));
                            company.setCustomer_id(obj.getInt("customer_id"));
                            company.setVisible(obj.getInt("visible"));
                            company.setAuditory(obj.getInt("auditoria"));
                            company.setLogo(obj.getString("logo"));
                            company.setMarkerPoint(obj.getString("markerPoint"));
                            company.setApp_id(obj.getString("app_id"));
                            company.setCreated_at(obj.getString("created_at"));
                            company.setUpdated_at(obj.getString("updated_at"));
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  company;
    }

    public static ArrayList<Audit> getAudits( int company_id){
        int success ;

        ArrayList<Audit> audits= new ArrayList<Audit>();
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("company_id", String.valueOf(company_id));

            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_company_audits.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("audits");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Audit audit = new Audit();
                            audit.setId(obj.getInt("id"));
                            audit.setCompany_audit_id(obj.getInt("company_audit_id"));
                            audit.setCompany_id(obj.getInt("company_id"));
                            if(obj.isNull("fullname")) audit.setFullname("");  else audit.setFullname(obj.getString("fullname"));;
                            audit.setOrden(obj.getInt("orden"));
                            audit.setAudit(obj.getInt("audit"));
                            if(obj.isNull("created_at")) audit.setCreated_at("");  else audit.setCreated_at(obj.getString("created_at"));;
                            if(obj.isNull("updated_at")) audit.setUpdated_at("");  else audit.setUpdated_at(obj.getString("updated_at"));;
                            audits.add(i,audit);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  audits;
    }

    public static ArrayList<AuditRoadStore> getAuditRoadStores(int company_id, int user_id){
        int success ;

        ArrayList<AuditRoadStore> auditRoadsStores= new ArrayList<AuditRoadStore>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            params.put("user_id", String.valueOf(user_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_audit_road_stores.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");

            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("audits");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            AuditRoadStore auditRoadStore = new AuditRoadStore();
                            auditRoadStore.setId(obj.getInt("id"));
                            auditRoadStore.setRoad_id(obj.getInt("road_id"));
                            auditRoadStore.setAudit_id(obj.getInt("audit_id"));
                            auditRoadStore.setStore_id(obj.getInt("store_id"));
                            auditRoadStore.setAuditStatus(obj.getInt("audit"));

                            auditRoadsStores.add(i,auditRoadStore);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  auditRoadsStores;
    }

    public static ArrayList<Poll> getPolls(int company_id){
        int success ;

        ArrayList<Poll> polls= new ArrayList<Poll>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_poll.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("polls");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            Poll poll = new Poll();
                            poll.setId(obj.getInt("id"));
                            poll.setCompany_audit_id(obj.getInt("company_audit_id"));
                            poll.setQuestion(obj.getString("question"));
                            if(obj.isNull("question")) poll.setQuestion("");  else poll.setQuestion(obj.getString("question"));
                            poll.setOrden(obj.getInt("orden"));
                            poll.setSino(obj.getInt("sino"));
                            poll.setOptions(obj.getInt("options"));
                            poll.setMedia(obj.getInt("media"));
                            poll.setPublicity(obj.getInt("publicity"));
                            poll.setCategoryProduct(obj.getInt("categoryProduct"));
                            poll.setProduct(obj.getInt("product"));
                            if(obj.isNull("created_at")) poll.setCreated_at("");  else poll.setCreated_at(obj.getString("created_at"));
                            if(obj.isNull("updated_at")) poll.setUpdated_at("");  else poll.setUpdated_at(obj.getString("updated_at"));
                            polls.add(i,poll);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  polls;
    }

    public static ArrayList<PollOption> getPollOptions(int company_id){
        int success ;

        ArrayList<PollOption> pollOptions= new ArrayList<PollOption>();
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("company_id", String.valueOf(company_id));
            JSONParserX jsonParser = new JSONParserX();
            // getting product details by making HTTP request
            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/admin_api/api_poll_option.php" ,"POST", params);
            // check your log for json response
            Log.d("Login attempt", json.toString());
            // json success, tag que retorna el json
            if (json == null) {
                Log.d("JSON result", "Está en nullo");
            } else{
                success = json.getInt("success");
                if (success > 0) {
                    JSONArray ObjJson;
                    ObjJson = json.getJSONArray("poll_options");
                    // looping through All Products
                    if(ObjJson.length() > 0) {

                        for (int i = 0; i < ObjJson.length(); i++) {
                            JSONObject obj = ObjJson.getJSONObject(i);
                            PollOption pollOption = new PollOption();
                            pollOption.setPoll_id(obj.getInt("poll_id"));
                            if(obj.isNull("options")) pollOption.setOptions("");  else pollOption.setOptions(obj.getString("options"));
                            if(obj.isNull("options_abreviado")) pollOption.setOptions_abreviado("");  else pollOption.setOptions_abreviado(obj.getString("options_abreviado"));
                            if(obj.isNull("codigo")) pollOption.setCodigo("");  else pollOption.setCodigo(obj.getString("codigo"));;
                            pollOption.setProduct_id(obj.getInt("product_id"));
                            if(obj.isNull("region")) pollOption.setRegion("");  else pollOption.setRegion(obj.getString("region"));
                            if(obj.isNull("created_at")) pollOption.setCreated_at("");  else pollOption.setCreated_at(obj.getString("created_at"));;
                            if(obj.isNull("updated_at")) pollOption.setUpdated_at("");  else pollOption.setUpdated_at(obj.getString("updated_at"));;
                            pollOptions.add(i,pollOption);
                        }
                    }
                    Log.d(LOG_TAG, "Ingresado correctamente");
                }else{
                    Log.d(LOG_TAG, "No se ingreso el registro");
                    //return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }
        return  pollOptions;
    }

    public static boolean saveLatLongStore(int store_id, double latitude,double longitude ) {

        int success;
        try {

            HashMap<String, String> params = new HashMap<>();

            params.put("id"             , String.valueOf(store_id));
            params.put("latitud"        , String.valueOf(latitude));
            params.put("longitud"       , String.valueOf(longitude));

            JSONParserX jsonParser = new JSONParserX();

            JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/updatePositionStore" ,"POST", params);
            Log.d(LOG_TAG, json.toString());

            // json success, tag que retorna el json

            if (json == null) {
                Log.d(LOG_TAG, "Está en nullo");
                return false;
            } else{
                success = json.getInt("success");
                if (success == 1) {
                    Log.d(LOG_TAG, "Se insertó registro correctamente");
                    return true;
                }else{
                    Log.d(LOG_TAG, "no insertó registro");
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
