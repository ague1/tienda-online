package com.example.myapplication.features.product.presentation.promotion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.product.domain.model.PromotionProduct;
import com.example.myapplication.features.product.application.usecase.GetPromotionUseCase;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PromotionViewModel extends ViewModel {

    private final GetPromotionUseCase getPromotionUseCase;

    private final MutableLiveData<UiState<List<PromotionProduct>>> promotionsState =
            new MutableLiveData<>(UiState.idle());

    private DocumentSnapshot lastDocument;
    private boolean loading = false;
    private boolean hasMore = true;

    @Inject
    public PromotionViewModel(
            GetPromotionUseCase getPromotionUseCase
    ) {
        this.getPromotionUseCase = getPromotionUseCase;
    }

    public LiveData<UiState<List<PromotionProduct>>> getPromotionsState() {
        return promotionsState;
    }

    public void loadFirstPage() {

        if (loading) {
            return;
        }

        loading = true;
        lastDocument = null;
        hasMore = true;

        promotionsState.setValue(
                UiState.loading()
        );

        getPromotionUseCase
                .getFirstPage()
                .addOnSuccessListener(page -> {

                    List<PromotionProduct> promotions =
                            new ArrayList<>(page.getPromotions());

                    promotionsState.setValue(
                            UiState.success(promotions)
                    );

                    lastDocument = page.getLastDocument();
                    hasMore = page.hasMore();
                    loading = false;
                })
                .addOnFailureListener(e -> {

                    loading = false;

                    promotionsState.setValue(
                            UiState.error("PROMOTIONS_LOAD_ERROR")
                    );
                });
    }

    public void loadNextPage() {

        if (loading
                || lastDocument == null
                || !hasMore) {
            return;
        }

        loading = true;

        getPromotionUseCase
                .getNextPage(lastDocument)
                .addOnSuccessListener(page -> {

                    List<PromotionProduct> current =
                            promotionsState.getValue() != null
                                    ? promotionsState
                                    .getValue()
                                    .getData()
                                    : null;

                    List<PromotionProduct> updated;

                    if (current == null) {
                        updated = new ArrayList<>();
                    } else {
                        updated = new ArrayList<>(current);
                    }

                    updated.addAll(page.getPromotions());

                    promotionsState.setValue(
                            UiState.success(updated)
                    );

                    lastDocument = page.getLastDocument();
                    hasMore = page.hasMore();
                    loading = false;
                })
                .addOnFailureListener(e ->
                        loading = false
                );
    }
}