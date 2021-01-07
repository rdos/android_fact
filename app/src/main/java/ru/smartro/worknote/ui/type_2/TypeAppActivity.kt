package ru.smartro.worknote.ui.type_2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_choose.*
import ru.smartro.worknote.R
import ru.smartro.worknote.adapter.TypeAppAdapter
import ru.smartro.worknote.service.body.TypeAppBody
import ru.smartro.worknote.ui.car_3.CarActivity

class TypeAppActivity : AppCompatActivity() {
    lateinit var adapter: TypeAppAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
        supportActionBar?.title = "Выберите тип приложения"
        val list = arrayListOf(
            TypeAppBody(0, "Инвентаризация"),
            TypeAppBody(0, "Факт")
        )
        adapter = TypeAppAdapter(list)
        choose_rv.adapter = adapter

        next_btn.setOnClickListener {
            startActivity(Intent(this, CarActivity::class.java))
        }

    }
}