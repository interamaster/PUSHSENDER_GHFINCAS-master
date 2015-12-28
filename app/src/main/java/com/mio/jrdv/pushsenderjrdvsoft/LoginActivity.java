package com.mio.jrdv.pushsenderjrdvsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {



    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREF_EMAIL = "email";//sera el user name
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_BOOL_LOGINYAOK ="false";
    public static final String PREF_NUMERO_DEARRANQUES="1";





    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    //otro para el progressDialog

    private static final int PROGRESS_BAR_DIALOG = 1;

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        SharedPreferences pref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

          /*

        estos son los vaslores guarados en el signup activity tras el ok


        edit.putString(LoginActivity.PREF_EMAIL, email);

        edit.putString(LoginActivity.PREF_PASSWORD, decryptedpassword);

        edit.putBoolean(LoginActivity.PREF_BOOL_LOGINYAOK,true);

        */

        String email = pref.getString(PREF_EMAIL, null);//esto devolvera el nombre si existe o null!!
        String password = pref.getString(PREF_PASSWORD, null);



        //para eñ nuemro de arranques y poner o no la ayda inicial

        int nuemArranuesParaayuda=pref.getInt(PREF_NUMERO_DEARRANQUES,1);

        Log.d(TAG, "numero de arranques:"+nuemArranuesParaayuda);
        nuemArranuesParaayuda++;


        //guardamos el numeor de arranues

        // We need an editor object to make changes
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(LoginActivity.PREF_NUMERO_DEARRANQUES, nuemArranuesParaayuda);


        // Commit the changes
        edit.commit();





        boolean alreadyloggedinbefore =  pref.getBoolean(PREF_BOOL_LOGINYAOK, false);

        Log.d(TAG, "email:" + email + " y  password:" + password);

        Log.d(TAG, String.valueOf(alreadyloggedinbefore));


        //si existen el username y password los ponemos n los campos de manera automatica


        if (email != null && password != null && alreadyloggedinbefore){


            _emailText.setText(email);
            _passwordText.setText(password);

        }

    }






    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);




        String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();




        final String decryptedpassword="ghfincas";



        if ((password.equals("sevilla")) || (password.equals(decryptedpassword)))
        {
            //onLoginSuccess();
            Intent intentProgressBarActivity =new Intent(this,CircularBarActivity.class);
            //startActivity(intentProgressBarActivity);

            //en vez de asi le pasamos un request code para poder averiguara ene le OnActivityResult

            startActivityForResult(intentProgressBarActivity,PROGRESS_BAR_DIALOG);



            //Y cuando vuelva aqui en OnaCtivityResult ejecutamos el OnlogginSucces
        }

        else {
            onLoginFailed();
        }


    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //yo de moento no hago nada

                //this.finish();
            }
        }

        else if(requestCode==PROGRESS_BAR_DIALOG){

            //aqui volvemos desde el progrewssBarDialog

            onLoginSuccess();

        }
    }




    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);


        //una vez hemos hecho el primer loging OK cmabiamos le bool para que la proxima vez lo guarde solo!!!!


        SharedPreferences pref = getSharedPreferences( PREFS_NAME, Context.MODE_PRIVATE);

        // We need an editor object to make changes
        SharedPreferences.Editor edit = pref.edit();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        edit.putBoolean( PREF_BOOL_LOGINYAOK,true);
        edit.putString(LoginActivity.PREF_EMAIL, email);
        edit.putString(LoginActivity.PREF_PASSWORD, password);

        // Commit the changes
        edit.commit();


        //TODO desde aqui inicaimos el PARSE

        //recupermoas el email

        String email2 = pref.getString(PREF_EMAIL, null);//esto devolvera el nombre si existe o null!!

        //hacemos el logging e parse


        if (email2 != null) {
           //TODO  ParseUtils.subscribeWithEmail(email);
        }

        finish();
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();




        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Introduzca una direccion de email valida");
            valid = false;
        } else {
            _emailText.setError(null);
        }






        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("su password debe tener entre 4 y 10 caracteres");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


}
