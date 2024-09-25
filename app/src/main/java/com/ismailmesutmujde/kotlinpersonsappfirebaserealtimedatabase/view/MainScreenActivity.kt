package com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.R
import com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.adapter.PersonsRecyclerViewAdapter
import com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.databinding.ActivityMainScreenBinding
import com.ismailmesutmujde.kotlinpersonsappfirebaserealtimedatabase.model.Persons

class MainScreenActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var bindingMainScreen : ActivityMainScreenBinding

    private lateinit var personsList : ArrayList<Persons>
    private lateinit var adapterPersons : PersonsRecyclerViewAdapter
    private lateinit var refPersons : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainScreen = ActivityMainScreenBinding.inflate(layoutInflater)
        val view = bindingMainScreen.root
        setContentView(view)

        bindingMainScreen.toolbar.title = "Persons Application"
        setSupportActionBar(bindingMainScreen.toolbar)

        bindingMainScreen.recyclerView.setHasFixedSize(true)
        bindingMainScreen.recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseDatabase.getInstance()
        refPersons = db.getReference("persons")

        personsList = ArrayList()

        /*
        val p1 = Persons(1,"Ahmet", "888888")
        val p2 = Persons(2,"Zeynep", "666666")
        val p3 = Persons(3,"Ece", "333333")

        personsList.add(p1)
        personsList.add(p2)
        personsList.add(p3)
        */

        adapterPersons = PersonsRecyclerViewAdapter(this, personsList, refPersons)
        bindingMainScreen.recyclerView.adapter = adapterPersons

        allPersons()

        bindingMainScreen.fab.setOnClickListener {
            showAlert()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    fun showAlert() {
        val design = LayoutInflater.from(this).inflate(R.layout.alert_design, null)
        val editTextPersonName = design.findViewById(R.id.editTextPersonName) as EditText
        val editTextPersonPhone = design.findViewById(R.id.editTextPersonPhone) as EditText

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Add Person")
        alertDialog.setView(design)
        alertDialog.setPositiveButton("Add") { dialogInterface, i ->
            val person_name = editTextPersonName.text.toString().trim()
            val person_phone = editTextPersonPhone.text.toString().trim()

            val person = Persons("", person_name, person_phone)
            refPersons.push().setValue(person)

            Toast.makeText(applicationContext, "${person_name} - ${person_phone}", Toast.LENGTH_SHORT).show()

        }
        alertDialog.setNegativeButton("Cancel") { dialogInterface, i ->


        }
        alertDialog.show()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchPerson(query)
        Log.e("Sent Search", query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        searchPerson(newText)
        Log.e("As Letters Enter", newText)
        return true
    }

    fun allPersons() {
        refPersons.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                personsList.clear()

                for(c in snapshot.children) {
                    val person = c.getValue(Persons::class.java)
                    if(person != null) {
                        person.person_id = c.key
                        personsList.add(person)
                    }
                }
                adapterPersons.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun searchPerson(searchingWord:String) {
        refPersons.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                personsList.clear()

                for(c in snapshot.children) {
                    val person = c.getValue(Persons::class.java)
                    if(person != null) {
                        if (person.person_name!!.contains(searchingWord)){
                            person.person_id = c.key
                            personsList.add(person)
                        }
                    }
                }
                adapterPersons.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}