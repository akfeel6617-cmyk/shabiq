package com.example

enum class PosterSize(val displayName: String, val width: Int, val height: Int) {
    STANDARD_PORTRAIT("1080x1350", 1080, 1350),
    STORY_PORTRAIT("1080x1920", 1080, 1920),
    A4_PRINT("A4 Print", 2480, 3508)
}

enum class PosterLayoutStyle {
    CLASSIC,
    MODERN,
    ELEGANT,
    MINIMALIST,
    ROYAL_ORNATE
}

enum class OrnamentStyle {
    NONE,
    DIAMOND_BAR,
    FLORAL_CORNERS,
    GOLDEN_GATE,
    DOUBLE_BORDER
}

data class PosterTemplate(
    val id: Int,
    val name: String,
    val bgResId: Int?,
    val bgStartColor: Int,
    val bgEndColor: Int,
    val isBgGradient: Boolean,
    val textColor: Int,
    val accentColor: Int,
    val goldColor: Int = 0xFFFFD700.toInt(),
    val layoutStyle: PosterLayoutStyle = PosterLayoutStyle.CLASSIC,
    val ornamentStyle: OrnamentStyle = OrnamentStyle.DIAMOND_BAR,
    val category: String = "Classic"
)

object TemplateRegistry {
    val CATEGORIES = listOf("Green", "Gold", "Modern", "Classic", "Ramadan", "Friday", "Minimal")

