package com.example.data

object InitialData {
    fun getInitialProducts(): List<ProductEntity> = listOf(
        // Grains & Staples (Essential supplies)
        ProductEntity(
            name = "Aashirvaad Shudh Chakki Atta 5kg",
            category = "Grains & Staples",
            stockQuantity = 16.0,
            unit = "pkt",
            price = 245.0,
            costPrice = 215.0,
            lowStockThreshold = 5.0,
            isEssential = true,
            barcode = "890103001"
        ),
        ProductEntity(
            name = "India Gate Basmati Rice 1kg",
            category = "Grains & Staples",
            stockQuantity = 12.0,
            unit = "pkt",
            price = 135.0,
            costPrice = 112.0,
            lowStockThreshold = 5.0,
            isEssential = true,
            barcode = "890103002"
        ),
        ProductEntity(
            name = "Tata Salt Vacuum Evaporated 1kg",
            category = "Grains & Staples",
            stockQuantity = 28.0,
            unit = "pkt",
            price = 28.0,
            costPrice = 22.0,
            lowStockThreshold = 8.0,
            isEssential = true,
            barcode = "890103003"
        ),
        ProductEntity(
            name = "Madhur Pure Crystal Sugar 1kg",
            category = "Grains & Staples",
            stockQuantity = 22.0,
            unit = "kg",
            price = 52.0,
            costPrice = 43.0,
            lowStockThreshold = 6.0,
            isEssential = true,
            barcode = "890103004"
        ),
        ProductEntity(
            name = "Toor Dal Unpolished 1kg",
            category = "Grains & Staples",
            stockQuantity = 8.0,
            unit = "kg",
            price = 165.0,
            costPrice = 142.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103005"
        ),
        ProductEntity(
            name = "Moong Dal Dhuli 500g",
            category = "Grains & Staples",
            stockQuantity = 3.0, // Low stock demo!
            unit = "pkt",
            price = 85.0,
            costPrice = 70.0,
            lowStockThreshold = 5.0,
            isEssential = true,
            barcode = "890103006"
        ),

        // Dairy & Daily Fresh (Essential supplies)
        ProductEntity(
            name = "Amul Taaza Toned Milk 500ml",
            category = "Dairy & Fresh",
            stockQuantity = 4.0, // Low stock demo!
            unit = "pkt",
            price = 27.0,
            costPrice = 24.0,
            lowStockThreshold = 8.0,
            isEssential = true,
            barcode = "890103007"
        ),
        ProductEntity(
            name = "Mother Dairy Fresh Paneer 200g",
            category = "Dairy & Fresh",
            stockQuantity = 0.0, // Out of stock demo!
            unit = "pkt",
            price = 90.0,
            costPrice = 78.0,
            lowStockThreshold = 3.0,
            isEssential = true,
            barcode = "890103008"
        ),
        ProductEntity(
            name = "Amul Butter Pasteurised 100g",
            category = "Dairy & Fresh",
            stockQuantity = 10.0,
            unit = "pc",
            price = 58.0,
            costPrice = 51.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103009"
        ),
        ProductEntity(
            name = "Amul Masti Dahi 400g Cup",
            category = "Dairy & Fresh",
            stockQuantity = 7.0,
            unit = "cup",
            price = 35.0,
            costPrice = 30.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103010"
        ),

        // Cooking Oils & Ghee (Essential supplies)
        ProductEntity(
            name = "Fortune Kachi Ghani Mustard Oil 1L",
            category = "Oils & Ghee",
            stockQuantity = 14.0,
            unit = "L",
            price = 158.0,
            costPrice = 138.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103011"
        ),
        ProductEntity(
            name = "Fortune Sunlite Refined Oil 1L",
            category = "Oils & Ghee",
            stockQuantity = 12.0,
            unit = "L",
            price = 145.0,
            costPrice = 128.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103012"
        ),
        ProductEntity(
            name = "Amul Pure Ghee 1L Tin",
            category = "Oils & Ghee",
            stockQuantity = 5.0,
            unit = "tin",
            price = 640.0,
            costPrice = 580.0,
            lowStockThreshold = 2.0,
            isEssential = true,
            barcode = "890103013"
        ),

        // Beverages & Spices
        ProductEntity(
            name = "Tata Tea Gold 500g",
            category = "Beverages & Spices",
            stockQuantity = 9.0,
            unit = "pkt",
            price = 320.0,
            costPrice = 280.0,
            lowStockThreshold = 3.0,
            isEssential = true,
            barcode = "890103014"
        ),
        ProductEntity(
            name = "Catch Turmeric Powder 200g",
            category = "Beverages & Spices",
            stockQuantity = 11.0,
            unit = "pkt",
            price = 65.0,
            costPrice = 50.0,
            lowStockThreshold = 4.0,
            isEssential = true,
            barcode = "890103015"
        ),
        ProductEntity(
            name = "MDH Deggi Mirch 100g",
            category = "Beverages & Spices",
            stockQuantity = 15.0,
            unit = "pkt",
            price = 95.0,
            costPrice = 80.0,
            lowStockThreshold = 4.0,
            isEssential = false,
            barcode = "890103016"
        ),

        // Snacks & Biscuits
        ProductEntity(
            name = "Parle-G Gold Biscuits 1kg Family Pack",
            category = "Snacks & Biscuits",
            stockQuantity = 18.0,
            unit = "pkt",
            price = 120.0,
            costPrice = 100.0,
            lowStockThreshold = 5.0,
            isEssential = false,
            barcode = "890103017"
        ),
        ProductEntity(
            name = "Maggi 2-Minute Masala Noodles 4-Pack",
            category = "Snacks & Biscuits",
            stockQuantity = 22.0,
            unit = "pkt",
            price = 56.0,
            costPrice = 47.0,
            lowStockThreshold = 6.0,
            isEssential = false,
            barcode = "890103018"
        ),

        // Hygiene & Cleaning
        ProductEntity(
            name = "Dettol Original Soap (Pack of 3)",
            category = "Hygiene & Cleaning",
            stockQuantity = 8.0,
            unit = "pkt",
            price = 125.0,
            costPrice = 105.0,
            lowStockThreshold = 3.0,
            isEssential = true,
            barcode = "890103019"
        ),
        ProductEntity(
            name = "Surf Excel Easy Wash Detergent 1kg",
            category = "Hygiene & Cleaning",
            stockQuantity = 10.0,
            unit = "pkt",
            price = 145.0,
            costPrice = 125.0,
            lowStockThreshold = 3.0,
            isEssential = true,
            barcode = "890103020"
        ),
        ProductEntity(
            name = "Vim Dishwash Bar 300g",
            category = "Hygiene & Cleaning",
            stockQuantity = 25.0,
            unit = "pc",
            price = 30.0,
            costPrice = 24.0,
            lowStockThreshold = 5.0,
            isEssential = true,
            barcode = "890103021"
        )
    )

