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
    int countpoints = 0;
    int totalPoints = 2115;
    int [][] pointsSave = new int [2][totalPoints];
    int [][] pointsSaveStore = new int [2][totalPoints];
    int myColor = Color.parseColor("#757575");
    int degreeOffset = 0;
    int trueDegree = 0;
    double radian = 0;
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
        System.out.println("height: " + height + "width: " + width);
        //assign points
        for (int xa = 70;xa<=width/2-10;xa=xa+20){
            for (int ya = (int) (0.6*height - 90); ya<=height-510;ya=ya+20){
                pointsSave[0][countpoints] = xa;
                pointsSave[1][countpoints] = ya;
                pointsSaveStore[0][countpoints] = xa;
                pointsSaveStore[1][countpoints] = ya;
                countpoints ++;
            }
        }
        for (int xb = (int) width/2-140;xb<=width-160;xb=xb+20){
            for (int yb = (int) height/5+310; yb<=0.6*height-110;yb=yb+20){
                pointsSave[0][countpoints] = xb;
                pointsSave[1][countpoints] = yb;
                pointsSaveStore[0][countpoints] = xb;
                pointsSaveStore[1][countpoints] = yb;
                countpoints ++;
            }
        }
        for (int xc = 70;xc<=width/2-10;xc=xc+20){
            for (int yc = (int) height/5+10; yc<=height/5+290;yc=yc+20){
                pointsSave[0][countpoints] = xc;
                pointsSave[1][countpoints] = yc;
                pointsSaveStore[0][countpoints] = xc;
                pointsSaveStore[1][countpoints] = yc;
                countpoints ++;
            }
        }
        System.out.println("points count:" + countpoints);
        System.out.println("data points x.....: " + Arrays.toString(Arrays.copyOfRange(pointsSaveStore[0], 1000, 1431)));
        System.out.println("data points y.....: " + Arrays.toString(Arrays.copyOfRange(pointsSaveStore[1], 1000, 1431)));
        System.out.println("length of data:" + totalPoints);

        //create walls anf layout
        walls = new ArrayList<>();
        ShapeDrawable d1 = new ShapeDrawable(new RectShape());
        d1.setBounds(width/2, height/5, width/2+10, height*2/5+100);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(width/2+150, height*2/5+100, width/2+160, height*3/5+50);
        ShapeDrawable d3 = new ShapeDrawable(new RectShape());
        d3.setBounds(60, height-500, width-150, height-490);
        ShapeDrawable d4 = new ShapeDrawable(new RectShape());
        d4.setBounds(50, height/5, 60, height-490);
        ShapeDrawable d5 = new ShapeDrawable(new RectShape());
        d5.setBounds(width/2, 3*height/5-100, width-150, height-500);
        ShapeDrawable d6 = new ShapeDrawable(new RectShape());
        d6.setBounds(width-150, height/5+300, width-140, height-500);
        ShapeDrawable d7 = new ShapeDrawable(new RectShape());
        d7.setBounds(width/2, height/5+290, width-140, height/5+300);
