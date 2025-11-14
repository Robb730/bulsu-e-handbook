package com.example.bulsustudenthandbook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;



public class Schedule extends AppCompatActivity {
    private String[] colorOptions = {
            "Red", "Blue", "Green", "Yellow", "Orange",
            "Purple", "Cyan", "Magenta", "Brown", "Gray"
    };

    private HashMap<String, Integer> colorMap = new HashMap<>();



        // Initialize color map



    ScheduleDBHelper dbHelper;
    EditText editSubjectCode, editSubjectName, editTime;
    Button btnSaveSchedule;
    EditText editDay; // new field


    String selectedDay = "Mon"; // default day (you will change later)

    HashMap<String, Integer> dayMap = new HashMap<>();
    HashMap<String, Integer> timeMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule); // FIRST

        editDay = findViewById(R.id.editDay);

        editDay.setOnClickListener(v -> showDayPicker());


        // initialize DB
        dbHelper = new ScheduleDBHelper(this);

        // Fix inset crash
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔹 Initialize UI components
        editSubjectCode = findViewById(R.id.editSubjectCode);
        editSubjectName = findViewById(R.id.editSubjectName);
        editTime = findViewById(R.id.editTime);  // AFTER setContentView
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        editTime.setOnClickListener(v -> showTimePicker());


        // 🔹 Initialize UI components
        editSubjectCode = findViewById(R.id.editSubjectCode);
        editSubjectName = findViewById(R.id.editSubjectName);
        editTime = findViewById(R.id.editTime);
        btnSaveSchedule = findViewById(R.id.btnSaveSchedule);

        // 🔹 Map days (column index)
        dayMap.put("Mon", 1);
        dayMap.put("Tue", 2);
        dayMap.put("Wed", 3);
        dayMap.put("Thu", 4);
        dayMap.put("Fri", 5);
        dayMap.put("Sat", 6);
        dayMap.put("Sun", 7);

        // 🔹 Map times (row index)
        timeMap.put("6:00 AM", 0);
        timeMap.put("7:00 AM", 1);
        timeMap.put("8:00 AM", 2);
        timeMap.put("9:00 AM", 3);
        timeMap.put("10:00 AM", 4);
        timeMap.put("11:00 AM", 5);
        timeMap.put("12:00 PM", 6);
        timeMap.put("1:00 PM", 7);
        timeMap.put("2:00 PM", 8);
        timeMap.put("3:00 PM", 9);
        timeMap.put("4:00 PM", 10);
        timeMap.put("5:00 PM", 11);
        timeMap.put("6:00 PM", 12);
        timeMap.put("7:00 PM", 13);


        // 🔹 Button: Save class
        btnSaveSchedule.setOnClickListener(v -> {
            String code = editSubjectCode.getText().toString();
            String name = editSubjectName.getText().toString();
            String time = editTime.getText().toString();

            if (code.isEmpty() || name.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("subject_code", code);
            cv.put("subject_name", name);
            cv.put("day", selectedDay);
            cv.put("time", time);

            db.insert("classes", null, cv);

            Toast.makeText(this, "Class Added!", Toast.LENGTH_SHORT).show();

            loadScheduleIntoGrid();
        });

        // Load existing classes on start
        loadScheduleIntoGrid();
    }

    // 🔹 Fill the grid with saved database items
    public void loadScheduleIntoGrid() {
        GridLayout gridCalendar = findViewById(R.id.gridCalendar);

        // Clear previous schedule
        for (int i = 0; i < gridCalendar.getChildCount(); i++) {
            if (i % 8 != 0) {  // don't clear "Time" column
                TextView cell = (TextView) gridCalendar.getChildAt(i);
                cell.setText("");
                cell.setBackgroundColor(Color.WHITE);
            }
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM classes", null);

        while (cursor.moveToNext()) {
            String code = cursor.getString(1);
            String name = cursor.getString(2);
            String day = cursor.getString(3);
            String timeRange = cursor.getString(4); // e.g., "7:00 AM - 10:00 AM"

            String[] parts = timeRange.split(" - ");
            String startTime = parts[0];
            String endTime = parts[1];

            int col = dayMap.get(day);
            int startRow = timeMap.get(startTime);
            int endRow = timeMap.get(endTime); // use end index as exclusive

            for (int row = startRow; row < endRow; row++) {
                int index = (row * 8) + col;
                TextView cell = (TextView) gridCalendar.getChildAt(index);
                cell.setText(code + "\n" + name);
                cell.setBackgroundColor(Color.parseColor("#C8E6C9"));
            }
        }


        cursor.close();
    }
    private void showTimePicker() {
        String[] times = {
                "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM",
                "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM",
                "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM",
                "6:00 PM", "7:00 PM", "8:00 PM"
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Start Time");
        builder.setItems(times, (dialog, startIndex) -> {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("Select End Time");
            builder2.setItems(times, (dialog2, endIndex) -> {
                if (endIndex <= startIndex) {
                    Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }
                editTime.setText(times[startIndex] + " - " + times[endIndex]);
            });
            builder2.show();
        });
        builder.show();
    }

    private void showDayPicker() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Day");
        builder.setItems(days, (dialog, which) -> {
            selectedDay = days[which]; // store selected day
            editDay.setText(selectedDay); // show in EditText
        });
        builder.show();
    }



}


