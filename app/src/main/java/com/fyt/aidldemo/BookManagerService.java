package com.fyt.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fyt
 */
public class BookManagerService extends Service {
    private final String TAG = getClass().getSimpleName();
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList
            = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private IBinder mIBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
            Log.d(TAG,"add succeed bookId: "+book.getBookId());
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        int listenerListSize = mListenerList.beginBroadcast();
        for (int i = 0; i < listenerListSize; i++){
            IOnNewBookArrivedListener listener = mListenerList.getRegisteredCallbackItem(i);
            if (listener != null){
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"New Book 1"));
        mBookList.add(new Book(2,"New Book 2"));
        new Thread(new ServiceWorker()).start();
    }

    private class ServiceWorker implements Runnable{
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
                while (!mIsServiceDestroyed.get()){
                    try {
                        Thread.sleep(5000);
                        int bookId = mBookList.size()+100;
                        Book book = new Book(bookId,"New Book#"+bookId);
                        onNewBookArrived(book);
                    } catch (InterruptedException | RemoteException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }
}
