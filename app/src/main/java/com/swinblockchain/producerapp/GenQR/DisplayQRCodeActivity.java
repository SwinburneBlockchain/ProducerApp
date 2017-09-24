package com.swinblockchain.producerapp.GenQR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.swinblockchain.producerapp.MainActivity;
import com.swinblockchain.producerapp.R;

/**
 * The DIsplay QR Code Activity class is responsible for rendering the generated SVG string from the server onto the screen.
 */
public class DisplayQRCodeActivity extends AppCompatActivity {

    ImageView imageView;
    TextView QRCodeInfo;

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
        String svgString = extras.getString("svgResponse");
        String productName = extras.getString("productName");
        String productID = extras.getString("productID");
        String batchID = extras.getString("batchID");

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
     * @param productID The ID of the product
     * @param batchID The batch ID of the product
     */
    private void addQRInfo(String productName, String productID, String batchID) {
        QRCodeInfo.setText("Product name: " + productName + "\nProduct ID: " + productID + "\nBatch ID: " + batchID);
    }

    public void saveToCameraRoll(View view) {
        Drawable drawable = imageView.getDrawable();
        Bitmap qrBit = drawableToBitmap(drawable);

        MediaStore.Images.Media.insertImage(getContentResolver(), qrBit, "Testtitle" , "testdesc");
    }


    /**
     * see https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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
}

