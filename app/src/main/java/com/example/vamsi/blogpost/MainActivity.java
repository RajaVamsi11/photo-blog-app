package com.example.vamsi.blogpost;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    //Declaration of Toolbar
    private Toolbar mainToolbar;
    //Declaration of FirebaseAuth class,which helps in Authentication
    private FirebaseAuth mAuth;
    //Declaration of FirebaseFirestore which helps in storing data and url of the images
    private FirebaseFirestore firebaseFirestore;
    //Declaration of current_user_id String
    private String current_user_id;

    //Declaration of Floating Action Button and Bottom Navigation Bar
    private FloatingActionButton addPostBtn;
    private BottomNavigationView mainBottomNav;

    //Fragments which are to be used to replace the default fragment in MainActivity
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting an instance of this class for authentication and Firebase storage
        // by calling getInstance() method,after calling this method we will be able to firebase features
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Binding toolbar to the xml layout and setting the toolbar
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        //Set the title of the action bar to the PhotoBlog
        getSupportActionBar().setTitle("Photo Blog");


        //If user is logged in and get the user details for further use and display all the posts.
        if(mAuth.getCurrentUser() != null) {
            //Bottom bar initialization and binding with the code that was written in xml with the help of id
            mainBottomNav = findViewById(R.id.mainBottomNav);
            //Add button Initialization
            addPostBtn = findViewById(R.id.add_post_btn);
            //Fragments Initialization
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();
            //Firstly on OnCreate() we will replace the fragment with homeFragment in MainActivity
            replaceFragment(homeFragment);


            //Bottom navigation bar items click listener,when user clicks an item in Bottom Navigation bar,
            //it will act according to the switch case condition used and binded by the ids of that particular item
            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true;
                        /*case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;*/
                        case R.id.bottom_action_account:
                            replaceFragment(accountFragment);
                            return true;
                        default:
                            return false;

                    }

                }
            });

            //When we click on post button(Floating Action Button) then it will send an Explict Intent to PostActicity
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Starts an Explict Intent
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                }
            });
        }

    }


    //This method will occur on start of the activity
    @Override
    protected void onStart() {
        super.onStart();
        //Initialization of FirebaseUser and getting the current user from firebase
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

        //If user is not logged in then send him to the zloginActivity
        if (currentuser == null) {
            sendToLogin();

        } else {
            //Retrieve the current user from firebase by id
            current_user_id = mAuth.getCurrentUser().getUid();
            //We are retriving the documents that in the Users collection and added onCompleteListener
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    //If the task is successful then send user to SetUpActivity to setUp the Account details
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            //Start thr Explict Intent to setUpActivity
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);

                        }
                    }
                    //Show the errors in the form of toasts
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error"+ error, Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }

    //This method is used to connect the main_menu.xml to the MainActicty by inflating that menu xml file
    //and will provide options which are declared in that menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //On options item click listener is used to act on the actions that in the main menu like logout and settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logOut();
                return true;
            case R.id.action_settings_btn:
                Intent settingIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingIntent);
                return true;
            default:
                return false;

        }

    }

    //This method is used to logOut and will send the user to LoginActivity by Explicit Intent
    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        //Declaration of explict Intent from MainActivity to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //Starting of the Intent
        startActivity(intent);
        finish();
    }

    //This methos is used to replace fragment by another fragment
    private void replaceFragment(Fragment fragment){

        //Initiaization and declaration of FragmentTransaction class and begin the transaction of fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Replace the fragment by given fragment which was passed as arguement
        fragmentTransaction.replace(R.id.main_content_fragment,fragment);
        //We must commit the transaction so that it can be worked properly
        fragmentTransaction.commit();

    }
}
