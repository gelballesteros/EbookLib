package geodapps.com.ebooklib.ebooklist;

import java.util.List;

import geodapps.com.ebooklib.data.Ebook;
import geodapps.com.ebooklib.data.IEbookRepository;

/**
 * Interfaces de Vista y Presentador de la lista de ebooks
 */
public interface IEbookListContract
{
    interface View
    {
        void createListAdapter(List<Ebook> books);
        void createList(EbookListAdapter adapter);
        void showProgressBar();
        void hideProgressBar();
        void showMessage(String msg);
        void showEmptyListMsg(String msg);
        void showList();
        void hideList();
        void showAskForWSConnection();
        void hideAskForWSConnection();
        void showDetail(Ebook book);
    }

    interface Presenter
    {
        void atachView(View view);
        void detachView();
        void askForBooks();
    }
}