    val templates: List<PosterTemplate> = buildList {
        // First 3 templates are the original static ones to preserve existing designs perfectly!
        add(
            PosterTemplate(
                id = 0,
                name = "Emerald Royal",
                bgResId = R.drawable.img_bg_emerald,
                bgStartColor = 0xFF0C5C43.toInt(),
                bgEndColor = 0xFF04291D.toInt(),
                isBgGradient = true,
                textColor = 0xFFFFFFFF.toInt(),
                accentColor = 0xFFFFD700.toInt(),
                layoutStyle = PosterLayoutStyle.CLASSIC,
                ornamentStyle = OrnamentStyle.DIAMOND_BAR,
                category = "Green"
            )
        )
        add(
            PosterTemplate(
                id = 1,
                name = "Midnight Indigo",
                bgResId = R.drawable.img_bg_indigo,
                bgStartColor = 0xFF0D47A1.toInt(),
                bgEndColor = 0xFF000F3A.toInt(),
                isBgGradient = true,
                textColor = 0xFFFFFFFF.toInt(),
                accentColor = 0xFFFFD700.toInt(),
                layoutStyle = PosterLayoutStyle.ELEGANT,
                ornamentStyle = OrnamentStyle.DOUBLE_BORDER,
                category = "Classic"
            )
        )
        add(
            PosterTemplate(
                id = 2,
                name = "Beige Minimalist",
                bgResId = R.drawable.img_bg_cream,
                bgStartColor = 0xFFFFFDF9.toInt(),
                bgEndColor = 0xFFF5ECE1.toInt(),
                isBgGradient = true,
                textColor = 0xFF1C2B25.toInt(),
                accentColor = 0xFF0C5C43.toInt(),
                layoutStyle = PosterLayoutStyle.MINIMALIST,
                ornamentStyle = OrnamentStyle.NONE,
                category = "Minimal"
            )
        )

        // Generate the remaining templates up to 1500
        for (i in 3 until 1500) {
            val category = CATEGORIES[i % CATEGORIES.size]
            val (bgStart, bgEnd) = when (category) {
                "Green" -> {
                    val greens = listOf(0xFF0C5C43.toInt(), 0xFF115E59.toInt(), 0xFF064E3B.toInt(), 0xFF0F766E.toInt(), 0xFF047857.toInt())
                    val c = greens[i % greens.size]
                    c to darkenColor(c)
                }
                "Gold" -> {
                    val golds = listOf(0xFF996515.toInt(), 0xFF804A00.toInt(), 0xFFCFB53B.toInt(), 0xFFB38F00.toInt(), 0xFFD4AF37.toInt())
                    val c = golds[i % golds.size]
                    c to darkenColor(c)
                }
                "Modern" -> {
                    val moderns = listOf(0xFF1A1A1A.toInt(), 0xFF2D3748.toInt(), 0xFF4A5568.toInt(), 0xFF0F172A.toInt(), 0xFF1E293B.toInt())
                    val c = moderns[i % moderns.size]
                    c to darkenColor(c)
                }
                "Classic" -> {
                    val classics = listOf(0xFF0D47A1.toInt(), 0xFF1A237E.toInt(), 0xFF880E4F.toInt(), 0xFF3E2723.toInt(), 0xFF311B92.toInt())
                    val c = classics[i % classics.size]
                    c to darkenColor(c)
                }
                "Ramadan" -> {
                    val ramadans = listOf(0xFF081C15.toInt(), 0xFF120C1F.toInt(), 0xFF0D1B2A.toInt(), 0xFF1F1A3A.toInt(), 0xFF03071E.toInt())
                    val c = ramadans[i % ramadans.size]
                    c to darkenColor(c)
                }
                "Friday" -> {
                    val fridays = listOf(0xFF004B23.toInt(), 0xFF006400.toInt(), 0xFFFFFDF9.toInt(), 0xFFFFFFFF.toInt(), 0xFFE2E8F0.toInt())
                    val c = fridays[i % fridays.size]
                    c to darkenColor(c)
                }
                "Minimal" -> {
                    val minimals = listOf(0xFFFFFDF9.toInt(), 0xFFF5ECE1.toInt(), 0xFFF8F9FA.toInt(), 0xFFECEFEF.toInt(), 0xFFE2E8F0.toInt())
                    val c = minimals[i % minimals.size]
                    c to darkenColor(c)
                }
                else -> 0xFF0C5C43.toInt() to 0xFF04291D.toInt()
            }

            val isDark = isColorDark(bgStart)
            val textColor = if (isDark) 0xFFFFFFFF.toInt() else 0xFF1C2B25.toInt()
            val accentColor = if (isDark) {
                val goldAccents = listOf(0xFFFFD700.toInt(), 0xFFCFB53B.toInt(), 0xFFE5C158.toInt(), 0xFFF7E7CE.toInt())
                goldAccents[i % goldAccents.size]
            } else {
                0xFF0C5C43.toInt()
            }

            val layoutStyle = PosterLayoutStyle.values()[i % PosterLayoutStyle.values().size]
            val ornamentStyle = OrnamentStyle.values()[i % OrnamentStyle.values().size]

            val name = when (category) {
                "Green" -> "زمردی وقار #${i}"
                "Gold" -> "زریں تاج #${i}"
                "Modern" -> "جدید نقش #${i}"
                "Classic" -> "روایتی جمال #${i}"
                "Ramadan" -> "رمضان مبارک #${i}"
                "Friday" -> "برکت جمعہ #${i}"
                "Minimal" -> "سادہ ڈیزائن #${i}"
                else -> "خوبصورت قالب #${i}"
            }

            add(
                PosterTemplate(
                    id = i,
                    name = name,
                    bgResId = null,
                    bgStartColor = bgStart,
                    bgEndColor = bgEnd,
                    isBgGradient = true,
                    textColor = textColor,
                    accentColor = accentColor,
                    goldColor = accentColor,
                    layoutStyle = layoutStyle,
                    ornamentStyle = ornamentStyle,
                    category = category
                )
            )
        }
    }

    private fun isColorDark(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val luminance = 0.299f * r + 0.587f * g + 0.114f * b
        return luminance < 140
    }

    private fun darkenColor(color: Int): Int {
        val a = (color shr 24) and 0xFF
        val r = (((color shr 16) and 0xFF) * 0.4f).toInt()
        val g = (((color shr 8) and 0xFF) * 0.4f).toInt()
        val b = ((color and 0xFF) * 0.4f).toInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
