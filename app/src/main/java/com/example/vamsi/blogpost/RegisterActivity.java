package com.example.vamsi.blogpost;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    // These are the EditTexts,Buttons and Progressbar that are defined in the activity_register.xml
    // These elements are declared as private and will be later binded to the ids of the specific elements which are given in
    // activity_register.xml
    private EditText regemailText;
    private EditText regPasstext;
    private Button regBtn;
    private EditText regPassConftext;
    private Button regLoginBtn;
    private ProgressBar regProgress;

    //FireBaseAuth class is a public abstract class FirebaseAuth extends Object
    // and first we will obtain an instance of this class by calling getInstance() which we will see later.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //We are stting the view to the specific layout when this Java class is called.
        // In this we are setting content view to layout activitylogin.xml
        setContentView(R.layout.activity_register);

        //Getting an instance of this class for authentication by calling getInstance() method,after calling this method we
        mAuth = FirebaseAuth.getInstance();

        //As said before we binded our private elements to the ids that are to be connected which are declaredin layout file
        regemailText = findViewById(R.id.reg_email);
        regPasstext = findViewById(R.id.reg_pass_btn);
        regPassConftext = findViewById(R.id.reg_confirm_password);
        regBtn = findViewById(R.id.reg_btn);
        regLoginBtn = findViewById(R.id.reg_login_btn);
        regProgress = findViewById(R.id.reg_progress);

        //we have set  on click listener on the button "regBtn", when it is clicked if the registration is successful it will send to AccountActivity
        // to make an account with photo and name else it will throws an error message according to the error that occured
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //we are taking the email , password and confPassowrd from the given three edit texts and storing them in the two
                // strings email,pass,passConf which are declared locally to check in the Firebase.
                String email = regemailText.getText().toString().trim();
                String pass = regPasstext.getText().toString().trim();
                String passConf = regPassConftext.getText().toString().trim();

                if(!emailVerify(email)){
                    Toast.makeText(RegisterActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();

                }else{

                    //checking if the email ,passs and congPass are not empty, if not empty then go on
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                        //Check if pass and re entered password are same
                        if (pass.equals(passConf)) {

                            //set the progressbar visible so that user can know that process in running
                            regProgress.setVisibility(View.VISIBLE);

                            //Authentication of FireBase provides with various kinds of methods for register like
                            //registerWithMailandPassword etc....here we used a method createUserWithEmailAndPassword which we
                            //passed email,pass inthat method and we add on complete listener if it is complete then do the following things

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                //On complete and there are two possibilities success or failure
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //If the task is sucessful then send the user to SetUpActivity using Explict intent
                                    if (task.isSuccessful()) {
                                        //Declared an Explict Intent from RegisterActivity to SetUpActivity
                                        Intent setUpIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                        //Started Explict Intent
                                        startActivity(setUpIntent);

                                        //finish() method is called because to finish the present layout so that when a user presses back button it will not exists
                                        //and it willl be finished
                                        finish();

                                    }
                                    //If the task is not sucessful then give a toast with the error message provided by the task
                                    else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    //After on completion make the progress bar invisible by writing the following code of line
                                    regProgress.setVisibility(View.INVISIBLE);

                                }
                            });


                        }
                        //If passwords do not match ,make a Toast howing that Passwords do not match
                        else {
                            Toast.makeText(RegisterActivity.this, "Passswords do not match", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

        // regLoginBtn is a button when clicks takes the user to loginActivity login by entering details
        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    //This is onStart method.This method will works as soon as this acticivty is started and check if
    //user is logged in and if the user is logged in,it will send to MainActivity otherwise it will send to RegisterActcivity
    @Override
    protected void onStart() {
        //FirebaseUser class is public abstract class FirebaseUser extends Object
        //implements Parcelable UserInfo which is used in helping to check if the user is logged in or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //If user is logged in then send him to the MianActivity
            sendTomain();
        }

        //super is called to restore the previous super class onStart()data
        super.onStart();
    }
    private boolean emailVerify(String email){
        int flag=0,f=0;
        for(int i=0;i<email.length();i++){
            if(email.charAt(i) == '@'){
                flag=1;
            } else if(email.charAt(i) == '.' && flag==1){
                flag=2;
            } else if((flag==2 && (int)email.charAt(i) >= 97 && (int)email.charAt(i) <= 122)){
                f=1;
            } else if(f==1){
                return false;
            }
        }
        if(f==1){
            return true;
        } else{
            return false;
        }
    }

    private void sendTomain() {
        //Declaration of Explicit Intent from LoginActivity to MainActicity
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        //Starting the mainIntent
        startActivity(mainIntent);

        //finish() method is called because to finish the present layout so that when a user presses back button it will exists
        // from app rather than showing Login layout if the user is logged in
        finish();

    }
}
