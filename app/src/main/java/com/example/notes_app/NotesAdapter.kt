package com.example.notes_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private var notes: List<Note>, context: Context) : RecyclerView.Adapter<NotesAdapter.NoteView>() {


    private val db: NotesDatabaseHelper = NotesDatabaseHelper(context)


    class NoteView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteView {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteView(view)
    }

    override fun getItemCount(): Int {
    return notes.size
    }

    override fun onBindViewHolder(holder: NoteView, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contextView.text = note.content
        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteNote(note.id)
            refreshData(db.getNotes())
            Toast.makeText(holder.itemView.context,"Note Deleted Succesfully",Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newNotes: List<Note>){
        notes = newNotes
        notifyDataSetChanged()
    }
}