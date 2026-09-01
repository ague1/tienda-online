package com.example.myapplication.features.product.application.usecase;

import com.example.myapplication.features.product.domain.model.PromotionPage;
import com.example.myapplication.features.product.data.repository.PromotionRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import javax.inject.Inject;

public class GetPromotionUseCase {
    private final PromotionRepository repository;

    @Inject
    public GetPromotionUseCase(
            PromotionRepository repository
    ) {
        this.repository = repository;
    }

    public Task<PromotionPage> getFirstPage() {
        return repository.getFirstPage();
    }

    public Task<PromotionPage> getNextPage(
            DocumentSnapshot lastDocument
    ) {
        return repository.getNextPage(lastDocument);
    }
}
