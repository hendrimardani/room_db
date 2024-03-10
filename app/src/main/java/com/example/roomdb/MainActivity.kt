package com.example.roomdb

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdb.Databases.EmployeeApp
import com.example.roomdb.Databases.EmployeeDao
import com.example.roomdb.Databases.EmployeeEntity
import com.example.roomdb.databinding.ActivityMainBinding
import com.example.roomdb.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding.btnAdd.setOnClickListener {
            // TODO call add record with employeeDao
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Main Activity"
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addRecord(employeeDao: EmployeeDao) {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name=name, email=email))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()

                // Clear the text
                binding.etName.text!!.clear()
                binding.etEmail.text!!.clear()
            }
        } else {
            Toast.makeText(applicationContext, "Name or Email cannot be blank!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupListOfDataIntoRecyclerView(
        employeeList: ArrayList<EmployeeEntity>,
        employeeDao: EmployeeDao
    ) {
        if (employeeList.isNotEmpty()) {
            val mainAdapter = MainAdapter(
                employeeList,
                { updateId ->
                    updateRecordDialog(updateId, employeeDao) },
                { deleteId ->
                    deleteRecordDialog(deleteId, employeeDao) }
            )
            // Count item list
            val countItem = employeeList.size
            binding.tvTotal.text = "$countItem item"

            binding.rvItemList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.rvItemList.adapter = mainAdapter
            binding.tvNoRecords.visibility = View.INVISIBLE
            binding.tvAllTheInsert.visibility = View.VISIBLE
            binding.tvTotal.visibility = View.VISIBLE
            binding.rvItemList.visibility = View.VISIBLE
        } else {
            binding.tvNoRecords.visibility = View.VISIBLE
            binding.tvAllTheInsert.visibility = View.INVISIBLE
            binding.tvTotal.visibility = View.INVISIBLE
            binding.rvItemList.visibility = View.INVISIBLE
        }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val updateDialogCustom = Dialog(this)
        updateDialogCustom.setCancelable(true)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialogCustom.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                binding.etUpdateName.setText(it.name)
                binding.etUpdateEmail.setText(it.email)
            }
        }

        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))
                    Toast.makeText(this@MainActivity, "Record updated", Toast.LENGTH_LONG).show()
                    updateDialogCustom.dismiss()
                }
            } else {
                 Toast.makeText(this, "Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            updateDialogCustom.dismiss()
        }

        // Display dialog
        updateDialogCustom.show()
    }

    private fun deleteRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(R.drawable.ic_alert)

        builder.setPositiveButton("Yes") { dialogIntergace, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(
                    this@MainActivity, "Record deleted successfully", Toast.LENGTH_LONG)
                    .show()
            }
            dialogIntergace.dismiss()
        }

        builder.setNegativeButton("No") { dialogIntergace, _ ->
            dialogIntergace.dismiss()
        }

        // Create dialog
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}