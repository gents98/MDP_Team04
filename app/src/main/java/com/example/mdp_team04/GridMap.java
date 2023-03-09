package com.example.mdp_team04;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class GridMap extends View {
    public GridMap(Context c) {
        super(c);
        initMap();
    }

    private Paint blackPaint = new Paint();
    private Paint obstacleColor = new Paint();
    private Paint robotColor = new Paint();
    private Paint startColor = new Paint();
    private Paint unexploredColor = new Paint();
    private Paint exploredColor = new Paint();
    private Paint obstacleDirectionColor = new Paint();
    private Paint fastestPathColor = new Paint();

    private Paint textSize = new Paint();

    private int obscounter = 0;
    int idcounter=0;
    private static String robotDirection = "None";
    private static int[] startCoord = new int[]{-1, -1};
    private static int[] curCoord = new int[]{-1, -1};
    private static int[] oldCoord = new int[]{-1, -1};
    private static ArrayList<int[]> obstacleCoord = new ArrayList<>();
    private static ArrayList<String[]> obstacleDirectionCoord = new ArrayList<>();

    private static ArrayList<String[]> saveObstacleDirectionCoord = new ArrayList<>();

    private static ArrayList<String[]> prevObstacleDirectionCoord = new ArrayList<>();
    private static int[] prevStartCoord = new int[]{-1,-1};
    private static String prevRobotDirection;
    private static boolean canDrawRobot = false;
    private static boolean setEditStatus = false;
    private static boolean startCoordStatus = false;
    private static boolean setObstacleStatus = false;
    private static boolean setNorthObstacleStatus = false;
    private static boolean setSouthObstacleStatus = false;
    private static boolean setWestObstacleStatus = false;
    private static boolean setEastObstacleStatus = false;
    private static boolean setEditDirectionObstacleStatus = false;
    private static boolean setEditStartCoordStatus = false;
    private static boolean unSetCellStatus = false;
    private static boolean setExploredStatus = false;
    private Bitmap obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);
    private Bitmap robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);

    private static final String TAG = "GridMap";
    private static final int COL = 20;
    private static final int ROW = 20;
    private static float cellSize;
    private static Cell[][] cells;
    String cellId;
    String[] order;
    private boolean mapDrawn = false;


    public GridMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initMap();
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        obstacleColor.setColor(Color.BLACK);
        robotColor.setColor(Color.GREEN);
        startColor.setColor(Color.CYAN);
        unexploredColor.setColor(Color.LTGRAY);
        exploredColor.setColor(Color.WHITE);
        obstacleDirectionColor.setColor(Color.WHITE);
        fastestPathColor.setColor(Color.MAGENTA);
    }

    private void initMap() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        showLog("Entering onDraw");
        super.onDraw(canvas);
        showLog("Redrawing map");

        //CREATE CELL COORDINATES
        showLog("Creating Cell");

        if (!mapDrawn) {
            this.createCell();
            mapDrawn = true;
        }

        drawIndividualCell(canvas);
        drawHorizontalLines(canvas);
        drawVerticalLines(canvas);
        drawGridNumber(canvas);
        if (getCanDrawRobot())
            drawRobot(canvas, curCoord);
        drawObstacleWithDirection(canvas, obstacleDirectionCoord);

        showLog("Exiting onDraw");
    }

    private void drawIndividualCell(Canvas canvas) {
        showLog("Entering drawIndividualCell");
        for (int x = 1; x <= COL; x++)
            for (int y = 0; y < ROW; y++)
                if (!cells[x][y].type.equals("image") && cells[x][y].getId().equals("-1")) {

                    canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                } else {
                    Paint textPaint = new Paint();
                    if(cells[x][y].type.equals("image"))
                        textPaint.setTextSize(14);
                    else
                        textPaint.setTextSize(10);
                    textPaint.setColor(Color.WHITE);
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawRect(cells[x][y].startX, cells[x][y].startY, cells[x][y].endX, cells[x][y].endY, cells[x][y].paint);
                    canvas.drawText(cells[x][y].getId(),(cells[x][y].startX+cells[x][y].endX)/2, cells[x][y].endY + (cells[x][y].startY-cells[x][y].endY)/4, textPaint);
                }

        showLog("Exiting drawIndividualCell");
    }

    public void drawCellid(int x, int y, String id) {
        showLog("Entering drawImageNumberCell");
        cells[x][20-y].setId(id);
        this.invalidate();
        showLog("Exiting drawImageNumberCell");
    }
    public void drawImageNumberCell(int x, int y, String id) {
        showLog("Entering drawImageNumberCell");
        cells[x][20-y].setType("image");
        cells[x][20-y].setId(id);
        this.invalidate();
        showLog("Exiting drawImageNumberCell");
    }

    private void drawHorizontalLines(Canvas canvas) {
        for (int y = 0; y <= ROW; y++)
            canvas.drawLine(cells[1][y].startX, cells[1][y].startY - (cellSize / 30), cells[20][y].endX, cells[20][y].startY - (cellSize / 30), blackPaint);
    }

    private void drawVerticalLines(Canvas canvas) {
        for (int x = 0; x <= COL; x++)
            canvas.drawLine(cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][0].startY - (cellSize / 30), cells[x][0].startX - (cellSize / 30) + cellSize, cells[x][19].endY + (cellSize / 30), blackPaint);
    }

    private void drawGridNumber(Canvas canvas) {
        showLog("Entering drawGridNumber");
        for (int x = 1; x <= COL; x++) {
            if (x > 9)
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX + (cellSize / 5), cells[x][20].startY + (cellSize / 3), blackPaint);
            else
                canvas.drawText(Integer.toString(x-1), cells[x][20].startX + (cellSize / 3), cells[x][20].startY + (cellSize / 3), blackPaint);
        }
        for (int y = 0; y < ROW; y++) {
            if ((20 - y) > 9)
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 2), cells[0][y].startY + (cellSize / 1.5f), blackPaint);
            else
                canvas.drawText(Integer.toString(19 - y), cells[0][y].startX + (cellSize / 1.5f), cells[0][y].startY + (cellSize / 1.5f), blackPaint);
        }
        showLog("Exiting drawGridNumber");
    }

