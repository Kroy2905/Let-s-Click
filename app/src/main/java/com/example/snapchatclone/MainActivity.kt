package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    var emaild: EditText? = null
    var passcode:EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emaild = findViewById(R.id.emailET)
        passcode = findViewById(R.id.PassET)
        setTitle("Let's Click")
        if (mAuth.currentUser != null) {
            login()
        }
    }


    fun goclicked(view: View){ // button clicked

        //sing in existing user
        mAuth.signInWithEmailAndPassword(emaild?.text.toString(), passcode?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information


                    login()
                } else {
                    // If sign in fails, sign up the user.

                    mAuth.createUserWithEmailAndPassword(emaild?.text.toString(), passcode?.text.toString()).addOnCompleteListener(this){task ->
                        if(task.isSuccessful) {
                            FirebaseDatabase.getInstance().getReference().child("users")
                                .child(task.getResult()?.user!!.uid).child("email")
                                .setValue(emaild?.text.toString())

                            login()

                        }else{
                            Toast.makeText(this,"Login Failed. Try again!",Toast.LENGTH_LONG).show();
                        }

                    }

                }
            }




    }
    fun login(){  // login function
       val intent = Intent(this,PostLogin::class.java)
        startActivity(intent)

    }


    fun signin(){ // sign in function

    }


}