package com.example.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.example.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    Spinner spinnerCourse;
    EditText textNote;
    EditText textTitle;
    private int notePosition;
    private boolean isCancelling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourse = findViewById(R.id.spinner);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapterCourses);

        readDisplayedStateValues();

        textNote = findViewById(R.id.text_note_text);
        textTitle = findViewById(R.id.note_text_title);


        if (!mIsNewNote)
            displayNotes(spinnerCourse, textTitle, textNote);


    }
    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelling) {
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(notePosition);
            }
        } else {
            saveNote();
        }
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) spinnerCourse.getSelectedItem());
        mNote.setTitle(textTitle.getText().toString());
        mNote.setText(textNote.getText().toString());
    }

    private void displayNotes(Spinner spinnerCourse, EditText textTitle, EditText textNote) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourse.setSelection(courseIndex);

        textTitle.setText(mNote.getTitle());
        textNote.setText(mNote.getText());
    }

    private void readDisplayedStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        } else {
            mNote = DataManager.getInstance().getNotes().get(position);
            //mNote = intent.getParcelableExtra(NOTE_POSTION);
            // mIsNewNote = mNote == null;

        }
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
        mNote = dm.getNotes().get(notePosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            isCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourse.getSelectedItem();
        String subject = textTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" +
                course.getTitle() + "\"\n" +
                textNote.getText();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);

    }
}
