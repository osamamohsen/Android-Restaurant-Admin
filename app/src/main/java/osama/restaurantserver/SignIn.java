package osama.restaurantserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import osama.restaurantserver.Common.Common;
import Models.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSignInSubmit;

    //Init DataBase
    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Init fields
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSignInSubmit = (Button) findViewById(R.id.btnSignInSubmit);


        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");

        //Submit Login
        btnSignInSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });
    }

    //Check User Exist
    private void signInUser(final String phone, String password) {
        final ProgressDialog dialog = new ProgressDialog(SignIn.this);
        dialog.setMessage("Please waiting ....");
        dialog.show();
        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                if(dataSnapshot.child(localPhone).exists()){
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    if(Boolean.parseBoolean(user.getIsStaff())){
                        if(user.getPassword().equals(localPassword)){
                            Toast.makeText(SignIn.this, "Sign in Successfully", Toast.LENGTH_SHORT).show();
                            Common.current_user = user;
                            Intent intentHome = new Intent(SignIn.this,Home.class);
                            startActivity(intentHome);
                            finish();
                        }
                        else
                            Toast.makeText(SignIn.this, "wrong in password", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(SignIn.this, "Please login with staff account", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignIn.this, "User not exist", Toast.LENGTH_SHORT).show();
                }
            }//end onDataCange

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
