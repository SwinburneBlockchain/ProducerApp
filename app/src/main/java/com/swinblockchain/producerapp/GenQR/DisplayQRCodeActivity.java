package com.swinblockchain.producerapp.GenQR;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.swinblockchain.producerapp.R;

public class DisplayQRCodeActivity extends AppCompatActivity {

    String svgResponse;
    ImageView qrCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qrcode);

        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();

        svgResponse = extras.getString("svgResponse");

        drawSvg(svgResponse);
    }

    private void drawSvg(String svgString) {

        ImageView  imageView = (ImageView) findViewById(R.id.qrCodeImage);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        try
        {
            SVG svg = SVG.getFromString(svgString);
            Drawable drawable = new PictureDrawable(svg.renderToPicture());
            imageView.setImageDrawable(drawable);
        }
        catch(SVGParseException e)
        {
            // TODO this
        }
    }
    }

