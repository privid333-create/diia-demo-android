package ua.gov.diia.mock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- КОЛЬОРИ В СТИЛІ «ДІЯ» ---
val DiiaBlue = Color(0xFF1B64F2)
val DiiaLightBlue = Color(0xFFEBF1FF)
val DiiaCardBg = Color(0xFFFFFFFF)
val DiiaBackground = Color(0xFFF3F5FA)
val DiiaTextDark = Color(0xFF131313)
val DiiaTextMuted = Color(0xFF757A84)
val DiiaGreen = Color(0xFF12B76A)

// --- МОДЕЛІ ДАНИХ ---
enum class DocType { PASSPORT, DRIVER_LICENSE, TAX_ID }

data class DocumentModel(
    val id: String,
    val type: DocType,
    val title: String,
    val fullName: String,
    val docNumber: String,
    val line1Title: String,
    val line1Value: String,
    val line2Title: String,
    val line2Value: String,
    val qrCodePayload: String
)

data class ServiceItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiiaTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun DiiaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = DiiaBlue,
            background = DiiaBackground,
            surface = Color.White
        ),
        content = content
    )
}

// --- ГОЛОВНИЙ ЕКРАН ТА НАВІГАЦІЯ ---
@Composable
fun MainAppScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.CreditCard else Icons.Outlined.CreditCard, "Документи") },
                    label = { Text("Документи", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = DiiaBlue, selectedTextColor = DiiaBlue, indicatorColor = DiiaLightBlue)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(if (selectedTab == 1) Icons.Filled.GridView else Icons.Outlined.GridView, "Послуги") },
                    label = { Text("Послуги", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = DiiaBlue, selectedTextColor = DiiaBlue, indicatorColor = DiiaLightBlue)
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(if (selectedTab == 2) Icons.Filled.Menu else Icons.Outlined.Menu, "Меню") },
                    label = { Text("Меню", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = DiiaBlue, selectedTextColor = DiiaBlue, indicatorColor = DiiaLightBlue)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DiiaBackground)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> DocumentsScreen()
                1 -> ServicesScreen()
                2 -> ProfileAndSettingsScreen()
            }
        }
    }
}

// --- 1. ЕКРАН ДОКУМЕНТІВ (ГОРИЗОНТАЛЬНИЙ ПЕЙДЖЕР) ---
@Composable
fun DocumentsScreen() {
    val documents = remember {
        listOf(
            DocumentModel(
                id = "1",
                type = DocType.PASSPORT,
                title = "Паспорт громадянина України",
                fullName = "ШЕВЧЕНКО ТАРАС ГРИГОРОВИЧ",
                docNumber = "003491823",
                line1Title = "Дата народження",
                line1Value = "09.03.1814",
                line2Title = "Дійсний до",
                line2Value = "Безстроково",
                qrCodePayload = "ID_PASS:UKR:003491823"
            ),
            DocumentModel(
                id = "2",
                type = DocType.DRIVER_LICENSE,
                title = "Посвідчення водія",
                fullName = "ШЕВЧЕНКО ТАРАС ГРИГОРОВИЧ",
                docNumber = "ВХА 948102",
                line1Title = "Категорії",
                line1Value = "B, B1, C",
                line2Title = "Дійсне до",
                line2Value = "24.08.2031",
                qrCodePayload = "DRV_LIC:UKR:ВХА948102"
            ),
            DocumentModel(
                id = "3",
                type = DocType.TAX_ID,
                title = "Картка платника податків (РНОКПП)",
                fullName = "ШЕВЧЕНКО ТАРАС ГРИГОРОВИЧ",
                docNumber = "3182904812",
                line1Title = "Дата народження",
                line1Value = "09.03.1814",
                line2Title = "Орган видачі",
                line2Value = "ГУ ДПС у м. Києві",
                qrCodePayload = "RNOKPP:3182904812"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { documents.size })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Шапка
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Дія",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DiiaTextDark
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Сканер QR", tint = DiiaBlue)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Пейджер карток
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            FlippableDocumentCard(document = documents[page])
        }

        // Індикатор сторінок
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(documents.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) DiiaBlue else Color.LightGray
                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                        .size(width = width, height = 8.dp)
                )
            }
        }
    }
}