    fun getInitialTransactions(products: List<ProductEntity>): List<Pair<SaleTransaction, List<SaleItemEntity>>> {
        val now = System.currentTimeMillis()
        val oneHourAgo = now - 3600_000L
        val threeHoursAgo = now - 3 * 3600_000L

        val pAtta = products.find { it.name.contains("Atta") } ?: products.first()
        val pMilk = products.find { it.name.contains("Milk") } ?: products[1]
        val pSalt = products.find { it.name.contains("Salt") } ?: products[2]
        val pSugar = products.find { it.name.contains("Sugar") } ?: products[3]
        val pOil = products.find { it.name.contains("Mustard Oil") } ?: products[4]

        val t1 = SaleTransaction(
            id = 1,
            timestamp = threeHoursAgo,
            totalAmount = 299.0,
            totalProfit = 42.0,
            totalItemsCount = 3,
            paymentMethod = "UPI",
            customerName = "Ramesh Kumar",
            customerPhone = "9876543210",
            note = "Morning essentials"
        )
        val items1 = listOf(
            SaleItemEntity(
                id = 1,
                saleId = 1,
                productId = pAtta.id,
                productName = pAtta.name,
                quantity = 1.0,
                unit = pAtta.unit,
                unitPrice = pAtta.price,
                totalPrice = pAtta.price,
                profit = pAtta.price - pAtta.costPrice
            ),
            SaleItemEntity(
                id = 2,
                saleId = 1,
                productId = pMilk.id,
                productName = pMilk.name,
                quantity = 2.0,
                unit = pMilk.unit,
                unitPrice = pMilk.price,
                totalPrice = pMilk.price * 2,
                profit = (pMilk.price - pMilk.costPrice) * 2
            )
        )

        val t2 = SaleTransaction(
            id = 2,
            timestamp = oneHourAgo,
            totalAmount = 238.0,
            totalProfit = 35.0,
            totalItemsCount = 3,
            paymentMethod = "CASH",
            customerName = "Sunita Verma",
            customerPhone = "",
            note = "Daily grocery"
        )
        val items2 = listOf(
            SaleItemEntity(
                id = 3,
                saleId = 2,
                productId = pOil.id,
                productName = pOil.name,
                quantity = 1.0,
                unit = pOil.unit,
                unitPrice = pOil.price,
                totalPrice = pOil.price,
                profit = pOil.price - pOil.costPrice
            ),
            SaleItemEntity(
                id = 4,
                saleId = 2,
                productId = pSalt.id,
                productName = pSalt.name,
                quantity = 1.0,
                unit = pSalt.unit,
                unitPrice = pSalt.price,
                totalPrice = pSalt.price,
                profit = pSalt.price - pSalt.costPrice
            ),
            SaleItemEntity(
                id = 5,
                saleId = 2,
                productId = pSugar.id,
                productName = pSugar.name,
                quantity = 1.0,
                unit = pSugar.unit,
                unitPrice = pSugar.price,
                totalPrice = pSugar.price,
                profit = pSugar.price - pSugar.costPrice
            )
        )

        return listOf(Pair(t1, items1), Pair(t2, items2))
    }
}
