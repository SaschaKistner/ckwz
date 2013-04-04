package de.ebusiness.rasthofplaner;

import org.json.JSONException;
import org.json.JSONObject;

import de.ebusiness.util.DatabaseHandler;
import de.ebusiness.util.UserFunctions;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class Registration extends Activity {
	
	Button btnRegister;
	Button btnLinkToLogin;
	EditText inputName;
	EditText inputVorname;
	EditText inputEmail;
	EditText inputTelefonnummer;
	EditText inputPassword;
	TextView registerErrorMsg;
	Spinner inputAnrede;
	
	// JSON Response node names
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static String KEY_ERROR_MSG = "error_msg";
	private static String KEY_UID = "uid";
	private static String KEY_NAME = "name";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrieren);
	
		// Importing all assets like buttons, text fields
				inputAnrede = (Spinner) findViewById(R.id.login_anrede);
				inputName = (EditText) findViewById(R.id.login_nachname);
				inputVorname = (EditText) findViewById(R.id.login_vorname);
				inputEmail = (EditText) findViewById(R.id.login_email);
				inputTelefonnummer = (EditText) findViewById(R.id.login_telefonnummer);
				inputPassword = (EditText) findViewById(R.id.registrieren_password);
				btnRegister = (Button) findViewById(R.id.registrieren_button_registrieren);
				//btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
				//registerErrorMsg = (TextView) findViewById(R.id.register_error);
				
				// Register Button Click event
				btnRegister.setOnClickListener(new View.OnClickListener() {			
					public void onClick(View view) {
						String anrede = inputAnrede.getSelectedItem().toString();
						String name = inputName.getText().toString();
						String vorname = inputVorname.getText().toString();
						String email = inputEmail.getText().toString();
						String telefonnummer = inputTelefonnummer.getText().toString();
						String password = inputPassword.getText().toString();
						UserFunctions userFunction = new UserFunctions();
						JSONObject json = userFunction.registerUser(anrede, name, vorname, email, telefonnummer, password);
						
						// check for login response
						try {
							if (json.getString(KEY_SUCCESS) != null) {
								//registerErrorMsg.setText("");
								String res = json.getString(KEY_SUCCESS); 
								if(Integer.parseInt(res) == 1){
									// user successfully registred
									// Store user details in SQLite Database
									DatabaseHandler db = new DatabaseHandler(getApplicationContext());
									JSONObject json_user = json.getJSONObject("user");
									
									// Clear all previous data in database
									userFunction.logoutUser(getApplicationContext());
									db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));						
									// Launch Dashboard Screen
									Intent dashboard = new Intent(getApplicationContext(), MainMenu.class);
									// Close all views before launching Dashboard
									dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(dashboard);
									// Close Registration Screen
									finish();
								}else{
									// Error in registration
									//registerErrorMsg.setText("Error occured in registration");
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		
	}
	
	public void onButtonClick(View view) {
		
		if (view.getId() == R.id.registrieren_button_zurueck) {
			this.finish();
			startActivity(new Intent(this,LogIn.class));
		}
		
	}
	
	

}
