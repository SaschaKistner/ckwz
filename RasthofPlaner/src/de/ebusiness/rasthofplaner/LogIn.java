package de.ebusiness.rasthofplaner;

import org.json.JSONException;
import org.json.JSONObject;

import de.ebusiness.util.DatabaseHandler;
import de.ebusiness.util.UserFunctions;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends Activity {
	
	Button btnLogin, btnRegistry;
	EditText inputEmail;
	EditText inputPassword;
	TextView loginErrorMsg;
	
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    private final UserFunctions userFunction = new UserFunctions();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
	inputEmail = (EditText) findViewById(R.id.login_vorname);
	inputPassword = (EditText) findViewById(R.id.login_password2);
	btnLogin = (Button) findViewById(R.id.login_button);
	btnRegistry = (Button) findViewById(R.id.registry_button);
	
	btnRegistry.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
			startActivity(new Intent(LogIn.this, Registration.class));			
		}
	});
	
	btnLogin.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
		    final ProgressDialog dialog = ProgressDialog.show(LogIn.this, "Logging in", "Please wait...", true);
			final String email = inputEmail.getText().toString();
			final String password = inputPassword.getText().toString();
		    new AsyncTask<Void, Void, Void>()
		    {
		    	JSONObject json;
		        @Override
		        protected Void doInBackground(Void... params)
		        {
		            json = checkCredentials(email, password);
		            return null;
		        }

				@Override
		        protected void onPostExecute(Void result)
		        {
		            dialog.dismiss();
		            try {
		                String res = json.getString(KEY_SUCCESS);
						if (json.getString(KEY_SUCCESS) != null) {
							if(Integer.parseInt(res) == 1){
								loggingIn(json, email);
							} else {
								AlertDialog.Builder alert = new AlertDialog.Builder(LogIn.this);
								alert.setTitle("Anmeldefehler");
								alert.setMessage("Benutzername und/oder Passwort sind inkorrekt!");
								alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {  
									@Override  
									public void onClick(DialogInterface dialog, int which) {  
										dialog.dismiss();                      
									}  
								});
								alert.show(); 
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }

		    }.execute();
            }
        });   
	}
	
    private JSONObject checkCredentials(String email, String password){
        UserFunctions userFunction = new UserFunctions();
        JSONObject json = userFunction.loginUser(email, password);
        return json;
	}
		
	private void loggingIn(JSONObject json, String email) throws JSONException {                
        // user successfully logged in
        // Store user details in SQLite Database
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        JSONObject json_user = json.getJSONObject("user");

        // Clear all previous data in database
        userFunction.logoutUser(getApplicationContext());
        db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        

        // Launch Dashboard Screen
        Intent dashboard = new Intent(getApplicationContext(), MainMenu.class);
        
        dashboard.putExtra("Name", email);

        // Close all views before launching Dashboard
        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboard);

        // Close Login Screen
        finish();
	}
}
