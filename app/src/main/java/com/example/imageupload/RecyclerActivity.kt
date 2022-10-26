package com.example.imageupload

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.imageupload.adapter.MyAdapter
import com.example.imageupload.databinding.ActivityMainBinding
import com.example.imageupload.databinding.ActivityRecyclerBinding
import com.example.imageupload.model.Person
import com.example.imageupload.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_recycler.*
import kotlinx.coroutines.launch

class RecyclerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var myViewModel : MyViewModel
    private val adapter by lazy { MyAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val person = Person("Gian", "Felipe",getBitmap())
            myViewModel.insertPerson(person)
        }

        myViewModel.readPerson.observe(this) {
            adapter.setData(it)
        }
    }

    private suspend fun getBitmap(): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest
            .Builder(this)
            .data(("https://avatars3.githubusercontent.com/u/14994036?s=400&u=2832879700f03d4b37ae1c09645352a352b9d2d0&v=4"))
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }
}