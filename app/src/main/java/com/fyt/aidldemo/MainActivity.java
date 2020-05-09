package com.fyt.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private IBookManager mIBookManager;
    private IOnNewBookArrivedListener mNewBookArrivedListener;

    private Binder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            bindBookManagerService();
        }
    };

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mIBookManager = IBookManager.Stub.asInterface(iBinder);


            try {
                mIBookManager.addBook(new Book(3,"New Book 3"));
                List<Book> bookList = mIBookManager.getBookList();
                Log.d(TAG,"bookList : "+ Arrays.toString(bookList.toArray()));
                mIBookManager.registerListener(mNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mIBookManager = null;
            Log.e(TAG, "binder died");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        mNewBookArrivedListener = new IOnNewBookArrivedListener.Stub(){

            @Override
            public void onNewBookArrived(Book newBook) throws RemoteException {
                Log.d(TAG,"onNewBookArrived newBook name: "+newBook.getBookName());
            }
        };

        bindBookManagerService();

    }

    private void bindBookManagerService(){
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIBookManager != null){
            try {
                mIBookManager.unregisterListener(mNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
