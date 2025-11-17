// net/sippory/presentation/dashboard/DashboardViewModel.kt
package net.sippory.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.sippory.data.dao.DashboardDao
import net.sippory.data.repository.DashboardRepository

class DashboardViewModel(
    repository: DashboardRepository,
) : ViewModel() {
    val typeRanking: StateFlow<List<DashboardDao.TypeRanking>> =
        repository.getTypeRanking()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val abvRanking: StateFlow<List<DashboardDao.AbvRanking>> =
        repository.getAbvRanking()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val averageRatingPerType: StateFlow<List<DashboardDao.TypeRating>> =
        repository.getAverageRatingPerType()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val mostConsumedBottleRanking: StateFlow<List<DashboardDao.BottleRanking>> =
        repository.getMostConsumedBottleRanking()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

class DashboardViewModelFactory(
    private val repository: DashboardRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(repository) as T
    }
}
