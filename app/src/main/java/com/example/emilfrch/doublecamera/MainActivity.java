package com.example.emilfrch.doublecamera;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import static android.R.attr.data;
import static android.R.attr.onClick;
import static android.R.color.holo_orange_dark;
import static android.R.color.holo_orange_light;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    int firstPicture = 0;
    Intent TOP, BOT;
    int IMAGE_CODE = 1;

    ImageView bg;
    int bgColor = -1;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this); // Ask if we can use their image folder

        bg = (ImageView) findViewById(R.id.imgBG);
        bg.setClickable(true);
        bg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view){
                bgColor++;
                changeBGColor();
            }});


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firstPicture == 0) {
                    TOP = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(TOP, IMAGE_CODE);
                }

                else if (firstPicture == 1)
                {
                    BOT = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    BOT.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(TOP, IMAGE_CODE);
                }

                else if (firstPicture == 2) {
                    takeScreenshot();
                    firstPicture = 0;
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (firstPicture == 0) {
            if (requestCode == IMAGE_CODE && RESULT_OK == resultCode) {
                Uri picture = data.getData();
                ImageView top = (ImageView) findViewById(R.id.imgTop);
                top.setImageURI(picture);
                firstPicture = 1;
            }
        }
        else if (firstPicture == 1)
        {
            if (requestCode == IMAGE_CODE && RESULT_OK == resultCode) {
                Uri picture = data.getData();
                ImageView bot = (ImageView) findViewById(R.id.imgBot);
                bot.setImageURI(picture);
               // bot.setRotation(90);
                firstPicture = 2;
            }
        }
    }

    private void changeBGColor(){
        switch (bgColor) {
            case 0:
                Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();
                bg.setColorFilter(Integer.parseInt("@android:color/holo_orange_dark"));
                break;

            case 1:
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                bg.setImageURI(Uri.parse("@android:color/holo_blue_bright"));
                break;

            case 2:
                Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                bg.setImageURI(Uri.parse("@android:color/holo_blue_light"));
                break;

            case 3:
                bg.setImageURI(Uri.parse("@android:color/holo_blue_dark"));
                break;

            case 4:
                bg.setImageURI(Uri.parse("@android:color/holo_green_light"));
                break;

            case 5:
                bg.setImageURI(Uri.parse("@android:color/holo_green_dark"));
                break;

            case 6:
                bg.setImageURI(Uri.parse("@android:color/holo_purple"));
                break;

            case 7:
                bg.setImageURI(Uri.parse("@android:color/holo_red_light"));
                break;

            case 8:
                bg.setImageURI(Uri.parse("@android:color/holo_red_dark"));
                break;

            case 9:
                bg.setImageURI(Uri.parse("@android:color/white"));
                Toast.makeText(this, bgColor, Toast.LENGTH_SHORT).show();
                break;

            case 10:
                bg.setImageURI(Uri.parse("@android:color/grey"));
                break;

            case 11:
                bg.setImageURI(Uri.parse("@android:color/black"));
                break;

            default:
                Toast.makeText(this, "What", Toast.LENGTH_SHORT).show();
                bg.setImageURI(Uri.parse("@android:color/holo_orange_light"));
                bgColor = -1;
                break;
        }
    }

    private void takeScreenshot() {
        fab.hide();
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
            Toast.makeText(this, "Error trying to take a screenshot", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        fab.show();
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
