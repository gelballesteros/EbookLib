package geodapps.com.ebooklib.ebooklist;

import java.io.InputStream;
import java.util.List;

import geodapps.com.ebooklib.data.Ebook;
import geodapps.com.ebooklib.data.IEbookRepository;

/**
 * Interfaces de Activity, Fragment y Presentador de la lista de ebooks
 * La Activity realiza la comunicación entre fragments
 */
public interface IEbookListContract
{

    interface Activity
    {
        void showDetail(int indx);
    }

    interface Fragment
    {
        EbookListAdapter createListAdapter(List<Ebook> books);
        void createList(EbookListAdapter adapter);
        void showProgressBar();
        void hideProgressBar();
        void showMessage(String msg);
        void updateList();
    }

    interface Presenter
    {
        void atachView(Fragment fragment);
        void detachView();
        void askForBooks();
        void ordenaLista(int modo);

    }
}
