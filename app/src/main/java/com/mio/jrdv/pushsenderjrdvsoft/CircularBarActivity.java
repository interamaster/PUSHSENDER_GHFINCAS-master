package com.mio.jrdv.pushsenderjrdvsoft;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class CircularBarActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_bar);


        final  CircularProgressBar c3 = (CircularProgressBar) findViewById(R.id.circularprogressbar1);
        c3.setTitle("GHFINCAS");
        c3.setSubTitle("GHFINCAS");
        //c3.setProgress(42);


        c3.animateProgressTo(0, 100, new CircularProgressBar.ProgressAnimationListener() {

            @Override
            public void onAnimationStart() {
            }

            @Override
            public void onAnimationProgress(int progress) {
                c3.setTitle(progress + "%");
            }

            @Override
            public void onAnimationFinish() {
                c3.setSubTitle("done");
                // onLoginSuccess(); en vez de aqui habria que ejecutarlo en el onaCtivityResilt del LoginActivity!!!
                //aqui hacemos finish para que vuelva LoginActivity y desde all ejecute el OnLogginSuccess y pasa al Main_Activity!!

                c3.setVisibility(View.INVISIBLE);

                finish();


            }
        });
    }
}