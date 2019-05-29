package com.plethub.paaro.core.models;

public class CustomerStat {

    private Long completedTrans;
    private Long ongoingTrans;
    private Long allTrans;

    public Long getCompletedTrans() {
        return completedTrans;
    }

    public void setCompletedTrans(Long completedTrans) {
        this.completedTrans = completedTrans;
    }

    public Long getOngoingTrans() {
        return ongoingTrans;
    }

    public void setOngoingTrans(Long ongoingTrans) {
        this.ongoingTrans = ongoingTrans;
    }

    public Long getAllTrans() {
        return allTrans;
    }

    public void setAllTrans(Long allTrans) {
        this.allTrans = allTrans;
    }

    public CustomerStat() {
    }

    public CustomerStat(Long completedTrans, Long ongoingTrans, Long allTrans) {
        this.completedTrans = completedTrans;
        this.ongoingTrans = ongoingTrans;
        this.allTrans = allTrans;
    }

    @Override
    public String toString() {
        return "CustomerStat{" +
                "completedTrans=" + completedTrans +
                ", ongoingTrans=" + ongoingTrans +
                ", allTrans=" + allTrans +
                '}';
    }
}
