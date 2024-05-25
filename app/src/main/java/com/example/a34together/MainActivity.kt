package com.example.a34together

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.a34together.models.users
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    var btnsigin: Button? = null
    var btnreg: Button? = null
    var auth: FirebaseAuth? = null
    var db: FirebaseDatabase? = null
    var user: DatabaseReference? = null
    var root: RelativeLayout? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        btnreg = findViewById(R.id.btnreg)
        btnsigin = findViewById(R.id.btnsigin)
        root = findViewById(R.id.root_element)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        user = db!!.getReference("Users")
        btnreg!!.setOnClickListener { showRegisterWindow() }
        btnsigin!!.setOnClickListener { showSiginWindow() }
    }

    // вход
    private fun showSiginWindow() {
        val dialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        dialog.setTitle("Войти.")
        dialog.setMessage("Пожалуйста, заполните данные для входа.")
        val inflater = LayoutInflater.from(this)
        val sig_in_w: View = inflater.inflate(R.layout.sigin_window, null)
        dialog.setView(sig_in_w)
        val email = sig_in_w.findViewById<EditText>(R.id.email)
        val password = sig_in_w.findViewById<EditText>(R.id.passf)
        dialog.setNegativeButton("Отменить.",
            DialogInterface.OnClickListener { dialogInterface, which -> dialogInterface.dismiss() })
        dialog.setPositiveButton("Войти.",
            DialogInterface.OnClickListener { dialogInterface, which ->
                if (TextUtils.isEmpty(email.getText().toString())) {
                    root?.let { Snackbar.make(it, "Введите почту.", Snackbar.LENGTH_SHORT).show() }
                    return@OnClickListener
                }
                if (password.getText().toString().length < 8) {
                    root?.let { Snackbar.make(it, "Введите пароль.", Snackbar.LENGTH_SHORT).show() }
                    return@OnClickListener
                }
                auth?.signInWithEmailAndPassword(
                    email.getText().toString(),
                    password.getText().toString()
                )
                    ?.addOnSuccessListener(OnSuccessListener<Any?> {
                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                        finish()
                    })?.addOnFailureListener(OnFailureListener { e ->
                        Snackbar.make(
                            root!!,
                            "Ошибка авторизации." + e.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    })
            })
        dialog.show()
    }

    // регистрация
    private fun showRegisterWindow() {
        val dialog: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        dialog.setTitle("Зарегистрироваться.")
        dialog.setMessage("Пожалуйста, заполните данные.")
        val inflater = LayoutInflater.from(this)
        val reg_w: View = inflater.inflate(R.layout.register_window, null)
        dialog.setView(reg_w)
        val email = reg_w.findViewById<EditText>(R.id.email)
        val password = reg_w.findViewById<EditText>(R.id.passf)
        val name = reg_w.findViewById<EditText>(R.id.name)
        val phone = reg_w.findViewById<EditText>(R.id.phonef)
        dialog.setNegativeButton("Отменить.",
            DialogInterface.OnClickListener { dialogInterface, which -> dialogInterface.dismiss() })
        dialog.setPositiveButton("Добавить.",
            DialogInterface.OnClickListener { dialogInterface, which ->
                if (TextUtils.isEmpty(email.getText().toString())) {
                    root?.let { Snackbar.make(it, "Введите почту.", Snackbar.LENGTH_SHORT).show() }
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(root!!, "Введите имя.", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(root!!, "Введите телефон.", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                if (password.getText().toString().length < 8) {
                    Snackbar.make(root!!, "Введите пароль.", Snackbar.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                auth!!.createUserWithEmailAndPassword(
                    email.getText().toString(),
                    password.getText().toString()
                )
                    .addOnSuccessListener(OnSuccessListener<Any?> {
                        val Userr = users()
                        Userr.setEmail(email.getText().toString())
                        Userr.setName(name.getText().toString())
                        Userr.setPass(password.getText().toString())
                        Userr.setPhone(phone.getText().toString())
                        FirebaseAuth.getInstance().getCurrentUser()?.let { it1 ->
                            user?.child(it1.getUid())
                                ?.setValue(Userr)
                                ?.addOnSuccessListener(OnSuccessListener<Void?> {
                                    Snackbar.make(root!!, "Пользователь добавлен!", Snackbar.LENGTH_SHORT)
                                        .show()
                                })
                        }
                    })
            })
        dialog.show()
    }
}

