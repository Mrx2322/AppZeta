package com.example.appzetar.Menu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appzetar.R

class ActivityMenu : AppCompatActivity() {

    private val entradas = listOf(
        TaskEntradas.ceviche,
        TaskEntradas.huancaina,
        TaskEntradas.otros
    )

    private lateinit var  rvEntradas : RecyclerView
    private lateinit var entradasAdapter: EntradasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponent()
        initUI()
    }

    private fun initComponent() {
        rvEntradas = findViewById(R.id.rvEntradas)
    }
    private fun initUI(){
        entradasAdapter = EntradasAdapter(entradas)
        rvEntradas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvEntradas.adapter = entradasAdapter
    }
}