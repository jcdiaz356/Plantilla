package com.dataservicios.plantilla.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.plantilla.R;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.AuditRoadStore;
import com.dataservicios.plantilla.model.Company;
import com.dataservicios.plantilla.model.Route;
import com.dataservicios.plantilla.model.Store;
import com.dataservicios.plantilla.repo.AuditRoadStoreRepo;
import com.dataservicios.plantilla.repo.CompanyRepo;
import com.dataservicios.plantilla.repo.RouteRepo;
import com.dataservicios.plantilla.repo.StoreRepo;
import com.dataservicios.plantilla.util.AuditUtil;
import com.dataservicios.plantilla.util.GPSTracker;
import com.dataservicios.plantilla.util.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StoreAuditActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String LOG_TAG = StoreAuditActivity.class.getSimpleName();

    private SessionManager        session;
    private GoogleMap             mMap;
    private Activity              activity =  this;
    private ProgressDialog        pDialog;
    private TextView              tvRouteFullName,tvRouteId,tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict ;
    private Button                btSaveGeo;
    private int                   user_id;
    private int                   store_id;
    private int                   company_id;
    private double                gpsLat;
    private double                gpsLon;
    private Bitmap                bmpMarker;
    private RouteRepo             routeRepo ;
    private AuditRoadStoreRepo    auditRoadStoreRepo ;
    private StoreRepo             storeRepo ;
    private CompanyRepo           companyRepo ;
    private Route                 route ;
    private Store                 store ;
    private GPSTracker            gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_audit);

        showToolbar(getString(R.string.title_activity_Stores_Audit),true);

        DatabaseManager.init(this);

        gpsTracker = new GPSTracker(activity);
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        routeRepo           = new RouteRepo(activity);
        storeRepo           = new StoreRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        tvRouteFullName     = (TextView) findViewById(R.id.tvRouteFullName) ;
        tvRouteId           = (TextView) findViewById(R.id.tvRouteId) ;
        tvStoreFullName     = (TextView) findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView) findViewById(R.id.tvStoreId) ;
        tvAddress           = (TextView) findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView) findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView) findViewById(R.id.tvDistrict) ;
        btSaveGeo           = (Button)   findViewById(R.id.btSaveGeo);
        store = (Store) storeRepo.findById(store_id);

        //ArrayList<Route> lista = (ArrayList<Route>) routeRepo.findAll();
        route = (Route) routeRepo.findById(store.getRoute_id());

        tvRouteFullName.setText(String.valueOf(route.getFullname()));
        tvRouteId.setText(String.valueOf(route.getId()));
        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));

        route.setAudit(route.getAudit() + 100);
        routeRepo.update(route);

        btSaveGeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!gpsTracker.canGetLocation()) {
                    gpsTracker.showSettingsAlert();
                } else {
                    gpsTracker.getLocation();
                    gpsLat = gpsTracker.getLatitude();
                    gpsLon = gpsTracker.getLongitude();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_update_gps);
                builder.setMessage(R.string.message_update_gps_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new saveLatLongStore().execute();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                builder.setCancelable(false);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LinearLayout lyContentButtons = (LinearLayout) findViewById(R.id.lyContentButtons);
        ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);


        Button[] buttonArray = new Button[auditRoadStores.size()];
        int i =0 ;
        for (AuditRoadStore ar: auditRoadStores) {
            //tvPrueba.append(String.valueOf(ar.getAudit_id()) +   String.valueOf(ar.getList().getFullname()) +"\n");
            buttonArray[i] = new Button(this,null,R.attr.button_style_r);
            buttonArray[i].setText(ar.getList().getFullname().toString());
            buttonArray[i].setTag(String.valueOf(ar.getList().getId()));
            if(ar.getAuditStatus() == 1) {
                buttonArray[i].setEnabled(false);
            }

            lyContentButtons.addView(buttonArray[i]);

            buttonArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(activity,v.getTag().toString(),Toast.LENGTH_SHORT).show();
                }
            });

            i ++ ;
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        new loadMarkerPointMap().execute();
    }

    public void showToolbar(String title, boolean upButton){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }
    /**
     * Carga el Bitmap del marquer desde internet
     */
    class loadMarkerPointMap extends AsyncTask<Void, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub
            URL url ;
            Bitmap bmp ;
            try {
                //companyRepo = new CompanyRepo(activity);
                Company company = (Company) companyRepo.findById(company_id);
                url = new URL(company.getMarkerPoint().toString());
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (Exception e) {
                bmp = null;
                e.printStackTrace();
            }
            return  bmp;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Bitmap bmp) {
            bmpMarker = bmp;
            getPositionStoreMap();
        }

    }
    private class saveLatLongStore extends AsyncTask<Void, String, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            boolean result;
            result = AuditUtil.saveLatLongStore(store_id,gpsLat,gpsLon);
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                store.setLatitude(gpsLat);
                store.setLongitude(gpsLon);
                storeRepo.update(store);

            }
            pDialog.dismiss();
            getPositionStoreMap();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


    }
    private void  getPositionStoreMap() {
        mMap.clear();
        LatLng latLong              = new LatLng(store.getLatitude(), store.getLongitude());
        MarkerOptions markerOption  = new MarkerOptions();
        markerOption.position(latLong);
        markerOption.title(store.getFullname());

        if (bmpMarker == null) markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_point)) ; else markerOption.icon(BitmapDescriptorFactory.fromBitmap(bmpMarker));
        Marker marker = mMap.addMarker(markerOption);
        marker.showInfoWindow();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(15).build();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLong));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        pDialog.dismiss();
    }
}
