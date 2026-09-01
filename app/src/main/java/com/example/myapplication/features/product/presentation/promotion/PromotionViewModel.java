package com.example.myapplication.features.product.presentation.promotion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

    private final MutableLiveData<List<PromotionProduct>> promotions =
            new MutableLiveData<>(new ArrayList<>());

    private DocumentSnapshot lastDocument;

    private boolean loading = false;

    private boolean hasMore = true;

    @Inject
    public PromotionViewModel(
            GetPromotionUseCase getPromotionUseCase
    ) {
        this.getPromotionUseCase = getPromotionUseCase;
    }

    public LiveData<List<PromotionProduct>> getPromotions() {
        return promotions;
    }

    public void loadFirstPage() {

        if (loading) {
            return;
        }

        loading = true;

        // Reiniciar completamente la paginación
        lastDocument = null;
        hasMore = true;

        getPromotionUseCase
                .getFirstPage()
                .addOnSuccessListener(page -> {

                    lastDocument =
                            page.getLastDocument();

                    hasMore =
                            page.hasMore();

                    promotions.setValue(
                            new ArrayList<>(
                                    page.getPromotions()
                            )
                    );

                    loading = false;
                })
                .addOnFailureListener(e ->

                    loading = false);
    }

    public void loadNextPage() {

        if (loading ||
                lastDocument == null ||
                !hasMore) {

            return;
        }

        loading = true;

        getPromotionUseCase
                .getNextPage(lastDocument)
                .addOnSuccessListener(page -> {

                    List<PromotionProduct> current =
                            promotions.getValue();

                    if (current == null) {
                        current = new ArrayList<>();
                    } else {
                        current = new ArrayList<>(current);
                    }

                    current.addAll(
                            page.getPromotions()
                    );

                    promotions.setValue(current);
                    lastDocument = page.getLastDocument();
                    hasMore = page.hasMore();
                    loading = false;
                })
                .addOnFailureListener(e -> loading = false);
    }
}

