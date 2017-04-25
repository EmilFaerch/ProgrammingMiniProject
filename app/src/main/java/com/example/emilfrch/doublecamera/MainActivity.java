package com.example.emilfrch.doublecamera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity {

    boolean firstPicture = true;
    Intent TOP, BOT;
    int IMAGE_CODE = 1;

    View rootView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (firstPicture == true) {
                    TOP = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   // TOP.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(TOP, IMAGE_CODE);
                }
                else
                {
                    BOT = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //BOT.putExtra("android.intent.extras.CAMERA_FACING", 2);
                    startActivityForResult(TOP, IMAGE_CODE);
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (firstPicture == true) {
            if (requestCode == IMAGE_CODE && RESULT_OK == resultCode) {
                Uri picture = data.getData();
                ImageView top = (ImageView) findViewById(R.id.imgTop);
                top.setImageURI(picture);
               // top.setRotation(90);
                firstPicture = false;
            }
        }
        else
        {
            if (requestCode == IMAGE_CODE && RESULT_OK == resultCode) {
                Uri picture = data.getData();
                ImageView bot = (ImageView) findViewById(R.id.imgBot);
                bot.setImageURI(picture);
               // bot.setRotation(90);
                firstPicture = true;

                store(getScreenShot(rootView), "DoublePic");
            }
        }
    }


    public Bitmap getScreenShot(View view) {
        fab.hide();
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        fab.show();
        return bitmap;
    }

    public void store(Bitmap bm, String fileName){
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            Toast.makeText(this, dirPath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
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
