package com.example.myapplication.features.order.domain.model;


import java.util.Date;

public class OrderTimeline {

    private Date placed;
    private Date pending;
    private Date confirmed;
    private Date processing;
    private Date delivered;

    public OrderTimeline() {
    }

    public Date getPlaced() {
        return placed;
    }

    public void setPlaced(Date placed) {
        this.placed = placed;
    }

    public Date getPending() {
        return pending;
    }

    public void setPending(Date pending) {
        this.pending = pending;
    }

    public Date getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Date confirmed) {
        this.confirmed = confirmed;
    }

    public Date getProcessing() {
        return processing;
    }

    public void setProcessing(Date processing) {
        this.processing = processing;
    }

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }
}

