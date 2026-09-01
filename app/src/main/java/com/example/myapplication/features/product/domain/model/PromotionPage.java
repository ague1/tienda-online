package com.example.myapplication.features.product.domain.model;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class PromotionPage {

    private final List<PromotionProduct> promotions;
    private final DocumentSnapshot lastDocument;
    private final boolean hasMore;

    public PromotionPage(
            List<PromotionProduct> promotions,
            DocumentSnapshot lastDocument,
            boolean hasMore
    ) {
        this.promotions = promotions;
        this.lastDocument = lastDocument;
        this.hasMore = hasMore;
    }

    public List<PromotionProduct> getPromotions() {
        return promotions;
    }

    public DocumentSnapshot getLastDocument() {
        return lastDocument;
    }

    public boolean hasMore() {
        return hasMore;
    }
}

