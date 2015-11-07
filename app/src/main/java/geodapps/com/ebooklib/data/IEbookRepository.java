package geodapps.com.ebooklib.data;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Interfaz a implementar por cualquier proveedor de ebooks
 */
public interface IEbookRepository
{
    interface EbookRepositoryCallBack
    {
        void onBooksLoaded();
        void onErrorLoading(String msg);
    }

    void adquireEbooks(EbookRepositoryCallBack callBack);
    ArrayList<Ebook> getBooks();
}
