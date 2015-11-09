package geodapps.com.ebooklib.ebookdetail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import geodapps.com.ebooklib.R;
import geodapps.com.ebooklib.data.Ebook;
import geodapps.com.ebooklib.ebooklist.IEbookListContract;

/**
 * Fragment que muestra el detalle de un libro con su portada y título
 * En pantallas sw600dp se muestra junto a la lista
 */
public class EbookDetailFragment extends Fragment implements IEbookDetailContract.View
{
    public static final String ARG_EBOOK = "ebook";
    public IEbookDetailContract.Presenter presenter;
    private TextView txtTile;
    private ImageView imgFrontPage;
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new EbookDetailPresenter();
        presenter.atachView(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ebook_detail, container, false);
        txtTile = (TextView) v.findViewById(R.id.txt_detail_title);
        imgFrontPage = (ImageView) v.findViewById(R.id.img_detail_frontpage);
        return v;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detachView();
    }

    @Override
    public void setImage(byte[] data)
    {
        if (data!=null)
        {
            imgFrontPage.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            imgFrontPage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    @Override
    public void setTitle(String txt)
    {
        txtTile.setText(txt);
    }

    @Override
    public void showProgressBar()
    {
        pd = new ProgressDialog(getActivity());
        pd.setTitle(getString(R.string.db_progress_title));
        pd.setMessage(getString(R.string.db_progress_metadata_msg));
        pd.setIndeterminate(true);
        pd.show();
    }

    @Override
    public void hideProgressBar()
    {
        if (pd!=null && pd.isShowing())
            pd.hide();
    }


}
