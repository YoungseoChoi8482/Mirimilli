package com.example.merge;

public class BranchData {
    private String branchName;
    private JobData top1, top2, top3;

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public JobData getTop1() {
        return top1;
    }

    public void setTop1(JobData top1) {
        this.top1 = top1;
    }

    public JobData getTop2() {
        return top2;
    }

    public void setTop2(JobData top2) {
        this.top2 = top2;
    }

    public JobData getTop3() {
        return top3;
    }

    public void setTop3(JobData top3) {
        this.top3 = top3;
    }
}
