package edu.mazer.resrec.viewmodels

import android.util.Log
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.childEvents
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import edu.mazer.resrec.model.Order
import edu.mazer.resrec.model.OrderWIthId
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class HomeViewModel : ViewModel() {
    val myAuth = FirebaseAuth.getInstance()
    val uid = myAuth.uid

    private val _currentOrders: MutableLiveData<MutableList<OrderWIthId>> =
        MutableLiveData(mutableListOf())
    val currentOrders: LiveData<MutableList<OrderWIthId>> = _currentOrders

    private val database =
        Firebase.database("https://kursovaya-5fdc1-default-rtdb.europe-west1.firebasedatabase.app/")
    val ordersRef = database.getReference("orders")
    val completeOrdersRef = database.getReference("completed")

    init {
        getOrdersFromDb()
    }

    fun signOut() {
        myAuth.signOut()
    }

    fun getOrdersFromDb() {
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allOrders = mutableListOf<OrderWIthId>()
                snapshot.children.forEach { retrievedOrder ->
                    val orderWithId = OrderWIthId(
                        retrievedOrder.key,
                        retrievedOrder.getValue<Order>()
                    )
                    allOrders.add(orderWithId)
                }
                _currentOrders.value = allOrders
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ReadFirestore", "Canceled read")
            }
        })
    }

    fun completeOrder(
        order: Order,
        id: String
    ) {
        val simpleDate = SimpleDateFormat("yyyy_MM_dd_HH_mm")
        val date = simpleDate.format(Date())
        completeOrdersRef.child(uid + date).setValue(order)
        Log.e("TAG", id)
        ordersRef.child(id).removeValue()
    }

}












