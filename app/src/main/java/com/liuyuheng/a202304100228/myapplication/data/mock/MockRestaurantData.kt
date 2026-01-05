package com.liuyuheng.a202304100228.myapplication.data.mock

import com.liuyuheng.a202304100228.myapplication.model.Restaurant

object MockRestaurantData {
    fun getRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                id = 1,
                name = "川味轩",
                address = "临安区武肃街288号",
                rating = 4.5f,
                description = "正宗川菜，麻辣鲜香，环境优雅。招牌菜：水煮鱼、麻婆豆腐、宫保鸡丁。厨师来自四川，保证地道口味。",
                cuisineType = "川菜",
                priceRange = "人均80-120元",
                imageUrl = "https://example.com/images/sichuan.jpg",
                latitude = 30.241871,
                longitude = 119.732963
            ),
            Restaurant(
                id = 2,
                name = "日式拉面小屋",
                address = "临安区农林大路168号",
                rating = 4.2f,
                description = "正宗日式拉面，汤头浓郁，面条劲道。特色：豚骨拉面、味噌拉面、叉烧拉面。每日新鲜熬制汤底。",
                cuisineType = "日料",
                priceRange = "人均50-80元",
                imageUrl = "https://example.com/images/ramen.jpg",
                latitude = 30.239871,
                longitude = 119.731963
            ),
            Restaurant(
                id = 3,
                name = "韩式烤肉王",
                address = "临安区城中街356号",
                rating = 4.7f,
                description = "新鲜食材，现场烤制，韩式小菜免费。推荐：五花肉、牛排、石锅拌饭。服务热情，环境舒适。",
                cuisineType = "韩料",
                priceRange = "人均100-150元",
                imageUrl = "https://example.com/images/bbq.jpg",
                latitude = 30.240871,
                longitude = 119.728963
            ),
            Restaurant(
                id = 4,
                name = "意大利风情餐厅",
                address = "临安区锦北街道科技大道",
                rating = 4.3f,
                description = "正宗意大利披萨和意面，浪漫氛围。特色：玛格丽特披萨、肉酱意面、提拉米苏。适合约会聚餐。",
                cuisineType = "西餐",
                priceRange = "人均150-200元",
                imageUrl = "https://example.com/images/italian.jpg",
                latitude = 30.237871,
                longitude = 119.726963
            ),
            Restaurant(
                id = 5,
                name = "海底捞火锅",
                address = "临安区锦城街道科技大道",
                rating = 4.8f,
                description = "服务一流的火锅连锁店，食材新鲜。特色：番茄锅底、麻辣锅底、虾滑。提供免费美甲等服务。",
                cuisineType = "火锅",
                priceRange = "人均120-180元",
                imageUrl = "https://example.com/images/hotpot.jpg",
                latitude = 30.241871,
                longitude = 119.734963
            ),
            Restaurant(
                id = 6,
                name = "老北京炸酱面",
                address = "临安区城中街128号",
                rating = 4.4f,
                description = "百年老字号，正宗老北京炸酱面。面条劲道，酱香浓郁，配菜丰富。还有豆汁、焦圈等北京小吃。",
                cuisineType = "中餐",
                priceRange = "人均30-50元",
                imageUrl = "https://example.com/images/noodle.jpg",
                latitude = 30.238871,
                longitude = 119.729963
            ),
            Restaurant(
                id = 7,
                name = "樱花日料",
                address = "临安区农林大路256号",
                rating = 4.6f,
                description = "精致日料，新鲜刺身，寿司精致。特色：三文鱼刺身、鳗鱼寿司、天妇罗。环境优雅，适合商务宴请。",
                cuisineType = "日料",
                priceRange = "人均200-300元",
                imageUrl = "https://example.com/images/sushi.jpg",
                latitude = 30.235871,
                longitude = 119.733963
            ),
            Restaurant(
                id = 8,
                name = "草原烧烤",
                address = "临安区科技大道128号",
                rating = 4.3f,
                description = "内蒙古风味烧烤，肉质鲜美，烤制专业。推荐：羊肉串、烤羊腿、烤鸡翅。分量足，价格实惠。",
                cuisineType = "烧烤",
                priceRange = "人均60-90元",
                imageUrl = "https://example.com/images/grill.jpg",
                latitude = 30.242871,
                longitude = 119.727963
            ),
            Restaurant(
                id = 9,
                name = "粤菜轩",
                address = "临安区锦城街道人民路",
                rating = 4.5f,
                description = "精致粤菜，清淡健康。特色：白切鸡、清蒸鱼、虾饺皇。食材新鲜，制作精细。",
                cuisineType = "粤菜",
                priceRange = "人均100-150元",
                imageUrl = "https://example.com/images/cantonese.jpg",
                latitude = 30.236871,
                longitude = 119.730963
            ),
            Restaurant(
                id = 10,
                name = "甜蜜时光甜品店",
                address = "临安区城中街168号",
                rating = 4.9f,
                description = "精致甜品，蛋糕香甜，冰淇淋丝滑。特色：草莓蛋糕、抹茶冰淇淋、提拉米苏。环境浪漫，适合约会。",
                cuisineType = "甜品",
                priceRange = "人均40-80元",
                imageUrl = "https://example.com/images/dessert.jpg",
                latitude = 30.239871,
                longitude = 119.731963
            )
        )
    }
}
