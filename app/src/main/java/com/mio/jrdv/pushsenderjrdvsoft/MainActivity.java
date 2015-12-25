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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mio.jrdv.pushsenderjrdvsoft.model.CustomArrayAdapter;
import com.mio.jrdv.pushsenderjrdvsoft.model.Vecino;
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
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //V0.4 initial spinner relleno al darle a filtro en Log(pte hacer en oncreate, o en refresh)
    //funciona el envio a todos y el envio selctivo a comunidad selccionada en spinner



    private EditText Text2Push;
    private static String TAG = MainActivity.class.getSimpleName();


    //par el spinner

    private List<String> NombreComunudadListParaspinner;
    private Spinner VecinosSpinner;


    //Para la listView:
    // ArrayList for Listview
    private List<ParseObject> ComunidadListParaSpinner;//Aquie se guaradan TODOS!!! los vecinos en un Array pero en PArse!!!

    private List<Vecino> ListaDeTodosVecinosOKParaListView;

    private  CustomArrayAdapter adapter;



    // List view
    private ListView listViewdevecinos;


    // Search EditText
    EditText inputSearch;


    //para el Query de la comunidad segun eltexto del spinner:

    private String Comunidad;
    private List<String> ListemailParaPushdesdeComunidad;

    //Paa Oner el nombre de la comunidad en el buton:

    private Button comunidadButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //un progressdialog que haremos visible al ahacer cualquer query

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_main);


        //definimos el button de la comunidad

        comunidadButton=(Button)findViewById(R.id.BotonEnvioSoloComunidad);



        Intent intent = new Intent(this, com.mio.jrdv.pushsenderjrdvsoft.LoginActivity.class);
        startActivity(intent);


        //seguimos

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("PUSH SENDER JRDVSOFT");
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);//sin esto no sale botn atras

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



        //hacemos un refresh de las comunidades:
        FiltrarComunidades();


        //Rellenamos la listViewDeVecinos y  el Find:(aqui ya s supone que le List de ListaDeTodosVecinosOKParaListView ya esta lleno!!

        listViewdevecinos = (ListView) findViewById(R.id.listviewDevecinos);
        inputSearch = (EditText) findViewById(R.id.search);



        //listViewdevecinos.setAdapter(new CustomArrayAdapter(this, ListaDeTodosVecinosOKParaListView));

        adapter=new CustomArrayAdapter(this, ListaDeTodosVecinosOKParaListView);
        listViewdevecinos.setAdapter(adapter);


        listViewdevecinos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
               // Toast.makeText(MainActivity.this, ListaDeTodosVecinosOKParaListView.get(position).getNombre(), Toast.LENGTH_SHORT) .show();
            }
        });







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

   /*

   //esto ya no fuinciona desde esta apk!! solo desde ghfincas...npi de porque pero me da =

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

*/

    public void FiltrarComunidades(){
        //ESTO ES UN COPY/PASTE DEL BUTTONFILTROENLOG!!!!

        //vamos a sacarlos todos:

        //ponemos el progressbar!!

        setProgressBarIndeterminateVisibility(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(AppConfig.PARSE_CLASS);
        query.selectKeys(Arrays.asList("email", "comunidad", "nombre", "telefono"));
        ;
        // Sorts the results in ascending order by the score field
        query.orderByDescending("comunidad");//lo pongo a reves porque luego para sacro en el spinner y qiutar duplicado los invierte el orden..


        //inizizlizamos el ArratLIst para el Adapter del spinner:

        NombreComunudadListParaspinner = new ArrayList<String>();

        //inizizlizamos el ArratLIst para el Adapter del listView:

        ListaDeTodosVecinosOKParaListView = new ArrayList<Vecino>();

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
                        Log.d("Que son:", String.valueOf(VecinosList.get(i).get("telefono")));


                        //y ya de camino rellenamos el array listpara la list View:ListaDeTodosVecinosOKParaListView
                        Vecino newVecino=new Vecino((String) VecinosList.get(i).get("comunidad"),(String) VecinosList.get(i).get("email"),
                                (String) VecinosList.get(i).get("nombre"), String.valueOf(VecinosList.get(i).get("telefono")));
                        ListaDeTodosVecinosOKParaListView.add(newVecino);




                    }

                    //copiamos el ArryList

                    // myObject = new ArrayList<Object>(myTempObject);//para ArrayList
                    //List<SomeBean> newList = new ArrayList<SomeBean>(otherList);//Para List

                    ComunidadListParaSpinner = new ArrayList<ParseObject>(VecinosList);

                    RellenarSpinnerComunidades();

                    //paramos el prorgressdialog

                    setProgressBarIndeterminateVisibility(false);


                    //refrescamos el listview

                     adapter.notifyDataSetChanged();


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

                //uan vez filtradas le ponemos el nombre al boton:


                if(Comunidad != null){

                    comunidadButton.setText("Enviar Comunidad: "+Comunidad);

                }


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

        //ponemos el progressbar!!

        setProgressBarIndeterminateVisibility(true);


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


                        //paramos el prorgressdialog

                        setProgressBarIndeterminateVisibility(false);


                    } else {
                        Log.d("Comunidad", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }


}
