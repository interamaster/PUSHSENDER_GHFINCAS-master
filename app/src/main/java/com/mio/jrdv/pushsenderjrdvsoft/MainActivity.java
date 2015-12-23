package com.mio.jrdv.pushsenderjrdvsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mio.jrdv.pushsenderjrdvsoft.parse.AppConfig;
import com.mio.jrdv.pushsenderjrdvsoft.parse.ParseUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //V0.4 initial spinner relleno al darle a filtro en Log(pte hacer en oncreate, o en refresh)
    //funciona el envio a todos y el envio selctivo a comunidad selccionada en spinner



    private EditText Text2Push;
    private static String TAG = MainActivity.class.getSimpleName();


    //par el spinner
    private List<ParseObject> ComunidadListParaSpinner;
    private List<String> NombreComunudadListParaspinner;
    private Spinner VecinosSpinner;




    //para el Query de la comunidad segun eltexto del spinner:

    private String Comunidad;
    private List<String> ListemailParaPushdesdeComunidad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = new Intent(this, com.mio.jrdv.pushsenderjrdvsoft.LoginActivity.class);
        startActivity(intent);


        //seguimos

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);//asi no sale le botona atras


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //definimos los TextViews


        Text2Push = (EditText) findViewById(R.id.TextoTopush);

        //no sbscribimos con el email(no se porque es nevcesario)

        //recupermoas el email

        SharedPreferences pref = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        String email = pref.getString(LoginActivity.PREF_EMAIL, null);//esto devolvera el nombre si existe o null!!

        //hacemos el logging e parse


        if (email != null) {
            ParseUtils.subscribeWithEmail(email);
        }

        //definmos el spinner
        VecinosSpinner = (Spinner) findViewById(R.id.VecinoSpinner);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void butonenviarPushaTodos(View view) throws ParseException {

        ParsePush push = new ParsePush();
        push.setChannel(AppConfig.PARSE_CHANNEL);

        String pushTosendText = Text2Push.getText().toString();


        if (!pushTosendText.isEmpty()) {
            Log.e(TAG, "Enviado Push a todos....:" + pushTosendText);

            push.setMessage(pushTosendText);
            push.sendInBackground();

            // List<ParseObject> subscribedChannels= ParseInstallation.getCurrentInstallation().getList("channels");//esto devuelve 1 !!=GHFINCAS


//            Object subscribedChannels= ParseInstallation.getCurrentInstallation().get("email");///esto devuelve mi email!!!hola2@gmail.com..


        }
    }

    public void CreaVecino(View view) {

        //vamos a crear en Parse un nuevo vecino aleatorio:

        ParseObject Vecino = new ParseObject(AppConfig.PARSE_CLASS);//PARSE_CLASS=Vecino!!!
        Vecino.put("email", "hola3@gmail.com");
        Vecino.put("nombre", "Chocoplancha el emjor");
        Vecino.put("comunidad", "Avestruces 34");
        Vecino.put("telefono", 789456123);
        Vecino.saveInBackground();

        Log.e(TAG, "Creado vecino:");


    }

    public void butonFiltroEnLog(View view) {

        //vamos a probar filtros:

        //vamos a hacer un query:
        /*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameScore");
        query.whereEqualTo("playerName", "Dan Stemkoski");
        query.findInBackground(new FindCallback<ParseObject>() {
                     public void done(List<ParseObject> scoreList, ParseException e) {
                         if (e == null) {
                                   Log.d("score", "Retrieved " + scoreList.size() + " scores");
                             } else {
                              Log.d("score", "Error: " + e.getMessage());
                     }
                       }
                    });

         */

        /*
        //y asi se hace el push selctivo:

        // Send push notification to query
              ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
               push.setMessage("Willie Hayes injured by own pop fly.");
                push.sendInBackground();
         */

        //asi seria con un array de emails
        /*

        String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
        query.whereContainedIn("playerName", Arrays.asList(names));
         */

/*
         ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConfig.PARSE_CHANNEL);



        String[] names = {"dgayurt@gmail.com", "info@lopidoyo.com", "hola2@gmail.com"};
      // query.whereContainedIn("email", Arrays.asList(names));
        query.whereEqualTo("email", "info@lopidoyo.com");



*/


        //vamos a sacarlos todos:

        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConfig.PARSE_CLASS);
        query.selectKeys(Arrays.asList("email", "comunidad", "nombre", "telefono"));
        ;
        // Sorts the results in ascending order by the score field
        query.orderByDescending("comunidad");//lo pongo a reves porque luego para sacro en el spinner y qiutar duplicado los invierte el orden..


        //inizizlizamos el ArratLIst para el Adapter del spinner:

        NombreComunudadListParaspinner = new ArrayList<String>();


        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> VecinosList, ParseException e) {

                if (e == null) {
                    Log.d("Vecinos", "Retrieved " + VecinosList.size() + " Vecinos");


                    for (int i = 0; i < VecinosList.size(); i++) {
                        Log.d("Que son:", (String) VecinosList.get(i).get("email"));
                        Log.d("Que son:", (String) VecinosList.get(i).get("comunidad"));
                        //añado la comunidad al arrylist de strings:
                        NombreComunudadListParaspinner.add(0, (String) VecinosList.get(i).get("comunidad"));

                        Log.d("Que son:", (String) VecinosList.get(i).get("nombre"));
                        Log.d("Que son:", String.valueOf(VecinosList.get(i).getInt("telefono")));
                    }

                    //copiamos el ArryList

                    // myObject = new ArrayList<Object>(myTempObject);//para ArrayList
                    //List<SomeBean> newList = new ArrayList<SomeBean>(otherList);//Para List

                    ComunidadListParaSpinner = new ArrayList<ParseObject>(VecinosList);

                    RellenarSpinnerComunidades();


                } else {
                    Log.d("Vecinos", "Error: " + e.getMessage());
                }
            }
        });


    }


    public void RellenarSpinnerComunidades() {


        //una vez sabidos lo ponemos en le spinner:NombreComunudadListParaspinner


        //Pero tambien lo vamos a filtrar para borrar los repetidos!!!
        /*
        If you have a List containing duplicates, and you want a List without, you could do:

        List<String> newList = new ArrayList<String>(new HashSet<String>(oldList));

        */

        // List<String>newListNombreComunudadListParaspinner=new ArrayList<String>(new HashSet<String>(NombreComunudadListParaspinner));//asi los pone sin orden!!
        List<String> newListNombreComunudadListParaspinner = new ArrayList<String>(new LinkedHashSet<String>(NombreComunudadListParaspinner));


        //Spinner VecinosSpinner = (Spinner) findViewById(R.id.VecinoSpinner);//lo pngo en oncreate mejor
        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,ComunidadListParaSpinner );//asi salen pointer del objeto no el texto
        // ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,NombreComunudadListParaspinner );//asi salen repetidos!!
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, newListNombreComunudadListParaspinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        VecinosSpinner.setAdapter(adapter);

         /*
        metodo del spinner al elegir:

          */

        VecinosSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Comunidad = parent.getItemAtPosition(position).toString();

                Log.d("Comunidad", "Elegida: " + Comunidad);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Toast.makeText(MainActivity.this, "NO HAS SELCCIONADO NINGUNA COMUNIDAD!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void EnvioComunidadSeleccionada(View view) {

        //Aqui enviaremos solo ala comunidad selccionada y si hay un texto:

        final String pushTosendText = Text2Push.getText().toString();




        if (!pushTosendText.isEmpty()) {
            Log.e(TAG, "Enviando Push Comunidad.:" + Comunidad + "con texto: " + pushTosendText);


            final ParseQuery<ParseObject> queryemailsdeComunidad = ParseQuery.getQuery(AppConfig.PARSE_CLASS);
            queryemailsdeComunidad.whereEqualTo("comunidad", Comunidad);
            queryemailsdeComunidad.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> VecinosListParaComunidadElegida, ParseException e) {
                    if (e == null) {
                        Log.d("Comunidad", "Total: " + VecinosListParaComunidadElegida.size() + " Emails");

                        //inizializamos el array que guardara los emails para chequear con instalaation y poder enviarlos
                        ListemailParaPushdesdeComunidad = new ArrayList<String>();


                        for (int i = 0; i < VecinosListParaComunidadElegida.size(); i++) {
                            Log.d("Que tienen email:", (String) VecinosListParaComunidadElegida.get(i).get("email"));

                            //poenmos esos email de esa comunidad àar enviarloa asi:
                           // String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
                           // query.whereContainedIn("playerName", Arrays.asList(names));

                            // //añado la comunidad al arrylist de strings:
                          //  NombreComunudadListParaspinner.add(0, (String) VecinosList.get(i).get("comunidad"));

                            ListemailParaPushdesdeComunidad.add(0,(String) VecinosListParaComunidadElegida.get(i).get("email"));

                        }


                        // Find devices associated with these comunidades
                        ParseQuery pushQuery = ParseInstallation.getQuery();
                       // pushQuery.whereMatchesQuery("email", queryemailsdeComunidad);//asi no va porque el query es de la Class vecino !!nod e installation

                        pushQuery.whereContainedIn("email",ListemailParaPushdesdeComunidad);



                        // Send push notification to query
                        ParsePush push = new ParsePush();
                        push.setQuery(pushQuery); // Set our Installation query
                        push.setMessage(pushTosendText);
                        push.sendInBackground();

                        Log.e(TAG, "Enviados Push Comunidad" );


                    } else {
                        Log.d("Comunidad", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

}
