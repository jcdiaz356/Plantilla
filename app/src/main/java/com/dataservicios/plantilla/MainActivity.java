package com.dataservicios.plantilla;

import android.Manifest;
import android.app.Activity;
import android.util.Log;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.Audit;
import com.dataservicios.plantilla.model.Company;
import com.dataservicios.plantilla.model.Poll;
import com.dataservicios.plantilla.model.PollOption;
import com.dataservicios.plantilla.repo.AuditRepo;
import com.dataservicios.plantilla.repo.CompanyRepo;
import com.dataservicios.plantilla.repo.PollOptionRepo;
import com.dataservicios.plantilla.repo.PollRepo;
import com.dataservicios.plantilla.util.AuditUtil;
import com.j256.ormlite.dao.ForeignCollection;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Logcat tag
    private static final int    REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private ProgressBar         pbLoad;
    private TextView            tvLoad, tv_Version ;
    private Activity            activity;
    private String              app_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = (Activity) this;
        pbLoad = (ProgressBar) findViewById(R.id.pbLoad);
        pbLoad.setIndeterminate(false);
        tvLoad = (TextView) findViewById(R.id.tvLoad);
        tv_Version = (TextView) findViewById(R.id.tvVersion);

        DatabaseManager.init(activity);
        PackageInfo pckInfo ;
        try {
            pckInfo= getPackageManager().getPackageInfo(getPackageName(),0);
            tv_Version.setText(pckInfo.versionName);
            app_id = pckInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(checkAndRequestPermissions()) loadLoginActivity();
    }

    private void loadLoginActivity() {

        new loadLogin().execute();
    }

    class loadLogin extends AsyncTask<Void, String, Boolean> {

        Company company ;
        ArrayList<Audit> audits;
        ArrayList<Poll> polls;
        ArrayList<PollOption> pollOptions;
        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            tvLoad.setText(values[0].toString());
        }
        @Override
        protected void onPreExecute() {

            tvLoad.setText(getString(R.string.text_loading));
           // super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            publishProgress(activity.getString(R.string.text_download_init));
            publishProgress(activity.getString(R.string.text_download_companies));

            company = AuditUtil.getCompany(1,1,app_id);
            if( company.getId() != 0 ) {
                CompanyRepo companyRepo = new CompanyRepo(activity);
                companyRepo.deleteAll();
                companyRepo.create(company);
                publishProgress(activity.getString(R.string.text_download_audits));
                audits = AuditUtil.getAudits(company.getId()) ;
                if(audits  != null) {
                    AuditRepo auditRepo  = new AuditRepo(activity);
                    auditRepo.deleteAll();
                    for (Audit a: audits) {
                        auditRepo.create(a);
                    }
                } else {
                    Log.i(LOG_TAG, "No se pudo obtener auditorias");
                    return false;
                }
                publishProgress(activity.getString(R.string.text_download_polls));
                polls = AuditUtil.getPolls(company.getId());

                publishProgress(activity.getString(R.string.text_download_polls_otpions));
                pollOptions = AuditUtil.getPollOptions(company.getId());

                PollRepo pollRepo = new PollRepo(activity);
                pollRepo.deleteAll();
                for (Poll p: polls) {
                    pollRepo.create(p);
                }

                PollOptionRepo  pollOptionRepo =  new PollOptionRepo(activity);
                pollOptionRepo.deleteAll();
                for(PollOption po: pollOptions) {

                    pollOptionRepo.create(po);
                }

                ArrayList<Poll> pollsn = (ArrayList<Poll>) pollRepo.findAll();

                Log.d(LOG_TAG, pollsn.toString());




            } else {
                Log.i(LOG_TAG, "No se pudo obtener companies");
                return false;
            }
            publishProgress(activity.getString(R.string.text_download_terminate));
            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if(result == true) {
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                String message  ;
                message = activity.getString(R.string.message_app_no_initialize) + "\n";

                if (company.getId() == 0) {
                    message = message.concat(activity.getString(R.string.message_no_get_company));
                    message = message.concat("\n");
                    //Toast.makeText(activity, R.string.message_no_get_company, Toast.LENGTH_LONG).show();
                    //activity.finish();
                }
                if (audits == null) {
                    message = message.concat(activity.getString(R.string.message_no_get_audit));
                    message = message.concat("\n");
                    //Toast.makeText(activity, R.string.message_no_get_audit, Toast.LENGTH_LONG).show();
                    //activity.finish();
                }
                alertDialogBasico(message);
            }
        }
    }

    //  Chequeando permisos de usuario Runtime
    private boolean checkAndRequestPermissions() {

        int locationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int callPhonePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        int readPhoneStatePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (callPhonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean respuestas = false ;
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {


            if (grantResults.length > 0) {
                boolean permissionsApp = true ;
                for(int i=0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //alertDialogBasico();
                        permissionsApp = false;
                        break;
                    }
                }
                if (permissionsApp==true)  loadLoginActivity();
                else alertDialogBasico(activity.getString(R.string.dialog_message_permission));
            }
        }
    }

    public void alertDialogBasico(String message) {

        // 1. Instancia de AlertDialog.Builder con este constructor
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        // 2. Encadenar varios métodos setter para ajustar las características del diálogo
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.show();

    }


}
