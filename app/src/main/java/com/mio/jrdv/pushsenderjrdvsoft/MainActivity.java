package com.mio.jrdv.pushsenderjrdvsoft;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnShowcaseEventListener {

    //V0.4 initial spinner relleno al darle a filtro en Log(pte hacer en oncreate, o en refresh)
    //funciona el envio a todos y el envio selctivo a comunidad selccionada en spinner
    //v08 push selctivo OK
    //v085 push con JSON en lugar de text ok
    //v0.99 cambiaods icnos, colores y añadida ayuda



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
                Snackbar snackbar =Snackbar.make(view, "Enviar Comunicacion a  Vecinos Seleccionados", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Enviando Comunicados...", Snackbar.LENGTH_SHORT);
                                //snackbar1.show();


                                //hacemos el filtrado de los vecinos que tengan el checked a true los metemso en otro array solo de emails
                                //y se lanza el push:


                                //Aqui enviaremos solo ala comunidad selccionada y si hay un texto:

                                final String pushTosendText = Text2Push.getText().toString();

                                //ponemos el progressbar!!

                                setProgressBarIndeterminateVisibility(true);


                                if (!pushTosendText.isEmpty()) {
                                    Log.e(TAG, "Enviando Push Vecinos Elegidos con texto: " + pushTosendText);




                                                //inizializamos un  array que guardara los emails de los vecinos legidos a mano:

                                                List<String> VecinosAmanoParaPush = new ArrayList<String>();


                                                for (int i = 0; i < ListaDeTodosVecinosOKParaListView.size(); i++) {


                                                    Vecino VecinoCheked= ListaDeTodosVecinosOKParaListView.get(i);

                                                    if (VecinoCheked.isChecked()){

                                                        //un vecino que si esta checked lo ñadimos al Array:

                                                        Log.d("Esta chekced a true:", VecinoCheked.getNombre());

                                                        VecinosAmanoParaPush.add(0,VecinoCheked.getEmail());


                                                    }



                                                    //poenmos esos email de esa comunidad àar enviarloa asi:
                                                    // String[] names = {"Jonathan Walsh", "Dario Wunsch", "Shawn Simon"};
                                                    // query.whereContainedIn("playerName", Arrays.asList(names));

                                                    // //añado la comunidad al arrylist de strings:
                                                    //  NombreComunudadListParaspinner.add(0, (String) VecinosList.get(i).get("comunidad"));


                                                }


                                                    if (VecinosAmanoParaPush.size()>=1) {

                                                        snackbar1.show();

                                                        // Find devices associated with these comunidades
                                                        ParseQuery pushQuery = ParseInstallation.getQuery();
                                                        // pushQuery.whereMatchesQuery("email", queryemailsdeComunidad);//asi no va porque el query es de la Class vecino !!nod e installation

                                                        pushQuery.whereContainedIn("email", VecinosAmanoParaPush);


                                                        // Send push notification to query
                                                        ParsePush push = new ParsePush();
                                                        push.setQuery(pushQuery); // Set our Installation query

                                                        //push.setMessage(pushTosendText);//lo hacemos como JSON:

                                                        JSONObject JSONOK= null;
                                                        try {
                                                            JSONOK = Text2Json(pushTosendText);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                        push.setData(JSONOK);
                                                        push.sendInBackground();

                                                        Log.e(TAG, "Enviados Push A vecinos Selccionados");


                                                        //paramos el prorgressdialog

                                                        setProgressBarIndeterminateVisibility(false);
                                                    }

                                                    else {
                                                        Toast.makeText(MainActivity.this, "NO HAS SELCCIONADO NINGUN VECINO!!!", Toast.LENGTH_SHORT).show();

                                                    }

                                        }


                                    else {

                                    Toast.makeText(MainActivity.this, "NO HAY NINGUN COMUNICADO!!!", Toast.LENGTH_SHORT).show();
                                }


                                }




                        });

                snackbar.show();


            }
        });


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



        //para que suba con el teclado:nonhace nada

       // listViewdevecinos.setTranscriptMode(listViewdevecinos.TRANSCRIPT_MODE_NORMAL);
        //listViewdevecinos.setStackFromBottom(true);

        //listViewdevecinos.setAdapter(new CustomArrayAdapter(this, ListaDeTodosVecinosOKParaListView));


        /*
        adapter=new CustomArrayAdapter(this, ListaDeTodosVecinosOKParaListView);
        listViewdevecinos.setAdapter(adapter);
    */


        //enables filtering for the contents of the given ListView
        listViewdevecinos.setTextFilterEnabled(true);



        listViewdevecinos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
               // Toast.makeText(MainActivity.this, ListaDeTodosVecinosOKParaListView.get(position).getNombre(), Toast.LENGTH_SHORT) .show();
            }
        });





        //Parael Search:

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
               // MainActivity.this.adapter.getFilter().filter(cs);

               // String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
              //  adapter.filter(text);
                adapter.getFilter().filter(cs.toString());



            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });





        //*****************************************************************************************
        //***************************HELP INTRO!!!!!**************************************
        //*****************************************************************************************
        //*****************************************************************************************



        int nuemArranuesParaayuda=pref.getInt(LoginActivity.PREF_NUMERO_DEARRANQUES,1);

        if (nuemArranuesParaayuda <=3){

            //solo lo hara las 3 primeras veces!!!



        Target viewTarget = new ViewTarget(R.id.fab, this);
        ShowcaseView sc=new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("VECINO")
                .setContentText("AL PULSAR ESTE BOTON SOLO SE ENVIARA AL/A LOS VECINOS ELEGIDOS")
                //singleShot(42)
                .setShowcaseEventListener(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .replaceEndButton(R.layout.view_custom_button)
                .build();

        sc.setTag(2);

        }

    }



    //********************************************del help:


    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        //cuando se cieera se llama esto:

    }

    @Override
    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {



        Log.e(TAG, "Showcasedidhide TAG:"+showcaseView.getTag() );


        if (showcaseView.getTag()==2 ){

            Target viewTarget2 = new ViewTarget(R.id.BotonEnvioSoloComunidad, this);
            ShowcaseView sc2= new ShowcaseView.Builder(this)
                    .setTarget(viewTarget2)
                    .setContentTitle("COMUNIDAD")
                    .setContentText("AL PULSAR ESTE BOTON SOLO SE ENVIARA A TODOS LOS VECINOS DE LA COMUNIDAD ELEGIDA")
                    // .singleShot(43)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .replaceEndButton(R.layout.view_custom_button)
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .replaceEndButton(R.layout.view_custom_button)
                    .build();

            sc2.setTag(3);

        }

        else if  (showcaseView.getTag()==3 ){

            //es el tercer help!!

            Target viewTarget3 = new ViewTarget(R.id.botonTodos, this);
            ShowcaseView sc2= new ShowcaseView.Builder(this)

                    .setTarget(viewTarget3)
                    .setContentTitle("TODOS")
                    .setContentText("AL PULSAR ESTE BOTON SOLO SE ENVIARA A TODOS LOS VECINOS QUE TENGAN  INSTALADA GHFINCAS")
                    //.singleShot(44)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .replaceEndButton(R.layout.view_custom_button)
                    .setShowcaseEventListener(this)
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .replaceEndButton(R.layout.view_custom_button)
                    .build();

            sc2.setTag(4);

        }




    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {

    }


    public JSONObject Text2Json  (String TExttoSendAsString) throws JSONException {

        //aqui recibimos el text:TExttoSendAsString(que es el del push




        JSONObject jsonfinal = new JSONObject();
        JSONObject jsondata=new JSONObject();




        //aqui le jsondata:

        jsondata.put("message",TExttoSendAsString);
        jsondata.put("title","GHFINCAS");


        jsonfinal.put("data",jsondata);

        jsonfinal.put("is_background",false);






        try {

            Log.d("My App", jsonfinal.toString());

        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON:");
        }


        //devolvemos el json
        return  jsonfinal;


    }



    public void butonenviarPushaTodos(View view) throws ParseException, JSONException {

        String pushTosendText = Text2Push.getText().toString();

        if (!pushTosendText.isEmpty()) {


            new AlertDialog.Builder(this)
                    .setTitle("PUSH A TODOS!!")
                    .setMessage("Seguro que quieres enviar PUSH a TODOS los vecinos?")
                    .setIcon(-1).setIcon(R.drawable.logo7alert32)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete


                            ParsePush push = new ParsePush();
                            push.setChannel(AppConfig.PARSE_CHANNEL);

                            String pushTosendText = Text2Push.getText().toString();



                            Log.e(TAG, "Enviado Push a todos....:" + pushTosendText);

                            //push.setMessage(pushTosendText);
                            //push.setData();//un JSON se envia asi

                            JSONObject JSONOK= null;
                            try {
                                JSONOK = Text2Json(pushTosendText);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            push.setData(JSONOK);


                           push.sendInBackground();

                            // List<ParseObject> subscribedChannels= ParseInstallation.getCurrentInstallation().getList("channels");//esto devuelve 1 !!=GHFINCAS


//            Object subscribedChannels= ParseInstallation.getCurrentInstallation().get("email");///esto devuelve mi email!!!hola2@gmail.com..


                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();









        }

        else {

            Toast.makeText(MainActivity.this, "NO HAY NINGUN COMUNICADO!!!", Toast.LENGTH_SHORT).show();
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




        //aqui ya podemos rellenar el lsitView:


        adapter=new CustomArrayAdapter(this, ListaDeTodosVecinosOKParaListView);
        listViewdevecinos.setAdapter(adapter);



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



            new AlertDialog.Builder(this)
                    .setTitle("PUSH SOLO A COMUNIDAD")
                    .setMessage("Seguro quieres enviar push a todos los vecinos de la Comunidad: "+Comunidad+"?")
                    .setIcon(-1).setIcon(R.drawable.logo7alert32)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete





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


                                        // push.setMessage(pushTosendText); lo mandamso como JSON


                                        JSONObject JSONOK= null;
                                        try {
                                            JSONOK = Text2Json(pushTosendText);
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }

                                        push.setData(JSONOK);


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
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();









        }

        else {

            Toast.makeText(MainActivity.this, "NO HAY NINGUN COMUNICADO!!!", Toast.LENGTH_SHORT).show();
        }
    }


}
