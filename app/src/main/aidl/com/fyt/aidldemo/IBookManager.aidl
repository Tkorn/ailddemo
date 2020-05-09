// IBookManager.aidl
package com.fyt.aidldemo;

// Declare any non-default types here with import statements
import com.fyt.aidldemo.Book;
import com.fyt.aidldemo.IOnNewBookArrivedListener;

interface IBookManager {

    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);

}
