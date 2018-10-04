package com.example.comg3.toomuchstuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by comg3 on 17/05/2018.
 */

public class EditDataActivity extends AppCompatActivity {
    private static final String TAG = "EditDataActivity";

    static DatabaseSQLite myDb;

    private EditText itemNameEditText, brandEditText;
    private Button updateButton, deleteButton;
    private Spinner sourceSpinner, clothingTypeSpinner;

    private String selectedName, selectedBrand, selectedSource, selectedType;
    private String selectedId;


    //this activity is loaded when the user wants to edit a clothing item's details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_clothing_item);

        //load in the widgets
        itemNameEditText = (EditText) findViewById(R.id.editItemNameEditText);
        brandEditText = (EditText) findViewById(R.id.editBrandEditText);
        updateButton = (Button) findViewById(R.id.updateItemButton);
        deleteButton = (Button) findViewById(R.id.deleteItemButton);
        sourceSpinner = (Spinner) findViewById(R.id.editSourceSpinner);
        clothingTypeSpinner = (Spinner) findViewById(R.id.editClothingTypeSpinner);


        //cal the database
        myDb = new DatabaseSQLite(this);

        Intent receivedIntent = getIntent();

        //get the extra information sent by the main activity
        selectedId =  receivedIntent.getStringExtra("Id");
        selectedName = receivedIntent.getStringExtra("name");
        selectedBrand = receivedIntent.getStringExtra("brand");
        selectedSource = receivedIntent.getStringExtra("source");
        selectedType = receivedIntent.getStringExtra("type");

        //set the Text View widgets their respective intents
        itemNameEditText.setText(selectedName);
        brandEditText.setText(selectedBrand);


        //create an array adapter for the two spinners
        ArrayAdapter<CharSequence> adapterSources = ArrayAdapter.createFromResource(this, R.array.sources, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterClothingTypes = ArrayAdapter.createFromResource(this, R.array.clothingTypes, android.R.layout.simple_spinner_item);

        //set each spinner item the values in the array and pre set its value to the value in selectedSource value
        adapterSources.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sourceSpinner.setAdapter(adapterSources);
        if(selectedSource != null){
            int spinnerPosition = adapterSources.getPosition(selectedSource);
            sourceSpinner.setSelection(spinnerPosition);
        }


        //set each spinner item the values in the array and pre set its value to the value in selectedType value

        adapterClothingTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        clothingTypeSpinner.setAdapter(adapterClothingTypes);
        if (selectedType!=null){
            int spinnerPosition = adapterClothingTypes.getPosition(selectedType);
            clothingTypeSpinner.setSelection(spinnerPosition);
        }



        //on the click of the update information button add each value to the given row in the db, all fields must be populated
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = selectedId;
                String itemName = itemNameEditText.getText().toString();
                String itemBrand = brandEditText.getText().toString();
                String itemSource = sourceSpinner.getSelectedItem().toString();
                String itemType = clothingTypeSpinner.getSelectedItem().toString();
                if(!itemId.equals(0) && !itemName.equals("") && !itemBrand.equals("") && !itemSource.equals("") && !itemType.equals("")){
                    myDb.updateItem(itemId,itemName, itemBrand, itemSource, itemType);
                    makeToast("Data updated");
                }
                else{
                    Toast.makeText(v.getContext(), "Please populate fields", Toast.LENGTH_SHORT).show();
                }

                //go back to the main activity after button press
                Intent editClothingItemIntent = new Intent(EditDataActivity.this, MainActivity.class);
                startActivity(editClothingItemIntent);
            }
        });

        //on click of delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteItem(selectedId); //calls delete item function in the db helper
                makeToast("Item Deleted");

                //go back to main activity
                Intent editClothingItemIntent = new Intent(EditDataActivity.this, MainActivity.class);
                startActivity(editClothingItemIntent);
            }
        });

    }
    public void makeToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void printLog(String text){
        Log.d(TAG, text);
    }
}
