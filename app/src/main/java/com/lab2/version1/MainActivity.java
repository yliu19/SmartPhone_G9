package com.lab2.version1;

import android.app.Activity;
import android.content.Context;
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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import android.graphics.DashPathEffect;
import java.lang.Math;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
/**
 * Smart Phone Sensing. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements SensorEventListener,OnClickListener {

    /**
     * The buttons.
     */
    private Button locate;
    /**
     * The text view.
     */
    private TextView title,state;
    // define the compass picture that will be use
    private ImageView comp;
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
    SensorManager sensorManager;
    /**
     * The accelerometer.
     */
    private Sensor accelerometer;
    /**
     * Accelerometer x value
     */
    private float aX = 0;
    /**
     * Accelerometer y value
     */
    private float aY = 0;
    /**
     * Accelerometer z value
     */
    private float aZ = 0;

    //other parameters
    boolean running = false;
    boolean step = false;
    float[] orientation = new float[3];
    float[] rMat = new float[9];
    int mAzimuth = 0;
    private float DegreeStart = 0f;
    private List<ShapeDrawable> walls;
    double square = 0;
    double prev_square = 0;
    double prev_dif = 0;
    double cur_dif = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
        locate = (Button) findViewById(R.id.button);

        // set the text view
        title = (TextView) findViewById(R.id.texttitle);
        state = (TextView) findViewById(R.id.button2);

        comp = (ImageView) findViewById(R.id.compass);

        // set listeners
        locate.setOnClickListener(this);

        //
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // if the default accelerometer exists
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // set accelerometer
            accelerometer = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // No accelerometer!
        }
        /////////////////////////////////////////////
        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        walls = new ArrayList<>();
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
        //fill colors
        int myColor = Color.parseColor("#757575");
        paint.setColor(myColor);
        paint.setStyle(Paint.Style.FILL);
        //laundrt room
        canvas.drawRect(width/2,height*3/5-100,width-150,height-500, paint);
        //home
        canvas.drawRect(60,height/5+300,width-150,height-500, paint);
        //bedroom
        canvas.drawRect(60,height*3/5-100,width/2-150,height-500, paint);
        //balcony
        canvas.drawRect(width-150,height/5+300,width,height*3/5-100, paint);
        //stairs
        canvas.drawRect(60,height/5,width/2,height/5+300, paint);
        //draw boundaries
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
        canvas.drawRect(60,height*3/5-100,width/2,height-500, paint);
        //stairs
        canvas.drawRect(60,height/5,width/2,height/5+300, paint);
        //living room
        canvas.drawRect(60,height/5+300,width/2-130,height*3/5-100, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(width/2+5,height*3/5+55,width-155,height-505, paint);
        canvas.drawRect(60,height/5+300,width/2-130,height*3/5-100, paint);
        // draw the objects
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
        Paint fgPaintSel = new Paint();
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[] {10f,20f}, 0f));
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        canvas.drawRect(60,height*4/5-300,width/4+30,height-500, fgPaintSel);
        canvas.drawRect(60,height*3/5-100,width/4+30,height*4/5-300, fgPaintSel);
        canvas.drawRect(width/4+30,height*4/5-300,width/2,height-500, fgPaintSel);
        canvas.drawRect(width/4+30,height*3/5-100,width/2,height*4/5-300, fgPaintSel);
        canvas.drawRect(width/2,height/5+300,width-150,height*2/5+100, fgPaintSel);
        //////////////////////////////////////////////

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
        System.out.println("Hello, you pressed");
        step = !step;
        System.out.println(step);
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Sensor rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotationVector != null) {
            sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found !", Toast.LENGTH_SHORT).show();
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);


    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (running) {
            if( sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR ){
                // calculate th rotation matrix
                SensorManager.getRotationMatrixFromVector( rMat, sensorEvent.values );
                // get the azimuth value (orientation[0]) in degree
                mAzimuth = (int) ( Math.toDegrees( SensorManager.getOrientation( rMat, orientation )[0] ) + 360 ) % 360;
                System.out.println("degrees: " + mAzimuth);

                // rotation animation - reverse turn degree degrees
                RotateAnimation ra = new RotateAnimation(
                        DegreeStart,
                        -mAzimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                // set the compass animation after the end of the reservation status
                ra.setFillAfter(true);
                // set how long the animation for the compass image will take place
                ra.setDuration(210);
                // Start animation of compass image
                comp.startAnimation(ra);
                DegreeStart = -mAzimuth;
            }
            else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
                // get the the x,y,z values of the accelerometer
                aX = sensorEvent.values[0];
                aY = sensorEvent.values[1];
                aZ = sensorEvent.values[2];
                square = Math.sqrt(Math.pow(aX,2)+Math.pow(aY,2)+Math.pow(aZ,2));
                cur_dif = square - prev_square;
                System.out.println("square: " + square + " predif: " + prev_dif + " curdif: " + cur_dif);
                if (cur_dif < 0 && prev_dif >= 0 && prev_square>=11) {
                    count = count + 1;
                    title.setText("You walked " + Integer.toString(count) + "steps");
                    System.out.println(count);
                }
                prev_dif = cur_dif;
                prev_square = square;
            }
/*            System.out.println(Arrays.toString(sensorEvent.values));
            state.setText(String.valueOf(sensorEvent.values));*/
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}