/*        ShapeDrawable d8 = new ShapeDrawable(new RectShape());
        d8.setBounds(50, 3*height/5-100, width/2-140, 3*height/5-90);
        ShapeDrawable d9 = new ShapeDrawable(new RectShape());
        d9.setBounds(width/2-150, height/5+290, width/2-140, 3*height/5-100);*/
        ShapeDrawable d10 = new ShapeDrawable(new RectShape());
        d10.setBounds(60, height/5+290, width/2-140, 3*height/5-100);
        ShapeDrawable d11 = new ShapeDrawable(new RectShape());
        d11.setBounds(50, height/5, width/2, height/5+10);
        ShapeDrawable d12 = new ShapeDrawable(new RectShape());
        d12.setBounds(width/4+40, 3*height/5-100, width/4+42, 4*height/5-320);
        walls.add(d1);
        walls.add(d2);
        walls.add(d3);
        walls.add(d4);
        walls.add(d5);
        walls.add(d6);
        walls.add(d7);
        walls.add(d10);
        walls.add(d11);
        walls.add(d12);
        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);
        Paint paint = new Paint();
        //fill colors
        paint.setColor(myColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(width/2-140,height/5+300,width,3*height/5-100, paint);
        canvas.drawRect(60,3*height/5-100,width/2,height-500, paint);
        canvas.drawRect(60,height/5+10,width/2,height/5+290, paint);
        //draw
        drawable = new ShapeDrawable(new OvalShape());
        for (int i = 0;i<totalPoints;i++) {
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(pointsSave[0][i] - 2, pointsSave[1][i] - 2, pointsSave[0][i] + 2, pointsSave[1][i] + 2);
            drawable.draw(canvas);
        }
/*        drawable.getPaint().setColor(Color.BLUE);
        drawable.setBounds(pointsSave[0][1000] - 20, pointsSave[1][1000] - 20, pointsSave[0][1000] + 20, pointsSave[1][1000] + 20);
        drawable.draw(canvas);*/
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
        //cell draw
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
        degreeOffset = mAzimuth;
        //pointsSave = pointsSaveStore;
        totalPoints = 2115;
        cleanDraw();
        for (int i = 0;i<totalPoints;i++) {
            pointsSave[0][i] = pointsSaveStore[0][i];
            pointsSave[1][i] = pointsSaveStore[1][i];
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(pointsSave[0][i] - 2, pointsSave[1][i] - 2, pointsSave[0][i] + 2, pointsSave[1][i] + 2);
            drawable.draw(canvas);
        }
        System.out.println("data points x.....: " + Arrays.toString(Arrays.copyOfRange(pointsSave[0], 1000, 1431)));
        System.out.println("data points y.....: " + Arrays.toString(Arrays.copyOfRange(pointsSave[1], 1000, 1431)));
        System.out.println("degree offset: " + degreeOffset);
        count = 0;
        title.setText("You walked " + Integer.toString(count) + "steps");
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
                trueDegree = mAzimuth - degreeOffset;
                if (trueDegree<0){
                    trueDegree = trueDegree + 360;
                }
                // rotation animation - reverse turn degree degrees
                RotateAnimation ra = new RotateAnimation(
                        DegreeStart,
                        -trueDegree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                // set the compass animation after the end of the reservation status
                ra.setFillAfter(true);
                // set how long the animation for the compass image will take place
                ra.setDuration(210);
                // Start animation of compass image
                comp.startAnimation(ra);
                DegreeStart = -trueDegree;
                state.setText(Integer.toString(trueDegree));
            }
            else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
                // get the the x,y,z values of the accelerometer
                aX = sensorEvent.values[0];
                aY = sensorEvent.values[1];
                aZ = sensorEvent.values[2];
                square = Math.sqrt(Math.pow(aX,2)+Math.pow(aY,2)+Math.pow(aZ,2));
                cur_dif = square - prev_square;
                //System.out.println("square: " + square + " predif: " + prev_dif + " curdif: " + cur_dif);
                if (cur_dif < 0 && prev_dif >= 0 && prev_square>=11) {
                    count = count + 1;
                    title.setText("You walked " + Integer.toString(count) + "steps");
                    //System.out.println(count);
                    for (int k=0;k<67.5/4;k++){
                        updateDrawPoints(4,trueDegree);
                    }



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

    /**
     * Determines if the drawable dot intersects with any of the walls.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision() {
        for(ShapeDrawable wall : walls) {
            if(isCollision(wall,drawable))
                return true;
        }
        return false;
    }

    /**
     * Determines if two shapes intersect.
     * @param first The first shape.
     * @param second The second shape.
     * @return True if they intersect, false otherwise.
     */
    private boolean isCollision(ShapeDrawable first, ShapeDrawable second) {
        Rect firstRect = new Rect(first.getBounds());
        return firstRect.intersect(second.getBounds());
    }


    private void updatePoints(int steps,double degrees){
        radian = degrees/180 * Math.PI;
        double deltax = steps*Math.sin(radian);
        double deltay = steps*Math.cos(radian);
        System.out.println("degrees: " + degrees + " steps: " + steps + " delta x:" + deltax + " delat y: "+ deltay);
        for (int i = 0;i<totalPoints;i++){
            pointsSave[0][i] = (int) Math.round(pointsSave[0][i] + deltax);
            pointsSave[1][i] = (int) Math.round(pointsSave[1][i] - deltay);
        }
    }

    private void updateDrawPoints(int steps,double degrees){
        cleanDraw();
        // redrawing of the points
        for (int i = 0;i<totalPoints;i++) {
            drawable.getPaint().setColor(myColor);
            drawable.setBounds(pointsSave[0][i] - 2, pointsSave[1][i] - 2, pointsSave[0][i] + 2, pointsSave[1][i] + 2);
            drawable.draw(canvas);
        }
        updatePoints(steps,degrees);
        for (int i = 0;i<totalPoints;i++) {
            drawable.setBounds(pointsSave[0][i] - 2, pointsSave[1][i] - 2, pointsSave[0][i] + 2, pointsSave[1][i] + 2);
            if(isCollision()) {
                for (int j = i; j < totalPoints - 1; j++) {
                    pointsSave[0][j] = pointsSave[0][j + 1];
                    pointsSave[1][j] = pointsSave[1][j + 1];
                }
                i--;
                totalPoints--;
            }
        }
/*        System.out.println("data points x.....: " + Arrays.toString(pointsSave[0]));
        System.out.println("data points y.....: " + Arrays.toString(pointsSave[1]));*/
        //System.out.println("length of data:" + totalPoints);
        for (int i = 0;i<totalPoints;i++) {
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(pointsSave[0][i] - 2, pointsSave[1][i] - 2, pointsSave[0][i] + 2, pointsSave[1][i] + 2);
            drawable.draw(canvas);
        }
    }
    private void cleanDraw(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Paint paint = new Paint();
        paint.setColor(myColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(width/2-140,height/5+310,width-150,3*height/5-110, paint);
        canvas.drawRect(70,3*height/5-90,width/2-10,height-510, paint);
        canvas.drawRect(70,height/5+10,width/2-10,height/5+290, paint);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

}