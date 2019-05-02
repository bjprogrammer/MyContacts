package com.bobby.mycontacts.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bobby.mycontacts.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {
    private long id;
    private String displayName = "" ,nickName ;
    private Bitmap bitmap = null;
    private String companyName,title ,email,phone;
    private TextView userName, userNickName, userPhone, userEmail, userCompany;
    private ImageView imageView;
    private CircleImageView userPic;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();

        id = getIntent().getLongExtra("id",0);
        getIdDetails(id);
    }

    private void init() {
        progressBar = findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.user_name);
        userNickName = findViewById(R.id.nick_name);
        userPhone = findViewById(R.id.user_phone);
        userEmail = findViewById(R.id.user_email);
        userCompany = findViewById(R.id.user_company);
        imageView = findViewById(R.id.profileimage_initial);
        userPic = findViewById(R.id.profileimage);
    }


    private void getIdDetails(long id)
    {
        progressBar.setVisibility(View.VISIBLE);
        ContentResolver cr = getContentResolver();
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
        uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);

        Cursor contactsCursor = cr.query(uri, null, null, null, null);
        if (contactsCursor.moveToFirst()) {
            do {
                long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID")); // Get contact ID
                Uri dataUri = ContactsContract.Data.CONTENT_URI;
                Cursor dataCursor = getContentResolver().query(dataUri, null, ContactsContract.Data.CONTACT_ID + " = " + contactId, null, null);

                if (dataCursor.moveToFirst()) {
                    displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    do {
                        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)) {
                            nickName = dataCursor.getString(dataCursor.getColumnIndex("data1")); // Get Nick Name
                        }

                        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                           phone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                        }
                        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                            email = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                        }

                        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                            companyName = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                            title = dataCursor.getString(dataCursor.getColumnIndex("data4"));
                        }

                        if (dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                            byte[] photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                            if (photoByte != null) {
                                bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);
                            }
                        }
                    } while (dataCursor.moveToNext());
                }
            } while (contactsCursor.moveToNext());
        }

        if(bitmap!=null){
            userPic.setImageBitmap(bitmap);
            imageView.setVisibility(View.INVISIBLE);
            userPic.setVisibility(View.VISIBLE);
        }
        else
        {
            imageView.setVisibility(View.VISIBLE);
            userPic.setVisibility(View.INVISIBLE);
            TextDrawable drawable = TextDrawable.builder().buildRound(displayName.substring(0, 1), Color.parseColor("#6e94aa"));
            imageView.setImageDrawable(drawable);
        }

        userName.setText(displayName);
        if(title!=null){
            userNickName.setText("("+title+")");
        }

        if(phone!=null){
            userPhone.setText(phone);
        }
        else {
            userPhone.setText("Not Available");
        }

        if(email!=null){
            userEmail.setText(email);
        }
        else {
            userEmail.setText("Not Available");
        }

        if (companyName!=null){
            userCompany.setText(companyName);
        }
        else
        {
            userCompany.setText("Not Available");
        }

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onRestart() {
        getIdDetails(id);
        super.onRestart();
    }
}
