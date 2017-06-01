package com.dataservicios.plantilla;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.dataservicios.plantilla.adapter.ImageAdapter;
import com.dataservicios.plantilla.model.Media;
import com.dataservicios.plantilla.repo.CompanyRepo;
import com.dataservicios.plantilla.repo.MediaRepo;
import com.dataservicios.plantilla.util.BitmapLoader;
import com.dataservicios.plantilla.util.GlobalConstant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by user on 06/02/2015.
 */
public class AndroidCustomGalleryActivity extends AppCompatActivity {
    public static final String LOG_TAG = AndroidCustomGalleryActivity.class.getSimpleName();
    private static final int TAKE_PICTURE = 1;

    private String                  mCurrentPhotoPath;
    private AlbumStorageDirFactory  mAlbumStorageDirFactory = null;
    private ImageAdapter            imageAdapter;
    private ArrayList<String>       f = new ArrayList<String>();// list of file paths
    private File[]                  listFile;
    private ArrayList<String>       names_file = new ArrayList<String>();
    private Activity                activity = (Activity) this;;
    private Media                   media;
    //private String                  store_id,publicities_id,tipo, product_id, poll_id,company_id,category_product_id,monto,razon_social;
    private CompanyRepo             companyRepo;


    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param media pregunta que se mostrará segun el oreden
     */
    public static void createInstance(Activity activity, Media media) {
        Intent intent = getLaunchIntent(activity, media);
        activity.startActivity(intent);
    }
    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param media
     * @return retorna un Intent listo para usar
     */
    private static Intent getLaunchIntent(Context context, Media media) {
        Intent intent = new Intent(context, AndroidCustomGalleryActivity.class);
        intent.putExtra("store_id"              ,media.getStore_id()           );
        intent.putExtra("poll_id"               ,media.getPoll_id()            );
        intent.putExtra("company_id"            ,media.getCompany_id()         );
        intent.putExtra("publicities_id"        ,media.getPublicity_id()       );
        intent.putExtra("product_id"            ,media.getProduct_id()         );
        intent.putExtra("category_product_id"   ,media.getCategory_product_id());
        intent.putExtra("monto"                 ,media.getMonto()              );
        intent.putExtra("razon_social"          ,media.getRazonSocial()        );
        intent.putExtra("tipo"                  ,media.getType()               );
        return intent;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        showToolbar(getString(R.string.title_activity_upload_photo),false);

        Bundle bundle = getIntent().getExtras();
        media= new Media();
        media.setStore_id(bundle.getInt("store_id"));
        media.setPoll_id(bundle.getInt("poll_id"));
        media.setCompany_id(bundle.getInt("company_id"));
        media.setPublicity_id(bundle.getInt("publicities_id"));
        media.setProduct_id(bundle.getInt("product_id"));
        media.setCategory_product_id(bundle.getInt("category_product_id"));
        media.setMonto(bundle.getString("monto"));
        media.setRazonSocial(bundle.getString("razon_social"));
        media.setType(bundle.getInt("tipo"));

        getFromSdcard();

        final GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter(activity,f);
        imagegrid.setAdapter(imageAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        Button btn_photo = (Button)findViewById(R.id.take_photo);
        Button btn_upload = (Button)findViewById(R.id.save_images);
        // Register the onClick listener with the implementation above
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // create intent with ACTION_IMAGE_CAPTURE action
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName =   String.format("%06d", Integer.parseInt(String.valueOf(media.getStore_id())))+ "_" + media.getCompany_id() + GlobalConstant.JPEG_FILE_PREFIX + timeStamp;
                File albumF = BitmapLoader.getAlbumDir(activity);
                // to save picture remove comment
                File file = new File(albumF,imageFileName+GlobalConstant.JPEG_FILE_SUFFIX);
                Uri photoPath = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
                mCurrentPhotoPath = BitmapLoader.getAlbumDir(activity).getAbsolutePath();
                // start camera activity
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //File file= new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/" + getAlbumName());
                File file= BitmapLoader.getAlbumDir(activity);
                if (file.isDirectory()) {
                    int position =0;
                    int contador = 0;
                    int holder_counter=0;
                    names_file.clear();
                    if (listFile.length>0){
                        //for (int i = 0; i < listFile.length; i++){
                        int total = imageAdapter.getCount();
                        int count = imagegrid.getAdapter().getCount();
                        for (int i = 0; i < count; i++) {
                           // LinearLayout itemLayout = (LinearLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                            RelativeLayout itemLayout = (RelativeLayout)imagegrid.getChildAt(i); // Find by under LinearLayout
                            CheckBox checkbox = (CheckBox)itemLayout.findViewById(R.id.itemCheckBox);
                            if(checkbox.isChecked())
                            {
                                contador ++;
                                // Log.d("Item "+String.valueOf(i), checkbox.getTag().toString());
                                //Toast.makeText(activity,checkbox.getTag().toString() ,Toast.LENGTH_LONG).show();
                                if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", media.getStore_id()) )) {
                                    String name = listFile[i].getName();
                                    names_file.add(name);
                                    //  holder_counter++;
                                    try {
                                        copyFile(BitmapLoader.getAlbumDir(activity) + "/" + listFile[i].getName(), BitmapLoader.getAlbumDirTemp(activity).getAbsolutePath() + "/" + listFile[i].getName());
                                        copyFile(BitmapLoader.getAlbumDir(activity) + "/" + listFile[i].getName(), BitmapLoader.getAlbumDirBackup(activity) + "/" + listFile[i].getName());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                listFile[i].delete();
                            }
                        }

                        if(contador > 0){

                            Toast.makeText(activity, "Seleccionó " + String.valueOf(contador) + " imágenes", Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(activity, R.string.message_selection_image, Toast.LENGTH_LONG).show();
                            return;
                        }


                        //return;

                    } else {

                        Toast toast;
                        toast = Toast.makeText(activity, R.string.message_image_no_found , Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                }

                MediaRepo mediaRepo = new MediaRepo(activity);
                for (int i = 0; i < names_file.size(); i++) {
                    String foto = names_file.get(i);
                    //String pathFile =getAlbumDirTemp().getAbsolutePath() + "/" + foto ;
                    String created_at = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").format(new Date());
                    media.setCreated_at(created_at);
                    media.setFile(foto);
                    mediaRepo.create(media);
                }

                ArrayList<Media> medias = (ArrayList<Media>) mediaRepo.findAll();
                Log.d(LOG_TAG,medias.toString());
                finish();

            }
        });
    }


    //Enviar a AgenteDetailActivity


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");

        // getting values from selected ListItem
        String aid = idPDV;
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked

                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }




    /* Photo album for this application */
//    private String getAlbumName() {
//       return getString(R.string.album_name);
//       // return GlobalConstant.albunName;
//    }

//    private String getAlbunNameTemp(){
//        return  getString(R.string.album_name_temp);
//    }
//
//    private String getAlbunNameBackup(){
//        return  getString(R.string.album_name_backup);
//    }
//
//    private File getAlbumDir() {
//        File storageDir = null;
//
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
//
//            if (storageDir != null) {
//                if (! storageDir.mkdirs()) {
//                    if (! storageDir.exists()){
//                        Log.d("CameraSample", "failed to create directory");
//                        return null;
//                    }
//                }
//            }
//
//        } else {
//            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
//        }
//
//        return storageDir;
//    }
//
//
//    private File getAlbumDirTemp() {
//        File storageDir = null;
//
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbunNameTemp());
//
//            if (storageDir != null) {
//                if (! storageDir.mkdirs()) {
//                    if (! storageDir.exists()){
//                        Log.d(getAlbunNameTemp(), "failed to create directory");
//                        return null;
//                    }
//                }
//            }
//
//        } else {
//            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
//        }
//
//        return storageDir;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }

//    private File getAlbumDirBackup() {
//        File storageDir = null;
//
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//
//            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbunNameBackup());
//
//            if (storageDir != null) {
//                if (! storageDir.mkdirs()) {
//                    if (! storageDir.exists()){
//                        Log.d(getAlbunNameBackup(), "failed to create directory");
//                        return null;
//                    }
//                }
//            }
//
//        } else {
//            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
//        }
//
//        return storageDir;
//    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            AndroidCustomGalleryActivity.createInstance((Activity) activity, this.media);
            finish();
        }

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }


    public void getFromSdcard()
    {

        //File file= new File(Environment.getExternalStorageDirectory().toString()+ GlobalConstant.directory_images + getAlbumName());
        File file= BitmapLoader.getAlbumDir(activity);
        if (file.isDirectory())
        {
            listFile = file.listFiles();
            if (listFile != null){
                for (int i = 0; i < listFile.length; i++)
                {
                    if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", media.getStore_id()) ))
                    {
                        f.add(listFile[i].getAbsolutePath());
                    }

                }
            }


        }
    }


    public void copyFile(String selectedImagePath, String string) throws IOException {
        InputStream in = new FileInputStream(selectedImagePath);
        OutputStream out = new FileOutputStream(string);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        //overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    private void showToolbar(String title, boolean upButton){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }
}
