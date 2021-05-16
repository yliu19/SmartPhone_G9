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
//        Aisle = (TextView) findViewById(R.id.aisle);

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


        ShapeDrawable d1 = new ShapeDrawable(new RectShape());
        d1.setBounds(width/2, height/5+300, width/2+10, height*2/5+100);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(width/2+150, height*2/5+100, width/2+160, height*3/5+50);
        walls.add(d1);
        walls.add(d2);

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        //laundrt room
        canvas.drawRect(width/2,height*3/5-100,width-150,height-500, paint);
        //inaccessible
        canvas.drawRect(width/2,height*3/5+50,width-150,height-500, paint);
        //home
        canvas.drawRect(60,height/5+300,width-150,height-500, paint);
        //bedroom
        canvas.drawRect(60,height*3/5-100,width/2-150,height-500, paint);
        //balcony
        canvas.drawRect(width-150,height/5+300,width,height*3/5-100, paint);
        //stairs
        canvas.drawRect(60,height/5,width/2,height/5+300, paint);



        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(width/2+5,height*3/5+55,width-155,height-505, paint);


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