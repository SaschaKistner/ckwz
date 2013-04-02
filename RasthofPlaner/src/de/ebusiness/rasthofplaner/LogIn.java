package de.ebusiness.rasthofplaner;

import org.json.JSONException;
import org.json.JSONObject;

import de.ebusiness.util.DatabaseHandler;
import de.ebusiness.util.UserFunctions;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LogIn extends Activity {
	
	Button btnLogin;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
	inputEmail = (EditText) findViewById(R.id.login_vorname);
	inputPassword = (EditText) findViewById(R.id.login_password2);
	btnLogin = (Button) findViewById(R.id.login_button_login);
	loginErrorMsg = (TextView) findViewById(R.id.login_error);
	
	btnLogin.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.loginUser(email, password);
 
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        loginErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS);
                        if(Integer.parseInt(res) == 1){
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
                        }else{
                            // Error in login
                            loginErrorMsg.setText("Incorrect username/password");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
            
            
			
		}
		
		

	
	
	
	
	public void onTextClick(View view) { 
		
		if (view.getId() == R.id.login_registrieren) {
			
			startActivity(new Intent(this,Registration.class));
			
		}
		
		
	}


}
