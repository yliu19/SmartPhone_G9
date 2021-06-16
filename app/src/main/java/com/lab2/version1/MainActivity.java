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

    double [] distToData = new double [90];
    double [] indexOfDist = new double [90];
    double temp = 0;
    int kval = 9;
    int [] classifyTo = {0,0,0};
    int result = -1;
    String[] actionS = {"Still","Walk","Jump"};
    double [] [] still = {
            {0.19507,0.49639,0.09753,0.18901,0.45662,0.13233,0.10679,0.08123,0.25143,0.21019,0.37567,0.36003,0.097549,0.13136,0.26735,0.18248,0.11716,0.37756,0.2098,0.15386,0.11169,0.083309,0.27975,0.20175,0.091478,0.12771,0.15249,0.071567,0.11676,0.12612},
            {0.12141,0.3172,0.08374,0.14838,0.28831,0.079392,0.095203,0.054961,0.16859,0.25787,0.21546,0.11972,0.11134,0.10943,0.14453,0.081796,0.084726,0.097262,0.069939,0.084027,0.0792,0.031316,0.20722,0.15165,0.081881,0.087379,0.054502,0.059079,0.0878,0.068963}
    };
    double [] [] walk = {
            {6.3345,2.9809,6.5266,6.6281,3.4636,4.0101,4.8374,4.0023,5.2336,4.8746,5.0295,5.532,5.0859,3.5678,4.2456,3.8224,3.2224,3.4011,4.0319,4.7705,4.3547,3.8386,5.2459,3.6757,5.1337,4.9413,4.8519,5.1239,4.145,4.7008},
            {3.2329,2.1023,2.2391,2.5911,2.0106,2.1904,2.6407,2.5238,1.9393,1.5621,2.6938,2.676,2.4268,1.7566,1.5204,2.6467,2.4089,2.9853,1.7639,2.2784,2.9919,1.7596,2.6233,2.611,2.2873,2.161,2.1358,2.9687,2.8458,2.9033}
    };
    double [] [] jump = {
            {11.536,15.261,19.305,17.029,13.204,15.133,18.48,11.538,11.46,10.563,14.179,20.672,15.944,15.552,18.304,16.542,15.486,18.378,9.9079,15.537,12.311,12.432,18.298,12.706,14.952,13.469,12.54,12.918,18.496,12.511},
            {4.0393,5.6788,6.5035,6.5781,5.1514,9.9855,9.5697,5.7952,5.9148,4.9502,6.0155,4.6433,5.4232,5.7093,11.427,8.9028,8.0331,4.9146,5.2794,6.572,5.6256,6.1625,7.8103,5.0212,6.4888,5.3544,4.4429,6.1381,6.0802,5.7532}
    };

    int countsave = 0;
    double [] stepsave = new double [100];
    double [] stepsavex = new double [100];
    double [] stepsavey = new double [100];
    double [] stepsavez = new double [100];

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
        //System.out.println("height: " + height + "width: " + width);
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
/*        System.out.println("points count:" + countpoints);
        System.out.println("data points x.....: " + Arrays.toString(Arrays.copyOfRange(pointsSaveStore[0], 1000, 1431)));
        System.out.println("data points y.....: " + Arrays.toString(Arrays.copyOfRange(pointsSaveStore[1], 1000, 1431)));
        System.out.println("length of data:" + totalPoints);*/

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
                //state.setText(Integer.toString(trueDegree));
            }
            else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
                // get the the x,y,z values of the accelerometer
                aX = sensorEvent.values[0];
                aY = sensorEvent.values[1];
                aZ = sensorEvent.values[2];
                //isWalk((double)aX,(double)aZ);
                square = Math.sqrt(Math.pow(aX,2)+Math.pow(aY,2)+Math.pow(aZ,2));

                if (countsave<100){
                    stepsave[countsave] = square;
                    stepsavex[countsave] =  (double) aX;
                    stepsavey[countsave] =  (double) aZ;
                    stepsavez[countsave] =  (double) aX;
                    countsave++;
                    System.out.println(countsave);
                } else {
                    System.out.println("step data.................:");
                    System.out.println(Arrays.toString(stepsave));
                    System.out.println(Arrays.toString(stepsavex));
                    System.out.println(Arrays.toString(stepsavey));
                    System.out.println(Arrays.toString(stepsavez));
                    countsave = 0;
                }

                cur_dif = square - prev_square;
                //System.out.println("square: " + square + " predif: " + prev_dif + " curdif: " + cur_dif);

                if (cur_dif < 0 && prev_dif >= 0 && prev_square>=12.5) {
                //if (aX>1){
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
        //System.out.println("degrees: " + degrees + " steps: " + steps + " delta x:" + deltax + " delat y: "+ deltay);
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

    private void isWalk(double ampX,double ampZ){
        for (int j=0; j<30; j++){
            distToData[j] = Math.sqrt((ampX-still[0][j])*(ampX-still[0][j])+(ampZ-still[1][j])*(ampZ-still[1][j]));
        }
        for (int j=30; j<60; j++){
            distToData[j] = Math.sqrt((ampX-walk[0][j-30])*(ampX-walk[0][j-30])+(ampZ-walk[1][j-30])*(ampZ-walk[1][j-30]));
        }
        for (int j=60; j<90; j++){
            distToData[j] = Math.sqrt((ampX-jump[0][j-60])*(ampX-jump[0][j-60])+(ampZ-jump[1][j-60])*(ampZ-jump[1][j-60]));
        }
/*        System.out.println("Distance to still,walk,jump ");
        System.out.println(Arrays.toString(distToData));*/
        //initialize the index
        for (int m = 0;m<90;m++){
            indexOfDist[m] = m;
        }
        //sort distance and find closest k points
        for (int i=0;i<90;i++){
            for (int m=i+1;m<90;m++){
                if (distToData[i]>distToData[m]){
                    temp = distToData[i];
                    distToData[i] = distToData[m];
                    distToData[m] = temp;
                    temp = indexOfDist[i];
                    indexOfDist[i] = indexOfDist[m];
                    indexOfDist[m] = temp;
                }
            }
        }
/*        System.out.println("Sorted Distance to still,walk,jump  ");
        System.out.println(Arrays.toString(distToData));
        System.out.println("Index of Sorted Distance " );
        System.out.println(Arrays.toString(indexOfDist));*/
        //classify the point
        for (int i=0;i<kval;i++){
            if (indexOfDist[i]<30){
                classifyTo[0]++;
            }
            else if (indexOfDist[i]>60){
                classifyTo[2]++;
            }
            else{
                classifyTo[1]++;
            }
        }
        //classify the recorded data
        int [] sorted = new int[3];
        for (int g=0;g<3;g++){
            sorted[g] = classifyTo[g];
        }
        Arrays.sort(sorted);
        System.out.println("classify to s,w,j:");
        System.out.println(Arrays.toString(classifyTo));
        System.out.println("sorted classify:");
        System.out.println(Arrays.toString(sorted));
        for (int l=0;l<3;l++){
            if (classifyTo[l]==sorted[2]){
                result = l;
            }
        }
        System.out.println("results is :" + result);
        state.setText(actionS[result]);
        // update the text.
        result = -1;
        System.out.println("task finished!");
    }

}