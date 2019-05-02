package com.bobby.mycontacts.main;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bobby.mycontacts.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;
    private Context context;

    public MyCursorAdapter(Context context,Cursor cursor) {
        super(context,cursor);
        this.context=context;
        this.cursorInflater=LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(R.layout.item_view, null);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView =  view.findViewById(R.id.contactName);
        ImageView imageView=view.findViewById(R.id.contactImage);
        ImageView initialView=view.findViewById(R.id.initialImage);

        String title = cursor.getString( cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String image = cursor.getString( cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
        if (image != null) {
            imageView.setImageURI(Uri.parse(image));
             initialView.setVisibility(View.INVISIBLE);
             imageView.setVisibility(View.VISIBLE);
        }
        else {
            initialView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            TextDrawable drawable = TextDrawable.builder().buildRound(title.substring(0, 1), Color.parseColor("#C9123D6D"));
            initialView.setImageDrawable(drawable);
        }

        textView.setText(title);
    }
}
