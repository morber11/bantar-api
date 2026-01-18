package com.bantar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TOPLIST_CATEGORY")
public class TopListCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TOPLIST_CATEGORY_ID")
    private long topListCategoryId;

    @Column(name = "CATEGORY_CODE", length = 100)
    private String categoryCode;

    @ManyToOne
    @JoinColumn(name = "TOPLIST_ID", nullable = false)
    private TopListEntity topList;

    public TopListCategoryEntity() {
    }

    public TopListCategoryEntity(long topListCategoryId, String categoryCode, TopListEntity topList) {
        this.topListCategoryId = topListCategoryId;
        this.categoryCode = categoryCode;
        this.topList = topList;
    }

    @SuppressWarnings("unused")
    public long getTopListCategoryId() {
        return topListCategoryId;
    }

    @SuppressWarnings("unused")
    public void setTopListCategoryId(long topListCategoryId) {
        this.topListCategoryId = topListCategoryId;
    }

    public String getCategory() {
        return categoryCode;
    }

    public void setCategory(String category) {
        this.categoryCode = category;
    }

    public TopListEntity getTopList() {
        return topList;
    }

    @SuppressWarnings("unused")
    public void setTopList(TopListEntity topList) {
        this.topList = topList;
    }
}