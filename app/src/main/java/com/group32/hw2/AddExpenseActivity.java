// Homework 02
// AddExpenseActivity.java
// Akarsh Gupta     - 800969888
// Ahmet Gencoglu   - 800982227
//

package com.group32.hw2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;




public class AddExpenseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // UI Component variable decelerations
    private EditText editTextName;
    private EditText editTextAmount;
    private Spinner spinnerCategory;
    private EditText editTextDate;
    private ImageButton imageButtonDatepicker;
    private ImageView imageView;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Setup UI Components
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        imageButtonDatepicker = (ImageButton) findViewById(R.id.imageButtonChooseDate);
        imageView = (ImageView) findViewById(R.id.imageView);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,Expense.categories);
        spinnerCategory.setAdapter(spinnerAdapter);

        Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        // When user clicks on the Date editText the DatePickerDialog will be used
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        imageButtonDatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        // Update the edit Text Field with the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,monthOfYear,dayOfMonth);
        Date chosenDate = calendar.getTime();

        // Set the date using the class defined format
        editTextDate.setText(Expense.dateFormat.format(chosenDate));
        // Clear any error if present
        editTextDate.setError(null);
    }

    public void addExpense(View view) throws ParseException {
        if (checkInputs()) {

            Date date = new Date(0);
            Double amount;
            String name;
            int category;
            // Get user input from UI elements
            name = editTextName.getText().toString();
            amount = Double.parseDouble(editTextAmount.getText().toString());
            // Try to get the date from the text field
            try{
                date = Expense.dateFormat.parse(editTextDate.getText().toString());
            }
            catch (ParseException exception) {
                Log.e("demo","Date Could not be Parsed");
            }

            category = spinnerCategory.getSelectedItemPosition();
            // Create new Expense
            Expense newExpense;
            // Initialize with data
            if (selectedImage != null) {
                newExpense = new Expense(date, amount, name, category, selectedImage.toString());
            } else {
                newExpense = new Expense(date, amount, name, category, "");
            }
            // Return the newly created Expense object to the requesting activity
            Intent intent = new Intent();
            intent.putExtra(MainActivity.EXPENSE_KEY, newExpense);
            setResult(1, intent);
            finish();
        }
    }

    public void getImage(View view){
        // Use an SDK dependent code to handle Image access from gallery
        if(Build.VERSION.SDK_INT > 19) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 100);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if user chose an image
        if (resultCode == RESULT_OK) {
            // If yes store the imageURI
            selectedImage = data.getData();
            imageView.setImageURI(selectedImage);
        }
    }

    boolean checkInputs(){
        boolean isInputGood = true;

        // Input validitation
        if (editTextName.getText().length() <= 0){
            editTextName.setError("Please Enter a Name for this Expense");
            isInputGood = false;
        }

        if (editTextAmount.getText().length() <= 0){
            editTextAmount.setError("Please enter an Amount");
            isInputGood = false;
        }

        if (editTextDate.getText().length() <= 0){
            editTextDate.setError("Please select a date");
            isInputGood = false;
        }

        if (spinnerCategory.getSelectedItemPosition() == 0){
            Toast.makeText(this,"Please select a category",Toast.LENGTH_SHORT).show();
            isInputGood = false;
        }

        return  isInputGood;
    }
}
