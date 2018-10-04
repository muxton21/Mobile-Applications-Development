package com.example.comg3.toomuchstuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by comg3 on 16/05/2018.
 */

public class ClothingListAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private ArrayList<Clothing> clothingList;

    public ClothingListAdapter(Context content, int layout, ArrayList<Clothing> clothingList) {
        this.context = content;
        this.layout = layout;
        this.clothingList = clothingList;
    }



    @Override
    public int getCount() {
        return clothingList.size();
    }

    @Override
    public Object getItem(int position) {
        return clothingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView clothingTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        //fills grid vie items with their values from the sql query
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.clothingTitle = row.findViewById(R.id.titleTextView);
            holder.imageView = row.findViewById(R.id.clothingPlaceholderImageView);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }

        //sets the text to the the title of the clothing item
        Clothing clothing = clothingList.get(position);

        holder.clothingTitle.setText(clothing.getTitle());

        //pulls the image stored in the db and decodes its byte array to display the image in the grid view
        byte[] clothingImage = clothing.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(clothingImage, 0, clothingImage.length);

        holder.imageView.setImageBitmap(bitmap);
        return row;
    }

}
