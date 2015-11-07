package geodapps.com.ebooklib.ebookdetail;

/**
 * Interfaces de Vista y Presentador de la vista de detalle de un ebook
 */
public interface IEbookDetailContract
{
    interface View
    {
        void setImage(String url);
        void setTitle(String txt);
    }

    interface Presenter
    {
        void atachView(View view);
        void detachView();
    }
}
