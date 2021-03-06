package geodapps.com.ebooklib.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import geodapps.com.ebooklib.ebooklist.EbookListActivity;
import geodapps.com.ebooklib.R;

public class LoginActivity extends AppCompatActivity implements ILoginContract.View
{

    ILoginContract.Presenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        presenter = new LoginPresenter();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.loginFab);
        fab.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                presenter.logIn(); //Envía petición de login
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presenter.onCreate(this);   //Vincula la vista con el presenter
        if (!presenter.needToLog()) //Comprueba si el repositorio require login. Si no, da como logueado
            onLogIn(true,null);
    }

    @Override
    protected void onPause()
    {
        presenter.detachView();     //Desvincula la vista del presenter por si se finaliza el login mientras la Activity ya no está
        super.onPause();
    }

    @Override
    public void onLogIn(boolean result,String msg)
    {
        if (result)
        {
            //Pasa a la EbookListActivity
            Intent i = new Intent(this, EbookListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else
            ((TextView)findViewById(R.id.txt_login)).setText(msg);
    }
}
