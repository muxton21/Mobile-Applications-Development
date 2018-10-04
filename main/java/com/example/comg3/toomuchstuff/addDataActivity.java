package com.example.comg3.toomuchstuff;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class addDataActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static DatabaseSQLite myDb;


    public static final int UPLOAD_IMAGE_REQUEST = 30;
    public static final int TAKE_PHOTO_REQUEST = 10;
    private ImageView imgPlaceholderImage;
    private EditText itemNameEditText, brandEditText;
    private Button addNewItemButton;
    private Spinner sourceSpinner, clothingTypeSpinner;
    private ImageView clothingPhotoImageView;
    private Bitmap clothingPhotoBitmap;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //inserts SQLite db
        myDb = new DatabaseSQLite(this);


        //calls the edit text fields to be later inputed into the my sql database
        itemNameEditText = (EditText) findViewById(R.id.itemNameEditText);
        brandEditText = (EditText) findViewById(R.id.brandEditText);


        imgPlaceholderImage = (ImageView) findViewById(R.id.clothingPhotoImageView);



        //code referring to the obtainment of an image from the camera and plugged into the image view


        //spinner codes for the add new source of clothing and type of clothing

        //instantiate array adaptors to be used in both spinners of the add New Item Page, the contents of the spinners is pulled from arrays in the strings.xml file
        ArrayAdapter<CharSequence> adapterSources = ArrayAdapter.createFromResource(this, R.array.sources, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterClothingTypes = ArrayAdapter.createFromResource(this, R.array.clothingTypes, android.R.layout.simple_spinner_item);

        //find the ID of the spinners
        sourceSpinner = findViewById(R.id.sourceSpinner);
        clothingTypeSpinner = findViewById(R.id.clothingTypeSpinner);

        //set the drop down view items
        adapterSources.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterClothingTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //add the arrays into the spinners as a drop down views
        sourceSpinner.setAdapter(adapterSources);
        sourceSpinner.setOnItemSelectedListener(this);
        clothingTypeSpinner.setAdapter(adapterClothingTypes);
        clothingTypeSpinner.setOnItemSelectedListener(this);




        addNewItemButton = (Button) findViewById(R.id.addNewItemButton);
        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adds the data from the screen to the sqlite database
                addData();


                //sends user back to the main page after data is submitted
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });


    }


    //nothing is needed to happen when the user clicks items in the spinners so these two classes are left blank
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //refers to the code created to access the camera and the request code the camera gives out
    public void takePhotoButtonClicked(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST);
    }
    //refers to the code created to access the gallery and the request code the gallery gives out
    public void onImageGalleryClicked(View view) {
        //invoke the local storage using implicit intent
        Intent uploadImageButton = new Intent(Intent.ACTION_PICK);
        //location of data
        File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String imageDirectoryPath = imageDirectory.getPath();
        Uri data = Uri.parse(imageDirectoryPath);

        //set data type to get all image types

        uploadImageButton.setDataAndType(data, "image/*");

        startActivityForResult(uploadImageButton, UPLOAD_IMAGE_REQUEST);
    }



    //function which adds data to the SQLite db
    public void addData() {
        clothingPhotoBitmap = ((BitmapDrawable) imgPlaceholderImage.getDrawable()).getBitmap();
        boolean isInserted;
        isInserted = myDb.insertData(
                itemNameEditText.getText().toString(),
                brandEditText.getText().toString(),
                sourceSpinner.getItemAtPosition(sourceSpinner.getSelectedItemPosition()).toString(),
                clothingTypeSpinner.getItemAtPosition(clothingTypeSpinner.getSelectedItemPosition()).toString(),
                DbBitmapUtility.getBytes(clothingPhotoBitmap));
        if(isInserted){
            Toast.makeText(addDataActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();

        } else{
            Toast.makeText(addDataActivity.this, "Error: Data Not Entered", Toast.LENGTH_LONG).show();}
    }


    //code referring to converting an image from bitmap to byte array  or from byte array to bitmap

    public static class DbBitmapUtility{
        //covert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        //convert byte array to bitmap

        public static Bitmap getImage(byte[] image){
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
    }



    //code referring to the accessing of the devices camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //if user chose to access the camera, proceed to code below
            if (requestCode == TAKE_PHOTO_REQUEST) {
                //camera responds
                Bitmap cameraImage = (Bitmap) data.getExtras().get("data"); //bitmap
                // image from camera obtained, then posted into imageView
                imgPlaceholderImage.setImageBitmap(cameraImage);
            }
            //heard from the image gallery
            if (requestCode == UPLOAD_IMAGE_REQUEST) {
                //camera responds address of image on sd card
                Uri imageUri = data.getData();

                //stream read the image from the SD card
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    //gets bitmap from stream
                    Bitmap bitmapImageFromSDCard = BitmapFactory.decodeStream(inputStream);

                    imgPlaceholderImage.setImageBitmap(bitmapImageFromSDCard);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to obtain image", Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}