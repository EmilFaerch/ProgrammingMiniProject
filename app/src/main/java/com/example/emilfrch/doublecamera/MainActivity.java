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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import static android.R.attr.data;
import static android.R.attr.onClick;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                Picasso.with(MainActivity.this).load(picture).into(top);
                firstPicture = 1;
                BOT = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                BOT.putExtra("android.intent.extras.CAMERA_FACING", 2);
                startActivityForResult(TOP, IMAGE_CODE);
            }
        }
        else if (firstPicture == 1)
        {
            if (requestCode == IMAGE_CODE && RESULT_OK == resultCode) {
                Uri picture = data.getData();
                ImageView bot = (ImageView) findViewById(R.id.imgBot);
                Picasso.with(MainActivity.this).load(picture).into(bot);
               // bot.setRotation(90);
                firstPicture = 2;
            }
        }
    }

    private void changeBGColor(){
        switch (bgColor) {
            case 0:
                bg.setImageResource(R.color.colorOrangeDark);
                break;

            case 1:
                bg.setImageResource(R.color.colorBlueLight);
                break;

            case 2:
                bg.setImageResource(R.color.colorBlue);
                break;

            case 3:
                bg.setImageResource(R.color.colorBlueDark);
                break;

            case 4:
                bg.setImageResource(R.color.colorPink);
                break;

            case 5:
                bg.setImageResource(R.color.colorMagenta);
                break;

            case 6:
                bg.setImageResource(R.color.colorRed);
                break;

            case 7:
                bg.setImageResource(R.color.colorRedDark);
                break;

            case 8:
                bg.setImageResource(R.color.colorGrey);
                break;

            case 9:
                bg.setImageResource(R.color.colorGreyDark);
                break;

            case 10:
                bg.setImageResource(R.color.colorBlack);
                break;

            case 11:
                bg.setImageResource(R.color.colorWhite);
                break;

            default:
                bg.setImageResource(R.color.colorOrangeLight);
                bgColor = -1;
                break;
        }
    }

    private void takeScreenshot() {
        fab.hide(); // Hide the floating button and because it has a "disappearing"-animation we have to add a delay before we take the picture
        new Handler().postDelayed(new Runnable() { // But this is actually the delay for the floating button to come back
            @Override
            public void run() {
                fab.show();
            }
        }, 1000); // 1000 milliseconds before it comes back

        new Handler().postDelayed(new Runnable() { // and this is the actual delay of taking the screenshot
            @Override
            public void run() {

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

                    Toast.makeText(MainActivity.this, "Picture saved!", Toast.LENGTH_SHORT).show();
                } catch (Throwable e) {
                    // Several error may come out with file handling or OOM
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error trying to take a screenshot", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 500); // 500 millisecond delay
    }

    /*
    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }
    */

/*
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
    */
}
