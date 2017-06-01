package com.dataservicios.plantilla.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.plantilla.AndroidCustomGalleryActivity;
import com.dataservicios.plantilla.R;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.AuditRoadStore;
import com.dataservicios.plantilla.model.Company;
import com.dataservicios.plantilla.model.Media;
import com.dataservicios.plantilla.model.Poll;
import com.dataservicios.plantilla.model.PollDetail;
import com.dataservicios.plantilla.model.Route;
import com.dataservicios.plantilla.model.Store;
import com.dataservicios.plantilla.repo.AuditRoadStoreRepo;
import com.dataservicios.plantilla.repo.CompanyRepo;
import com.dataservicios.plantilla.repo.PollRepo;
import com.dataservicios.plantilla.repo.RouteRepo;
import com.dataservicios.plantilla.repo.StoreRepo;
import com.dataservicios.plantilla.util.AuditUtil;
import com.dataservicios.plantilla.util.GPSTracker;
import com.dataservicios.plantilla.util.SessionManager;
import com.dataservicios.plantilla.view.fragment.RouteFragment;
import com.google.android.gms.maps.GoogleMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PollActivity extends AppCompatActivity {
    private static final String LOG_TAG = StoreAuditActivity.class.getSimpleName();
    private SessionManager      session;
    private Activity            activity =  this;
    private ProgressDialog      pDialog;
    private TextView            tvStoreFullName,tvStoreId,tvAddress ,tvReferencia,tvDistrict,tvAuditoria,tvPoll ;
    private EditText            etComent;
    private Button              btSaveGeo;
    private Button              btSave;
    private Switch              swYesNo;
    private ImageButton         btPhoto;
    private LinearLayout        lyComment;
    private LinearLayout        lyOptions;
    private int                 user_id;
    private int                 store_id;
    private int                 audit_id;
    private int                 company_id;
    private int                 orderPoll;
    private RouteRepo           routeRepo ;
    private AuditRoadStoreRepo  auditRoadStoreRepo ;
    private StoreRepo           storeRepo ;
    private CompanyRepo         companyRepo ;
    private PollRepo            pollRepo ;
    private Route               route ;
    private Store               store ;
    private Poll                poll;
    private PollDetail          pollDetail;
    private AuditRoadStore      auditRoadStore;
    private GPSTracker          gpsTracker;
    private int                 isYesNo;
    private String              comment;

    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param company_id
     * @param audit_id
     * @param orderPoll pregunta que se mostrará segun el oreden
     */
    public static void createInstance(Activity activity, int company_id, int audit_id, int orderPoll) {
        Intent intent = getLaunchIntent(activity, company_id,audit_id,orderPoll);
        activity.startActivity(intent);
    }
    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param store_id
     * @param audit_id
     * @return retorna un Intent listo para usar
     */
    private static Intent getLaunchIntent(Context context, int store_id, int audit_id, int orderPoll) {
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra("store_id", store_id);
        intent.putExtra("audit_id", audit_id);
        intent.putExtra("orderPoll", orderPoll);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        showToolbar(getString(R.string.title_activity_Stores_Audit),true);

        DatabaseManager.init(this);

        gpsTracker = new GPSTracker(activity);
        if(!gpsTracker.canGetLocation()){
            gpsTracker.showSettingsAlert();
        }

        Bundle bundle = getIntent().getExtras();
        store_id = bundle.getInt("store_id");
        audit_id = bundle.getInt("audit_id");
        orderPoll = bundle.getInt("orderPoll");

        session = new SessionManager(activity);
        HashMap<String, String> userSesion = session.getUserDetails();
        user_id = Integer.valueOf(userSesion.get(SessionManager.KEY_ID_USER)) ;

        routeRepo           = new RouteRepo(activity);
        storeRepo           = new StoreRepo(activity);
        companyRepo         = new CompanyRepo(activity);
        auditRoadStoreRepo  = new AuditRoadStoreRepo(activity);
        pollRepo            = new PollRepo(activity);

        ArrayList<Company> companies = (ArrayList<Company>) companyRepo.findAll();
        for (Company c: companies){
            company_id = c.getId();
        }

        tvStoreFullName     = (TextView)    findViewById(R.id.tvStoreFullName) ;
        tvStoreId           = (TextView)    findViewById(R.id.tvStoreId) ;
        tvAddress           = (TextView)    findViewById(R.id.tvAddress) ;
        tvReferencia        = (TextView)    findViewById(R.id.tvReferencia) ;
        tvDistrict          = (TextView)    findViewById(R.id.tvDistrict) ;
        tvAuditoria         = (TextView)    findViewById(R.id.tvAuditoria) ;
        tvPoll              = (TextView)    findViewById(R.id.tvPoll) ;
        btSaveGeo           = (Button)      findViewById(R.id.btSaveGeo);
        btSave              = (Button)      findViewById(R.id.btSave);
        btPhoto             = (ImageButton) findViewById(R.id.btPhoto);
        swYesNo             = (Switch)      findViewById(R.id.swYesNo);
        etComent            = (EditText)    findViewById(R.id.etComent);
        lyComment           = (LinearLayout)findViewById(R.id.lyComment);
        lyOptions           = (LinearLayout)findViewById(R.id.lyOptions);

        store               = (Store)           storeRepo.findById(store_id);
        route               = (Route)           routeRepo.findById(store.getRoute_id());
        auditRoadStore      = (AuditRoadStore)  auditRoadStoreRepo.findByStoreIdAndAuditId(store_id,audit_id);
        poll                = (Poll)            pollRepo.findByCompanyAuditIdAndOrder(auditRoadStore.getList().getCompany_audit_id(),orderPoll);

        tvStoreFullName.setText(String.valueOf(store.getFullname()));
        tvStoreId.setText(String.valueOf(store.getId()));
        tvAddress.setText(String.valueOf(store.getAddress()));
        tvReferencia.setText(String.valueOf(store.getUrbanization()));
        tvDistrict.setText(String.valueOf(store.getDistrict()));
        tvAuditoria.setText(auditRoadStore.getList().getFullname().toString());
        tvPoll.setText(poll.getQuestion().toString());



        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(R.string.message_save);
                builder.setMessage(R.string.message_save_information);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(swYesNo.isChecked())isYesNo=1; else isYesNo =0;
                        comment = etComent.getText().toString();

                        new savePoll().execute();
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

    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    private void takePhoto() {

        Media media = new Media();
        media.setStore_id(store_id);
        media.setPoll_id(poll.getId());
        media.setCompany_id(company_id);
        media.setType(1);
        AndroidCustomGalleryActivity.createInstance((Activity) activity, media);
    }



    class savePoll extends AsyncTask<Void, Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            boolean result = logicProcess(orderPoll);

            return result;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                resulProcess(orderPoll);
            } else {
                Toast.makeText(activity , R.string.message_no_save_data , Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
        }
    }

    private boolean logicProcess(int orderPoll) {

        switch (orderPoll) {
            case 1:
                pollDetail = new PollDetail();
                pollDetail.setPoll_id(poll.getId());
                pollDetail.setStore_id(store_id);
                pollDetail.setSino(1);
                pollDetail.setOptions(0);
                pollDetail.setLimits(0);
                pollDetail.setMedia(1);
                pollDetail.setComment(0);
                pollDetail.setResult(isYesNo);
                pollDetail.setLimite("0");
                pollDetail.setComentario(comment);
                pollDetail.setAuditor(user_id);
                pollDetail.setProduct_id(0);
                pollDetail.setCategory_product_id(0);
                pollDetail.setPublicity_id(0);
                pollDetail.setCompany_id(company_id);
                pollDetail.setCommentOptions(0);
                pollDetail.setSelectdOptions("");
                pollDetail.setSelectedOtionsComment("");
                pollDetail.setPriority(0);

                if (isYesNo == 1) {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                } else {
                    if (!AuditUtil.insertPollDetail(pollDetail)) return false;
                    if (!AuditUtil.closeAuditStore(audit_id, store_id, company_id, route.getId())) return false;

                    ArrayList<AuditRoadStore> auditRoadStores = (ArrayList<AuditRoadStore>) auditRoadStoreRepo.findByStoreId(store_id);
                    for (AuditRoadStore m: auditRoadStores){
                        m.setAuditStatus(1);
                        auditRoadStoreRepo.update(m);
                    }

                }


                break;
            case 12:
        }
        return true;
    }

    private void resulProcess (int orderPoll) {

        switch (orderPoll) {
            case 1:

                if(isYesNo==1) {
                    PollActivity.createInstance(activity, store_id,audit_id,2);
                    finish();
                } else if(isYesNo==0){
                    finish();
                }
                break;
            case 12:
        }

    }

    /**
     * Estableciendo propiedades de un Poll (Media, comment, options)
     */
    private void establishigPropertyPol() {

        if(poll.getMedia() == 1) {
            btPhoto.setVisibility(View.VISIBLE);
        } else {
            btPhoto.setVisibility(View.INVISIBLE);
        }

        if(poll.getComment() == 1) {
            lyComment.setVisibility(View.VISIBLE);
        } else {
            lyComment.setVisibility(View.INVISIBLE);
        }

    }


}
