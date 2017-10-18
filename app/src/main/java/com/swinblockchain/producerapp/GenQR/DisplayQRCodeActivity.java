package com.swinblockchain.producerapp.GenQR;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.swinblockchain.producerapp.App;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.R.attr.bitmap;
import static com.swinblockchain.producerapp.App.getContext;

/**
 * The Display QR Code Activity class is responsible for rendering the generated SVG string from the server onto the screen.
 *
 * @author John Humphrys
 */
public class DisplayQRCodeActivity extends AppCompatActivity {

    ImageView imageView;
    TextView QRCodeInfo;

    String productName;
    String productID;
    String batchID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qrcode);

        init();
    }

    /**
     * Initialise variables
     */
    private void init() {
        imageView = (ImageView) findViewById(R.id.qrCodeImage);
        QRCodeInfo = (TextView) findViewById(R.id.QRCodeInfo);

        Bundle extras = getIntent().getExtras();
        String svgString = extras.getString("svg");
        productName = extras.getString("productName");
        productID = extras.getString("productID");
        batchID = extras.getString("batchID");

        drawSvg(svgString);
        addQRInfo(productName, productID, batchID);
    }

    /**
     * Draw the QR code
     *
     * @param svgString The SVG string to render
     */
    private void drawSvg(String svgString) {

        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        try {
            SVG svg = SVG.getFromString(svgString);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            imageView.setImageDrawable(drawable);

        } catch (SVGParseException e) {
            startError("Unable to render QR code\nError Code: " + e.toString());
        }

    }

    /**
     * Displays the information at the bottom of the QR code
     *
     * @param productName The name of the product
     * @param productID   The ID of the product
     * @param batchID     The batch ID of the product
     */
    private void addQRInfo(String productName, String productID, String batchID) {
        QRCodeInfo.setText("Product name: " + productName + "\nProduct ID: " + productID + "\nBatch ID: " + batchID);
    }

    /**
     * Saves the QR code to the camera roll
     *
     * @param view
     */
    public void saveToCameraRoll(View view) {

        File storedImagePath = generateImagePath("Product name:" + productName + " - Product ID:" + productID + " - Batch ID:" + batchID, "png");

        String permission = "android.permission.READ_EXTERNAL_STORAGE";
        int res = getContext().checkCallingOrSelfPermission(permission);
        if (res == PackageManager.PERMISSION_GRANTED) {

            if (!compressAndSaveImage(storedImagePath, drawableToBitmap(imageView.getDrawable()))) {
                Toast.makeText(getApplicationContext(), "Saving QR code was unsuccessful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "QR Code successfully saved to camera roll", Toast.LENGTH_SHORT).show();
            }
            Uri url = addImageToGallery(getContext().getContentResolver(), "png", storedImagePath);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    /**
     * Gets the images directory
     *
     * @return The image directory
     */
    private static File getImagesDirectory() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "ProductChain");//Environment.getExternalStorageDirectory()
        if (!file.mkdirs() && !file.isDirectory()) {
            Log.e("mkdir", "Directory not created");
        }
        return file;
    }

    /**
     * Generates the path to save the image to
     *
     * @param title   The title of the image
     * @param imgType The type of image
     * @return The file to write the bytes to
     */
    public static File generateImagePath(String title, String imgType) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        return new File(getImagesDirectory(), title + "_" + sdf.format(new Date()) + "." + imgType);
    }

    /**
     * Compresses and saves the image
     *
     * @param file   The file to save the image to
     * @param bitmap The bitmap to save to file
     * @return A boolean if the save succeeded
     */
    public boolean compressAndSaveImage(File file, Bitmap bitmap) {
        boolean result = false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                Log.w("image manager", "Compression success");
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Uri addImageToGallery(ContentResolver cr, String imgType, File filepath) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, productName + " - PID:" + productID + " - BID:" + batchID);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, productName + " - PID:" + productID + " - BID:" + batchID);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Product name: " + productName + "\nProduct ID: " + productID + "\nBatch ID: " + batchID);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + imgType);
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, filepath.toString());

        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * see https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {

                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        // Create a background images otherwise the saved image is all black due to png transparency
        Bitmap updatedBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        updatedBitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(updatedBitmap);
        canvas.drawBitmap(updatedBitmap, 0f, 0f, null);
        bitmap.recycle();

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return updatedBitmap;
    }

    /**
     * Returns user to the main screen and displays error message
     *
     * @param errorMessage The error message to display
     */
    private void startError(String errorMessage) {
        Intent i = new Intent(DisplayQRCodeActivity.this, MainActivity.class);
        i.putExtra("errorMessage", errorMessage);
        startActivity(i);

    }

    /**
     * On back pressed sends the user to the main activity to prevent unexpected results
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent(DisplayQRCodeActivity.this, MainActivity.class);
        startActivity(i);
    }
}

