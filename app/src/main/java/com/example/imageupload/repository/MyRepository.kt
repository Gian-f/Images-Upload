package com.example.imageupload.repository

import androidx.lifecycle.LiveData
import com.example.imageupload.dao.MyDao
import com.example.imageupload.model.Person


class MyRepository(private val myDao: MyDao) {

    val readPerson: LiveData<List<Person>> = myDao.readPerson()

    suspend fun insertPerson(person: Person){
        myDao.insertPerson(person)
    }

}