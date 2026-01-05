package com.liuyuheng.a202304100228.myapplication.data.mock

import com.liuyuheng.a202304100228.myapplication.model.Recipe

object MockRecipeData {
    fun getRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                id = 1,
                name = "西红柿炒鸡蛋",
                description = "经典家常菜，酸甜可口，营养丰富",
                ingredients = listOf("西红柿", "鸡蛋", "盐", "糖", "葱", "油"),
                steps = listOf(
                    "西红柿洗净切块，鸡蛋打散",
                    "热锅凉油，下鸡蛋炒熟盛出",
                    "锅中放油，下葱花爆香",
                    "放入西红柿翻炒出汁",
                    "加入炒好的鸡蛋，加盐糖调味",
                    "翻炒均匀即可出锅"
                ),
                cookingTime = "15分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/tomato_egg.jpg",
                category = "家常菜"
            ),
            Recipe(
                id = 2,
                name = "宫保鸡丁",
                description = "川菜经典，麻辣鲜香，鸡肉嫩滑",
                ingredients = listOf("鸡胸肉", "花生米", "干辣椒", "花椒", "葱", "姜", "蒜", "生抽", "料酒", "淀粉", "糖", "醋", "盐"),
                steps = listOf(
                    "鸡胸肉切丁，用料酒、生抽、淀粉腌制",
                    "花生米炸酥备用",
                    "调制宫保汁：生抽、糖、醋、盐、淀粉",
                    "热锅凉油，下花椒、干辣椒爆香",
                    "放入鸡丁炒至变色",
                    "加入葱姜蒜炒香",
                    "倒入宫保汁炒匀",
                    "最后加入花生米即可"
                ),
                cookingTime = "25分钟",
                difficulty = "中等",
                imageUrl = "https://example.com/images/kungpao.jpg",
                category = "川菜"
            ),
            Recipe(
                id = 3,
                name = "红烧肉",
                description = "色泽红亮，肥而不腻，入口即化",
                ingredients = listOf("五花肉", "冰糖", "生抽", "老抽", "料酒", "葱", "姜", "八角", "桂皮", "香叶"),
                steps = listOf(
                    "五花肉切块，冷水下锅焯水",
                    "锅中放冰糖，小火炒糖色",
                    "放入五花肉翻炒上色",
                    "加入葱姜、八角、桂皮、香叶炒香",
                    "加入生抽、老抽、料酒",
                    "加开水没过肉，大火烧开转小火",
                    "炖煮1小时至软烂",
                    "大火收汁即可"
                ),
                cookingTime = "90分钟",
                difficulty = "中等",
                imageUrl = "https://example.com/images/braised_pork.jpg",
                category = "家常菜"
            ),
            Recipe(
                id = 4,
                name = "麻婆豆腐",
                description = "麻辣鲜香，豆腐嫩滑，下饭神器",
                ingredients = listOf("嫩豆腐", "牛肉末", "豆瓣酱", "花椒粉", "辣椒粉", "葱", "姜", "蒜", "生抽", "淀粉"),
                steps = listOf(
                    "豆腐切块，用盐水浸泡",
                    "热锅凉油，下牛肉末炒散",
                    "加入豆瓣酱炒出红油",
                    "加入葱姜蒜炒香",
                    "加适量水烧开",
                    "放入豆腐块，小火煮5分钟",
                    "用淀粉勾芡",
                    "撒花椒粉和葱花即可"
                ),
                cookingTime = "20分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/mapo_tofu.jpg",
                category = "川菜"
            ),
            Recipe(
                id = 5,
                name = "清蒸鱼",
                description = "鲜嫩清淡，保持鱼肉原味",
                ingredients = listOf("鲈鱼", "葱", "姜", "蒸鱼豉油", "料酒", "盐", "食用油"),
                steps = listOf(
                    "鲈鱼处理干净，在鱼身划几刀",
                    "用盐和料酒腌制10分钟",
                    "鱼身铺上姜片和葱段",
                    "水开后上锅蒸8-10分钟",
                    "倒掉蒸出的汤汁",
                    "铺上新鲜葱丝",
                    "淋上蒸鱼豉油",
                    "热油浇在葱丝上即可"
                ),
                cookingTime = "20分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/steamed_fish.jpg",
                category = "粤菜"
            ),
            Recipe(
                id = 6,
                name = "糖醋排骨",
                description = "酸甜可口，色泽红亮，老少皆宜",
                ingredients = listOf("排骨", "冰糖", "醋", "生抽", "料酒", "葱", "姜", "盐"),
                steps = listOf(
                    "排骨洗净焯水",
                    "锅中放油，下冰糖炒糖色",
                    "放入排骨翻炒上色",
                    "加入葱姜炒香",
                    "加生抽、料酒、醋",
                    "加开水没过排骨",
                    "大火烧开转小火炖40分钟",
                    "加盐调味，大火收汁即可"
                ),
                cookingTime = "60分钟",
                difficulty = "中等",
                imageUrl = "https://example.com/images/sweet_sour_ribs.jpg",
                category = "家常菜"
            ),
            Recipe(
                id = 7,
                name = "青椒肉丝",
                description = "清爽下饭，简单易做",
                ingredients = listOf("猪肉丝", "青椒", "葱", "姜", "生抽", "料酒", "淀粉", "盐"),
                steps = listOf(
                    "猪肉丝用料酒、生抽、淀粉腌制",
                    "青椒切丝",
                    "热锅凉油，下肉丝炒熟盛出",
                    "锅中放油，下葱姜爆香",
                    "放入青椒丝炒至断生",
                    "加入肉丝翻炒",
                    "加盐调味即可"
                ),
                cookingTime = "15分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/pepper_pork.jpg",
                category = "家常菜"
            ),
            Recipe(
                id = 8,
                name = "水煮鱼",
                description = "麻辣鲜香，鱼肉嫩滑，汤汁浓郁",
                ingredients = listOf("草鱼", "豆芽", "白菜", "干辣椒", "花椒", "豆瓣酱", "葱", "姜", "蒜", "料酒", "盐", "蛋清", "淀粉"),
                steps = listOf(
                    "鱼肉切片，用蛋清、淀粉、盐腌制",
                    "豆芽、白菜焯水铺在盆底",
                    "锅中放油，下豆瓣酱炒出红油",
                    "加入葱姜蒜、花椒、干辣椒炒香",
                    "加水烧开，放入鱼片煮熟",
                    "连汤带鱼倒入盆中",
                    "撒上辣椒粉和花椒",
                    "热油浇在辣椒粉上即可"
                ),
                cookingTime = "30分钟",
                difficulty = "困难",
                imageUrl = "https://example.com/images/boiled_fish.jpg",
                category = "川菜"
            ),
            Recipe(
                id = 9,
                name = "蒜蓉西兰花",
                description = "清爽健康，营养丰富",
                ingredients = listOf("西兰花", "大蒜", "盐", "蚝油", "食用油"),
                steps = listOf(
                    "西兰花切小朵，焯水备用",
                    "大蒜切末",
                    "热锅凉油，下蒜末爆香",
                    "放入西兰花翻炒",
                    "加盐和蚝油调味",
                    "翻炒均匀即可"
                ),
                cookingTime = "10分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/garlic_broccoli.jpg",
                category = "素菜"
            ),
            Recipe(
                id = 10,
                name = "蛋炒饭",
                description = "简单快手，粒粒分明",
                ingredients = listOf("米饭", "鸡蛋", "葱", "盐", "食用油"),
                steps = listOf(
                    "鸡蛋打散",
                    "热锅凉油，下鸡蛋炒熟盛出",
                    "锅中放油，下葱花爆香",
                    "放入米饭炒散",
                    "加入炒好的鸡蛋",
                    "加盐调味即可"
                ),
                cookingTime = "10分钟",
                difficulty = "简单",
                imageUrl = "https://example.com/images/egg_fried_rice.jpg",
                category = "主食"
            )
        )
    }
    
    fun getRecipesByIngredients(ingredients: List<String>): List<Recipe> {
        val allRecipes = getRecipes()
        return allRecipes.filter { recipe ->
            ingredients.any { ingredient ->
                recipe.ingredients.any { recipeIngredient ->
                    recipeIngredient.contains(ingredient, ignoreCase = true)
                }
            }
        }
    }
}