//    private void drawRobot(Canvas canvas, int[] curCoord) {
//        showLog("Entering drawRobot");
//        int androidRowCoord = this.convertRow(curCoord[1]);
//        for (int y = androidRowCoord; y <= androidRowCoord + 1; y++)
//            canvas.drawLine(cells[curCoord[0] - 1][y].startX, cells[curCoord[0] - 1][y].startY - (cellSize / 30), cells[curCoord[0] + 1][y].endX, cells[curCoord[0] + 1][y].startY - (cellSize / 30), robotColor);
//        for (int x = curCoord[0] - 1; x < curCoord[0] + 1; x++)
//            canvas.drawLine(cells[x][androidRowCoord - 1].startX - (cellSize / 30) + cellSize, cells[x][androidRowCoord - 1].startY, cells[x][androidRowCoord + 1].startX - (cellSize / 30) + cellSize, cells[x][androidRowCoord + 1].endY, robotColor);
//
//        switch (this.getRobotDirection()) {
//            case "N":
//                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord + 1].startX, cells[curCoord[0] - 1][androidRowCoord + 1].endY, (cells[curCoord[0]][androidRowCoord - 1].startX + cells[curCoord[0]][androidRowCoord - 1].endX) / 2, cells[curCoord[0]][androidRowCoord - 1].startY, blackPaint);
//                canvas.drawLine((cells[curCoord[0]][androidRowCoord - 1].startX + cells[curCoord[0]][androidRowCoord - 1].endX) / 2, cells[curCoord[0]][androidRowCoord - 1].startY, cells[curCoord[0] + 1][androidRowCoord + 1].endX, cells[curCoord[0] + 1][androidRowCoord + 1].endY, blackPaint);
//                break;
//            case "S":
//                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord - 1].startX, cells[curCoord[0] - 1][androidRowCoord - 1].startY, (cells[curCoord[0]][androidRowCoord + 1].startX + cells[curCoord[0]][androidRowCoord + 1].endX) / 2, cells[curCoord[0]][androidRowCoord + 1].endY, blackPaint);
//                canvas.drawLine((cells[curCoord[0]][androidRowCoord + 1].startX + cells[curCoord[0]][androidRowCoord + 1].endX) / 2, cells[curCoord[0]][androidRowCoord + 1].endY, cells[curCoord[0] + 1][androidRowCoord - 1].endX, cells[curCoord[0] + 1][androidRowCoord - 1].startY, blackPaint);
//                break;
//            case "E":
//                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord - 1].startX, cells[curCoord[0] - 1][androidRowCoord - 1].startY, cells[curCoord[0] + 1][androidRowCoord].endX, cells[curCoord[0] + 1][androidRowCoord - 1].endY + (cells[curCoord[0] + 1][androidRowCoord].endY - cells[curCoord[0] + 1][androidRowCoord - 1].endY) / 2, blackPaint);
//                canvas.drawLine(cells[curCoord[0] + 1][androidRowCoord].endX, cells[curCoord[0] + 1][androidRowCoord - 1].endY + (cells[curCoord[0] + 1][androidRowCoord].endY - cells[curCoord[0] + 1][androidRowCoord - 1].endY) / 2, cells[curCoord[0] - 1][androidRowCoord + 1].startX, cells[curCoord[0] - 1][androidRowCoord + 1].endY, blackPaint);
//                break;
//            case "W":
//                canvas.drawLine(cells[curCoord[0] + 1][androidRowCoord - 1].endX, cells[curCoord[0] + 1][androidRowCoord - 1].startY, cells[curCoord[0] - 1][androidRowCoord].startX, cells[curCoord[0] - 1][androidRowCoord - 1].endY + (cells[curCoord[0] - 1][androidRowCoord].endY - cells[curCoord[0] - 1][androidRowCoord - 1].endY) / 2, blackPaint);
//                canvas.drawLine(cells[curCoord[0] - 1][androidRowCoord].startX, cells[curCoord[0] - 1][androidRowCoord - 1].endY + (cells[curCoord[0] - 1][androidRowCoord].endY - cells[curCoord[0] - 1][androidRowCoord - 1].endY) / 2, cells[curCoord[0] + 1][androidRowCoord + 1].endX, cells[curCoord[0] + 1][androidRowCoord + 1].endY, blackPaint);
//                break;
//            default:
//                Toast.makeText(this.getContext(), "Error with drawing robot (unknown direction)", Toast.LENGTH_LONG).show();
//                break;
//        }
//        showLog("Exiting drawRobot");
//    }

    private ArrayList<String[]> getObstacleDirectionCoord() {
        return obstacleDirectionCoord;
    }

    public String getRobotDirection() {
        return robotDirection;
    }

    public boolean getMapDrawn() {
        return mapDrawn;
    }

    public void setUnSetCellStatus(boolean status) {
        unSetCellStatus = status;
    }

    public boolean getUnSetCellStatus() {
        return unSetCellStatus;
    }

    public void setSetObstacleStatus(boolean status) {
        setObstacleStatus = status;
    }

    public boolean getSetObstacleStatus() {
        return setObstacleStatus;
    }

    public void setSetNorthObstacleStatus(boolean northObstacleStatus) {
        setNorthObstacleStatus = northObstacleStatus;
    }

    public boolean getSetNorthObstacleStatus() {
        return setNorthObstacleStatus;
    }

    public void setSetSouthObstacleStatus(boolean southObstacleStatus) {
        setSouthObstacleStatus = southObstacleStatus;
    }

    public boolean getSetSouthObstacleStatus() {
        return setSouthObstacleStatus;
    }

    public void setSetEastObstacleStatus(boolean eastObstacleStatus) {
        setEastObstacleStatus = eastObstacleStatus;
    }

    public boolean getSetEastObstacleStatus() {
        return setEastObstacleStatus;
    }

    public void setSetWestObstacleStatus(boolean westObstacleStatus) {
        setWestObstacleStatus = westObstacleStatus;
    }

    public boolean getSetWestObstacleStatus() {
        return setWestObstacleStatus;
    }

    public void setExploredStatus(boolean status) {
        setExploredStatus = status;
    }

    public boolean getExploredStatus() {
        return setExploredStatus;
    }

    public void setEditMapStatus(boolean status) { setEditStatus = status; }

    public void setStartCoordStatus(boolean status) {
        startCoordStatus = status;
    }

    private boolean getStartCoordStatus() {
        return startCoordStatus;
    }

    public boolean getCanDrawRobot() {
        return canDrawRobot;
    }

    private void createCell() {
        showLog("Entering cellCreate");
        cells = new Cell[COL + 1][ROW + 1];
        this.calculateDimension();
        cellSize = this.getCellSize();

        for (int x = 0; x <= COL; x++)
            for (int y = 0; y <= ROW; y++)
                cells[x][y] = new Cell(x * cellSize + (cellSize / 30), y * cellSize + (cellSize / 30), (x + 1) * cellSize, (y + 1) * cellSize, unexploredColor, "unexplored");
        showLog("Exiting createCell");
    }

    public void setStartCoord(int col, int row) {
        showLog("Entering setStartCoord");
        startCoord[0] = col;
        startCoord[1] = row;
        String direction = getRobotDirection();
        if(direction.equals("None")) {
            direction = "N";
        }
        if (this.getStartCoordStatus() || setEditStartCoordStatus)
            this.setCurCoord(col, row, direction);
        showLog("Exiting setStartCoord");
    }

    private int[] getStartCoord() {
        return startCoord;
    }

    public void setCurCoord(int col, int row, String direction) {
        showLog("Entering setCurCoord");
        curCoord[0] = col;
        curCoord[1] = row;
        this.setRobotDirection(direction);
        this.updateRobotAxis(col, row, direction);

        row = this.convertRow(row);
        for (int x = col - 1; x <= col + 1; x++)
            for (int y = row - 1; y <= row + 1; y++)
                if (x <= COL && (20-y) <= ROW && x >= 1 && (20-y) >= 1)
                    cells[x][y].setType("robot");
        showLog("Exiting setCurCoord");
    }

    public int[] getCurCoord() {
        return curCoord;
    }

    private void calculateDimension() {
        this.setCellSize(getWidth()/(COL+1));
    }

    private int convertRow(int row) {
        return (20 - row);
    }

    private void setCellSize(float cellSize) {
        GridMap.cellSize = cellSize;
    }

    private float getCellSize() {
        return cellSize;
    }

    private void setOldRobotCoord(int oldCol, int oldRow) {
        showLog("Entering setOldRobotCoord");
        oldCoord[0] = oldCol;
        oldCoord[1] = oldRow;
        oldRow = this.convertRow(oldRow);
        for (int x = oldCol - 1; x <= oldCol + 1; x++)
            for (int y = oldRow - 1; y <= oldRow + 1; y++)
                if (x <= COL && (20-y) <= ROW && x >= 1 && (20-y) >= 1)
                    cells[x][y].setType("explored");
        showLog("Exiting setOldRobotCoord");
    }

    private int[] getOldRobotCoord() {
        return oldCoord;
    }

    private void setObstacleDirectionCoordinate(int col, int row, String obstacleDirection) {
        showLog("Entering setObstacleDirectionCoordinate");
        String[] obstacleDirCoord = new String[3];
        obstacleDirCoord[0] = String.valueOf(col);
        obstacleDirCoord[1] = String.valueOf(row);
        obstacleDirCoord[2] = obstacleDirection;
        this.getObstacleDirectionCoord().add(obstacleDirCoord);

        row = convertRow(row);
        cells[col][row].setType("obstacleDirection");
        showLog("Exiting setObstacleDirectionCoordinate");
    }

    public void setRobotDirection(String direction) {
        robotDirection = direction;
        this.invalidate();;
    }

    private void updateRobotAxis(int col, int row, String direction) {
        TextView xAxisTextView =  ((Activity)this.getContext()).findViewById(R.id.xAxisTextView);
        TextView yAxisTextView =  ((Activity)this.getContext()).findViewById(R.id.yAxisTextView);

        xAxisTextView.setText(String.valueOf(col-1));
        yAxisTextView.setText(String.valueOf(row-1));
    }

    private void setObstacleCoord(int col, int row) {
        showLog("Entering setObstacleCoord");
        int[] obstacleCoord = new int[]{col, row};
        GridMap.obstacleCoord.add(obstacleCoord);
        row = this.convertRow(row);
        cells[col][row].setType("obstacle");
        showLog("Exiting setObstacleCoord");
    }

    private ArrayList<int[]> getObstacleCoord() {
        return obstacleCoord;
    }

    private void showLog(String message) {
        Log.d(TAG, message);
    }

    private void drawObstacleWithDirection(Canvas canvas, ArrayList<String[]> obstacleDirectionCoord) {
        showLog("Entering drawObstacleWithDirection");
        RectF rect;

        for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
            int col = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
            int row = convertRow(Integer.parseInt(obstacleDirectionCoord.get(i)[1]));
            rect = new RectF(col * cellSize, row * cellSize, (col + 1) * cellSize, (row + 1) * cellSize);
            switch (obstacleDirectionCoord.get(i)[2]) {
                case "N":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_north_obstacle);
                    break;
                case "E":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_east_obstacle);
                    break;
                case "S":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_south_obstacle);
                    break;
                case "W":
                    obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_west_obstacle);
                    break;
                default:
                    break;
            }
            canvas.drawBitmap(obstacleDirectionBitmap, null, rect, null);
            showLog("Exiting drawObstacleWithDirection");
        }
    }

    private void drawRobot(Canvas canvas, int[] curCoord) {
        showLog("Entering drawObstacleWithDirection");
        RectF rect;

        for (int i = 0; i < curCoord.length ; i++) {
            int col = curCoord[0];
            int row = convertRow(curCoord[1]);
            rect = new RectF((col-1) * cellSize, (row-1) * cellSize, (col + 2) * cellSize, (row + 2) * cellSize);
            switch (this.getRobotDirection()) {
                case "N":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f1_north);
                    break;
                case "E":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f1_east);
                    break;
                case "S":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f1_south);
                    break;
                case "W":
                    robotDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.f1_west);
                    break;
                default:
                    break;
            }
            canvas.drawBitmap(robotDirectionBitmap, null, rect, null);
            showLog("Exiting drawObstacleWithDirection");
        }
    }

    private class Cell {
        float startX, startY, endX, endY;
        Paint paint;
        String type;
        String id = "-1";


        private Cell(float startX, float startY, float endX, float endY, Paint paint, String type) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.paint = paint;
            this.type = type;
        }

        public void setType(String type) {
            this.type = type;
            switch (type) {
                case "obstacle":
                    this.paint = obstacleColor;

                    break;
                case "robot":
                    this.paint = robotColor;
                    break;
                case "start":
                    this.paint = startColor;
                    break;
                case "unexplored":
                    this.paint = unexploredColor;
                    break;
                case "explored":
                    this.paint = exploredColor;
                    break;
                case "obstacleDirection":
                    this.paint = obstacleColor;
                    break;
                case "fastestPath":
                    this.paint = fastestPathColor;
                    break;
                case "image":
                    this.paint = obstacleColor;

                default:
                    showLog("setTtype default: " + type);
                    break;
            }
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showLog("Entering onTouchEvent");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int) (event.getX() / cellSize);
            int row = this.convertRow((int) (event.getY() / cellSize));
            ToggleButton setStartPointToggleBtn = ((Activity)this.getContext()).findViewById(R.id.setStartPointToggleBtn);

            if (startCoordStatus) {
                if (canDrawRobot) {
                    int[] startCoord = this.getStartCoord();
                    if (startCoord[0] >= 1 && startCoord[1] >= 1) {
                        startCoord[1] = this.convertRow(startCoord[1]);
                        for (int x = startCoord[0] - 1; x <= startCoord[0] + 1; x++)
                            for (int y = startCoord[1] - 1; y <= startCoord[1] + 1; y++)
                                if (x <= COL && (20-y) <= ROW && x >= 1 && (20-y) >= 1)
                                    cells[x][y].setType("unexplored");
                    }
                }
                else
                    canDrawRobot = true;
                this.setStartCoord(column, row);
                startCoordStatus = false;
                String direction = getRobotDirection();
                if(direction.equals("None")) {
                    direction = "N";
                }
                updateRobotAxis(column, row, direction);
                if (setStartPointToggleBtn.isChecked())
                    setStartPointToggleBtn.toggle();
                this.invalidate();
                return true;
            }
            if (setEditStatus) {

                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        prevObstacleDirectionCoord = new ArrayList<>();
                        prevObstacleDirectionCoord.add(obstacleDirectionCoord.get(i));
                        setEditDirectionObstacleStatus = true;
                    }
                }

                for (int i = 0; i < startCoord.length; i++) {
                    showLog(startCoord[0] + " " + column + " " + startCoord[1] + " " + row);
                    if (startCoord[0] == column && startCoord[1] == row) {
                        showLog("TRUE");
                        prevStartCoord[0] = startCoord[0];
                        prevStartCoord[1] = startCoord[1];
                        prevRobotDirection = getRobotDirection();
                        setEditStartCoordStatus = true;
                    }
                }

                this.invalidate();
                return true;
            }
            if (setObstacleStatus) {

                this.setObstacleCoord(column, row);
                this.invalidate();
                return true;
            }
            if (setNorthObstacleStatus) {
                obscounter ++;
                if (checkUnexploredCell(column, row))
                {
                    this.setObstacleDirectionCoordinate(column, row, "N");
                    drawCellid(column, row, Integer.toString(obscounter));

                }
                this.invalidate();
                return true;
            }
            if (setSouthObstacleStatus) {
                obscounter ++;
                if (checkUnexploredCell(column, row)) {
                    this.setObstacleDirectionCoordinate(column, row, "S");
                    drawCellid(column, row, Integer.toString(obscounter));

                }
                this.invalidate();
                return true;
            }
            if (setEastObstacleStatus) {
                obscounter ++;
                if (checkUnexploredCell(column, row)) {
                    this.setObstacleDirectionCoordinate(column, row, "E");
                    drawCellid(column, row, Integer.toString(obscounter));
                }
                this.invalidate();
                return true;
            }
            if (setWestObstacleStatus) {
                obscounter ++;
                if (checkUnexploredCell(column, row)) {
                    this.setObstacleDirectionCoordinate(column, row, "W");
                    drawCellid(column, row, Integer.toString(obscounter));
                }
                this.invalidate();
                return true;
            }
            if (setExploredStatus) {
                cells[column][20-row].setType("explored");
                this.invalidate();
                return true;
            }
            if (unSetCellStatus) {
                cells[column][20 - row].setType("unexplored");
                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
                    if (obstacleDirectionCoord.get(i)[0].equals(Integer.toString(column)) && obstacleDirectionCoord.get(i)[1].equals(Integer.toString(row))) {
                        cells[Integer.parseInt(prevObstacleDirectionCoord.get(i)[0])][20 - Integer.parseInt(prevObstacleDirectionCoord.get(i)[1])].setId("");
                        obstacleDirectionCoord.remove(i);

                    }
                }
                this.invalidate();
                return true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP && setEditStatus) {
            int column = (int) (event.getX() / cellSize);
            int row = this.convertRow((int) (event.getY() / cellSize));

            if (setEditDirectionObstacleStatus) {
                if (column > COL || row > ROW || checkUnexploredCell(column, row)) {

                    for (int i = 0; i < prevObstacleDirectionCoord.size(); i++) {
                        cells[Integer.parseInt(prevObstacleDirectionCoord.get(i)[0])][20 - Integer.parseInt(prevObstacleDirectionCoord.get(i)[1])].setType("unexplored");
                        for (int j = 0; j < obstacleDirectionCoord.size(); j++) {
                            if (obstacleDirectionCoord.get(j)[0].equals(prevObstacleDirectionCoord.get(i)[0]) && obstacleDirectionCoord.get(j)[1].equals(prevObstacleDirectionCoord.get(i)[1])) {
                                obstacleDirectionCoord.remove(j);
                                cellId = cells[Integer.parseInt(prevObstacleDirectionCoord.get(i)[0])][20 - Integer.parseInt(prevObstacleDirectionCoord.get(i)[1])].getId();
                                cells[Integer.parseInt(prevObstacleDirectionCoord.get(i)[0])][20 - Integer.parseInt(prevObstacleDirectionCoord.get(i)[1])].setId("");

                            }
                        }

                        if (column <= COL && row <= ROW) {
                            this.setObstacleDirectionCoordinate(column, row, prevObstacleDirectionCoord.get(i)[2]);
                            cells[column][20 - row].setId(cellId);
                        }


                    }

                    prevObstacleDirectionCoord = new ArrayList<>();
                    setEditDirectionObstacleStatus = false;

                    this.invalidate();
                    return true;
                }
            }

            else if (setEditStartCoordStatus) {
                if (column > COL || row > ROW || checkUnexploredCell(column, row)) {

                    int[] startCoord = this.getStartCoord();
                    if (startCoord[0] >= 1 && startCoord[1] >= 1) {
                        startCoord[1] = this.convertRow(startCoord[1]);
                        for (int x = startCoord[0] - 1; x <= startCoord[0] + 1; x++)
                            for (int y = startCoord[1] - 1; y <= startCoord[1] + 1; y++)
                                if (x <= COL && (20-y) <= ROW && x >= 1 && (20-y) >= 1)
                                    cells[x][y].setType("unexplored");
                    }
                    if (column <= 20 && row <= 20) {
                        this.setStartCoord(column, row);
                        this.setRobotDirection(prevRobotDirection);
                        String direction = getRobotDirection();
                        if (direction.equals("None")) {
                            direction = "N";
                        }
                        try {
                            int directionInt = 0;
                            if (direction.equals("N")) {
                                directionInt = 0;
                            } else if (direction.equals("W")) {
                                directionInt = 3;
                            } else if (direction.equals("E")) {
                                directionInt = 1;
                            } else if (direction.equals("S")) {
                                directionInt = 2;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateRobotAxis(column, row, direction);
                    }
                    else {
                        canDrawRobot = false;
                        startCoord = new int[]{-1, -1};
                        curCoord = new int[]{-1, -1};
                        robotDirection = "None";
                    }
                    setEditStartCoordStatus = false;
                    this.invalidate();
                    return true;
                }
            }

        }
        showLog("Exiting onTouchEvent");
        return false;
    }

    private boolean checkUnexploredCell(int col, int row) {
        return cells[col][20 - row].type.equals("unexplored");
    }

    public void toggleCheckedBtn(String buttonName) {
        ToggleButton setEditToggleBtn = ((Activity)this.getContext()).findViewById(R.id.setEditToggleBtn);
        ToggleButton setStartPointToggleBtn = ((Activity)this.getContext()).findViewById(R.id.setStartPointToggleBtn);
        ToggleButton northObstacleToggleBtn = ((Activity)this.getContext()).findViewById(R.id.northObstacleToggleBtn);
        ToggleButton southObstacleToggleBtn = ((Activity)this.getContext()).findViewById(R.id.southObstacleToggleBtn);
        ToggleButton eastObstacleToggleBtn = ((Activity)this.getContext()).findViewById(R.id.eastObstacleToggleBtn);
        ToggleButton westObstacleToggleBtn = ((Activity)this.getContext()).findViewById(R.id.westObstacleToggleBtn);
        ToggleButton clearToggleBtn = ((Activity)this.getContext()).findViewById(R.id.clearToggleBtn);

        if (!buttonName.equals("setEditToggleBtn"))
            if (setEditToggleBtn.isChecked()) {
                this.setEditMapStatus(false);
                setEditToggleBtn.toggle();
            }

        if (!buttonName.equals("setStartPointToggleBtn"))
            if (setStartPointToggleBtn.isChecked()) {
                this.setStartCoordStatus(false);
                setStartPointToggleBtn.toggle();
            }

        if (!buttonName.equals("northObstacleToggleBtn"))
            if (northObstacleToggleBtn.isChecked()) {
                this.setSetNorthObstacleStatus(false);
                northObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("southObstacleToggleBtn"))
            if (southObstacleToggleBtn.isChecked()) {
                this.setSetSouthObstacleStatus(false);
                southObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("eastObstacleToggleBtn"))
            if (eastObstacleToggleBtn.isChecked()) {
                this.setSetEastObstacleStatus(false);
                eastObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("westObstacleToggleBtn"))
            if (westObstacleToggleBtn.isChecked()) {
                this.setSetWestObstacleStatus(false);
                westObstacleToggleBtn.toggle();
            }

        if (!buttonName.equals("clearToggleBtn"))
            if (clearToggleBtn.isChecked()) {
                this.setUnSetCellStatus(false);
                clearToggleBtn.toggle();
            }
    }


    public void resetMap() {
        showLog("Entering resetMap");
        obscounter = 0;
        idcounter =0;
        obstacleDirectionCoord = new ArrayList<>();
        updateRobotAxis(1, 1, "None");

        this.toggleCheckedBtn("None");

        startCoord = new int[]{-1, -1};
        curCoord = new int[]{-1, -1};
        oldCoord = new int[]{-1, -1};
        robotDirection = "None";
        obstacleDirectionCoord = new ArrayList<>();
        obstacleCoord = new ArrayList<>();
        mapDrawn = false;
        canDrawRobot = false;
        Bitmap arrowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);
        obstacleDirectionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_clear_24);

        prevObstacleDirectionCoord = new ArrayList<>();
        setEditDirectionObstacleStatus = false;
        setEditStartCoordStatus = false;

        showLog("Exiting resetMap");
        this.invalidate();
    }

    public void saveMap(){
        showLog("Entering saveMap");
        saveObstacleDirectionCoord = obstacleDirectionCoord;
    }

    public void loadMap(){
        int col;
        int row;

        showLog("Entering loadMap");
        //obstacleDirectionCoord = saveObstacleDirectionCoord;

        for (int i = 0; i < saveObstacleDirectionCoord.size(); i++) {
            obscounter ++;
            col = Integer.parseInt(saveObstacleDirectionCoord.get(i)[0]);
            row = Integer.parseInt(saveObstacleDirectionCoord.get(i)[1]);
            if (checkUnexploredCell(col, row))
            {
                this.setObstacleDirectionCoordinate(col, row, saveObstacleDirectionCoord.get(i)[2]);
                drawCellid(col, row, Integer.toString(obscounter));

            }
            this.invalidate();
            }

        }



    public void updateMap() {
        showLog("Entering updateMap");

        final String target = "ALGO";
        String message;
        int[] startCoord = this.getStartCoord();

        if (startCoord[0] != -1) {
            // Start Coord
            message = target + "," + "Startpoint" + "," + (startCoord[0] - 1) + "," + (startCoord[1] - 1) + "," + getRobotDirection().toLowerCase();
            BluetoothUtils.write(message.getBytes());
            delay();
        }

        // All Directional Obstacle
        for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
            int obstacleDirectionCoordCol = 0;
            int obstacleDirectionCoordRow = 0;
            try {
                obstacleDirectionCoordCol = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
                obstacleDirectionCoordRow = Integer.parseInt(obstacleDirectionCoord.get(i)[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            message = target + "," + "Obstacle" + "," + (obstacleDirectionCoordCol - 1)  + "," + (obstacleDirectionCoordRow - 1) + "," + obstacleDirectionCoord.get(i)[2].toLowerCase();
            BluetoothUtils.write(message.getBytes());
            delay();
        }
        message = "ALGO,Obstacle,finish";
        BluetoothUtils.write(message.getBytes());
        showLog("Exiting updateMap");
    }

    public void handleMessageReceive(String[] input) {
        showLog("Entering handleMessage");
        switch (input[1]) {
            case "Robot":
                if (curCoord[0] != -1) {
                    int col = Integer.parseInt(input[2]) + 1;
                    int row = Integer.parseInt(input[3]) + 1;
                    if (col <= COL && row <= ROW && col > 0 && row > 0) {
                        setOldRobotCoord(curCoord[0], curCoord[1]);
                        setCurCoord(col, row, input[4].toUpperCase());
                    }
                }
                break;
            case "ObstacleOrder":

                order = input[2].split(""); // 54321
                MainActivity.statusTextView.setText("Obs Order Received");


                break;
            case "ObstacleImg":
                try {
                int index = Integer.parseInt(order[idcounter]); // index of obstacle array to retrieve. 1 onwards
                int col = Integer.parseInt(obstacleDirectionCoord.get(index-1)[0]);  // retrieve obstacle index's col
                int row = Integer.parseInt(obstacleDirectionCoord.get(index-1)[1]);
                if (input[2].equals("99"))
                    drawImageNumberCell(col, row, "");
                else
                    drawImageNumberCell(col, row, input[2]);
                }
                catch (Exception e){
            }

                idcounter ++;

//                int col = Integer.parseInt(input[2]) + 1;
//                int row = Integer.parseInt(input[3]) + 1;
//                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
//                    int obstacleDirectionCoordCol = 0;
//                    int obstacleDirectionCoordRow = 0;
//                    showLog(col + ", " + row + " == " + obstacleDirectionCoord.get(i)[0] + ", " + obstacleDirectionCoord.get(i)[1]);
//                    try {
//                        obstacleDirectionCoordCol = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
//                        obstacleDirectionCoordRow = Integer.parseInt(obstacleDirectionCoord.get(i)[1]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (obstacleDirectionCoordCol ==  col && obstacleDirectionCoordRow == row) {
//                        drawImageNumberCell(col, row, input[4]);
//                    }
//                }
//                int col = curCoord[1];
//                int row = curCoord[0];
//                for (int i = 0; i < obstacleDirectionCoord.size(); i++) {
//                    int obstacleDirectionCoordCol = 0;
//                    int obstacleDirectionCoordRow = 0;
//                    showLog(col + ", " + row + " == " + obstacleDirectionCoord.get(i)[0] + ", " + obstacleDirectionCoord.get(i)[1]);
//                    try {
//                        obstacleDirectionCoordCol = Integer.parseInt(obstacleDirectionCoord.get(i)[0]);
//                        obstacleDirectionCoordRow = Integer.parseInt(obstacleDirectionCoord.get(i)[1]);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (obstacleDirectionCoordCol == col && obstacleDirectionCoordRow == row) {
//                if (robotDirection.equals("N")) {
//                    Log.d("x,y coord", "y:" + col + 3);
//                    drawImageNumberCell(row, col+3, input[2]);
//                } else if (robotDirection.equals("S")) {
//                    drawImageNumberCell(row, col-3, input[2]);
//                } else if (robotDirection.equals("E")) {
//                    drawImageNumberCell(row+3, col, input[2]);
//                } else if (robotDirection.equals("W")) {
//                    drawImageNumberCell(row-3, col, input[2]);
//                }
      //  }
    //}


                break;
            case "w":
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "W");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "S");
                }

                break;
            case "x":       //robot wants to move sharp right "east"
                if(robotDirection.equals("N"))      //if direction now is north
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

//                    setOldRobotCoord(curCoord[0], curCoord[1]);
//                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    //setCurCoord(curCoord[0]+3, curCoord[1]+2, "E");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "S");

                    //setCurCoord(curCoord[0]+2, curCoord[1]-3, "S");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    //setCurCoord(curCoord[0]-2, curCoord[1]+3, "N");
                }
                else if (robotDirection.equals("S")) {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "W");
                    //setCurCoord(curCoord[0]-3, curCoord[1]-2, "W");
                }
                break;
            case "s":
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "W");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "S");
                }
                break;
            case "z": //sharp left
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "W");

                    //setCurCoord(curCoord[0]-3, curCoord[1]+2, "W");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    //setCurCoord(curCoord[0]+2, curCoord[1]+3, "N");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "S");
                 //   setCurCoord(curCoord[0]-2, curCoord[1]-1, "S");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");
                 //   setCurCoord(curCoord[0]+3, curCoord[1]-2, "E");
                }
                break;
            case "a": //wide left
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "W");
                  //  setCurCoord(curCoord[0]-3, curCoord[1]-5, "E");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    //setCurCoord(curCoord[0]-5, curCoord[1]-3, "S");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "S");
                    //setCurCoord(curCoord[0]+5, curCoord[1]-3, "N");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");
                    //setCurCoord(curCoord[0]+3, curCoord[1]+5, "W");
                }
                break;
            case "d": //wide right
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");
                    //setCurCoord(curCoord[0]+3, curCoord[1]-5, "W");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "S");
                    // setCurCoord(curCoord[0]-3, curCoord[1]-5, "N");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    //setCurCoord(curCoord[0]+3, curCoord[1]+5, "S");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "W");
                    //setCurCoord(curCoord[0]-3, curCoord[1]+5, "E");
                }
                break;
            case "e": //reverse right
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "W");

                    //setCurCoord(curCoord[0]-3, curCoord[1]+2, "W");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    //setCurCoord(curCoord[0]+2, curCoord[1]+3, "N");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "S");
                    //   setCurCoord(curCoord[0]-2, curCoord[1]-1, "S");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");
                    //   setCurCoord(curCoord[0]+3, curCoord[1]-2, "E");
                }
                break;
            case "q": //reverse left
                if(robotDirection.equals("N"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "E");

                    //setCurCoord(curCoord[0]-3, curCoord[1]+2, "W");
                }
                else if (robotDirection.equals("E"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]-1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "S");
                    //setCurCoord(curCoord[0]+2, curCoord[1]+3, "N");
                }
                else if (robotDirection.equals("W"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]-1, "N");
                    //   setCurCoord(curCoord[0]-2, curCoord[1]-1, "S");
                }
                else if (robotDirection.equals("S"))
                {
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");
                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0], curCoord[1]+1, "N");


                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "N");

                    setOldRobotCoord(curCoord[0], curCoord[1]);
                    setCurCoord(curCoord[0]+1, curCoord[1], "W");
                    //   setCurCoord(curCoord[0]+3, curCoord[1]-2, "E");
                }
                break;
            default:
                break;
        }
        showLog("Exiting handleMessage");
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
