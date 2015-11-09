package geodapps.com.ebooklib.ebookdetail;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import geodapps.com.ebooklib.R;
import geodapps.com.ebooklib.data.Ebook;

public class EbookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EbookDetailFragment detailFrag = (EbookDetailFragment) getFragmentManager()
                .findFragmentById(R.id.detail_frag);

        int indx = getIntent().getExtras().getInt(EbookDetailFragment.ARG_EBOOK);
        detailFrag.presenter.showDetail(indx);
}

}
