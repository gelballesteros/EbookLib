package geodapps.com.ebooklib.ebooklist;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import geodapps.com.ebooklib.R;
import geodapps.com.ebooklib.ebookdetail.EbookDetailActivity;
import geodapps.com.ebooklib.ebookdetail.EbookDetailFragment;

public class EbookListActivity extends AppCompatActivity implements IEbookListContract.Activity
{
    private EbookListFragment listFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getFragmentManager();
        listFrag = (EbookListFragment) fm.findFragmentById(R.id.list_frag);

        // Si el fragment existe, se está reciclando de un cambio de configuración
        if (listFrag == null) {
            listFrag = new EbookListFragment();
            fm.beginTransaction().add(listFrag, "").commit();
        }
    }

    /**
     * Muestra el fragment con el detalle del ebook
     * Comprueba si está presente el fragment de Detail (según ancho de pantalla) para mostrarlo o pasar a EbookDetailActivity
     * @param indx índice del ebook a mostrar
     */
    @Override
    public void showDetail(int indx)
    {
        EbookDetailFragment detailFrag = (EbookDetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_frag);

        if (detailFrag == null || detailFrag.getView()==null)
        {
            // detailFrag no está en la layout,
            // así que empieza una nueva Activity para mostrarlo, pasándole el ebook como extra
            Intent intent = new Intent(this, EbookDetailActivity.class);
            intent.putExtra(EbookDetailFragment.ARG_EBOOK, indx);
            startActivity(intent);
        } else
        {
            // detailFrag ya está en la layout, así que le dice a su presenter que lo actualice
            detailFrag.getView().setVisibility(View.VISIBLE);
            detailFrag.presenter.showDetail(indx);

        }

       /* getFragmentManager()
                .beginTransaction()
                .replace(R.id.container_list_fragment, EbookDetailFragment.newInstance(book)).addToBackStack(null)
                .commit();*/
    }

}
