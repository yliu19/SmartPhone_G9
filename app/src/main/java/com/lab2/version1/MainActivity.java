package com.lab2.version1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The buttons.
     */
//    private Button up, left, right, down;
    /**
     * The text view.
     */
//    private TextView textView;
    /**
     * The shape.
     */
    private ShapeDrawable drawable;
    /**
     * The canvas.
     */
    private Canvas canvas;
    /**
     * The walls.
     */
    private List<ShapeDrawable> walls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
//        up = (Button) findViewById(R.id.button1);

        // set the text view
//        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
//        up.setOnClickListener(this);

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        System.out.println(width);
        System.out.println(height);

        // create a drawable object
//        drawable = new ShapeDrawable(new OvalShape());
//        drawable.getPaint().setColor(Color.BLUE);
//        drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);

        walls = new ArrayList<>();
        float [] outerRadii = new float[] {};
        float [] innerRadii = new float[] {};
        //ShapeDrawable outline = new ShapeDrawable(new RoundRectShape(outerRadii,20, innerRadii));
        ShapeDrawable d_left = new ShapeDrawable(new RectShape());
        d_left.setBounds(20, height/5, 30, height-200);
        ShapeDrawable d_right = new ShapeDrawable(new RectShape());
        d_right.setBounds(width-200, height/5, width-210, height-200);




        ShapeDrawable d = new ShapeDrawable(new RectShape());
        d.setBounds(width/2-200, height/2-90, width/2+200, height/2-80);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(width/2-200, height/2+60, width/2+200, height/2+70);
        ShapeDrawable d3 = new ShapeDrawable(new RectShape());
        d3.setBounds(width/2+200, height/2-90, width/2+210, height/2+70);
        walls.add(d);
        walls.add(d2);
        walls.add(d3);
        walls.add(d_left);

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects
//        drawable.draw(canvas);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }


}