// --- КАРТКА З 3D-ЕФЕКТОМ ПЕРЕВОРОТУ ---
@Composable
fun FlippableDocumentCard(document: DocumentModel) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "CardFlipAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { isFlipped = !isFlipped },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DiiaCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        if (rotation <= 90f) {
            // Лицьова сторона
            DocumentFrontSide(document = document, onQrClick = { isFlipped = true })
        } else {
            // Зворотна сторона (QR та перевірка)
            Box(Modifier.graphicsLayer { rotationY = 180f }) {
                DocumentBackSide(document = document, onBackClick = { isFlipped = false })
            }
        }
    }
}

@Composable
fun DocumentFrontSide(document: DocumentModel, onQrClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Заголовок картки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "УКРАЇНА",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiiaTextMuted,
                    letterSpacing = 2.sp
                )
                Text(
                    text = document.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiiaTextDark
                )
            }
            Icon(
                Icons.Default.VerifiedUser,
                contentDescription = "Державний герб",
                tint = DiiaBlue,
                modifier = Modifier.size(32.dp)
            )
        }

        // Фото + Основні дані
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Демо-фотографія
            Box(
                modifier = Modifier
                    .size(90.dp, 115.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3E8EF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = document.fullName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    color = DiiaTextDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "№ ${document.docNumber}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DiiaBlue
                )
            }
        }

        // Поля атрибутів
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = document.line1Title, fontSize = 11.sp, color = DiiaTextMuted)
                Text(text = document.line1Value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(text = document.line2Title, fontSize = 11.sp, color = DiiaTextMuted)
                Text(text = document.line2Value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Статус валідності + кнопка QR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(DiiaGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Документ дійсний", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DiiaGreen)
            }

            Button(
                onClick = onQrClick,
                colors = ButtonDefaults.buttonColors(containerColor = DiiaLightBlue),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null, tint = DiiaBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("QR-код", color = DiiaBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DocumentBackSide(document: DocumentModel, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Для перевірки дійсності",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DiiaTextDark
        )

        // Імітація великого QR-коду
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DiiaLightBlue)
                .border(2.dp, DiiaBlue.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.QrCode2,
                contentDescription = "QR Code",
                tint = DiiaBlue,
                modifier = Modifier.size(160.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Код дійсний 3:00 хв",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DiiaTextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = document.qrCodePayload,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        OutlinedButton(
            onClick = onBackClick,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Повернутися до документа", color = DiiaBlue)
        }
    }
}

// --- 2. ЕКРАН ПОСЛУГ ---
@Composable
fun ServicesScreen() {
    val services = listOf(
        ServiceItem("єВідновлення", "Подання заяви на допомогу", Icons.Default.Home),
        ServiceItem("Штрафи авто", "Перевірка та сплата", Icons.Default.DirectionsCar),
        ServiceItem("Податки ФОП", "Сплата єдиного податку та ЄСВ", Icons.Default.ReceiptLong),
        ServiceItem("Військові облігації", "Підтримка економіки", Icons.Default.Shield),
        ServiceItem("єПідтримка", "Державні виплати", Icons.Default.Favorite),
        ServiceItem("Довідка про доходи", "Отримання документа за хвилину", Icons.Default.Description)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Послуги",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DiiaTextDark
        )
        Text(
            text = "Державні послуги онлайн",
            fontSize = 14.sp,
            color = DiiaTextMuted,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        services.forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DiiaLightBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(service.icon, contentDescription = null, tint = DiiaBlue)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(service.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DiiaTextDark)
                        Text(service.subtitle, fontSize = 12.sp, color = DiiaTextMuted)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
    }
}

// --- 3. ЕКРАН ПРОФІЛЮ ТА НАЛАШТУВАНЬ ---
@Composable
fun ProfileAndSettingsScreen() {
    var biometricEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Меню",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DiiaTextDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Профіль користувача
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(DiiaBlue, Color(0xFF6C9EFF)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ТШ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Шевченко Тарас", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("РНОКПП: 3182904812", fontSize = 13.sp, color = DiiaTextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Безпека та налаштування", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DiiaTextMuted)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Вхід за біометрією (Face/Touch ID)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DiiaBlue)
                    )
                }

                HorizontalDivider(color = DiiaBackground)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push-сповіщення", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = DiiaBlue)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Про застосунок", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DiiaTextMuted)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Версія застосунку", fontSize = 14.sp, color = DiiaTextDark)
                    Text("4.12.0 (демо)", fontSize = 14.sp, color = DiiaTextMuted, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Підтримка та довідка", fontSize = 14.sp, color = DiiaTextDark)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Держава в смартфоні",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Medium
        )
    }
}
