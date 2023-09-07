package com.example.landtech.presentation.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.landtech.data.database.models.Order

class OrdersViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<Order>>()
    val orders get() = _orders

    init {
        val orderList = listOf(
            Order(
                number = "AC-00000001",
                date = "05.04.2023",
                client = "John Doe",
                workType = "Обслуживание",
                engineer = "Harry Maguire",
                startDate = "05.04.2023 10:23:32",
                machine = "Автомобиль",
                sn = "000010101010",
                ln = "232323",
                en = "121232323",
                driveTime = 123.0,
                typeOrder = "Ремонт с запчастями",
                status = "Новый"
            ),
            Order(
                number = "AC-00000002",
                date = "06.04.2023",
                client = "Barack Obama",
                workType = "Ремонт",
                engineer = "Katie Perry",
                startDate = "05.04.2023 15:23:32",
                machine = "Мотоцикл",
                sn = "60710101010",
                ln = "54232323",
                en = "4332323",
                driveTime = 323.0,
                typeOrder = "Произвольное обслуживание",
                status = "Новый"
            ),
            Order(
                number = "AC-00000003",
                date = "07.04.2023",
                client = "Michelle Platini",
                typeOrder = "Ремонт с запчастями",
                status = "Новый"
            ),
            Order(
                number = "AC-00000004",
                date = "08.04.2023",
                client = "Zinedine Zidane",
                typeOrder = "Ремонт с запчастями",
                status = "Новый"
            ),
            Order(
                number = "AC-00000005", date = "09.04.2023", client = "Rustam Kasimdjanov",
                typeOrder = "Ремонт с запчастями",
                status = "Новый"
            ),
            Order(
                number = "AC-00000006", date = "10.04.2023", client = "Mesut Ozil",
                typeOrder = "Произвольное обслуживание",
                status = "Новый"
            ),
            Order(
                number = "AC-00000007", date = "11.04.2023", client = "Andreas Iniesta",
                typeOrder = "Произвольное обслуживание",
                status = "Новый"
            ),
            Order(number = "AC-00000001", date = "05.04.2023", client = "John Doe"),
            Order(number = "AC-00000002", date = "06.04.2023", client = "Barack Obama"),
            Order(number = "AC-00000003", date = "07.04.2023", client = "Michelle Platini"),
            Order(number = "AC-00000004", date = "08.04.2023", client = "Zinedine Zidane"),
            Order(number = "AC-00000005", date = "09.04.2023", client = "Rustam Kasimdjanov"),
            Order(number = "AC-00000006", date = "10.04.2023", client = "Mesut Ozil"),
            Order(number = "AC-00000007", date = "11.04.2023", client = "Andreas Iniesta"),
            Order(number = "AC-00000001", date = "05.04.2023", client = "John Doe"),
            Order(number = "AC-00000002", date = "06.04.2023", client = "Barack Obama"),
            Order(number = "AC-00000003", date = "07.04.2023", client = "Michelle Platini"),
            Order(number = "AC-00000004", date = "08.04.2023", client = "Zinedine Zidane"),
            Order(number = "AC-00000005", date = "09.04.2023", client = "Rustam Kasimdjanov"),
            Order(number = "AC-00000006", date = "10.04.2023", client = "Mesut Ozil"),
            Order(number = "AC-00000007", date = "11.04.2023", client = "Andreas Iniesta"),
        )

        _orders.value = orderList
    }
}