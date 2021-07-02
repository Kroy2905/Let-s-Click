package com.example.snapchatclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue


class PostLogin : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    var snapListview: ListView? = null
    var emails:ArrayList<String> = ArrayList()
    var snaps:ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_login)
        setTitle("Inbox")
        snapListview = findViewById(R.id.snapLV)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapListview?.adapter = adapter
      //  val currentU = FirebaseAuth.getInstance().currentUser
       // val uId = currentU!!.uid
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.currentUser!!.uid)
            .child("snaps").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {  // WE are inside the database
                    emails.add(snapshot.child("from").value as String)
                    snaps.add(snapshot)
                    adapter.notifyDataSetChanged()
                    Log.i("VALUE OF FROM",snapshot.child("from").value as String)


                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    var i = 0
                    for(snap:DataSnapshot in snaps){
                        if(snap.key == snapshot.key){
                            snaps.removeAt(i)
                            emails.removeAt(i)

                        }
                        i++
                    }
                    adapter.notifyDataSetChanged()


                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {}


            })
        snapListview?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapshot = snaps.get(position)
            val intent = Intent(this,ViewSnap::class.java)  // going to view snap ...............................


            intent.putExtra("imagename", snapshot.child("imagename").value as String)
            intent.putExtra("imageurl",snapshot.child("imageurl").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapkey",snapshot.key)
            startActivity(intent)


        }


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.snaps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.createSnap){
        val intent = Intent(this, CreateSnapActivity::class.java)
            startActivity(intent)
        }else{
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mAuth.signOut()
    }
}