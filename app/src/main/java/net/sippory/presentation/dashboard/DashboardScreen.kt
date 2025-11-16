// net/sippory/presentation/dashboard/DashboardScreen.kt
package net.sippory.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import net.sippory.data.AppDatabase
import net.sippory.data.repository.DashboardRepository

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel? = null,        // 외부 주입 가능하게 옵션
    onBack: (() -> Unit)? = null
) {
    val actualVm = viewModel ?: run {
        val context = LocalContext.current
        val db = remember { AppDatabase.getDatabase(context) }       // DB 싱글톤
        val repo = remember { DashboardRepository(db.dashboardDao()) } // DAO → Repo
        viewModel(factory = DashboardViewModelFactory(repo))          // Repo → VM
    }

    val typeRanking by actualVm.typeRanking.collectAsState()
    val abvRanking by actualVm.abvRanking.collectAsState()
    val averageRatingPerType by actualVm.averageRatingPerType.collectAsState()
    val mostConsumed by actualVm.mostConsumedBottleRanking.collectAsState()

    LazyColumn(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            RankingSection(
                title = "내가 주로 마시는 주종 랭킹",
                items = typeRanking.map { bottle -> "${bottle.type} (${bottle.count}회)" }
            )
        }
        item {
            RankingSection(
                title = "자주 마신 도수 랭킹",
                items = abvRanking.map { bottle -> "${bottle.abv}% (${bottle.count}회)" }
            )
        }
        item {
            RankingSection(
                title = "주종별 평균 평점 랭킹",
                items = averageRatingPerType.map { bottle -> "${bottle.type} (평균 ${String.format("%.1f", bottle.averageRating)}점)" }
            )
        }
        item {
            RankingSection(
                title = "가장 많이 마신 술 랭킹",
                items = mostConsumed.map { bottle -> "${bottle.name} (${bottle.count}회)" }
            )
        }
        if (onBack != null) {
            item {
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("뒤로가기")
                }
            }
        }
    }
}

@Composable
private fun RankingSection(
    title: String,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (items.isEmpty()) {
                Text("아직 데이터가 없습니다.", color = Color.Gray)
            } else {
                items.forEachIndexed { idx, s ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("${idx + 1}.", fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
                        Text(s)
                    }
                }
            }
        }
    }
}
