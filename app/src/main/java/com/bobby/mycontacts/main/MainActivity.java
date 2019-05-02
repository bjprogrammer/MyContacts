package com.bobby.mycontacts.main;

import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.andrognito.flashbar.Flashbar;
import com.bobby.mycontacts.R;
import com.bobby.mycontacts.utils.Constants;
import com.bobby.mycontacts.utils.HelperFunctions;
import com.github.loadingview.LoadingView;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private ListView contact_listview;
    private MyCursorAdapter adapter;
    private CursorLoader cursorLoader;

    //Variables used in ProgressBar
    private LoadingView loadingView;
    private TextView loadingMessage;

    //Variables used in bottom snackbar
    private Flashbar.Builder flashbarBuilder;
    private Flashbar flashbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contact_listview = findViewById(R.id.contact_listview);
        loadingMessage = findViewById(R.id.loadingMessage);
        loadingView = findViewById(R.id.loadingView);

        setupCursorAdapter();
        contact_listview.setAdapter(adapter);

        //Displaying ProgressBar till contacts are not fetched
        loadingView.start();
        loadingMessage.setText(getText(R.string.fetch_contacts));

        contact_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = ((CursorAdapter)parent.getAdapter()).getCursor();
                c.moveToPosition(position);

                Long selectedId =c.getLong(0);
                Intent intent=new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", selectedId);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            }
        });

        //Checking contacts permission
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            LoaderManager.getInstance(this).initLoader(Constants.LOADER_ID, new Bundle(), contactsLoader);
        } else {
            String permission[] = {Manifest.permission.READ_CONTACTS};
            ActivityCompat.requestPermissions(this, permission, Constants.CONTACTS_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        LoaderManager.getInstance(this).initLoader(Constants.LOADER_ID, new Bundle(), contactsLoader);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
            flashbarBuilder = HelperFunctions.setSnackbar("Read Contacts Permission Required", "This app has certain features that require use of contacts directory. Please grant contacts permissions from app settings", R.drawable.phone_book, MainActivity.this, getResources().getColor(R.color.colorPrimaryDark));
            flashbarBuilder.positiveActionText("OK")
                    .negativeActionText("CANCEL")
                    .positiveActionTextColorRes(R.color.colorAccent)
                    .negativeActionTextColorRes(R.color.colorAccent)
                    .positiveActionTapListener(new Flashbar.OnActionTapListener() {
                        @Override
                        public void onActionTapped(Flashbar flashbar) {
                            if(flashbar!=null) {
                                flashbar.dismiss();
                            }
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            setResult(RESULT_OK);
                            startActivityForResult(intent, Constants.CONTACTS_REQUEST_CODE);
                        }
                    })
                    .negativeActionTapListener(new Flashbar.OnActionTapListener() {
                        @Override
                        public void onActionTapped(Flashbar flashbar) {
                            if(flashbar!=null) {
                                flashbar.dismiss();
                            }
                            System.exit(0);
                        }
                    });
            flashbar = flashbarBuilder.build();
            flashbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.CONTACTS_REQUEST_CODE) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
                LoaderManager.getInstance(this).initLoader(Constants.LOADER_ID, new Bundle(), contactsLoader);
            }
        }
    }

    // Create simple cursor adapter to connect the cursor dataset we load with a ListView
    private void setupCursorAdapter() {
        adapter = new MyCursorAdapter(this, null);
    }

    // Defines the asynchronous callback for the contacts data loader
    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader = new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    // Define the columns to retrieve
                    String[] projectionFields = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.PHOTO_URI, ContactsContract.Contacts.DISPLAY_NAME};

                  cursorLoader = new CursorLoader(MainActivity.this,
                            ContactsContract.Contacts.CONTENT_URI, projectionFields, // projection fields
                            null, null, // the selection args
                            ContactsContract.Contacts.DISPLAY_NAME + " ASC " // the sort order
                    );
                    return cursorLoader;
                }

                // When the system finishes retrieving the Cursor through the CursorLoader,
                // a call to the onLoadFinished() method takes place.
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    if(loadingView!=null && loadingMessage!=null) {
                        loadingView.stop();
                        loadingView.setVisibility(View.GONE);
                        loadingMessage.setText("");
                    }

                    adapter.swapCursor(cursor);
                }

                // This method is triggered when the loader is being reset
                // and the loader data is no longer available. Called if the data
                // in the provider changes and the Cursor becomes stale.
                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    adapter.swapCursor(null);
                }
            };
}