package com.example.astrohandbook

data class PlanetInfo(
    val name: String,
    val imageResId: Int,           // Картинка планеты
    val description: String,        // Полное описание
    val diameter: String,           // Диаметр
    val distanceFromSun: String,    // Расстояние от Солнца
    val orbitalPeriod: String,      // Период обращения
    val temperature: String,        // Температура
    val moons: String,              // Спутники
    val atmosphere: String          // Атмосфера
) {
    companion object {
        fun getInfoForPlanet(planetName: String): PlanetInfo {
            return when (planetName) {
                "Солнце" -> PlanetInfo(
                    name = "Солнце",
                    imageResId = R.drawable.sun,
                    description = "Солнце — звезда, единственная в Солнечной системе. Вокруг Солнца обращаются другие объекты этой системы: планеты и их спутники, карликовые планеты и их спутники, астероиды, метеороиды, кометы и космическая пыль.",
                    diameter = "1 392 700 км",
                    distanceFromSun = "0 км (центр системы)",
                    orbitalPeriod = "225-250 млн лет (обращение вокруг центра Галактики)",
                    temperature = "5 500°C (поверхность), 15 млн°C (ядро)",
                    moons = "8 планет, 5 карликовых планет, множество малых тел",
                    atmosphere = "Водород (73%), Гелий (25%)"
                )
                "Меркурий" -> PlanetInfo(
                    name = "Меркурий",
                    imageResId = R.drawable.mercury,
                    description = "Меркурий — ближайшая к Солнцу планета. Названа в честь древнеримского бога торговли. Это самая маленькая планета земной группы.",
                    diameter = "4 879 км",
                    distanceFromSun = "57.9 млн км",
                    orbitalPeriod = "88 дней",
                    temperature = "от -173°C до 427°C",
                    moons = "нет",
                    atmosphere = "Кислород, Натрий, Водород, Гелий, Калий (очень разреженная)"
                )
                "Венера" -> PlanetInfo(
                    name = "Венера",
                    imageResId = R.drawable.venus,
                    description = "Венера — вторая по удалённости от Солнца планета. Названа в честь древнеримской богини любви. Это самая горячая планета Солнечной системы.",
                    diameter = "12 104 км",
                    distanceFromSun = "108.2 млн км",
                    orbitalPeriod = "225 дней",
                    temperature = "462°C (средняя)",
                    moons = "нет",
                    atmosphere = "Углекислый газ (96.5%), Азот (3.5%)"
                )
                "Земля" -> PlanetInfo(
                    name = "Земля",
                    imageResId = R.drawable.earth,
                    description = "Земля — третья от Солнца планета. Единственное известное тело во Вселенной, населённое живыми существами. 70% поверхности покрыто водой.",
                    diameter = "12 742 км",
                    distanceFromSun = "149.6 млн км",
                    orbitalPeriod = "365.25 дней",
                    temperature = "от -89°C до 58°C",
                    moons = "1 (Луна)",
                    atmosphere = "Азот (78%), Кислород (21%), Аргон (0.9%)"
                )
                "Марс" -> PlanetInfo(
                    name = "Марс",
                    imageResId = R.drawable.mars,
                    description = "Марс — четвёртая по удалённости от Солнца планета. Названа в честь древнеримского бога войны. Имеет красный цвет из-за оксида железа на поверхности.",
                    diameter = "6 779 км",
                    distanceFromSun = "227.9 млн км",
                    orbitalPeriod = "687 дней",
                    temperature = "от -153°C до 20°C",
                    moons = "2 (Фобос и Деймос)",
                    atmosphere = "Углекислый газ (95%), Азот (3%), Аргон (1.6%)"
                )
                else -> PlanetInfo(
                    name = planetName,
                    imageResId = R.drawable.earth,
                    description = "Информация о планете отсутствует",
                    diameter = "неизвестно",
                    distanceFromSun = "неизвестно",
                    orbitalPeriod = "неизвестно",
                    temperature = "неизвестно",
                    moons = "неизвестно",
                    atmosphere = "неизвестно"
                )
            }
        }
    }
}