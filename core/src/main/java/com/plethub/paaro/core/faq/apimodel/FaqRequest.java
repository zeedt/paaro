package com.plethub.paaro.core.faq.apimodel;

import com.plethub.paaro.core.models.Faq;

public class FaqRequest extends Faq {

    private Integer pageNo;

    private Integer pageSize;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
