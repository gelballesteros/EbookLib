package geodapps.com.ebooklib.ebookdetail;

import geodapps.com.ebooklib.data.Ebook;

/**
 * Interfaces de Vista y Presentador de la vista de detalle de un ebook
 */
public interface IEbookDetailContract
{
    interface View
    {
        void setImage(byte[] data);
        void setTitle(String txt);
        void showProgressBar();
        void hideProgressBar();
    }

    interface Presenter
    {
        void atachView(View view);
        void detachView();
        void showDetail(int indx);
    }
}
