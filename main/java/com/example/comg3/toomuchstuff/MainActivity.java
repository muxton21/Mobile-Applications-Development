package com.example.comg3.toomuchstuff;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.comg3.toomuchstuff.DatabaseSQLite.COL_1;
import static com.example.comg3.toomuchstuff.DatabaseSQLite.COL_2;
import static com.example.comg3.toomuchstuff.DatabaseSQLite.COL_3;
import static com.example.comg3.toomuchstuff.DatabaseSQLite.COL_4;
import static com.example.comg3.toomuchstuff.DatabaseSQLite.COL_5;
import static com.example.comg3.toomuchstuff.DatabaseSQLite.TABLE_NAME;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static DatabaseSQLite myDb;




    // declare the items on the activity
    public ArrayList<byte[]> image;
    public ArrayList<String> caption;

    TextView recordCountTextView;
    TextView titleTextView;
    GridView itemsGridView;
    ArrayList<Clothing> mList;
    ClothingListAdapter clothingListAdapter = null;
    Spinner filterSpinner;
    ImageView clothingPlaceholderImageView;
    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on create call the SQLite database
        myDb = new DatabaseSQLite(this);

        //call the count records function , gets the number of records in the SQLite database
        countRecords();

        //populate gridview with the items in the SQLite database
        populateGridView(" SELECT * FROM "+ TABLE_NAME+" ORDER BY id DESC ");

        //calls the function which goes to the add a new item activity on click
        goToAddNewItemScreen();
        //spinner codes for the add new source of clothing and type of clothing

        //instantiate array adaptors to be used in both spinners of the add New Item Page, the contents of the spinners is pulled from arrays in the strings.xml file
        ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(this, R.array.filterList, android.R.layout.simple_spinner_item);

        //find the ID of the spinners
        filterSpinner = findViewById(R.id.filterClothingSpinner);

        //set the drop down view items
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //add the arrays into the spinners as a drop down views
        filterSpinner.setAdapter(adapterFilter);


        //calls the function which on the tap of a spinner item changes the items in the grid view
        updateGridView();
        titleTextView = (TextView) findViewById(R.id.titleTextView);

        clothingPlaceholderImageView = (ImageView) findViewById(R.id.clothingPlaceholderImageView);

        //calls the function responsible for a grid view tap
        onGridViewSelect();

        //function responsible for the click of the find charity shop button
        findCharityShop();

        //calls function which calls a daily notification
        createNotification();



    }





    //functions called in onCreate() method





    //gets the number of records in the sqlite DB
    public void countRecords() {

        //counts the number of records in the SQLite database
        int recordCount = new DatabaseSQLite(this).count();


        //changes the tet in the text view to the number of records in the db
        recordCountTextView = (TextView) findViewById(R.id.recordCountTextView);
        recordCountTextView.setText("You Have: "+recordCount + " Items ");
    }


    //function that enables on the click of the add icon to go to the add new items screen
    public void goToAddNewItemScreen() {
        Button toAddImage = (Button) findViewById(R.id.addNewItemButton);
        toAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), addDataActivity.class);
                startActivity(startIntent);
            }
        });
    }
    //function that enables on the click of the find charity shop button to open google maps and search the current location for charity shop
    public void findCharityShop() {
        Button findCharityShop = (Button) findViewById(R.id.findCharityShopButton);
        findCharityShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?z=10&q=charity shop");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }


    //function which populates the grid view with the image and title of each item in the SQLite database
    public void populateGridView(String sql) {
        itemsGridView = findViewById(R.id.itemsGridView);

        //creates array list and adpter for the grid view
        mList = new ArrayList<>();
        clothingListAdapter = new ClothingListAdapter(this, R.layout.grid_view_layout, mList );
        itemsGridView.setAdapter(clothingListAdapter);

        //cursor scans through the results of the sql query
        Cursor cursor = myDb.getData(sql);
        mList.clear();
        while (cursor.moveToNext()){

            //adds each result to the array list
            String title = cursor.getString(1);
            byte[] img = cursor.getBlob(5);

            mList.add(new Clothing(title,img));
        }

    }

    // function which populates the grid view based on the spinner item selected by the user
    public void updateGridView(){
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //get the text of the spinner item selected
                String text = filterSpinner.getSelectedItem().toString();

                //if text of selected item is 'All" show all the records else get update the grid view with data only where the clothing type is equal to the type selected in the spinner
                if(text.equals("All")){
                    populateGridView(" SELECT * FROM "+TABLE_NAME+" ORDER BY id DESC ");
                }
                else{
                    populateGridView(" SELECT * FROM "+TABLE_NAME+ " WHERE type='" + text+"' ORDER BY id DESC; ");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                populateGridView(" SELECT * FROM "+TABLE_NAME+ " ORDER BY id DESC ");
            }
        });
    }



    //function which on the click of the grid view opens the edit item activity and populates the fields with the data of the clicked item
    public void onGridViewSelect(){
        itemsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemsGridView.setClickable(true);

                //create an array list and cursor as is standard for populating items with the results of a database query
                Cursor cursor = MainActivity.myDb.getData(" SELECT ID FROM userWardrobe ORDER BY id DESC "); //call sql statement
                ArrayList<Integer> arrID = new ArrayList<Integer>();
                //scan through the result items and add the value to the array list
                while (cursor.moveToNext()){
                    arrID.add(cursor.getInt(0));
                }
                Integer clickedID = arrID.get(position); //store the id of the item. the ID and position of the item will be equal.

                //repeat to obtain each column of the sql query result to be 'POST'ed to the edit item activity
                ArrayList<String> clickedItemId = new ArrayList<String>();
                Cursor c = MainActivity.myDb.getData(" SELECT "+COL_1+" FROM "+TABLE_NAME+" WHERE id=' "+clickedID+" ' ");
                while (c.moveToNext()){
                    clickedItemId.add(c.getString(0));
                }

                ArrayList<String> clickedItemName = new ArrayList<String>();
                c = MainActivity.myDb.getData(" SELECT "+COL_2+" FROM "+TABLE_NAME+" WHERE id=' "+clickedID+" ' ");
                while (c.moveToNext()){
                    clickedItemName.add(c.getString(0));
                }

                ArrayList<String> clickedItemBrand = new ArrayList<String>();
                c = MainActivity.myDb.getData(" SELECT " + COL_3 + " FROM " + TABLE_NAME + " WHERE id=' " + clickedID + " ' ");
                while (c.moveToNext()){
                    clickedItemBrand.add(c.getString(0));
                }
                ArrayList<String> clickedItemSource = new ArrayList<String>();
                c = MainActivity.myDb.getData(" SELECT "+COL_4+" FROM "+TABLE_NAME+" WHERE id=' "+clickedID+" ' ");
                while (c.moveToNext()){
                    clickedItemSource.add(c.getString(0));
                }

                ArrayList<String> clickedItemType = new ArrayList<String>();
                c = MainActivity.myDb.getData(" SELECT " + COL_5 + " FROM " + TABLE_NAME + " WHERE id=' " + clickedID + " ' ");
                while (c.moveToNext()){
                    clickedItemType.add(c.getString(0));
                }

                //move to the EditDataActivity and 'POST' the data about the selected item to that activity
                Intent editClothingItemIntent = new Intent(MainActivity.this, EditDataActivity.class);
                editClothingItemIntent.putExtra("Id", clickedItemId.get(0));
                editClothingItemIntent.putExtra("name",clickedItemName.get(0));
                editClothingItemIntent.putExtra("brand",clickedItemBrand.get(0));
                editClothingItemIntent.putExtra("source",clickedItemSource.get(0));
                editClothingItemIntent.putExtra("type", clickedItemType.get(0));
                startActivity(editClothingItemIntent); //start the activity

            }
        });
    }

    //creates the menu in the action bar for the user guide
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //on tap of the user guide menu option opens the web view displaying the user guide
        switch (item.getItemId()){
            case R.id.userGuideMenuItem:
                Intent intent = new Intent(this, webView.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //function which creates a notification at a set time each day
    public void createNotification(){
        Calendar calendar = Calendar.getInstance();

        //sets the time interval of the notification reminder
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 00);


        //intent created to the notification class
        Intent intent = new Intent(getApplicationContext(), notificationReceiver.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");
        //get broadcast receiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationReceiver.requestCode , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //create alarmManager variable to call the notification everyday
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    public void makeToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void printLog(String text){
        Log.d(TAG, text);
    }
}



