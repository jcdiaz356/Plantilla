package com.dataservicios.plantilla.app;


import android.app.Application;
import android.util.Log;



public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();

	private boolean serviceRunningFlag;

	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "onCreated");
//		startService(new Intent(this, UpdateServices.class));
//		startService(new Intent(this, MonitoGPSServices.class));
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public void setServiceRunningFlag(boolean serviceRunningFlag) {
		this.serviceRunningFlag = serviceRunningFlag;
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminated");
//		stopService(new Intent(this, UpdateServices.class));
//		stopService(new Intent(this, MonitoGPSServices.class));
	}
}