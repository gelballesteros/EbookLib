package geodapps.com.ebooklib.data;

import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Interfaz a implementar por cualquier proveedor de ebooks
 */
public interface IEbookRepository
{
    interface LoginRepositoryCallBack
    {
        void onLogIn(boolean result, String msg);
    }

    interface EbookRepositoryCallBack
    {
        void onBooksLoaded();
        void onErrorLoading(String msg);
    }

    void adquireEbooks(EbookRepositoryCallBack callBack);
    ArrayList<Ebook> getBooks();
    boolean needToLog();
    void logIn(LoginRepositoryCallBack callBack);
    void onCreate(Context cnt);
}
