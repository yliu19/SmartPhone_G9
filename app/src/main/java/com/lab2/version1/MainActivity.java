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
import android.graphics.RectF;
import android.graphics.Paint;
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
    private TextView Aisle;
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
        Aisle = (TextView) findViewById(R.id.aisle);

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

        float [] outerRadii = new float[] {5,5,5,5,5,5,5,5};
        float [] innerRadii = new float[] {5,5,5,5,5,5,5,5};

        System.out.println("step 1");
        Rect rect = new Rect(width/2,height*3/5+50, width-150,height-500);
        RectF rectF = new RectF(rect);
        ShapeDrawable outline = new ShapeDrawable(new RoundRectShape(outerRadii,rectF, innerRadii));
        ShapeDrawable d_left = new ShapeDrawable(new RectShape());
        d_left.setBounds(60, height/5, 70, height-500);
        ShapeDrawable d_right = new ShapeDrawable(new RectShape());
        d_right.setBounds(width-160, height/5+300, width-150, height-500);
        ShapeDrawable d = new ShapeDrawable(new RectShape());
        d.setBounds(60, height-510, width-150, height-500);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(60, height/5+300, width, height/5+310);
        ShapeDrawable d3 = new ShapeDrawable(new RectShape());
        d3.setBounds(width/2, height*3/5+50, width-150, height*3/5+60);
        ShapeDrawable d4 = new ShapeDrawable(new RectShape());
        d4.setBounds(60, height*3/5-100, width/2-150, height*3/5-90);
        ShapeDrawable d5 = new ShapeDrawable(new RectShape());
        d5.setBounds(60, height/5, width/2, height/5+10);
        ShapeDrawable d6 = new ShapeDrawable(new RectShape());
        d6.setBounds(width/2, height/5, width/2+10, height*2/5+100);
        ShapeDrawable d7 = new ShapeDrawable(new RectShape());
        d7.setBounds(width/2, height*3/5-100, width/2+10, height-500);
        ShapeDrawable d8 = new ShapeDrawable(new RectShape());
        d8.setBounds(width/2-150, height*3/5-100, width/2-140, height-500);
        ShapeDrawable d9 = new ShapeDrawable(new RectShape());
        d9.setBounds(width/2, height*3/5-100, width, height*3/5-90);
        ShapeDrawable d10 = new ShapeDrawable(new RectShape());
        d10.setBounds(width/2+150, height*2/5+100, width/2+160, height*3/5+50);
        ShapeDrawable d11 = new ShapeDrawable(new RectShape());
        d11.setBounds(width-10, height/5+300, width, height*3/5-90);
        walls.add(d);
        walls.add(d2);
        walls.add(d3);
        walls.add(d4);
        walls.add(d5);
        walls.add(d6);
        walls.add(d7);
        walls.add(d8);
        walls.add(d9);
        walls.add(d10);
        walls.add(d11);
        walls.add(d_left);
        walls.add(d_right);

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawRect(width/2,height*3/5+50,width-150,height-500, paint);


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