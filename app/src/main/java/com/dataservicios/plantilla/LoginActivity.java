package com.dataservicios.plantilla;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataservicios.plantilla.db.DatabaseManager;
import com.dataservicios.plantilla.model.User;
import com.dataservicios.plantilla.repo.UserRepo;
import com.dataservicios.plantilla.util.AuditUtil;
import com.dataservicios.plantilla.util.SessionManager;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Button btLogin;
    private TextInputEditText etUsuario,etPassword;
    //private DatabaseHelper db;
    private UserRepo userRepo ;
    private ProgressDialog pDialog;
    private Activity myActivity = (Activity) this;
    private SessionManager session;
    private String userLogin, passwordLogin, simSNLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btLogin = (Button) findViewById(R.id.btLogin);
        etUsuario =   (TextInputEditText) findViewById(R.id.etUser);
        etPassword = (TextInputEditText) findViewById(R.id.etPassword);

        etPassword.setText("123456");


        session = new SessionManager(getApplicationContext());

        DatabaseManager.init(myActivity);
        userRepo = new UserRepo(myActivity);

        if( userRepo.findAll().size() > 0) {
            //User users = new User();
            List<User> usersList = (List<User>) userRepo.findAll();
            if(usersList.size()>0) {
                User users = new User();
                users=usersList.get(0);
                etUsuario.setText(users.getEmail());
            }
        }

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsuario.getText().toString().trim().equals("") )
                {
                    Toast toast = Toast.makeText(myActivity, R.string.message_requiere_user , Toast.LENGTH_SHORT);
                    toast.show();
                    etUsuario.requestFocus();
                    return;
                }else if (etPassword.getText().toString().trim().equals("")) {
                    Toast toast = Toast.makeText(myActivity, R.string.message_requiere_password, Toast.LENGTH_SHORT);
                    toast.show();
                    etPassword.requestFocus();
                    return;
                }else {

                    TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    ///String imei = tm.getDeviceId();
                    //String imei1 = tm.getLine1Number();
                    simSNLogin = tm.getSimSerialNumber();
                    userLogin = etUsuario.getText().toString();
                    passwordLogin = etPassword.getText().toString();
                    new AttemptLogin().execute();
                }
            }
        });
    }



    class AttemptLogin extends AsyncTask<Void, String, User> {

        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage(getString(R.string.text_loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected User doInBackground(Void... params) {
            // TODO Auto-generated method stub

            User user = new User();
            user = AuditUtil.userLogin(userLogin, passwordLogin, simSNLogin);
            return user;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(User user) {
            // dismiss the dialog once product deleted

            if( user.getId() == 0) {

                Toast.makeText(myActivity, R.string.message_login_error, Toast.LENGTH_LONG).show();
            } else {

                userRepo.deleteAll();
                userRepo.create(user);
                session.createLoginSession(user.getFullname().toString(), user.getEmail(), String.valueOf(user.getId()));
                Intent i = new Intent(myActivity, PanelAdminActivity.class);
                startActivity(i);
                finish();

                Toast.makeText(myActivity, R.string.message_login_success, Toast.LENGTH_LONG).show();
            }

            pDialog.dismiss();


        }

    }
}
