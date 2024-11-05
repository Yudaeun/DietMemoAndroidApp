package com.pro.diet_memo

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerDialog
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import org.checkerframework.checker.units.qual.Length
import java.util.Calendar
import java.util.GregorianCalendar

class MainActivity : AppCompatActivity() {

    val dataModelList= mutableListOf<DataModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database
        val myRef = database.getReference("myMemo")
        val listView=findViewById<ListView>(R.id.mainLV)
        val adapterList=ListViewAdapter(dataModelList)

        listView.adapter = adapterList

        myRef.child(Firebase.auth.currentUser!!.uid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dataModelList.clear()

                for(dataModel in snapshot.children){
                    Log.d("Data",dataModel.toString())
                    dataModelList.add(dataModel.getValue(DataModel::class.java)!!)
                }
                adapterList.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val writeBtn = findViewById<FloatingActionButton>(R.id.writeBtn)
        writeBtn.setOnClickListener {
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
            val mBuilder=AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("운동 메모 다이얼로그")

            val mAlertDialog = mBuilder.show()
            val DateSelectBtn=mAlertDialog.findViewById<Button>(R.id.dateSelectBtn)
            var dateText = ""

            DateSelectBtn?.setOnClickListener {

                val today = GregorianCalendar()
                val year: Int = today.get(Calendar.YEAR)
                val month: Int = today.get(Calendar.MONTH)
                val day: Int = today.get(Calendar.DATE)

                val dlg= DatePickerDialog(this,object:DatePickerDialog.OnDateSetListener{
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
                        Log.d("MAIN","${year}, ${month+1}, ${day}")
                        DateSelectBtn.setText("${year}. ${month+1}. ${day}")

                        dateText="${year}. ${month+1}. ${day}"
                    }

                }, year, month, day)

                dlg.show()
            }

            val saveBtn=mAlertDialog.findViewById<Button>(R.id.saveBtn)
            saveBtn?.setOnClickListener {
                val healthMemo=mAlertDialog.findViewById<EditText>(R.id.healthMemoTxt)?.text.toString()
                val database = Firebase.database
                val myRef = database.getReference("myMemo").child(Firebase.auth.currentUser!!.uid)
                val model=DataModel(dateText,healthMemo)

                myRef.push().setValue(model)

                Toast.makeText(this,"메모가 저장되었습니다.",Toast.LENGTH_SHORT).show()

                mAlertDialog.dismiss()
            }

        }

    }

}