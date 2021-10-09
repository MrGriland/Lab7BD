package bgv.fit.bstu.lab7bd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<Note> notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File myFile = new File(getFilesDir().toString() + "/" + "7Lab.json");
        if(!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notes = Read();
        DatePicker datePicker = findViewById(R.id.datedp);
        Calendar today = Calendar.getInstance();
        datePicker.setMaxDate(new Date().getTime());
        EditText info = findViewById(R.id.infoet);
        Button change = findViewById(R.id.changeb);
        Button remove = findViewById(R.id.deleteb);
        Button save = findViewById(R.id.saveb);
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                //Toast.makeText(getApplicationContext(), String.valueOf(datePicker.getDayOfMonth())+"."+String.valueOf(datePicker.getMonth())+"."+String.valueOf(datePicker.getYear()), Toast.LENGTH_SHORT).show();
                if(!notes.isEmpty()) {
                    for (Note n :
                            notes) {
                        if (n.date.equals(String.valueOf(datePicker.getDayOfMonth()) + "." + String.valueOf(datePicker.getMonth()) + "." + String.valueOf(datePicker.getYear()))) {
                            info.setText(n.text);
                            change.setVisibility(View.VISIBLE);
                            remove.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                            break;
                        } else {
                            info.setText("");
                            change.setVisibility(View.GONE);
                            remove.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else
                {
                    info.setText("");
                    change.setVisibility(View.GONE);
                    remove.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public List<Note> Read()
    {
        List<Note> notes = new ArrayList<Note>();
        File myFile = new File(getFilesDir().toString() + "/" + "7Lab.json");
        try {
            FileInputStream inputStream = new FileInputStream(myFile);
            /*
             * Буфферезируем данные из выходного потока файла
             */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            /*
             * Класс для создания строк из последовательностей символов
             */
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            try {
                /*
                 * Производим построчное считывание данных из файла в конструктор строки,
                 * Псоле того, как данные закончились, производим вывод текста в TextView
                 */
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                Gson gson = new Gson();
                DataItems dataItems = gson.fromJson(stringBuilder.toString(), DataItems.class);
                try {
                    if (dataItems.getNotes() != null) {
                        for (Note note :
                                dataItems.getNotes()) {
                            notes.add(note);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public void Delete(View view)
    {
        DatePicker datePicker = findViewById(R.id.datedp);
        Button change = findViewById(R.id.changeb);
        Button remove = findViewById(R.id.deleteb);
        Button save = findViewById(R.id.saveb);
        EditText info = findViewById(R.id.infoet);
        if(!notes.isEmpty()) {
            for (Note n :
                    notes) {
                if (n.date.equals(String.valueOf(datePicker.getDayOfMonth()) + "." + String.valueOf(datePicker.getMonth()) + "." + String.valueOf(datePicker.getYear()))) {
                    info.setText("");
                    change.setVisibility(View.GONE);
                    remove.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                    notes.remove(n);
                    break;
                }
            }
        }
        Rewrite();
    }

    public void Change(View view)
    {
        DatePicker datePicker = findViewById(R.id.datedp);
        EditText editText = findViewById(R.id.infoet);
        if(!notes.isEmpty()) {
            for (Note n :
                    notes) {
                if (n.date.equals(String.valueOf(datePicker.getDayOfMonth()) + "." + String.valueOf(datePicker.getMonth()) + "." + String.valueOf(datePicker.getYear()))) {
                    n.text = editText.getText().toString();
                    break;
                }
            }
        }
        Rewrite();
    }

    public void Save(View view)
    {
        DatePicker datePicker = findViewById(R.id.datedp);
        Button change = findViewById(R.id.changeb);
        Button remove = findViewById(R.id.deleteb);
        Button save = findViewById(R.id.saveb);
        EditText info = findViewById(R.id.infoet);
        Note note = new Note();
        note.date = String.valueOf(datePicker.getDayOfMonth()) + "." + String.valueOf(datePicker.getMonth()) + "." + String.valueOf(datePicker.getYear());
        note.text = info.getText().toString();
        notes.add(note);
        change.setVisibility(View.VISIBLE);
        remove.setVisibility(View.VISIBLE);
        save.setVisibility(View.GONE);
        Rewrite();
    }

    public void Rewrite()
    {
        File myFile = new File(getFilesDir().toString() + "/" + "7Lab.json");
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setNotes(notes);
        String jsonString = gson.toJson(dataItems);

        try {
            FileOutputStream outputStream = new FileOutputStream(myFile);
            /*
             * Буфферезируем данные из выходного потока файла
             */
            outputStream.write(jsonString.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}