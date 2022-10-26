package com.example.imageupload.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.imageupload.repository.MyRepository
import com.example.imageupload.database.MyDatabase
import com.example.imageupload.model.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application): AndroidViewModel(application) {

    private val dao = MyDatabase.getDatabase(application).myDao()
    private val repository = MyRepository(dao)

    val readPerson: LiveData<List<Person>> = repository.readPerson

    fun insertPerson(person: Person){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertPerson(person)
        }
    }

}