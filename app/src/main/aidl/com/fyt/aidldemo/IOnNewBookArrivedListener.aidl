// IOnNewBookArrivedListener.aidl
package com.fyt.aidldemo;

// Declare any non-default types here with import statements
import com.fyt.aidldemo.Book;
interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
