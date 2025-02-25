package com.example.notes_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.notes_app.databinding.ActivityAddNoteBinding
import com.example.notes_app.databinding.ActivityMainBinding

class AddNoteActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)



        db= NotesDatabaseHelper(this)

        binding.saveButton.setOnClickListener {
            val title =binding.titleEditText.text.toString()
            val content= binding.contentEditText.text.toString()
            val note=Note(0,title,content)
            db.insertNote(note)
            finish()
            Toast.makeText(this,"Notes saved",Toast.LENGTH_SHORT).show()
        }
    }
}