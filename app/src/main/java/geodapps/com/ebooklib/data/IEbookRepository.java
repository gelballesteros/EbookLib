package geodapps.com.ebooklib.data;

import android.content.Context;

import java.util.ArrayList;

/**
 * Interfaz a implementar por cualquier proveedor de ebooks
 */
public interface IEbookRepository
{
    int MODO_ORDENA_LISTA_TITULO = 0;
    int MODO_ORDENA_LISTA_FECHA = 1;

    interface LoginRepositoryCallBack
    {
        void onLogIn(boolean result, String msg);
    }

    interface EbookRepositoryCallBack
    {
        void onBooksLoaded();
        void onErrorLoading(String msg);
        void onBookMetadata(int indx);
    }



    void adquireEbooks(EbookRepositoryCallBack callBack);
    ArrayList<Ebook> getBooks();
    boolean needToLog();
    void logIn(LoginRepositoryCallBack callBack);
    void onCreate(Context cnt);
    void ordenaLista(int modo);
    void getEbookData(int indx,EbookRepositoryCallBack callBack);
}
